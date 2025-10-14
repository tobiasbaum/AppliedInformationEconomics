package de.set.aie.base;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Eine Zeitreihe, die es einfach ermöglicht, "Deals im Zeitverlauf" (also z.B. Verkäufe von Lizenz+Wartung, ...)
 * abzubilden.
 */
public class DealsOverTimeSeries extends TimeSeries {

    private final TimeSeries dealCounts;
    private final VarId dealSizeInitial;
    private final VarId dealSizeFactorRestYears;
    private final VarId dealDuration;

    private final ConcurrentHashMap<Integer, UniqueId> idForTime = new ConcurrentHashMap<>();

    /**
     * Konstruktor.
     * @param dealCounts Anzahl der Deals pro Jahr / Wahrscheinlichkeit, einen Deal pro Jahr zu machen.
     * @param dealSizeInitial Betrag je Deal im ersten Jahr des Deals.
     * @param dealSizeFactorRestYears
     *          Faktor für den Betrag je Deal abhängig vom Ursprungsbetrag für Folgejahre (z. B. Wartung).
     * @param dealDuration Laufzeit eines Deals (zusätzlich zum initialen Jahr, d.h. 0 = nur initiales Jahr).
     */
    public DealsOverTimeSeries(
            TimeSeries dealCounts,
            VarId dealSizeInitial,
            VarId dealSizeFactorRestYears,
            VarId dealDuration) {
        this.dealCounts = dealCounts;
        this.dealSizeInitial = dealSizeInitial;
        this.dealSizeFactorRestYears = dealSizeFactorRestYears;
        this.dealDuration = dealDuration;
    }

    private static final class DealsOverTimeVariable extends RandomVariable {
        private final int myTime;
        private final IntFunction<UniqueId> idFunction;
        private final List<RandomVariable> dealCounts;
        private final RandomVariable dealSizeInitial;
        private final RandomVariable dealSizeFactorRestYears;
        private final RandomVariable dealDuration;

        public DealsOverTimeVariable(
                int myTime,
                IntFunction<UniqueId> idFunction,
                List<RandomVariable> dealCounts,
                RandomVariable dealSizeInitial,
                RandomVariable dealSizeFactorRestYears,
                RandomVariable dealDuration) {
            this.myTime = myTime;
            this.idFunction = idFunction;
            this.dealCounts = dealCounts;
            this.dealSizeInitial = dealSizeInitial;
            this.dealSizeFactorRestYears = dealSizeFactorRestYears;
            this.dealDuration = dealDuration;
        }

        @Override
        public Quantity observe(RandomSource r, SimulationRun run) {
            VarId myId = idFunction.apply(myTime);
            if (!run.hasPersistentValue(myId)) {
                fillValuesUntilMyself(r, run);
            }
            return run.getPersistentValue(myId);
        }

        private void fillValuesUntilMyself(RandomSource r, SimulationRun run) {
            // Zeitreihe von Anfang an bis zu diesem Zeitpunkt vollständig simulieren
            for (int time = 0; time <= myTime; time++) {
                UniqueId id = idFunction.apply(time);
                // wenn ein Wert für das Jahr vorliegt, dann wurde es bereits vollständig simuliert
                if (run.hasPersistentValue(id)) {
                    continue;
                }
                Quantity dealCount = dealCounts.get(time).observe(r, run);
                long count = r.round(dealCount.getNumber());
                for (int deal = 0; deal < count; deal++) {
                    Quantity dealSize = dealSizeInitial.observe(r, run);
                    Quantity factor = dealSizeFactorRestYears.observe(r, run);
                    Quantity maintenance = dealSize.times(factor);
                    long duration = r.round(dealDuration.observe(r, run).getNumber());
                    addToYear(run, id, dealSize);
                    for (int i = 1; i <= duration; i++) {
                        addToYear(run, idFunction.apply(time + i), maintenance);
                    }
                }
                // da alle Vorjahre schon simuliert wurden, ist jetzt auch dieses Jahr vollständig simuliert
                run.persist(id, getForYear(run, id));
            }
        }

        private void addToYear(SimulationRun run, UniqueId id, Quantity toAdd) {
            run.persistObject(id, getForYear(run, id).plus(toAdd));
        }

        private Quantity getForYear(SimulationRun run, UniqueId id) {
            return run.hasPersistentObject(id) ?
                    (Quantity) run.getPersistentObject(id) : Quantity.of(0, this.getUnit());
        }

        @Override
        public QUnit getUnit() {
            return dealSizeInitial.getUnit().times(dealCounts.get(0).getUnit());
        }

        @Override
        public VarKind getType() {
            return VarKind.C;
        }
    }

    @Override
    public Function<Model.Instance, RandomVariable> getFor(int time) {
        return (Model.Instance inst) -> new DealsOverTimeVariable(
                time,
                this::getId,
                IntStream.rangeClosed(0, time)
                        .mapToObj((int i) -> dealCounts.getFor(i).apply(inst))
                        .collect(Collectors.toList()),
                inst.get(dealSizeInitial),
                inst.get(dealSizeFactorRestYears),
                inst.get(dealDuration)
        );
    }

    private UniqueId getId(int time) {
        return idForTime.computeIfAbsent(time, (Integer i) -> new UniqueId());
    }

}

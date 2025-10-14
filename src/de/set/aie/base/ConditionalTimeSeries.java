package de.set.aie.base;

import java.util.function.Function;

/**
 * Zeitreihe, die abhängig von einer Bedingung entweder die eine oder die andere Basis-Zeitreihe liefert.
 */
public class ConditionalTimeSeries extends TimeSeries {

    private static final RandomVariable TRUE = Distributions.fixed(1, QUnit.scalar());
    private static final RandomVariable FALSE = Distributions.fixed(0, QUnit.scalar());

    private final VarId decision;
    private final TimeSeries v1;
    private final TimeSeries v2;

    public ConditionalTimeSeries(Model m, VarId v1prop, TimeSeries v1, TimeSeries v2) {
        this.decision = v1prop.subvar("decision");
        // Damit für jeden Zeitpunkt die gleiche Zeitreihe kommt, muss die Entscheidung
        // persistent sein. Deshalb wird hier eine entsprechende Variable eingeführt.
        // Wenn sie bereits existiert, wird die bestehende genutzt (unter der Annahme, dass sie passend angelegt wurde)
        if (!m.getAllPersistentVariables().contains(decision)) {
            m.add(this.decision, (Model.Instance inst) ->
                    Distributions.conditional(inst.get(v1prop), TRUE, FALSE));
        }
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public Function<Model.Instance, RandomVariable> getFor(int time) {
        return (Model.Instance inst) ->
                Distributions.conditional(
                    inst.get(decision),
                    v1.getFor(time).apply(inst),
                    v2.getFor(time).apply(inst)
                );
    }
}

package de.set.aie.base;

import java.util.function.Function;

/**
 * Ableitung einer Basis-Zeitreihe.
 */
public class DifferentialTimeSeries extends TimeSeries {
    private final TimeSeries base;

    public DifferentialTimeSeries(TimeSeries base) {
        this.base = base;
    }

    @Override
    public Function<Model.Instance, RandomVariable> getFor(int time) {
        return (Model.Instance inst) -> {
            if (time == 0) {
                // zum Zeitpunkt 0 wird aktuell immer fest Steigung 0 zurückgegeben
                return base.getFor(0).apply(inst).times(0);
            } else {
                RandomVariable cur = base.getFor(time).apply(inst);
                RandomVariable prev = base.getFor(time - 1).apply(inst);
                return cur.minus(prev);
            }
        };
    }
}

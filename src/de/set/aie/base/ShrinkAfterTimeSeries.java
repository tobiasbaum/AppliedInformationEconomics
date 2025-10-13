package de.set.aie.base;

import java.util.function.Function;

/**
 * Zeitreihe, die auf einer Basis-Zeitreihe aufsetzt und diese nach einem bestimmten Zeitpunkt exponentiell
 * schrumpfen (oder wachsen) lässt.
 */
public class ShrinkAfterTimeSeries extends TimeSeries {
    private final TimeSeries base;
    private final VarId yearsUntil;
    private final VarId shrinkRate;

    public ShrinkAfterTimeSeries(TimeSeries base, VarId yearsUntil, VarId shrinkRate) {
        this.base = base;
        this.yearsUntil = yearsUntil;
        this.shrinkRate = shrinkRate;
    }

    @Override
    public Function<Model.Instance, RandomVariable> getFor(int time) {
        return (Model.Instance inst) -> {
            if (time == 0) {
                return base.getFor(time).apply(inst);
            } else {
                return Distributions.conditional(
                        inst.get(yearsUntil).lessThan(Distributions.fixed(time, Unit.scalar())),
                        base.getFor(time).apply(inst),
                        this.getFor(time - 1).apply(inst).times(inst.get(shrinkRate))
                );
            }
        };
    }
}

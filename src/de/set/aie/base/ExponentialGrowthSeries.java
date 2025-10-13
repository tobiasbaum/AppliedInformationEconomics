package de.set.aie.base;

import java.util.function.Function;

/**
 * Zeitreihe für exponentielles Wachstum.
 */
public class ExponentialGrowthSeries extends TimeSeries {
    private final VarId base;
    private final VarId growthRate;

    public ExponentialGrowthSeries(VarId base, VarId growthRate) {
        this.base = base;
        this.growthRate = growthRate;
    }

    @Override
    public Function<Model.Instance, RandomVariable> getFor(int time) {
        assert time >= 0;
        if (time <= 0) {
            return (Model.Instance inst) -> inst.get(base);
        } else {
            return (Model.Instance inst) -> getFor(time - 1).apply(inst).times(inst.get(growthRate));
        }
    }
}

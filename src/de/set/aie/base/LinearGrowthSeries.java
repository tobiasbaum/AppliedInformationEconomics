package de.set.aie.base;

import java.util.function.Function;

/**
 * Zeitreihe für lineares Wachstum.
 */
public class LinearGrowthSeries extends TimeSeries {
    private final VarId base;
    private final VarId growth;

    public LinearGrowthSeries(VarId base, VarId growth) {
        this.base = base;
        this.growth = growth;
    }

    @Override
    public Function<Model.Instance, RandomVariable> getFor(int time) {
        return (Model.Instance inst) -> inst.get(base).plus(inst.get(growth).times(time));
    }
}

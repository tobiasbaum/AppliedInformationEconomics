package de.set.aie.base;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Zeitreihe, die sich aus der komponentenweisen Verknüpfung zweier anderer Zeitreihen ergibt.
 */
public class CombinedTimeSeries extends TimeSeries {
    private BiFunction<RandomVariable, RandomVariable, RandomVariable> combiner;
    private TimeSeries s1;
    private TimeSeries s2;

    public CombinedTimeSeries(TimeSeries s1, TimeSeries s2,
                              BiFunction<RandomVariable, RandomVariable, RandomVariable> combiner) {
        this.combiner = combiner;
        this.s1 = s1;
        this.s2 = s2;
    }

    @Override
    public Function<Model.Instance, RandomVariable> getFor(int time) {
        return (Model.Instance inst) ->
                combiner.apply(
                        s1.getFor(time).apply(inst),
                        s2.getFor(time).apply(inst));
    }
}

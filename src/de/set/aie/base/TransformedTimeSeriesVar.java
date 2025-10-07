package de.set.aie.base;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Eine Zeitreihe, bei der jede Komponente durch Verknüpfung mit einer dynamisch abgefragten Zufallsvariablen
 * verändert wird.
 */
public class TransformedTimeSeriesVar extends TimeSeries {
    private BiFunction<RandomVariable, RandomVariable, RandomVariable> transformer;
    private TimeSeries s1;
    private String otherVarName;

    public TransformedTimeSeriesVar(TimeSeries s1, String otherVarName,
                                    BiFunction<RandomVariable, RandomVariable, RandomVariable> transformer) {
        this.transformer = transformer;
        this.s1 = s1;
        this.otherVarName = otherVarName;
    }

    @Override
    public Function<Model.Instance, RandomVariable> getFor(int time) {
        return (Model.Instance inst) ->
                transformer.apply(
                        s1.getFor(time).apply(inst),
                        inst.get(otherVarName));
    }
}

package de.set.aie.base;

import java.util.function.Function;

/**
 * Zeitreihe, die abhängig von einer Bedingung entweder die eine oder die andere Basis-Zeitreihe liefert.
 */
public class ConditionalTimeSeries extends TimeSeries {

    private final String v1prop;
    private final TimeSeries v1;
    private final TimeSeries v2;

    public ConditionalTimeSeries(String v1prop, TimeSeries v1, TimeSeries v2) {
        this.v1prop = v1prop;
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public Function<Model.Instance, RandomVariable> getFor(int time) {
        // nur wenn v1prop persistent ist, kommt sicher immer die komplette Zeitreihe 1 oder 2; sonst kann Mischmasch
        // passieren
        return (Model.Instance inst) ->
                Distributions.conditional(
                        inst.get(v1prop),
                        v1.getFor(time).apply(inst),
                        v2.getFor(time).apply(inst)
                );
    }
}

package de.set.aie.base;

import java.util.function.Function;

/**
 * Eine Verschiebung einer Basis-Zeitreihe um einen bestimmten Zeitraum.
 */
public class DelayedTimeSeries extends TimeSeries {
    private TimeSeries base;
    private int delay;

    public DelayedTimeSeries(TimeSeries base, int delay) {
        this.base = base;
        this.delay = delay;
    }

    @Override
    public Function<Model.Instance, ? extends RandomVariable> getFor(int time) {
        if (time < delay) {
            // am Anfang wird mit Nullen aufgefüllt
            return (Model.Instance inst) -> Distributions.fixed(0, base.getFor(0).apply(inst).getUnit());
        } else {
            return base.getFor(time - delay);
        }
    }
}

package de.set.aie.base;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Zeitreihe, die auf spezifischen Zufallsvariablen für spezifische Jahre beruht.
 */
public class ConcreteTimeSeries extends TimeSeries {
    public enum Repeat {
        /**
         * Alles, was nicht explizit angegeben ist, führt zu einem Fehler.
         */
        NONE,
        /**
         * Alles nach dem letzten angegebenen Zeitpunkt beruht auf der gleichen Zufallsvariablen.
         */
        LAST
    }

    private final List<VarId> vars = new ArrayList<>();
    private final Repeat repeat;

    public ConcreteTimeSeries(Repeat repeat, List<VarId> varNames) {
        this.vars.addAll(varNames);
        this.repeat = repeat;
    }

    @Override
    public Function<Model.Instance, RandomVariable> getFor(int time) {
        assert time >= 0;
        if (time >= vars.size()) {
            switch (repeat) {
                case NONE:
                    throw new AssertionError("invalid time: " + time + ", max is " + (vars.size() - 1));
                case LAST:
                    return (Model.Instance inst) -> inst.get(vars.get(vars.size() - 1));
                default:
                    throw new AssertionError("invalid repeat: " + repeat);
            }
        } else {
            return (Model.Instance inst) -> inst.get(vars.get(time));
        }
    }
}

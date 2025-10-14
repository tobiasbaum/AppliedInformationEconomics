package de.set.aie.base;

import java.util.LinkedList;
import java.util.stream.DoubleStream;

public class StubRV extends RandomVariable {
    private LinkedList<Double> observations = new LinkedList<>();

    public StubRV(double... observations) {
        DoubleStream.of(observations).forEach(this.observations::add);
    }

    @Override
    public Quantity observe(RandomSource r, SimulationRun run) {
        double d = observations.removeFirst();
        return Quantity.of(d, this.getUnit());
    }

    @Override
    public QUnit getUnit() {
        return QUnit.scalar();
    }

    @Override
    public VarKind getType() {
        return VarKind.D;
    }
}

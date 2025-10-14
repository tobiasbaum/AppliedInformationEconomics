package de.set.aie.base;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class ExponentialGrowthSeriesTest {

    private static double DELTA = 0.0000001;

    private static void checkPersistent(RandomVariable v, double expected, SimulationRun run) {
        RandomSource rs = RandomSource.wrap(new Random(123));
        assertEquals(v.observe(rs, run).getNumber(), expected, DELTA);
        // bei einer persistenten Variable muss für den gleichen Simulationslauf beim zweiten
        // Aufruf der gleiche Wert kommen
        assertEquals(v.observe(rs, run).getNumber(), expected, DELTA);
    }

    @Test
    public void testExponentialGrowthSeriesYear0() {
        VarId base = VarId.of("base");
        VarId exp = VarId.of("exp");
        ExponentialGrowthSeries s = new ExponentialGrowthSeries(base, exp);
        Model m = new Model();
        m.add(base, new StubRV(10, 11));
        m.add(exp, new StubRV(2, 4));
        Model.Instance inst = m.instantiate();
        RandomVariable v = s.getFor(0).apply(inst);
        // beim ersten Simulationslauf kommt 10
        checkPersistent(v, 10, new SimulationRun());
        // beim zweiten Simulationslauf kommt 11
        checkPersistent(v, 11, new SimulationRun());
    }

    @Test
    public void testExponentialGrowthSeriesMultipleYears() {
        VarId base = VarId.of("base");
        VarId exp = VarId.of("exp");
        ExponentialGrowthSeries s = new ExponentialGrowthSeries(base, exp);
        Model m = new Model();
        m.add(base, new StubRV(10, 6));
        m.add(exp, new StubRV(2, 3));
        Model.Instance inst = m.instantiate();
        SimulationRun run1 = new SimulationRun();
        checkPersistent(s.getFor(2).apply(inst), 40, run1);
        checkPersistent(s.getFor(3).apply(inst), 80, run1);
        checkPersistent(s.getFor(1).apply(inst), 20, run1);
        SimulationRun run2 = new SimulationRun();
        checkPersistent(s.getFor(1).apply(inst), 18, run2);
        checkPersistent(s.getFor(2).apply(inst), 54, run2);
        checkPersistent(s.getFor(3).apply(inst), 162, run2);
    }

}

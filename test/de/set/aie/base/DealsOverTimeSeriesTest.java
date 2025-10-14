package de.set.aie.base;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class DealsOverTimeSeriesTest {
    private static double DELTA = 0.0000001;

    private static void checkPersistent(RandomVariable v, double expected, SimulationRun run) {
        RandomSource rs = RandomSource.wrap(new Random(123));
        assertEquals(v.observe(rs, run).getNumber(), expected, DELTA);
        // bei einer persistenten Variable muss für den gleichen Simulationslauf beim zweiten
        // Aufruf der gleiche Wert kommen
        assertEquals(v.observe(rs, run).getNumber(), expected, DELTA);
    }

    @Test
    public void testDealsOverTime() {
        VarId count1 = VarId.of("count1");
        VarId count2 = VarId.of("count2");
        VarId count3 = VarId.of("count3");
        VarId count4 = VarId.of("count4");
        VarId count5 = VarId.of("count5");
        VarId count6 = VarId.of("count6");
        TimeSeries dealsPerYear = new ConcreteTimeSeries(
                ConcreteTimeSeries.Repeat.NONE,
                Arrays.asList(count1, count2, count3, count4, count5, count6));
        VarId size = VarId.of("size");
        VarId factor = VarId.of("factor");
        VarId duration = VarId.of("duration");
        DealsOverTimeSeries s = new DealsOverTimeSeries(dealsPerYear, size, factor, duration);
        Model m = new Model();
        // jedes Jahr sollte genau einmal angefragt werden, deshalb jeweils ein Wert
        m.add(count1, new StubRV(0));
        m.add(count2, new StubRV(1));
        m.add(count3, new StubRV(2));
        m.add(count4, new StubRV(0));
        m.add(count5, new StubRV(1));
        m.add(count6, new StubRV(1));
        // fünf Deals => fünf Werte; welcher Deal welcher Wert ist, ist Implementierungsdetail
        m.add(size, new StubRV(100, 100, 100, 100, 100));
        m.add(factor, new StubRV(0.11, 0.11, 0.11, 0.11, 0.11));
        m.add(duration, new StubRV(2, 2, 2, 2, 2));
        Model.Instance inst = m.instantiate();
        SimulationRun run = new SimulationRun();
        checkPersistent(s.getFor(2).apply(inst), 211, run);
        checkPersistent(s.getFor(4).apply(inst), 122, run);
        checkPersistent(s.getFor(1).apply(inst), 100, run);
        checkPersistent(s.getFor(3).apply(inst), 33, run);
        checkPersistent(s.getFor(5).apply(inst), 111, run);
        checkPersistent(s.getFor(0).apply(inst), 0, run);
    }

}

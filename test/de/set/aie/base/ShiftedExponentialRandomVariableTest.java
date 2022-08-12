/**
 * Copyright 2021-2022 SET GmbH, Tobias Baum.
 *
 * This file is part of AppliedInformationEconomics.
 *
 * AppliedInformationEconomics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AppliedInformationEconomics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package de.set.aie.base;

import static de.set.aie.base.BlockDistributedRandomVariableTest.checkCountBetween;
import static de.set.aie.base.BlockDistributedRandomVariableTest.sample;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ShiftedExponentialRandomVariableTest {

    @Test
    public void testDistribution1() {
        final ShiftedExponentialRandomVariable v = new ShiftedExponentialRandomVariable(3, 8, false, Unit.of("h"));
        assertEquals("D", v.getType());
        final double[] samples = sample(v, Unit.of("h")); //$NON-NLS-1$
        checkCountBetween(samples, Double.NEGATIVE_INFINITY, 3.0, 0, 0);
        checkCountBetween(samples, 3.0, 8.0, 90000, 400);
        checkCountBetween(samples, 3.0, 4.5051, 50000, 500);
        checkCountBetween(samples, 8.0, Double.POSITIVE_INFINITY, 10000, 200);
    }

    @Test
    public void testDistribution2() {
        final ShiftedExponentialRandomVariable v = new ShiftedExponentialRandomVariable(4, 10, true, Unit.of("EUR"));
        assertEquals("D", v.getType());
        final double[] samples = sample(v, Unit.of("EUR")); //$NON-NLS-1$
        checkCountBetween(samples, Double.NEGATIVE_INFINITY, 4.0, 10000, 200);
        checkCountBetween(samples, 4.0, 10.0, 90000, 400);
        checkCountBetween(samples, 8.1938, 10.0, 50000, 500);
        checkCountBetween(samples, 10.0, Double.POSITIVE_INFINITY, 0, 0);
    }

}

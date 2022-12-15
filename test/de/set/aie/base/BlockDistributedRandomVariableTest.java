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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

public class BlockDistributedRandomVariableTest {

    @Test
    public void testDistribution1() {
        final BlockDistributedRandomVariable v = new BlockDistributedRandomVariable(3, 8, Unit.of("h"));
        assertEquals("D", v.getType());
        final double[] samples = sample(v, Unit.of("h")); //$NON-NLS-1$
        checkCountBetween(samples, Double.NEGATIVE_INFINITY, 3.0, 5000, 200);
        checkCountBetween(samples, 3.0, 8.0, 90000, 400);
        checkCountBetween(samples, 3.0, 5.5, 45000, 400);
        checkCountBetween(samples, 5.5, 8.0, 45000, 400);
        checkCountBetween(samples, 8.0, Double.POSITIVE_INFINITY, 5000, 200);
    }

    static void checkCountBetween(
            final double[] samples, final double from, final double to, final int expectedCount, final int range) {
        int count = 0;
        for (final double sample : samples) {
            if (sample >= from && sample < to) {
                count++;
            }
        }
        assertTrue("count " + count + " but expected larger than " + (expectedCount - range), count >= expectedCount - range);
        assertTrue("count " + count + " but expected smaller than " + (expectedCount + range), count <= expectedCount + range);
    }

    static double[] sample(final RandomVariable v, final Unit expectedUnit) {
        assertEquals(expectedUnit, v.getUnit());
        final double[] ret = new double[100_000];
        final RandomSource r = RandomSource.wrap(new Random(1234));
        for (int i = 0; i < ret.length; i++) {
            final Quantity q = v.observe(r, 0);
            assertEquals(expectedUnit, q.getUnit());
            ret[i] = q.getNumber();
        }
        return ret;
    }

}

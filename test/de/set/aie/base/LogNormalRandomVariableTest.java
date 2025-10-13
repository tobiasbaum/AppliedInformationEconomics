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

public class LogNormalRandomVariableTest {

    @Test
    public void testDistribution1() {
        final LogNormalRandomVariable v = Distributions.logNormal(1, 100, QUnit.of("h"));
        assertEquals(VarKind.D, v.getType());
        final double[] samples = sample(v, QUnit.of("h")); //$NON-NLS-1$
        checkCountBetween(samples, Double.NEGATIVE_INFINITY, 0.0, 0, 0);
        checkCountBetween(samples, 0.0, 1.0, 5000, 100);
        checkCountBetween(samples, 1.0, 100.0, 90000, 500);
        checkCountBetween(samples, 100.0, Double.POSITIVE_INFINITY, 5000, 100);
    }

}

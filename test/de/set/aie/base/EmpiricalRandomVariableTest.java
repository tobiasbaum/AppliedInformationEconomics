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

public class EmpiricalRandomVariableTest {

    @Test
    public void testDistribution1() {
        final EmpiricalRandomVariable v = new EmpiricalRandomVariable(QUnit.of("EUR"), 1.0, 10.0, 15.0, 23.0);
        assertEquals(VarKind.D, v.getType());
        final double[] samples = sample(v, QUnit.of("EUR")); //$NON-NLS-1$
        checkCountBetween(samples, 0.5, 1.5, 25000, 400);
        checkCountBetween(samples, 9.5, 10.5, 25000, 400);
        checkCountBetween(samples, 14.5, 15.5, 25000, 400);
        checkCountBetween(samples, 22.5, 23.5, 25000, 400);
    }

}

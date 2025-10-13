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

public class BinomRandomVariableTest {

    @Test
    public void testDistribution1() {
        final BinomRandomVariable v = new BinomRandomVariable(
                Distributions.fixed(Quantity.of(99, "h")),
                Distributions.fixed(Quantity.of(0.5, Unit.scalar())));
        assertEquals(VarKind.C, v.getType());
        final double[] samples = sample(v, Unit.of("h")); //$NON-NLS-1$
        checkCountBetween(samples, Double.NEGATIVE_INFINITY, 0.0, 0, 0);
        checkCountBetween(samples, 0.0, 49.5, 50000, 500);
        checkCountBetween(samples, 99.5, Double.POSITIVE_INFINITY, 0, 0);
    }

    @Test
    public void testDistribution2() {
        final BinomRandomVariable v = new BinomRandomVariable(
                Distributions.fixed(Quantity.of(99, "h")),
                Distributions.fixed(Quantity.of(0.0, Unit.scalar())));
        assertEquals(VarKind.C, v.getType());
        final double[] samples = sample(v, Unit.of("h")); //$NON-NLS-1$
        checkCountBetween(samples, Double.NEGATIVE_INFINITY, 0.0, 0, 0);
        checkCountBetween(samples, 0.0, 0.5, 100000, 0);
        checkCountBetween(samples, 0.5, Double.POSITIVE_INFINITY, 0, 0);
    }

    @Test
    public void testDistribution3() {
        final BinomRandomVariable v = new BinomRandomVariable(
                Distributions.fixed(Quantity.of(99, "h")),
                Distributions.fixed(Quantity.of(1.0, Unit.scalar())));
        assertEquals(VarKind.C, v.getType());
        final double[] samples = sample(v, Unit.of("h")); //$NON-NLS-1$
        checkCountBetween(samples, Double.NEGATIVE_INFINITY, 98.5, 0, 0);
        checkCountBetween(samples, 99.0, 99.5, 100000, 0);
        checkCountBetween(samples, 99.5, Double.POSITIVE_INFINITY, 0, 0);
    }

}

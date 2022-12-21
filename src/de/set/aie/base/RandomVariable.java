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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public abstract class RandomVariable {

    public abstract Quantity observe(RandomSource r, final SimulationRun run);

    public void simulateTo(final File file, final long seed) throws IOException {
        final RandomSource r = RandomSource.wrap(new Random(seed));
        try (FileOutputStream out = new FileOutputStream(file)) {
            for (int i = 0; i < 10_000; i++) {
                final Quantity q = this.observe(r, new SimulationRun());
                final String s = Double.toString(q.getNumber()).replace('.', ',') + ";" + q.getUnit() + "\n";
                out.write(s.getBytes("UTF-8"));
            }
        }
    }

    public RandomVariable plus(final RandomVariable other) {
        return new RandomVariableSum(this, other);
    }

    public RandomVariable minus(final RandomVariable other) {
        return new RandomVariableDifference(this, other);
    }

    public RandomVariable times(final RandomVariable other) {
        return new RandomVariableProduct(this, other);
    }

    public RandomVariable times(final Quantity factor) {
        return this.times(new FixedRandomVariable(factor));
    }

    public RandomVariable times(final double factor) {
        return this.times(Quantity.of(factor, Unit.scalar()));
    }

    public RandomVariable sumOfN(final RandomVariable count) {
        return new RandomVariableSumOfN(this, count);
    }

    public RandomVariable sumOfN(final int count) {
        return new RandomVariableSumOfN(this, new FixedRandomVariable(Quantity.of(count, Unit.scalar())));
    }

    public RandomVariable div(final RandomVariable other) {
        return new RandomVariableQuotient(this, other);
    }

    public RandomVariable bound(final double lowerBound, final double upperBound) {
        return new RangeBoundRandomVariable(this, lowerBound, upperBound);
    }

    public RandomVariable nonNegative() {
        return this.bound(0, Double.POSITIVE_INFINITY);
    }

    public abstract Unit getUnit();

    public abstract String getType();

    public Sample sample(final long seed, final int sampleCount) {
        final double[] numbers = new double[sampleCount];
        final RandomSource r = RandomSource.wrap(new Random(seed));
        for (int i = 0; i < sampleCount; i++) {
            numbers[i] = this.observe(r, new SimulationRun()).getNumber();
        }
        return new Sample(numbers, this.getUnit());
    }

    public void printMeanAndMedian(final long seed, final int sampleCount) {
        System.out.println(this.sample(seed, sampleCount));
    }

    public Mean mean(final long seed, final int sampleCount) {
        double sum = 0.0;
        final RandomSource r = RandomSource.wrap(new Random(seed));
        for (int i = 0; i < sampleCount; i++) {
            sum += this.observe(r, new SimulationRun()).getNumber();
        }
        return new Mean(sum, sampleCount);
    }

}

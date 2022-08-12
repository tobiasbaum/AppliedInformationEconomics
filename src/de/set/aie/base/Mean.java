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

public class Mean {

    private final double sum;
    private final long count;

    Mean(final double sum, final long count) {
        this.sum = sum;
        this.count = count;
    }

    public static Mean of(final double[] numbers) {
        double sum = 0.0;
        for (final double d : numbers) {
            sum += d;
        }
        return new Mean(sum, numbers.length);
    }

    public static Mean undefined() {
        return new Mean(0.0, 0);
    }

    public double get() {
        return this.sum / this.count;
    }

    public Mean add(final Mean other) {
        return new Mean(this.sum + other.sum, this.count + other.count);
    }

    Mean add(final double sumToAdd, final int countToAdd) {
        return new Mean(this.sum + sumToAdd, this.count + countToAdd);
    }

    @Override
    public String toString() {
        return Quantity.of(this.get(), "").toString();
    }

}

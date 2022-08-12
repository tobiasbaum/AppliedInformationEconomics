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

import java.util.Arrays;

public class Sample {

    private final double[] numbers;
    private final Unit unit;

    Sample(final double[] numbers, final Unit unit) {
        this.numbers = numbers;
        this.unit = unit;
        Arrays.sort(numbers);
    }

    @Override
    public String toString() {
        final StringBuilder ret = new StringBuilder();
        ret.append(format("Mean:    ", Quantity.of(this.mean(), this.unit))).append('\n');
        ret.append(format("Min:     ", Quantity.of(this.numbers[0], this.unit))).append('\n');
        ret.append(format("1. Quart:", Quantity.of(this.numbers[this.numbers.length / 4], this.unit))).append('\n');
        ret.append(format("Median:  ", Quantity.of(this.median(), this.unit))).append('\n');
        ret.append(format("3. Quart:", Quantity.of(this.numbers[this.numbers.length * 3 / 4], this.unit))).append('\n');
        ret.append(format("Max:     ", Quantity.of(this.numbers[this.numbers.length - 1], this.unit))).append('\n');
        return ret.toString();
    }

    private static String format(final String string, final Quantity q) {
        return String.format("%s %17s", string, q);
    }

    public Quantity meanQ() {
        return Quantity.of(this.mean(), this.unit);
    }

    public double mean() {
        return Mean.of(this.numbers).get();
    }

    public double median() {
        return this.numbers[this.numbers.length / 2];
    }

}

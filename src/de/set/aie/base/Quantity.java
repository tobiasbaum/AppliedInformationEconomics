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

import java.util.Locale;

public class Quantity implements Comparable<Quantity> {

    private final double value;
    private final Unit unit;

    private Quantity(final double v, final Unit u) {
        this.value = v;
        this.unit = u;
    }

    public static Quantity of(final double value, final String unit) {
        return of(value, Unit.of(unit));
    }

    public static Quantity of(final double value, final Unit unit) {
        return new Quantity(value, unit);
    }

    public Quantity plus(final Quantity other) {
        if (!this.unit.equals(other.unit)) {
            throw new AssertionError("incompatible units: " + this.unit + " vs " + other.unit);
        }
        return new Quantity(this.value + other.value, this.unit);
    }

    public Quantity minus(final Quantity other) {
        if (!this.unit.equals(other.unit)) {
            throw new AssertionError("incompatible units: " + this.unit + " vs " + other.unit);
        }
        return new Quantity(this.value - other.value, this.unit);
    }

    public Quantity times(final Quantity other) {
        return new Quantity(this.value * other.value, this.unit.times(other.unit));
    }

    public Quantity div(final Quantity other) {
        return new Quantity(this.value / other.value, this.unit.div(other.unit));
    }

    @Override
    public int hashCode() {
        return Double.hashCode(this.value) + this.unit.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Quantity)) {
            return false;
        }
        final Quantity other = (Quantity) o;
        return this.value == other.value && this.unit.equals(other.unit);
    }

    @Override
    public int compareTo(Quantity other) {
        if (!this.unit.equals(other.unit)) {
            throw new IllegalArgumentException("Incompatible units: " + this.unit + " vs " + other.unit);
        }
        return Double.compare(this.value, other.value);
    }

    @Override
    public String toString() {
        return String.format(Locale.GERMAN, "%,.2f%s", this.value, this.unit.toString());
    }

    public double getNumber() {
        return this.value;
    }

    public Unit getUnit() {
        return this.unit;
    }

}

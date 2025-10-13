/**
 * Copyright 2021-2024 SET GmbH, Tobias Baum.
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

/**
 * A combination of two normal distributions, one for the "left side" of the mode, the other for the "right side"
 * of the mode.
 */
public class NormalCombRandomVariable extends RandomVariable {

    private final double mode;
    private final double sdLeft;
    private final double sdRight;
    private final QUnit unit;

    public NormalCombRandomVariable(double lower, double mode, double upper, QUnit unit) {
        assert lower <= mode;
        assert mode <= upper;
        this.mode = mode;
        this.sdLeft = (mode - lower) / Distributions.NORMAL_TAIL_FACTOR;
        this.sdRight = (upper - mode) / Distributions.NORMAL_TAIL_FACTOR;
        this.unit = unit;
    }

    @Override
    public Quantity observe(final RandomSource r, final SimulationRun run) {
        final boolean left = r.nextBool(0.5);
        final double value;
        if (left) {
            if (this.sdLeft == 0.0) {
                value = r.nextBool(0.95) ? this.mode : Math.nextDown(this.mode);
            } else {
                value = this.mode - Math.abs(r.nextGaussian() * this.sdLeft);
            }
        } else {
            if (this.sdRight == 0.0) {
                value = r.nextBool(0.95) ? this.mode : Math.nextUp(this.mode);
            } else {
                value = this.mode + Math.abs(r.nextGaussian() * this.sdRight);
            }
        }
        return Quantity.of(value, this.unit);
    }

    @Override
    public QUnit getUnit() {
        return this.unit;
    }

    @Override
    public VarKind getType() {
        return VarKind.D;
    }

}

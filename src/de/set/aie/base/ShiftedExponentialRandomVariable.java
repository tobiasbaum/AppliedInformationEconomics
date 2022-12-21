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

public class ShiftedExponentialRandomVariable extends RandomVariable {

    private final double lambda;
    private final double shift;
    private final boolean directionInverse;
    private final Unit unit;

    public ShiftedExponentialRandomVariable(
            final double lower, final double upper, final boolean directionInverse, final Unit unit) {
        final double diff = upper - lower;
        this.lambda = 2.30259 / diff;
        this.directionInverse = directionInverse;
        this.shift = directionInverse ? upper : lower;
        this.unit = unit;
    }

    @Override
    public Quantity observe(final RandomSource r, final SimulationRun run) {
        final double sampled = Math.log(1.0 - r.nextDouble()) / -this.lambda;
        final double shifted = this.directionInverse ? this.shift - sampled : this.shift + sampled;
        return Quantity.of(shifted, this.getUnit());
    }

    @Override
    public Unit getUnit() {
        return this.unit;
    }

    @Override
    public String getType() {
        return "D"; //$NON-NLS-1$
    }

}

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

public class RangeBoundRandomVariable extends RandomVariable {

    private final RandomVariable base;
    private final double lower;
    private final double upper;

    public RangeBoundRandomVariable(final RandomVariable randomVariable, final double lower, final double upper) {
        this.base = randomVariable;
        this.lower = lower;
        this.upper = upper;
    }

    @Override
    public Quantity observe(final RandomSource r, final SimulationRun run) {
        final Quantity q = this.base.observe(r, run);
        if (q.getNumber() < this.lower) {
            return Quantity.of(this.lower, q.getUnit());
        }
        if (q.getNumber() > this.upper) {
            return Quantity.of(this.upper, q.getUnit());
        }
        return q;
    }

    @Override
    public Unit getUnit() {
        return this.base.getUnit();
    }

    @Override
    public String getType() {
        return this.base.getType();
    }

}

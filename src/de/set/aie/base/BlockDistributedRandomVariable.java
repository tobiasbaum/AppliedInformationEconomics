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

public class BlockDistributedRandomVariable extends RandomVariable {

    private final double lower;
    private final double upper;
    private final Unit unit;

    public BlockDistributedRandomVariable(final double lower, final double upper, final Unit unit) {
        this.lower = lower;
        this.upper = upper;
        this.unit = unit;
    }

    @Override
    public Quantity observe(final RandomSource r, final SimulationRun run) {
        final double type = r.nextDouble();
        final double diff = this.upper - this.lower;
        if (type < 0.05) {
            return Quantity.of(this.lower - diff + diff * r.nextDouble(), this.unit);
        } else if (type < 0.95) {
            return Quantity.of(this.lower + diff * r.nextDouble(), this.unit);
        } else {
            return Quantity.of(this.upper + diff * r.nextDouble(), this.unit);
        }
    }

    @Override
    public Unit getUnit() {
        return this.unit;
    }

    @Override
    public VarKind getType() {
        return VarKind.D;
    }

}

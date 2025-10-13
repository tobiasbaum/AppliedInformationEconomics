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

public class EmpiricalRandomVariable extends RandomVariable {

    private final double[] values;
    private final Unit unit;

    public EmpiricalRandomVariable(final Unit unit, final double... values) {
        assert values.length > 0;
        this.values = values;
        this.unit = unit;
    }

    @Override
    public Quantity observe(final RandomSource r, final SimulationRun run) {
        final int index = r.nextInt(this.values.length);
        return Quantity.of(this.values[index], this.getUnit());
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

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

public class ConditionalDistribution extends RandomVariable {

    private final RandomVariable v1;
    private final RandomVariable v2;
    private final RandomVariable v1Prop;

    public ConditionalDistribution(final double v1Prop, final RandomVariable v1, final RandomVariable v2) {
        this(new FixedRandomVariable(Quantity.of(v1Prop, Unit.scalar())), v1, v2);
    }

    public ConditionalDistribution(final RandomVariable v1Prop, final RandomVariable v1, final RandomVariable v2) {
        assert v1.getUnit().equals(v2.getUnit());
        assert v1Prop.getUnit().equals(Unit.scalar());
        this.v1 = v1;
        this.v2 = v2;
        this.v1Prop = v1Prop;
    }

    @Override
    public Quantity observe(final RandomSource r, final SimulationRun run) {
        if (r.nextDouble() < this.v1Prop.observe(r, run).getNumber()) {
            return this.v1.observe(r, run);
        } else {
            return this.v2.observe(r, run);
        }
    }

    @Override
    public Unit getUnit() {
        return this.v1.getUnit();
    }

    @Override
    public String getType() {
        return "C";
    }

}

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

import java.util.Random;

public class ConditionalValue extends RandomVariable {

    private final Quantity v1;
    private final Quantity v2;
    private final double v1Prop;

    public ConditionalValue(final double v1Prop, final Quantity v1, final Quantity v2) {
        assert v1.getUnit().equals(v2.getUnit());
        this.v1 = v1;
        this.v2 = v2;
        this.v1Prop = v1Prop;
    }

    public ConditionalValue(final double v1Prop, final Quantity v1) {
        this(v1Prop, v1, v1.times(Quantity.of(0, Unit.scalar())));
    }

    @Override
    public Quantity observe(final Random r, final int run) {
        if (r.nextDouble() < this.v1Prop) {
            return this.v1;
        } else {
            return this.v2;
        }
    }

    @Override
    public Unit getUnit() {
        return this.v1.getUnit();
    }

    @Override
    public String getType() {
        return "D";
    }

}

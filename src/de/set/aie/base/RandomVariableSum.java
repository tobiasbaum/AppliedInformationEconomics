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

public class RandomVariableSum extends RandomVariable {

    private final RandomVariable r1;
    private final RandomVariable r2;

    public RandomVariableSum(final RandomVariable r1, final RandomVariable r2) {
        this.r1 = r1;
        this.r2 = r2;
    }

    @Override
    public Quantity observe(final RandomSource r, final int run) {
        return this.r1.observe(r, run).plus(this.r2.observe(r, run));
    }

    @Override
    public Unit getUnit() {
        return this.r1.getUnit();
    }

    @Override
    public String getType() {
        return "C"; //$NON-NLS-1$
    }

}

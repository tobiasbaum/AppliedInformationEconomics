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

public class RandomVariableSumOfN extends RandomVariable {

    private final RandomVariable toSum;
    private final RandomVariable count;

    public RandomVariableSumOfN(final RandomVariable toSum, final RandomVariable count) {
        this.toSum = toSum;
        this.count = count;
    }

    @Override
    public Quantity observe(final Random r, final int run) {
        final Quantity count = this.count.observe(r, run);
        final long max = Math.round(count.getNumber());
        double sum = 0.0;
        for (long i = 0; i < max; i++) {
            sum += this.toSum.observe(r, run).getNumber();
        }
        return Quantity.of(sum, this.getUnit());
    }

    @Override
    public Unit getUnit() {
        return this.count.getUnit().times(this.toSum.getUnit());
    }

    @Override
    public String getType() {
        return "C"; //$NON-NLS-1$
    }

}

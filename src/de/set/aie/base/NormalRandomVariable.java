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

public class NormalRandomVariable extends RandomVariable {

    private final double mean;
    private final double sd;
    private final Unit unit;

    public NormalRandomVariable(final double mean, final double sd, final Unit unit) {
        this.mean = mean;
        this.sd = sd;
        this.unit = unit;
    }

    @Override
    public Quantity observe(final Random r, final int run) {
        return Quantity.of(r.nextGaussian() * this.sd + this.mean, this.unit);
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

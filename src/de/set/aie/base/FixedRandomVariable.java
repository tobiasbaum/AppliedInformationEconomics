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

public class FixedRandomVariable extends RandomVariable {

    private final Quantity q;

    public FixedRandomVariable(final Quantity q) {
        this.q = q;
    }

    @Override
    public Quantity observe(final Random r, final int run) {
        return this.q;
    }

    @Override
    public Unit getUnit() {
        return this.q.getUnit();
    }

    @Override
    public String getType() {
        return "F"; //$NON-NLS-1$
    }

}

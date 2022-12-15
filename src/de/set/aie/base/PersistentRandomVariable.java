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

public class PersistentRandomVariable extends RandomVariable {

    private final RandomVariable base;
    private int savedRun;
    private Quantity savedValue;

    public PersistentRandomVariable(final RandomVariable randomVariable) {
        this.base = randomVariable;
    }

    @Override
    public Quantity observe(final RandomSource r, final int run) {
        if (this.savedValue == null || this.savedRun != run) {
            this.savedValue = this.base.observe(r, run);
            this.savedRun = run;
        }
        return this.savedValue;
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

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

public class UncertainDistributionVariable extends RandomVariable {

    private final RandomVariable index;
    private final RandomVariable[] dists;

    public UncertainDistributionVariable(final RandomVariable index, final RandomVariable... dists) {
        this.index = index;
        this.dists = dists;
    }

    @Override
    public Quantity observe(final RandomSource r, final SimulationRun run) {
        final int i = (int) Math.round(this.index.observe(r, run).getNumber());
        return this.dists[i].observe(r, run);
    }

    @Override
    public QUnit getUnit() {
        return this.dists[0].getUnit();
    }

    @Override
    public VarKind getType() {
        return VarKind.U;
    }

}

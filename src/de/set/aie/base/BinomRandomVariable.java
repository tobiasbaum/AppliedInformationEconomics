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

public class BinomRandomVariable extends RandomVariable {

    private final RandomVariable count;
    private final RandomVariable propability;

    public BinomRandomVariable(final RandomVariable count, final RandomVariable prop) {
        assert prop.getUnit().equals(Unit.scalar());
        this.count = count;
        this.propability = prop;
    }

    @Override
    public Quantity observe(final RandomSource r, final SimulationRun run) {
        final long cnt = Math.round(this.count.observe(r, run).getNumber());
        final double p = this.propability.observe(r, run).getNumber();
        long sum = 0;
        for (long i = 0; i < cnt; i++) {
            if (r.nextDouble() < p) {
                sum++;
            }
        }
        return Quantity.of(sum, this.getUnit());
    }

    @Override
    public Unit getUnit() {
        return this.count.getUnit();
    }

    @Override
    public VarKind getType() {
        return VarKind.C; //$NON-NLS-1$
    }
}

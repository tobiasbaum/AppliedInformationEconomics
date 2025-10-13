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

public class TriangularRandomVariable extends RandomVariable {

    private final Quantity lower;
    private final Quantity mode;
    private final Quantity upper;

    private TriangularRandomVariable(final Quantity lower, final Quantity mode, final Quantity upper) {
        assert lower.getUnit().equals(mode.getUnit());
        assert mode.getUnit().equals(upper.getUnit());
        assert lower.getNumber() <= mode.getNumber();
        assert mode.getNumber() <= upper.getNumber();
        assert lower.getNumber() < upper.getNumber();
        this.lower = lower;
        this.mode = mode;
        this.upper = upper;
    }

    public static TriangularRandomVariable fromConfIntv(
            final Quantity lower005, final Quantity mode, final Quantity upper095) {
        throw new RuntimeException("not yet supported");
    }

    public static TriangularRandomVariable fromAbsoluteMinMax(
            final Quantity absoluteMin, final Quantity mode, final Quantity absoluteMax) {
        return new TriangularRandomVariable(absoluteMin, mode, absoluteMax);
    }

    @Override
    public Quantity observe(final RandomSource r, final SimulationRun run) {
        final double a = this.lower.getNumber();
        final double b = this.upper.getNumber();
        final double c = this.mode.getNumber();
        final double F = (c - a) / (b - a);
        final double rand = Math.random();
        if (rand < F) {
            return Quantity.of(a + Math.sqrt(rand * (b - a) * (c - a)), this.getUnit());
        } else {
            return Quantity.of(b - Math.sqrt((1 - rand) * (b - a) * (b - c)), this.getUnit());
        }
    }

    @Override
    public Unit getUnit() {
        return this.mode.getUnit();
    }

    @Override
    public VarKind getType() {
        return VarKind.D;
    }

}

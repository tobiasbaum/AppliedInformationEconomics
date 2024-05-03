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
    private final String name;

    PersistentRandomVariable(String name, final RandomVariable randomVariable) {
        this.name = name;
        this.base = randomVariable;
    }

    @Override
    public Quantity observe(final RandomSource r, final SimulationRun run) {
        try {
            if (!run.hasPersistentValue(this.name)) {
                final Quantity v = this.base.observe(r, run);
                run.persist(this.name, v);
                return v;
            } else {
                return run.getPersistentValue(this.name);
            }
        } catch (final Throwable t) {
            throw new RuntimeException("problem with " + this.name, t);
        }
    }

    @Override
    public Unit getUnit() {
        return this.base.getUnit();
    }

    @Override
    public String getType() {
        return this.base.getType();
    }

    public static PersistentRandomVariable ensurePersistent(String name2, RandomVariable toPersist) {
        if (toPersist instanceof PersistentRandomVariable) {
            assert name2.equals(((PersistentRandomVariable) toPersist).name);
            return (PersistentRandomVariable) toPersist;
        } else {
            return new PersistentRandomVariable(name2, toPersist);
        }
    }

}

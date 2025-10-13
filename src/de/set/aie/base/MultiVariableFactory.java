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

import java.util.Map;
import java.util.function.Function;

import de.set.aie.base.Model.Instance;

public interface MultiVariableFactory {

    public abstract Map<VarId, Function<Instance, RandomVariable>> create(VarId baseName);

    public abstract MultiVariableFactory bound(double lowerBound, double upperBound);

    public default MultiVariableFactory nonNegative() {
        return this.bound(0, Double.POSITIVE_INFINITY);
    }

}

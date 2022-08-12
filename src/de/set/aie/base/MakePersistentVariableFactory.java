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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import de.set.aie.base.Model.Instance;

class MakePersistentVariableFactory implements MultiVariableFactory {

    private final MultiVariableFactory base;

    public MakePersistentVariableFactory(final MultiVariableFactory base) {
        this.base = base;
    }

    @Override
    public Map<String, Function<Instance, RandomVariable>> create(final String baseName) {
        final Map<String, Function<Instance, RandomVariable>> m = new LinkedHashMap<>(this.base.create(baseName));
        m.replaceAll((final String name, final Function<Instance, RandomVariable> f) ->
                f.andThen(RandomVariable::makePersistent));
        return m;
    }

}

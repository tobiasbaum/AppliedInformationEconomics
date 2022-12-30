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
import java.util.Set;

public class SimulationRun {

    private final Map<String, Quantity> values = new LinkedHashMap<>();
    private final Map<String, Object> objects = new LinkedHashMap<>();

    public boolean hasPersistentValue(String name) {
        return this.values.containsKey(name);
    }

    public void persist(String name, Quantity v) {
        assert !this.values.containsKey(name);
        this.values.put(name, v);
    }

    public Quantity getPersistentValue(String name) {
        final Quantity q = this.values.get(name);
        if (q == null) {
            throw new AssertionError("variable was not persisted: " + name);
        }
        return q;
    }

    public boolean hasPersistentObject(String name) {
        return this.objects.containsKey(name);
    }

    public void persistObject(String name, Object v) {
        assert !this.values.containsKey(name);
        this.objects.put(name, v);
    }

    public Object getPersistentObject(String name) {
        final Object q = this.objects.get(name);
        if (q == null) {
            throw new AssertionError("object was not persisted: " + name);
        }
        return q;
    }

    public Set<? extends String> getPersistentValueNames() {
        return this.values.keySet();
    }

}

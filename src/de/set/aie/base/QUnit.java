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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class QUnit {

    private final List<String> numer;
    private final List<String> denom;

    private QUnit(final List<String> n, final List<String> d) {
        final Iterator<String> iter = n.iterator();
        while (iter.hasNext()) {
            final String v = iter.next();
            if (d.contains(v)) {
                d.remove(v);
                iter.remove();
            }
        }
        Collections.sort(n);
        Collections.sort(d);
        this.numer = n;
        this.denom = d;
    }

    public static QUnit of(final String name) {
        return new QUnit(Collections.singletonList(name), Collections.emptyList());
    }

    public static QUnit scalar() {
        return new QUnit(Collections.emptyList(), Collections.emptyList());
    }

    @Override
    public int hashCode() {
        return this.numer.hashCode() + 23 * this.denom.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof QUnit)) {
            return false;
        }
        final QUnit other = (QUnit) o;
        return this.numer.equals(other.numer) && this.denom.equals(other.denom);
    }

    @Override
    public String toString() {
        final String ns;
        if (this.numer.isEmpty()) {
            ns = this.denom.isEmpty() ? "" : "1";
        } else {
            ns = String.join("*", this.numer);
        }
        if (this.denom.isEmpty()) {
            return ns;
        } else {
            return ns + "/" + String.join("*", this.denom);
        }
    }

    public QUnit times(final QUnit unit) {
        final List<String> n = this.combine(this.numer, unit.numer);
        final List<String> d = this.combine(this.denom, unit.denom);
        return new QUnit(n, d);
    }

    public QUnit div(final QUnit unit) {
        final List<String> n = this.combine(this.numer, unit.denom);
        final List<String> d = this.combine(this.denom, unit.numer);
        return new QUnit(n, d);
    }

    private List<String> combine(final List<String> l1, final List<String> l2) {
        final List<String> combined = new ArrayList<>();
        combined.addAll(l1);
        combined.addAll(l2);
        return combined;
    }

}

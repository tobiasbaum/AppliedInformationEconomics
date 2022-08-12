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

import org.junit.Test;

import de.set.aie.base.Model.Instance;

public class ModelTest {

    @Test
    public void test() throws Exception {
        final Model m = new Model();
        m.add("dir", Distributions.empirical(Unit.scalar(), -1.0, 1.0));
        m.add("val", Distributions.shiftedExp(1.0, 10.0, Unit.of("EUR")));
        m.add("combined", (final Instance i) -> i.get("dir").times(i.get("val")));

        m.printValuesOfInformation(132, "combined");
    }

}

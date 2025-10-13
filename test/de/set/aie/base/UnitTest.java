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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class UnitTest {

    @Test
    public void testToString1() {
        final QUnit u = QUnit.of("EUR");
        assertEquals("EUR", u.toString());
    }

    @Test
    public void testToString2() {
        final QUnit u1 = QUnit.of("EUR");
        final QUnit u2 = QUnit.of("cm");
        assertEquals("EUR*cm", u1.times(u2).toString());
    }

    @Test
    public void testToString3() {
        final QUnit u = QUnit.of("EUR");
        assertEquals("EUR*EUR", u.times(u).toString());
    }

    @Test
    public void testToString4() {
        final QUnit u = QUnit.of("EUR");
        assertEquals("", u.div(u).toString());
    }

    @Test
    public void testToString5() {
        final QUnit u1 = QUnit.of("EUR");
        final QUnit u2 = QUnit.of("h");
        assertEquals("EUR/h", u1.div(u2).toString());
    }

    @Test
    public void testToString6() {
        final QUnit u = QUnit.of("EUR");
        assertEquals("1/EUR", u.div(u).div(u).toString());
    }

    @Test
    public void testToString7() {
        final QUnit u = QUnit.of("EUR");
        assertEquals("EUR", u.times(u).div(u).toString());
    }

    @Test
    public void testToString8() {
        final QUnit u = QUnit.of("EUR");
        assertEquals("EUR", u.div(u).times(u).toString());
    }

    @Test
    public void testEquals() {
        final QUnit u1 = QUnit.of("EUR");
        final QUnit u2 = QUnit.of("EUR");
        assertEquals(u1, u2);
        assertNotEquals(u1, u2.times(u1));
        assertNotEquals(u1, u2.div(u1));
    }

}

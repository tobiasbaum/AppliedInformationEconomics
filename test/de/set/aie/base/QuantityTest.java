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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

@SuppressWarnings("nls")
public class QuantityTest {

    @Test
    public void testPlus1() {
        assertEquals(Quantity.of(3, "h"), Quantity.of(1, "h").plus(Quantity.of(2, "h")));
        assertEquals(Quantity.of(7, "EUR"), Quantity.of(4, "EUR").plus(Quantity.of(3, "EUR")));
        try {
            Quantity.of(4, "EUR").plus(Quantity.of(3, "h"));
            fail("expected exception");
        } catch (final AssertionError e) {
        }
    }

    @Test
    public void testMinus1() {
        assertEquals(Quantity.of(-1, "h"), Quantity.of(1, "h").minus(Quantity.of(2, "h")));
        assertEquals(Quantity.of(1, "EUR"), Quantity.of(4, "EUR").minus(Quantity.of(3, "EUR")));
        try {
            Quantity.of(4, "EUR").minus(Quantity.of(3, "h"));
            fail("expected exception");
        } catch (final AssertionError e) {
        }
    }

    @Test
    public void testTimes1() {
        assertEquals(Quantity.of(2, "h"), Quantity.of(1, "h").times(Quantity.of(2, QUnit.scalar())));
        assertEquals(Quantity.of(12, QUnit.of("EUR").times(QUnit.of("EUR"))), Quantity.of(4, "EUR").times(Quantity.of(3, "EUR")));
    }

    @Test
    public void testHashCode() {
        assertEquals(Quantity.of(2, "h").hashCode(), Quantity.of(2, "h").hashCode());
    }

    @Test
    public void testEquals() {
        final Quantity q1 = Quantity.of(2, "EUR");
        final Quantity q2 = Quantity.of(2, "EUR");
        final Quantity q3 = Quantity.of(2, "h");
        final Quantity q4 = Quantity.of(1, "EUR");
        assertTrue(q1.equals(q1));
        assertTrue(q1.equals(q2));
        assertFalse(q1.equals(null));
        assertFalse(q1.equals(new Object()));
        assertFalse(q1.equals(q3));
        assertFalse(q1.equals(q4));
    }

    @Test
    public void testToString() {
        assertEquals("2,00", Quantity.of(2, QUnit.scalar()).toString());
        assertEquals("3,10EUR", Quantity.of(3.1, "EUR").toString());
        assertEquals("1.234.567,10EUR", Quantity.of(1234567.1, "EUR").toString());
    }

    @Test
    public void testGetter() {
        assertEquals(2.0, Quantity.of(2, QUnit.scalar()).getNumber(), 0.000000001);
        assertEquals(QUnit.scalar(), Quantity.of(2, QUnit.scalar()).getUnit());
    }

}

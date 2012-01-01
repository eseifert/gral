/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2012 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael[at]erichseifert.de>
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.erichseifert.gral.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DummyDataTest {
	@Test
	public void testCreate() {
		Comparable<?>[] expected = { Integer.valueOf(42), new Double(1.23), "foobar" };

		for (Comparable<?> value : expected) {
			DummyData integer = new DummyData(2, 3, value);
			assertEquals(2, integer.getColumnCount());
			assertEquals(3, integer.getRowCount());
			assertEquals(integer.getColumnCount(), integer.getColumnTypes().length);
			for (Comparable<?> cell : integer) {
				assertEquals(value, cell);
			}
		}
	}

}

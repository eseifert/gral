/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2012 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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

import org.junit.BeforeClass;
import org.junit.Test;

public class EnumeratedDataTest {
	private static final double DELTA = 1e-15;
	private static DataTable table;

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setUpBeforeClass() {
		table = new DataTable(Integer.class, Integer.class);
		table.add(3, 1); // 0
		table.add(2, 3); // 1
		table.add(1, 2); // 2
	}

	@Test
	public void testCreate() {
		// without parameters
		EnumeratedData withoutParams = new EnumeratedData(table);
		assertEquals(table.getColumnCount() + 1, withoutParams.getColumnCount());
		assertEquals(table.getRowCount(), withoutParams.getRowCount());
		assertEquals(0.0, ((Number) withoutParams.get(0, 0)).doubleValue(), DELTA);
		assertEquals(1.0, ((Number) withoutParams.get(0, 1)).doubleValue(), DELTA);
		assertEquals(2.0, ((Number) withoutParams.get(0, 2)).doubleValue(), DELTA);

		// with parameters
		EnumeratedData withParams = new EnumeratedData(table, -1, 2.0);
		assertEquals(table.getColumnCount() + 1, withParams.getColumnCount());
		assertEquals(table.getRowCount(), withParams.getRowCount());
		assertEquals(-1.0, ((Number) withParams.get(0, 0)).doubleValue(), DELTA);
		assertEquals( 1.0, ((Number) withParams.get(0, 1)).doubleValue(), DELTA);
		assertEquals( 3.0, ((Number) withParams.get(0, 2)).doubleValue(), DELTA);
	}

}

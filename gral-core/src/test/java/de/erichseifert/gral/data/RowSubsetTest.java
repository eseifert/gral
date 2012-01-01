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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class RowSubsetTest {
	private DataTable table;
	private RowSubset data;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() {
		table = new DataTable(Integer.class, Integer.class);
		table.add(1, 1); // 0
		table.add(2, 3); // 1
		table.add(3, 2); // 2
		table.add(4, 6); // 3
		table.add(5, 4); // 4
		table.add(6, 8); // 5
		table.add(7, 9); // 6
		table.add(8, 11); // 7

		data = new RowSubset(table) {
			@Override
			public boolean accept(Row row) {
				Comparable<?> cell = row.get(0);
				return (cell instanceof Number) &&
					(((Number) cell).doubleValue() % 2.0) == 0.0;
			}
		};
	}

	@Test
	public void testCreate() {
		assertEquals(table.getColumnCount(), data.getColumnCount());
		assertEquals(table.getRowCount()/2, data.getRowCount());
		assertArrayEquals(table.getColumnTypes(), data.getColumnTypes());
	}

	@Test
	public void testGetIntInt() {
		assertEquals( 2, data.get(0, 0));
		assertEquals( 4, data.get(0, 1));
		assertEquals( 6, data.get(0, 2));
		assertEquals( 8, data.get(0, 3));
		assertEquals( 3, data.get(1, 0));
		assertEquals( 6, data.get(1, 1));
		assertEquals( 8, data.get(1, 2));
		assertEquals(11, data.get(1, 3));
	}

	@Test
	public void testGetInt() {
		assertEquals(table.getRow(1), data.getRow(0));
		assertEquals(table.getRow(3), data.getRow(1));
		assertEquals(table.getRow(5), data.getRow(2));
		assertEquals(table.getRow(7), data.getRow(3));
	}

	@Test
	public void testDataAdded() {
		int sizeBefore = data.getRowCount();
		table.add(10, -1);
		assertTrue(data.getRowCount() > sizeBefore);
	}

	@Test
	public void testDataUpdated() {
		int sizeBefore = data.getRowCount();

		// Change one rows to be included in subset
		table.set(0, 1, -2);
		assertEquals(sizeBefore, data.getRowCount());

		// Change one rows to be excluded from subset
		table.set(0, 1, -3);
		assertTrue(data.getRowCount() < sizeBefore);

		// Change two rows to be included in subset
		table.set(0, 1, -2);
		table.set(0, 2, -2);
		assertTrue(data.getRowCount() > sizeBefore);
	}

	@Test
	public void testDataRemoved() {
		int sizeBefore = data.getRowCount();
		table.remove(1);
		assertTrue(data.getRowCount() < sizeBefore);
	}
}

/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
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
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.data.comparators.Ascending;
import de.erichseifert.gral.data.comparators.Descending;

public class DataTableTest {
	private DataTable table;

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
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCreate() {
		// Constructor with types
		DataTable table1 = new DataTable(Integer.class, Double.class, Long.class, Float.class);
		table1.add(-10, 1.0, 1L, 10f);
		table1.add(-20, 2.0, 2L, 20f);
		table1.add(-30, 3.0, 3L, 30f);
		assertEquals(4, table1.getColumnCount());
		assertEquals(Integer.class, table1.getColumnTypes()[0]);
		assertEquals(Double.class, table1.getColumnTypes()[1]);
		assertEquals(Long.class, table1.getColumnTypes()[2]);
		assertEquals(Float.class, table1.getColumnTypes()[3]);
		assertEquals(3, table1.getRowCount());

		// Copy constructor
		DataTable table2 = new DataTable(table1);
		assertEquals(table1.getColumnCount(), table2.getColumnCount());
		assertEquals(table1.getColumnTypes()[0], table2.getColumnTypes()[0]);
		assertEquals(table1.getColumnTypes()[1], table2.getColumnTypes()[1]);
		assertEquals(table1.getColumnTypes()[2], table2.getColumnTypes()[2]);
		assertEquals(table1.getColumnTypes()[3], table2.getColumnTypes()[3]);
		assertEquals(table1.getRowCount(), table2.getRowCount());
		assertEquals(table1.getRow(0), table2.getRow(0));
		assertEquals(table1.getRow(1), table2.getRow(1));
		assertEquals(table1.getRow(2), table2.getRow(2));
	}

	@Test
	public void testAdd() {
		// Wrong number of columns
		try {
			table.add(1);
		fail("Expected IllegalArgumentException exception.");
		} catch (IllegalArgumentException e) {
		}

		// Wrong type of columns
		try {
			table.add(1.0, 1.0);
			fail("Expected IllegalArgumentException exception.");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testSet() {
		table.set(1, 2, -1);
		assertEquals(-1, table.get(1, 2));

		// Illegal column index
		try {
			table.set(2, 0, 1);
			fail("Expected IndexOutOfBoundsException exception.");
		} catch (IndexOutOfBoundsException e) {
		}
	}

	@Test
	public void testRemove() {
		table.remove(0);

		// Invalid (negative) index
		try {
			table.remove(-1);
			fail("Expected IndexOutOfBoundsException exception.");
		} catch (IndexOutOfBoundsException e) {
		}
		// Invalid (positive) index
		try {
			table.remove(table.getRowCount());
			fail("Expected IndexOutOfBoundsException exception.");
		} catch (IndexOutOfBoundsException e) {
		}
	}

	@Test
	public void testGetIntInt() {
		assertEquals(6, table.get(1, 3));
		assertEquals(5, table.get(0, 4));
		assertEquals(1, table.get(0, 0));
		assertEquals(11, table.get(1, 7));
	}

	@Test
	public void testIterator() {
		int i = 0;
		int colCount = table.getColumnCount();
		for (Comparable<?> cell : table) {
			int col = i % colCount;
			int row = i / colCount;
			Comparable<?> expected = table.get(col, row);
			assertEquals(expected, cell);
			i++;
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSort() {
		DataTable table = new DataTable(Integer.class, Integer.class, Integer.class);
		int[] original = {
				9,	1,	3,
				4,	4,	2,
				4,	2,	1,
				8,	1,	9,
				8,	1,	7,
				6,	2,	4,
				4,	6,	5,
				3,	3,	5
		};
		int i = 0;
		while (i < original.length) {
			table.add(original[i++], original[i++], original[i++]);
		}

		table.sort(new Ascending(1), new Descending(0), new Ascending(2));

		int[] expected = {
				9,	1,	3,
				8,	1,	7,
				8,	1,	9,
				6,	2,	4,
				4,	2,	1,
				3,	3,	5,
				4,	4,	2,
				4,	6,	5
		};
		i = 0;
		while (i < expected.length) {
			assertEquals(expected[i], table.get(i%3, i/3));
			i++;
		}
	}

	@Test
	public void testClear() {
		table.clear();
		assertEquals(0, table.getRowCount());
	}

}

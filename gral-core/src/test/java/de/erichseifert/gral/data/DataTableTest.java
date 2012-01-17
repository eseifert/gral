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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.data.comparators.Ascending;
import de.erichseifert.gral.data.comparators.Descending;
import de.erichseifert.gral.data.statistics.Statistics;

public class DataTableTest {
	private static final double DELTA = TestUtils.DELTA;

	private DataTable table;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() {
		table = new DataTable(Integer.class, Integer.class);
		table.add(1,  1); // 0
		table.add(2,  3); // 1
		table.add(3,  2); // 2
		table.add(4,  6); // 3
		table.add(5,  4); // 4
		table.add(6,  8); // 5
		table.add(7,  9); // 6
		table.add(8, 11); // 7
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCreate() {
		// Constructor with types
		DataTable table1 = new DataTable(Integer.class, Double.class, Long.class, Float.class);
		assertEquals(4, table1.getColumnCount());
		assertEquals(0, table1.getRowCount());
		Class<? extends Comparable<?>>[] types1 = table1.getColumnTypes();
		assertEquals(Integer.class, types1[0]);
		assertEquals(Double.class, types1[1]);
		assertEquals(Long.class, types1[2]);
		assertEquals(Float.class, types1[3]);

		// Constructor with single type
		DataTable table2 = new DataTable(3, Double.class);
		assertEquals(3, table2.getColumnCount());
		assertEquals(0, table1.getRowCount());
		Class<? extends Comparable<?>>[] types2 = table2.getColumnTypes();
		for (int i = 0; i < types2.length; i++) {
			assertEquals(Double.class, types2[i]);
		}

		// Copy constructor
		DataTable table3 = new DataTable(table1);
		assertEquals(table1.getColumnCount(), table3.getColumnCount());
		assertEquals(table1.getRowCount(), table3.getRowCount());
		Class<? extends Comparable<?>>[] types3 = table1.getColumnTypes();
		for (int i = 0; i < types3.length; i++) {
			assertEquals(types1[i], types3[i]);
		}
	}

	@Test
	public void testAdd() {
		int sizeBefore = table.getRowCount();
		table.add(0, -1);
		table.add(1, -2);
		table.add(2, -3);
		assertEquals(sizeBefore + 3, table.getRowCount());

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
		int sizeBefore = table.getRowCount();

		table.set(1, 2, -1);
		assertEquals(sizeBefore, table.getRowCount());
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
		int sizeBefore = table.getRowCount();
		table.remove(0);
		assertEquals(sizeBefore - 1, table.getRowCount());

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

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		DataSource original = table;
		DataSource deserialized = TestUtils.serializeAndDeserialize(original);

    	// Test metadata
    	assertArrayEquals(original.getColumnTypes(), deserialized.getColumnTypes());
    	assertEquals(original.getColumnCount(), deserialized.getColumnCount());
    	assertEquals(original.getRowCount(), deserialized.getRowCount());

		// Test values
    	for (int row = 0; row < original.getRowCount(); row++) {
        	for (int col = 0; col < original.getColumnCount(); col++) {
            	assertEquals(
        			String.format("Wrong data at col=%d, row=%d.", col, row),
        			original.get(col, row), deserialized.get(col, row));
        	}
    	}

    	// Test statistics
    	String[] stats = { Statistics.N, Statistics.SUM, Statistics.MEAN, Statistics.VARIANCE };
    	for (String stat : stats) {
    		assertEquals(
				original.getStatistics().get(stat),
				deserialized.getStatistics().get(stat),
				DELTA);
		}
    }
}

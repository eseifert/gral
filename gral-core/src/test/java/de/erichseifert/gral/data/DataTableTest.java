/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <mseifert[at]error-reports.org>
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.data.comparators.Ascending;
import de.erichseifert.gral.data.comparators.Descending;
import de.erichseifert.gral.data.statistics.Statistics;
import org.hamcrest.CoreMatchers;

public class DataTableTest {
	private static final double DELTA = TestUtils.DELTA;

	private static class MockDataListener implements DataListener {
		private DataChangeEvent[] added;
		private DataChangeEvent[] updated;
		private DataChangeEvent[] removed;

		public void dataAdded(DataSource source, DataChangeEvent... events) {
			added = events;
		}

		public void dataUpdated(DataSource source, DataChangeEvent... events) {
			updated = events;
		}

		public void dataRemoved(DataSource source, DataChangeEvent... events) {
			removed = events;
		}
	}

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
		for (Class<? extends Comparable<?>> aTypes2 : types2) {
			assertEquals(Double.class, aTypes2);
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
	public void testDataTableCreatedFromColumnsContainsValuesInColumnOrder() {
		int someRowIndex = 1;
		// TODO: Properly mock Column objects
		DataSource firstColumnData = new DummyData(1, someRowIndex + 1, 1.0);
		Column<?> firstColumn = firstColumnData.getColumn(0);
		DataSource secondColumnData = new DummyData(1, someRowIndex + 1, 2.0);
		Column<?> secondColumn = secondColumnData.getColumn(0);

		DataSource table = new DataTable(firstColumn, secondColumn);

		assertThat(table.getRecord(1), CoreMatchers.<Comparable<?>>hasItems(firstColumn.get(someRowIndex), secondColumn.get(someRowIndex)));
	}

	@Test
	public void testAdd() {
		int sizeBefore = table.getRowCount();
		table.add(0, -1);
		table.add(1, -2);
		int rowIndex = table.add(2, -3);
		assertEquals(sizeBefore + 3, table.getRowCount());
		assertEquals(table.getRowCount() - 1, rowIndex);

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
	public void testAddCollectionReturnsInsertedPosition() {
		DataTable table = new DataTable();
		table.add();
		table.add();

		int insertedPosition = table.add(Collections.<Comparable<?>>emptyList());

		assertThat(insertedPosition, is(2));
	}

	@Test
	public void testContainsARowAfterAddingARecord() {
		DataTable table = new DataTable();
		Record record = new Record();

		table.add(record);

		assertThat(table.getRowCount(), is(1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddRecordThrowsExceptionIfColumnCountDoesNotMatch() {
		DataTable table = new DataTable(String.class, Double.class);
		Record record = new Record("1");

		table.add(record);
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
	public void testEventsAdd() {
		table.add(12, 34);

		MockDataListener listener = new MockDataListener();
		table.addDataListener(listener);
		assertNull(listener.added);
		assertNull(listener.updated);
		assertNull(listener.removed);

		int row = table.add(56, 78);
		assertNotNull(listener.added);
		assertNull(listener.updated);
		assertNull(listener.removed);

		assertEquals(2, listener.added.length);
		assertEquals(0, listener.added[0].getCol());
		assertEquals(row, listener.added[0].getRow());
		assertNull(listener.added[0].getOld());
		assertEquals(56, listener.added[0].getNew());
		assertEquals(1, listener.added[1].getCol());
		assertEquals(row, listener.added[1].getRow());
		assertNull(listener.added[1].getOld());
		assertEquals(78, listener.added[1].getNew());
	}

	@Test
	public void testEventsUpdate() {
		int row = table.add(12, 34);

		MockDataListener listener = new MockDataListener();
		table.addDataListener(listener);
		assertNull(listener.added);
		assertNull(listener.updated);
		assertNull(listener.removed);

		Comparable<?> valueOld = table.set(1, row, 42);
		assertNull(listener.added);
		assertNotNull(listener.updated);
		assertNull(listener.removed);

		assertEquals(34, valueOld);

		assertEquals(1, listener.updated.length);
		assertEquals(1, listener.updated[0].getCol());
		assertEquals(row, listener.updated[0].getRow());
		assertEquals(34, listener.updated[0].getOld());
		assertEquals(42, listener.updated[0].getNew());
	}

	@Test
	public void testEventsRemove() {
		int row = table.add(12, 34);

		MockDataListener listener = new MockDataListener();
		table.addDataListener(listener);
		assertNull(listener.added);
		assertNull(listener.updated);
		assertNull(listener.removed);

		table.remove(row);
		assertNull(listener.added);
		assertNull(listener.updated);
		assertNotNull(listener.removed);

		assertEquals(2, listener.removed.length);
		assertEquals(0, listener.removed[0].getCol());
		assertEquals(row, listener.removed[0].getRow());
		assertEquals(12, listener.removed[0].getOld());
		assertNull(listener.removed[0].getNew());
		assertEquals(1, listener.removed[1].getCol());
		assertEquals(row, listener.removed[1].getRow());
		assertEquals(34, listener.removed[1].getOld());
		assertNull(listener.removed[1].getNew());
	}

	@Test
	public void testEventsClear() {
		MockDataListener listener = new MockDataListener();
		table.addDataListener(listener);
		assertNull(listener.added);
		assertNull(listener.updated);
		assertNull(listener.removed);

		int cols = table.getColumnCount();
		int rows = table.getRowCount();
		table.clear();
		assertNull(listener.added);
		assertNull(listener.updated);
		assertNotNull(listener.removed);

		assertEquals(cols * rows, listener.removed.length);
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

	@Test
	public void testSetName() {
		table.setName("name");
		assertEquals("name", table.getName());
	}
}

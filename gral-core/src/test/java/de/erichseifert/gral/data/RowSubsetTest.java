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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.data.statistics.Statistics;

public class RowSubsetTest {
	private static final double DELTA = TestUtils.DELTA;

	private static final class MockRowSubset extends RowSubset {
		/** Version id for serialization. */
		private static final long serialVersionUID = -601722212974379219L;

		public MockRowSubset(DataSource original) {
			super(original);
		}

		@Override
		public boolean accept(Row row) {
			Comparable<?> cell = row.get(0);
			return (cell instanceof Number) &&
				(((Number) cell).doubleValue() % 2.0) == 0.0;
		}
	}

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

		data = new MockRowSubset(table);
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
		assertEquals(table.getRecord(1), data.getRecord(0));
		assertEquals(table.getRecord(3), data.getRecord(1));
		assertEquals(table.getRecord(5), data.getRecord(2));
		assertEquals(table.getRecord(7), data.getRecord(3));
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

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		DataSource original = data;
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

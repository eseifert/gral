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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.data.statistics.Statistics;

public class RowTest {
	private static final double DELTA = TestUtils.DELTA;
	private static DataTable table;

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setUpBeforeClass() {
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
	public void testCreation() {
		Row row1 = new Row(table, 0);
		assertEquals(table, row1.getSource());
		assertEquals(0, row1.getIndex());
		assertEquals(table.getColumnCount(), row1.size());

		Row row2 = new Row(table, 1);
		assertEquals(table, row2.getSource());
		assertEquals(1, row2.getIndex());
		assertEquals(table.getColumnCount(), row2.size());
	}

	@Test
	public void testGet() {
		Row row1 = new Row(table, 1);
		assertEquals(table.get(0, 1), row1.get(0));
		assertEquals(table.get(1, 1), row1.get(1));

		Row row2 = new Row(null, 1);
		assertEquals(null, row2.get(0));
		assertEquals(null, row2.get(1));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testEquality() {
		Row row1 = new Row(table, 1);
		Row row2 = new Row(table, 1);
		Row row3 = new Row(table, 2);

		assertTrue(row1.equals(row2));

		assertFalse(row1.equals(row3));
		assertFalse(row1.equals(null));
		assertFalse(row1.equals(new Object()));

		// Different data source shouldn't matter
		DataTable table1 = new DataTable(Integer.class, Integer.class);
		table1.add(2, 3);
		assertTrue(row1.equals(new Row(table1, 0)));

		// Different column count should yield error
		DataTable table2 = new DataTable(Integer.class, Integer.class, Integer.class);
		table2.add(2, 3, 0);
		assertFalse(row1.equals(new Row(table2, 0)));

		// Different data types should yield error
		DataTable table3 = new DataTable(Integer.class, Double.class);
		table3.add(2, 3.0);
		assertFalse(row1.equals(new Row(table3, 0)));
	}

	@Test
	public void testToString() {
		Row row1 = new Row(table, 1);
		Row row2 = new Row(table, 1);
		assertNotNull(row1.toString());
		assertFalse(row1.toString().isEmpty());
		assertEquals(row1.toString(), row2.toString());
	}

	@Test
	public void testStatistics() {
		Row row1 = new Row(table, 1);
		assertEquals( 2.0, row1.getStatistics(Statistics.N),   DELTA);
		assertEquals( 2.0, row1.getStatistics(Statistics.MIN), DELTA);
		assertEquals( 3.0, row1.getStatistics(Statistics.MAX), DELTA);
		assertEquals( 5.0, row1.getStatistics(Statistics.SUM), DELTA);
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		DataAccessor original = new Row(table, 1);
		DataAccessor deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(table.getColumnCount(), deserialized.getSource().getColumnCount());
		assertEquals(table.getRowCount(), deserialized.getSource().getRowCount());
		assertEquals(1, deserialized.getIndex());
	}
}

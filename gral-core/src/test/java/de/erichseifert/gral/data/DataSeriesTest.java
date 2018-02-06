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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.BeforeClass;
import org.junit.Test;

public class DataSeriesTest {
	private static DataTable table;

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setUpBeforeClass() {
		table = new DataTable(Integer.class, Integer.class, Integer.class);
		table.add(1, 3, 5); // 0
		table.add(2, 8, 2); // 1
		table.add(3, 5, 6); // 2
		table.add(4, 6, 2); // 3
		table.add(5, 4, 1); // 4
		table.add(6, 9, 5); // 5
		table.add(7, 8, 7); // 6
		table.add(8, 1, 9); // 7
	}

	@Test
	public void testCreate() {
		// without name
		DataSeries unnamed = new DataSeries(table, 2, 1);
		assertEquals(2, unnamed.getColumnCount());
		assertEquals(table.getRowCount(), unnamed.getRowCount());
		assertEquals(unnamed.getColumnCount(), unnamed.getColumnTypes().length);
		assertEquals(null, unnamed.getName());

		// with name
		DataSeries named = new DataSeries("foo", table, 2, 1);
		assertEquals(2, named.getColumnCount());
		assertEquals(table.getRowCount(), named.getRowCount());
		assertEquals(named.getColumnCount(), named.getColumnTypes().length);
		assertEquals("foo", named.getName());

		// without columns
		DataSeries allCols = new DataSeries("bar", table);
		assertEquals(table.getColumnCount(), allCols.getColumnCount());
		assertEquals(table.getRowCount(), allCols.getRowCount());
		assertEquals(allCols.getColumnCount(), allCols.getColumnTypes().length);
		assertEquals("bar", allCols.getName());
	}

	@Test
	public void testGetInt() {
		DataSeries series = new DataSeries(table, 2, 1);

		for (int row = 0; row < series.getRowCount(); row++) {
			Record rowTable = table.getRecord(row);
			Record rowSeries = series.getRecord(row);
			assertEquals(rowTable.get(2), rowSeries.get(0));
			assertEquals(rowTable.get(1), rowSeries.get(1));
			assertEquals(2, rowSeries.size());
		}

		// Invalid (negative) index
		assertNotNull(series.getRecord(-1));
		// Invalid (positive) index
		assertNotNull(series.getRecord(series.getRowCount()));
	}

	@Test
	public void testGetIntInt() {
		DataSeries series = new DataSeries(table, 2, 1);

		for (int row = 0; row < series.getRowCount(); row++) {
			assertEquals(table.get(2, row), series.get(0, row));
			assertEquals(table.get(1, row), series.get(1, row));
		}

		// Invalid (negative) index
		assertNull(series.get(-1, -1));
		// Invalid (positive) index
		assertNull(series.get(series.getColumnCount(), series.getRowCount()));
	}

	@Test
	public void testGetColumnCount() {
		DataSeries series = new DataSeries(table, 2, 1);
		assertEquals(2, series.getColumnCount());
	}

	@Test
	public void testToString() {
		DataSeries series = new DataSeries("name", table, 2, 1);
		assertEquals("name", series.toString());
		assertEquals(series.getName(), series.toString());
	}

}

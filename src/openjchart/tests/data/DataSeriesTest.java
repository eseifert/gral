/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.tests.data;

import static org.junit.Assert.assertEquals;
import openjchart.data.DataSeries;
import openjchart.data.DataTable;

import org.junit.BeforeClass;
import org.junit.Test;

public class DataSeriesTest {
	private static DataTable table;

	@BeforeClass
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
	public void testCreation() {
		DataSeries series1 = new DataSeries(table, 2, 1);
		assertEquals(2, series1.getColumnCount());
		assertEquals(table.getRowCount(), series1.getRowCount());
		assertEquals(null, series1.getName());

		DataSeries series2 = new DataSeries("name", table, 2, 1);
		assertEquals(2, series2.getColumnCount());
		assertEquals(table.getRowCount(), series2.getRowCount());
		assertEquals("name", series2.getName());
	}

	@Test
	public void testGetInt() {
		DataSeries series = new DataSeries(table, 2, 1);

		for (int row = 0; row < series.getRowCount(); row++) {
			Number[] rowTable = table.get(row);
			Number[] rowSeries = series.get(row);
			assertEquals(rowTable[2], rowSeries[0]);
			assertEquals(rowTable[1], rowSeries[1]);
			assertEquals(2, rowSeries.length);
		}
	}

	@Test
	public void testGetIntInt() {
		DataSeries series = new DataSeries(table, 2, 1);

		for (int row = 0; row < series.getRowCount(); row++) {
			assertEquals(table.get(2, row), series.get(0, row));
			assertEquals(table.get(1, row), series.get(1, row));
		}
	}

	@Test
	public void testGetColumnCount() {
		DataSeries series = new DataSeries(table, 2, 1);
		assertEquals(2, series.getColumnCount());
	}

	@Test
	public void testName() {
		DataSeries series = new DataSeries(table, 2, 1);
		assertEquals(null, series.getName());
		series.setName("name");
		assertEquals("name", series.getName());
	}

	@Test
	public void testToString() {
		DataSeries series = new DataSeries("name", table, 2, 1);
		assertEquals("name", series.toString());
		assertEquals(series.getName(), series.toString());
	}

}

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

package openjchart.tests.data.statistics;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import openjchart.data.DataTable;
import openjchart.data.statistics.Histogram;

import org.junit.BeforeClass;
import org.junit.Test;

public class HistogramTest {
	private static DataTable table;

	@BeforeClass
	public static void setUpBeforeClass() {
		table = new DataTable(Integer.class, Integer.class);
		table.add(1, 1); // 0
		table.add(1, 3); // 1
		table.add(2, 2); // 2
		table.add(2, 2); // 3
		table.add(5, 4); // 4
		table.add(1, 2); // 5
		table.add(2, 9); // 6
		table.add(4, 1); // 7
	}

	@Test
	public void testCreation() {
		Histogram histogram = new Histogram(table, 4);

		assertEquals(table.getColumnCount(), histogram.getColumnCount());
		assertEquals(4, histogram.getRowCount());
	}

	@Test
	public void testEqualBreaks() {
		Histogram histogram = new Histogram(table, 4);

		long[] expected = {
			3, 5,  // 1.0-2.0, 1.0-3.0
			3, 2,  // 2.0-3.0, 3.0-5.0
			0, 0,  // 3.0-4.0, 5.0-7.0
			1, 0   // 4.0-5.0, 7.0-9.0
		};
		int i = 0;
		while (i < expected.length) {
			int col = i%2, row = i/2;
			assertEquals("column " + (col+1)+", row " + (row+1)+":", expected[i], histogram.get(col, row));
			i++;
		}
	}

	@Test
	public void testCustomBreaks() {
		Histogram histogram = new Histogram(table, new Number[][] {{1.0, 2.0, 3.0, 4.0, 5.0}, {1.0, 3.0, 5.0, 7.0, 9.0}});

		long[] expected = {
			3, 5,  // 1.0-2.0, 1.0-3.0
			3, 2,  // 2.0-3.0, 3.0-5.0
			0, 0,  // 3.0-4.0, 5.0-7.0
			1, 0   // 4.0-5.0, 7.0-9.0
		};
		int i = 0;
		while (i < expected.length) {
			int col = i%2, row = i/2;
			assertEquals("column " + (col+1)+", row " + (row+1)+":", expected[i], histogram.get(col, row));
			i++;
		}
	}

	@Test
	public void testGet() {
		Histogram histogram = new Histogram(table, 4);

		assertArrayEquals(new Number[] {3l, 5l}, histogram.get(0));
		assertArrayEquals(new Number[] {3l, 2l}, histogram.get(1));
		assertArrayEquals(new Number[] {0l, 0l}, histogram.get(2));
		assertArrayEquals(new Number[] {1l, 0l}, histogram.get(3));
	}

	@Test
	public void testCellLimits() {
		Histogram histogram = new Histogram(table, 4);
		Number[][] expected = new Number[][] {{1.0, 2.0, 3.0, 4.0, 5.0}, {1.0, 3.0, 5.0, 7.0, 9.0}};

		for (int colIndex = 0; colIndex < histogram.getColumnCount(); colIndex++) {
			Number[] col = expected[colIndex];
			for (int rowIndex = 0; rowIndex < col.length-1; rowIndex++) {
				Number[] cellLimits = histogram.getCellLimits(colIndex, rowIndex);
				assertEquals(col[rowIndex], cellLimits[0]);
				assertEquals(col[rowIndex + 1], cellLimits[1]);
			}
		}
	}

}
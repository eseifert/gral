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
package de.erichseifert.gral.data.statistics;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.graphics.Orientation;

public class AbstractHistogram2DTest {
	private DataTable table;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() {
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
	public void testCreate() {
		AbstractHistogram2D histogram = new Histogram2D(table, Orientation.VERTICAL, 4);

		assertEquals(table.getColumnCount(), histogram.getColumnCount());
		assertEquals(4, histogram.getRowCount());
	}

	@Test
	public void testEqualBreaks() {
		AbstractHistogram2D histogram = new Histogram2D(table, Orientation.VERTICAL, 4);

		long[] expected = {
			3L, 5L,  // 1.0-2.0, 1.0-3.0
			3L, 2L,  // 2.0-3.0, 3.0-5.0
			0L, 0L,  // 3.0-4.0, 5.0-7.0
			1L, 0L   // 4.0-5.0, 7.0-9.0
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
		AbstractHistogram2D histogram = new Histogram2D(table, Orientation.VERTICAL,
				new Number[][] {{1.0, 2.0, 3.0, 4.0, 5.0}, {1.0, 3.0, 5.0, 7.0, 9.0}});

		long[] expected = {
			3L, 5L,  // 1.0-2.0, 1.0-3.0
			3L, 2L,  // 2.0-3.0, 3.0-5.0
			0L, 0L,  // 3.0-4.0, 5.0-7.0
			1L, 0L   // 4.0-5.0, 7.0-9.0
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
		AbstractHistogram2D histogram = new Histogram2D(table, Orientation.VERTICAL, 4);

		assertEquals(3L, histogram.get(0, 0));
		assertEquals(5L, histogram.get(1, 0));
		assertEquals(3L, histogram.get(0, 1));
		assertEquals(2L, histogram.get(1, 1));
		assertEquals(0L, histogram.get(0, 2));
		assertEquals(0L, histogram.get(1, 2));
		assertEquals(1L, histogram.get(0, 3));
		assertEquals(0L, histogram.get(1, 3));
	}

	@Test
	public void testCellLimits() {
		Histogram2D histogram = new Histogram2D(table, Orientation.VERTICAL, 4);
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

	@Test
	public void testDataAdd() {
		AbstractHistogram2D histogram = new Histogram2D(table, Orientation.VERTICAL, 4);
		assertEquals(3L, histogram.get(0, 0));
		table.add(1, 1);
		assertEquals(4L, histogram.get(0, 0));
	}

	@Test
	public void testDataRemove() {
		AbstractHistogram2D histogram = new Histogram2D(table, Orientation.VERTICAL, 4);
		assertEquals(3L, histogram.get(0, 0));
		table.remove(0);
		assertEquals(2L, histogram.get(0, 0));
	}
}

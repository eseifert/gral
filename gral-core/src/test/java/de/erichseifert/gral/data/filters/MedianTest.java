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
package de.erichseifert.gral.data.filters;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.statistics.Statistics;

public class MedianTest {
	private static final double DELTA = TestUtils.DELTA;

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
		Median filter = new Median(table, 3, 1, Filter2D.Mode.REPEAT, 0);

		assertEquals(table.getColumnCount(), filter.getColumnCount());
		assertEquals(table.getRowCount(), filter.getRowCount());
	}

	@Test
	public void testMode() {
		Median filter = new Median(table, 3, 1, Filter2D.Mode.REPEAT, 0);

		for (Filter2D.Mode mode : Filter2D.Mode.values()) {
			filter.setMode(mode);
			assertEquals(mode, filter.getMode());
		}
	}

	@Test
	public void testWindowSize() {
		Median filter = new Median(table, 3, 1, Filter2D.Mode.REPEAT, 0);
		assertEquals(3, filter.getWindowSize());

		filter.setWindowSize(1);
		assertEquals(1, filter.getWindowSize());
	}

	@Test
	public void testOffset() {
		Median filter = new Median(table, 3, 1, Filter2D.Mode.REPEAT, 0);
		assertEquals(1, filter.getOffset());

		filter.setOffset(0);
		assertEquals(0, filter.getOffset());
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		Filter2D original = new Median(table, 3, 1, Filter2D.Mode.REPEAT, 0);
		Filter2D deserialized = TestUtils.serializeAndDeserialize(original);

    	// Test metadata
    	assertEquals(original.getMode(), deserialized.getMode());
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

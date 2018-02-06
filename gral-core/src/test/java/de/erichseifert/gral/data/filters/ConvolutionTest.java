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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.statistics.Statistics;

public class ConvolutionTest {
	private static final double DELTA = TestUtils.DELTA;

	private static DataTable table;
	private static Kernel kernel;

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setUpBeforeClass() {
		table = new DataTable(Double.class, Double.class, String.class);
		table.add(1.0, 1.0, "a"); // 0
		table.add(2.0, 1.0, "b"); // 1
		table.add(3.0, 1.0, "c"); // 2
		table.add(4.0, 1.0, "d"); // 3
		table.add(5.0, 1.0, "e"); // 4
		table.add(6.0, 1.0, "f"); // 5
		table.add(7.0, 1.0, "g"); // 6
		table.add(8.0, 1.0, "h"); // 7

		kernel = new Kernel(1.0, 1.0, 1.0);
	}

	@Test
	public void testCreate() {
		Convolution filter = new Convolution(table, kernel, Filter2D.Mode.ZERO, 0, 1);
		assertEquals(table.getColumnCount(), filter.getColumnCount());
		assertEquals(table.getRowCount(), filter.getRowCount());
		assertEquals(table.getColumnCount(), filter.getColumnTypes().length);

		try {
			new Convolution(table, kernel, Filter2D.Mode.ZERO, 0, 2);
			fail("Filtering a non-numeric column must raise an IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testKernel() {
		Convolution filter = new Convolution(table, kernel, Filter2D.Mode.ZERO, 0, 1);
		assertEquals(kernel, filter.getKernel());
	}

	@Test
	public void testMode() {
		Convolution filter = new Convolution(table, kernel, Filter2D.Mode.OMIT, 0, 1);

		for (Filter2D.Mode mode : Filter2D.Mode.values()) {
			filter.setMode(mode);
			assertEquals(mode, filter.getMode());
		}
	}

	@Test
	public void testColumns() {
		Convolution filter = new Convolution(table, kernel, Filter2D.Mode.ZERO, 0);

		assertEquals(3.0, ((Number) filter.get(0, 0)).doubleValue(), DELTA);
		assertEquals(6.0, ((Number) filter.get(0, 1)).doubleValue(), DELTA);
		assertEquals(9.0, ((Number) filter.get(0, 2)).doubleValue(), DELTA);

		assertEquals(1.0, ((Number) filter.get(1, 0)).doubleValue(), DELTA);
		assertEquals(1.0, ((Number) filter.get(1, 1)).doubleValue(), DELTA);
		assertEquals(1.0, ((Number) filter.get(1, 2)).doubleValue(), DELTA);
	}

	@Test
	public void testModeOmit() {
		Convolution filter = new Convolution(table, kernel, Filter2D.Mode.OMIT, 0, 1);

		assertTrue(Double.isNaN(((Number) filter.get(0, 0)).doubleValue()));
		assertEquals(6.0, ((Number) filter.get(0, 1)).doubleValue(), DELTA);
		assertTrue(Double.isNaN(((Number) filter.get(0, 7)).doubleValue()));

		assertTrue(Double.isNaN(((Number) filter.get(1, 0)).doubleValue()));
		assertEquals(3.0, ((Number) filter.get(1, 1)).doubleValue(), DELTA);
		assertTrue(Double.isNaN(((Number) filter.get(1, 7)).doubleValue()));
	}

	@Test
	public void testModeZero() {
		Convolution filter = new Convolution(table, kernel, Filter2D.Mode.ZERO, 0, 1);

		assertEquals( 3.0, ((Number) filter.get(0, 0)).doubleValue(), DELTA);
		assertEquals( 6.0, ((Number) filter.get(0, 1)).doubleValue(), DELTA);
		assertEquals(15.0, ((Number) filter.get(0, 7)).doubleValue(), DELTA);

		assertEquals( 2.0, ((Number) filter.get(1, 0)).doubleValue(), DELTA);
		assertEquals( 3.0, ((Number) filter.get(1, 1)).doubleValue(), DELTA);
		assertEquals( 2.0, ((Number) filter.get(1, 7)).doubleValue(), DELTA);
	}

	@Test
	public void testModeRepeat() {
		Convolution filter = new Convolution(table, kernel, Filter2D.Mode.REPEAT, 0, 1);

		assertEquals( 4.0, ((Number) filter.get(0, 0)).doubleValue(), DELTA);
		assertEquals( 6.0, ((Number) filter.get(0, 1)).doubleValue(), DELTA);
		assertEquals(23.0, ((Number) filter.get(0, 7)).doubleValue(), DELTA);

		assertEquals( 3.0, ((Number) filter.get(1, 0)).doubleValue(), DELTA);
		assertEquals( 3.0, ((Number) filter.get(1, 1)).doubleValue(), DELTA);
		assertEquals( 3.0, ((Number) filter.get(1, 7)).doubleValue(), DELTA);
	}

	@Test
	public void testModeMirror() {
		Convolution filter = new Convolution(table, kernel, Filter2D.Mode.MIRROR, 0, 1);

		assertEquals( 5.0, ((Number) filter.get(0, 0)).doubleValue(), DELTA);
		assertEquals( 6.0, ((Number) filter.get(0, 1)).doubleValue(), DELTA);
		assertEquals(22.0, ((Number) filter.get(0, 7)).doubleValue(), DELTA);

		assertEquals( 3.0, ((Number) filter.get(1, 0)).doubleValue(), DELTA);
		assertEquals( 3.0, ((Number) filter.get(1, 1)).doubleValue(), DELTA);
		assertEquals( 3.0, ((Number) filter.get(1, 7)).doubleValue(), DELTA);
	}

	@Test
	public void testModeCircular() {
		Convolution filter = new Convolution(table, kernel, Filter2D.Mode.CIRCULAR, 0, 1);

		assertEquals(11.0, ((Number) filter.get(0, 0)).doubleValue(), DELTA);
		assertEquals( 6.0, ((Number) filter.get(0, 1)).doubleValue(), DELTA);
		assertEquals(16.0, ((Number) filter.get(0, 7)).doubleValue(), DELTA);

		assertEquals( 3.0, ((Number) filter.get(1, 0)).doubleValue(), DELTA);
		assertEquals( 3.0, ((Number) filter.get(1, 1)).doubleValue(), DELTA);
		assertEquals( 3.0, ((Number) filter.get(1, 7)).doubleValue(), DELTA);
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		Convolution original = new Convolution(table, kernel, Filter2D.Mode.ZERO, 0, 1);
		Convolution deserialized = TestUtils.serializeAndDeserialize(original);

    	// Test metadata
    	assertEquals(original.getKernel().size(), deserialized.getKernel().size());
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

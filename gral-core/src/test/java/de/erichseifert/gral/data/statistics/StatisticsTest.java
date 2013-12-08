/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
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
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.util.Orientation;

public class StatisticsTest {
	private static final double DELTA = 1e-10;
	private DataTable table;
	private Statistics stats;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() {
		table = new DataTable(Integer.class, Integer.class, Integer.class);
		table.add(0, 1, 2); // 0
		table.add(1, 3, 3); // 1
		table.add(2, 2, 4); // 2
		table.add(2, 2, 5); // 3
		table.add(5, 4, 6); // 4
		table.add(1, 2, 7); // 5
		table.add(2, 9, 8); // 6
		table.add(4, 1, 9); // 7

		stats = table.getStatistics();
	}

	@Test
	public void testSum() {
		assertEquals(85.0, stats.get(Statistics.SUM), DELTA);
		// Horizontal
		assertEquals( 3.0, stats.get(Statistics.SUM, Orientation.HORIZONTAL, 0), DELTA);
		assertEquals( 7.0, stats.get(Statistics.SUM, Orientation.HORIZONTAL, 1), DELTA);
		assertEquals( 8.0, stats.get(Statistics.SUM, Orientation.HORIZONTAL, 2), DELTA);
		// Vertical
		assertEquals(17.0, stats.get(Statistics.SUM, Orientation.VERTICAL, 0), DELTA);
		assertEquals(24.0, stats.get(Statistics.SUM, Orientation.VERTICAL, 1), DELTA);
		assertEquals(44.0, stats.get(Statistics.SUM, Orientation.VERTICAL, 2), DELTA);
	}

	@Test
	public void testMean() {
		assertEquals(85.0/24.0, stats.get(Statistics.MEAN), DELTA);
		// Horizontal
		assertEquals( 3.0/ 3.0, stats.get(Statistics.MEAN, Orientation.HORIZONTAL, 0), DELTA);
		assertEquals( 7.0/ 3.0, stats.get(Statistics.MEAN, Orientation.HORIZONTAL, 1), DELTA);
		assertEquals( 8.0/ 3.0, stats.get(Statistics.MEAN, Orientation.HORIZONTAL, 2), DELTA);
		// Vertical
		assertEquals(17.0/ 8.0, stats.get(Statistics.MEAN, Orientation.VERTICAL, 0), DELTA);
		assertEquals(24.0/ 8.0, stats.get(Statistics.MEAN, Orientation.VERTICAL, 1), DELTA);
		assertEquals(44.0/ 8.0, stats.get(Statistics.MEAN, Orientation.VERTICAL, 2), DELTA);
	}

	@Test
	public void testMin() {
		assertEquals(0.0, stats.get(Statistics.MIN), DELTA);
		// Horizontal
		assertEquals(0.0, stats.get(Statistics.MIN, Orientation.HORIZONTAL, 0), DELTA);
		assertEquals(1.0, stats.get(Statistics.MIN, Orientation.HORIZONTAL, 1), DELTA);
		assertEquals(2.0, stats.get(Statistics.MIN, Orientation.HORIZONTAL, 2), DELTA);
		// Vertical
		assertEquals(0.0, stats.get(Statistics.MIN, Orientation.VERTICAL, 0), DELTA);
		assertEquals(1.0, stats.get(Statistics.MIN, Orientation.VERTICAL, 1), DELTA);
		assertEquals(2.0, stats.get(Statistics.MIN, Orientation.VERTICAL, 2), DELTA);
	}

	@Test
	public void testMax() {
		assertEquals(9.0, stats.get(Statistics.MAX), DELTA);
		// Horizontal
		assertEquals(2.0, stats.get(Statistics.MAX, Orientation.HORIZONTAL, 0), DELTA);
		assertEquals(3.0, stats.get(Statistics.MAX, Orientation.HORIZONTAL, 1), DELTA);
		assertEquals(4.0, stats.get(Statistics.MAX, Orientation.HORIZONTAL, 2), DELTA);
		// Vertical
		assertEquals(5.0, stats.get(Statistics.MAX, Orientation.VERTICAL, 0), DELTA);
		assertEquals(9.0, stats.get(Statistics.MAX, Orientation.VERTICAL, 1), DELTA);
		assertEquals(9.0, stats.get(Statistics.MAX, Orientation.VERTICAL, 2), DELTA);
	}

	@Test
	public void testN() {
		assertEquals(24.0, stats.get(Statistics.N), DELTA);
		// Horizontal
		assertEquals( 3.0, stats.get(Statistics.N, Orientation.HORIZONTAL, 0), DELTA);
		assertEquals( 3.0, stats.get(Statistics.N, Orientation.HORIZONTAL, 1), DELTA);
		assertEquals( 3.0, stats.get(Statistics.N, Orientation.HORIZONTAL, 2), DELTA);
		// Vertical
		assertEquals( 8.0, stats.get(Statistics.N, Orientation.VERTICAL, 0), DELTA);
		assertEquals( 8.0, stats.get(Statistics.N, Orientation.VERTICAL, 1), DELTA);
		assertEquals( 8.0, stats.get(Statistics.N, Orientation.VERTICAL, 2), DELTA);
	}

	@Test
	public void testSumOfDiffSquares() {
		assertEquals(157.95833333333337, stats.get(Statistics.SUM_OF_DIFF_SQUARES), DELTA);
		// Horizontal
		assertEquals(  2.00000000000000, stats.get(Statistics.SUM_OF_DIFF_SQUARES, Orientation.HORIZONTAL, 0), DELTA);
		assertEquals(  2.66666666666666, stats.get(Statistics.SUM_OF_DIFF_SQUARES, Orientation.HORIZONTAL, 1), DELTA);
		assertEquals(  2.66666666666666, stats.get(Statistics.SUM_OF_DIFF_SQUARES, Orientation.HORIZONTAL, 2), DELTA);
		// Vertical
		assertEquals( 18.87500000000000, stats.get(Statistics.SUM_OF_DIFF_SQUARES, Orientation.VERTICAL, 0), DELTA);
		assertEquals( 48.00000000000000, stats.get(Statistics.SUM_OF_DIFF_SQUARES, Orientation.VERTICAL, 1), DELTA);
		assertEquals( 42.00000000000000, stats.get(Statistics.SUM_OF_DIFF_SQUARES, Orientation.VERTICAL, 2), DELTA);
	}

	@Test
	public void testSumOfDiffCubics() {
		assertEquals(340.50347222222221, stats.get(Statistics.SUM_OF_DIFF_CUBICS), DELTA);
		// Horizontal
		assertEquals(  0.00000000000000, stats.get(Statistics.SUM_OF_DIFF_CUBICS, Orientation.HORIZONTAL, 0), DELTA);
		assertEquals( -1.77777777777777, stats.get(Statistics.SUM_OF_DIFF_CUBICS, Orientation.HORIZONTAL, 1), DELTA);
		assertEquals(  1.77777777777777, stats.get(Statistics.SUM_OF_DIFF_CUBICS, Orientation.HORIZONTAL, 2), DELTA);
		// Vertical
		assertEquals( 17.90625000000000, stats.get(Statistics.SUM_OF_DIFF_CUBICS, Orientation.VERTICAL, 0), DELTA);
		assertEquals(198.00000000000000, stats.get(Statistics.SUM_OF_DIFF_CUBICS, Orientation.VERTICAL, 1), DELTA);
		assertEquals(  0.00000000000000, stats.get(Statistics.SUM_OF_DIFF_CUBICS, Orientation.VERTICAL, 2), DELTA);
	}

	@Test
	public void testSumOfDiffQuads() {
		assertEquals(2723.1039496527756, stats.get(Statistics.SUM_OF_DIFF_QUADS), DELTA);
		// Horizontal
		assertEquals(   2.0000000000000, stats.get(Statistics.SUM_OF_DIFF_QUADS, Orientation.HORIZONTAL, 0), DELTA);
		assertEquals(   3.5555555555555, stats.get(Statistics.SUM_OF_DIFF_QUADS, Orientation.HORIZONTAL, 1), DELTA);
		assertEquals(   3.5555555555555, stats.get(Statistics.SUM_OF_DIFF_QUADS, Orientation.HORIZONTAL, 2), DELTA);
		// Vertical
		assertEquals( 104.2753906250000, stats.get(Statistics.SUM_OF_DIFF_QUADS, Orientation.VERTICAL, 0), DELTA);
		assertEquals(1332.0000000000000, stats.get(Statistics.SUM_OF_DIFF_QUADS, Orientation.VERTICAL, 1), DELTA);
		assertEquals( 388.5000000000000, stats.get(Statistics.SUM_OF_DIFF_QUADS, Orientation.VERTICAL, 2), DELTA);
	}

	/**
	 * Tests skewness of a table, of its rows, and its columns for correctness.
	 * The results of R "moments" package are used for validation.
	 */
	@Test
	public void testSkewness() {
		assertEquals(  -2.1597406540506, stats.get(Statistics.SKEWNESS), DELTA);
		// Horizontal
		assertEquals(  -3.0000000000000, stats.get(Statistics.SKEWNESS, Orientation.HORIZONTAL, 0), DELTA);
		assertEquals(  -3.7071067811865, stats.get(Statistics.SKEWNESS, Orientation.HORIZONTAL, 1), DELTA);
		assertEquals(  -2.2928932188134, stats.get(Statistics.SKEWNESS, Orientation.HORIZONTAL, 2), DELTA);
		// Vertical
		assertEquals(  -2.3823830637406, stats.get(Statistics.SKEWNESS, Orientation.VERTICAL, 0), DELTA);
		assertEquals(  -1.3159758018366, stats.get(Statistics.SKEWNESS, Orientation.VERTICAL, 1), DELTA);
		assertEquals(  -3.0000000000000, stats.get(Statistics.SKEWNESS, Orientation.VERTICAL, 2), DELTA);
	}

	/**
	 * Tests kurtosis of a table, of its rows, and its columns for correctness.
	 * The results of R "moments" package are used for validation.
	 */
	@Test
	public void testKurtosis() {
		assertEquals(  -0.3806690393420, stats.get(Statistics.KURTOSIS), DELTA);
		// Horizontal
		assertEquals(  -1.5000000000000, stats.get(Statistics.KURTOSIS, Orientation.HORIZONTAL, 0), DELTA);
		assertEquals(  -1.5000000000000, stats.get(Statistics.KURTOSIS, Orientation.HORIZONTAL, 1), DELTA);
		assertEquals(  -1.5000000000000, stats.get(Statistics.KURTOSIS, Orientation.HORIZONTAL, 2), DELTA);
		// Vertical
		assertEquals(  -0.6584798912328, stats.get(Statistics.KURTOSIS, Orientation.VERTICAL, 0), DELTA);
		assertEquals(   1.6250000000000, stats.get(Statistics.KURTOSIS, Orientation.VERTICAL, 1), DELTA);
		assertEquals(  -1.2380952380952, stats.get(Statistics.KURTOSIS, Orientation.VERTICAL, 2), DELTA);
	}

	@Test
	public void testQuartiles() {
		// Quartile 1
		assertEquals(2.00, stats.get(Statistics.QUARTILE_1), DELTA);
		// Horizontal
		assertEquals(0.50, stats.get(Statistics.QUARTILE_1, Orientation.HORIZONTAL, 0), DELTA);
		assertEquals(2.00, stats.get(Statistics.QUARTILE_1, Orientation.HORIZONTAL, 1), DELTA);
		assertEquals(2.00, stats.get(Statistics.QUARTILE_1, Orientation.HORIZONTAL, 2), DELTA);
		// Vertical
		assertEquals(1.00, stats.get(Statistics.QUARTILE_1, Orientation.VERTICAL, 0), DELTA);
		assertEquals(1.75, stats.get(Statistics.QUARTILE_1, Orientation.VERTICAL, 1), DELTA);
		assertEquals(3.75, stats.get(Statistics.QUARTILE_1, Orientation.VERTICAL, 2), DELTA);

		// Quartile 2
		assertEquals(2.50, stats.get(Statistics.QUARTILE_2), DELTA);
		// Horizontal
		assertEquals(1.00, stats.get(Statistics.QUARTILE_2, Orientation.HORIZONTAL, 0), DELTA);
		assertEquals(3.00, stats.get(Statistics.QUARTILE_2, Orientation.HORIZONTAL, 1), DELTA);
		assertEquals(2.00, stats.get(Statistics.QUARTILE_2, Orientation.HORIZONTAL, 2), DELTA);
		// Vertical
		assertEquals(2.00, stats.get(Statistics.QUARTILE_2, Orientation.VERTICAL, 0), DELTA);
		assertEquals(2.00, stats.get(Statistics.QUARTILE_2, Orientation.VERTICAL, 1), DELTA);
		assertEquals(5.50, stats.get(Statistics.QUARTILE_2, Orientation.VERTICAL, 2), DELTA);

		// Quartile 3
		assertEquals(5.00, stats.get(Statistics.QUARTILE_3), DELTA);
		// Horizontal
		assertEquals(1.50, stats.get(Statistics.QUARTILE_3, Orientation.HORIZONTAL, 0), DELTA);
		assertEquals(3.00, stats.get(Statistics.QUARTILE_3, Orientation.HORIZONTAL, 1), DELTA);
		assertEquals(3.00, stats.get(Statistics.QUARTILE_3, Orientation.HORIZONTAL, 2), DELTA);
		// Vertical
		assertEquals(2.50, stats.get(Statistics.QUARTILE_3, Orientation.VERTICAL, 0), DELTA);
		assertEquals(3.25, stats.get(Statistics.QUARTILE_3, Orientation.VERTICAL, 1), DELTA);
		assertEquals(7.25, stats.get(Statistics.QUARTILE_3, Orientation.VERTICAL, 2), DELTA);

		// Median == Quartile 2
		assertEquals(stats.get(Statistics.MEDIAN), stats.get(Statistics.QUARTILE_2), DELTA);
	}

	@Test
	public void testNonExistant() {
		assertTrue(Double.isNaN(stats.get("foobar")));
	}

	// FIXME Change test to cause invocation of dataUpdate only
	@Test
	public void testDataUpdate() {
		// Modify table data to cause update
		table.add(24, -11, 42);
		table.set(1, 1, -42);

		// Check statistics
		assertEquals( 27.0, stats.get(Statistics.N),   DELTA);
		assertEquals(-42.0, stats.get(Statistics.MIN), DELTA);
		assertEquals( 42.0, stats.get(Statistics.MAX), DELTA);
		assertEquals( 95.0, stats.get(Statistics.SUM), DELTA);
		// Horizontal
		assertEquals(  3.0, stats.get(Statistics.N,   Orientation.HORIZONTAL, 1), DELTA);
		assertEquals(-42.0, stats.get(Statistics.MIN, Orientation.HORIZONTAL, 1), DELTA);
		assertEquals(  3.0, stats.get(Statistics.MAX, Orientation.HORIZONTAL, 1), DELTA);
		assertEquals(-38.0, stats.get(Statistics.SUM, Orientation.HORIZONTAL, 1), DELTA);
		// Vertical
		assertEquals(  9.0, stats.get(Statistics.N,   Orientation.VERTICAL, 1), DELTA);
		assertEquals(-42.0, stats.get(Statistics.MIN, Orientation.VERTICAL, 1), DELTA);
		assertEquals(  9.0, stats.get(Statistics.MAX, Orientation.VERTICAL, 1), DELTA);
		assertEquals(-32.0, stats.get(Statistics.SUM, Orientation.VERTICAL, 1), DELTA);
	}

	// TODO Add tests for dataAdded and dataRemoved
}

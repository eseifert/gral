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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.data.DataTable;
import org.hamcrest.CoreMatchers;

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

		assertThat(table.getRowStatistics(Statistics.SUM),
				CoreMatchers.<Comparable<?>>hasItems(3.0, 7.0, 8.0));
		assertThat(table.getColumnStatistics(Statistics.SUM),
				CoreMatchers.<Comparable<?>>hasItems(17.0, 24.0, 44.0));
	}

	@Test
	public void testMean() {
		assertEquals(85.0/24.0, stats.get(Statistics.MEAN), DELTA);

		assertThat(table.getRowStatistics(Statistics.MEAN),
				CoreMatchers.<Comparable<?>>hasItems(3.0/3.0, 7.0/3.0, 8.0/3.0));
		assertThat(table.getColumnStatistics(Statistics.MEAN),
				CoreMatchers.<Comparable<?>>hasItems(17.0/8.0, 24.0/8.0, 44.0/8.0));
	}

	@Test
	public void testMin() {
		assertEquals(0.0, stats.get(Statistics.MIN), DELTA);

		assertThat(table.getRowStatistics(Statistics.MIN),
				CoreMatchers.<Comparable<?>>hasItems(0.0, 1.0, 2.0));
		assertThat(table.getColumnStatistics(Statistics.MIN),
				CoreMatchers.<Comparable<?>>hasItems(0.0, 1.0, 2.0));
	}

	@Test
	public void testMax() {
		assertEquals(9.0, stats.get(Statistics.MAX), DELTA);

		assertThat(table.getRowStatistics(Statistics.MAX),
				CoreMatchers.<Comparable<?>>hasItems(2.0, 3.0, 4.0));
		assertThat(table.getColumnStatistics(Statistics.MAX),
				CoreMatchers.<Comparable<?>>hasItems(5.0, 9.0, 9.0));
	}

	@Test
	public void testN() {
		assertEquals(24.0, stats.get(Statistics.N), DELTA);

		assertThat(table.getRowStatistics(Statistics.N),
				CoreMatchers.<Comparable<?>>hasItems(3.0, 3.0, 3.0));
		assertThat(table.getColumnStatistics(Statistics.N),
				CoreMatchers.<Comparable<?>>hasItems(8.0, 8.0, 8.0));
	}

	@Test
	public void testSumOfDiffSquares() {
		assertEquals(157.95833333333337, stats.get(Statistics.SUM_OF_DIFF_SQUARES), DELTA);

		// Horizontal
		assertEquals(2.00000000000000, ((Number) table.getRowStatistics(Statistics.SUM_OF_DIFF_SQUARES).get(0, 0)).doubleValue(), DELTA);
		assertEquals(2.66666666666666, ((Number) table.getRowStatistics(Statistics.SUM_OF_DIFF_SQUARES).get(0, 1)).doubleValue(), DELTA);
		assertEquals(2.66666666666666, ((Number) table.getRowStatistics(Statistics.SUM_OF_DIFF_SQUARES).get(0, 2)).doubleValue(), DELTA);
		// Vertical
		assertEquals(18.87500000000000, ((Number) table.getColumnStatistics(Statistics.SUM_OF_DIFF_SQUARES).get(0, 0)).doubleValue(), DELTA);
		assertEquals(48.00000000000000, ((Number) table.getColumnStatistics(Statistics.SUM_OF_DIFF_SQUARES).get(1, 0)).doubleValue(), DELTA);
		assertEquals(42.00000000000000, ((Number) table.getColumnStatistics(Statistics.SUM_OF_DIFF_SQUARES).get(2, 0)).doubleValue(), DELTA);
	}

	@Test
	public void testSumOfDiffCubics() {
		assertEquals(340.50347222222221, stats.get(Statistics.SUM_OF_DIFF_CUBICS), DELTA);
		// Horizontal
		assertEquals( 0.00000000000000, ((Number) table.getRowStatistics(Statistics.SUM_OF_DIFF_CUBICS).get(0, 0)).doubleValue(), DELTA);
		assertEquals(-1.77777777777777, ((Number) table.getRowStatistics(Statistics.SUM_OF_DIFF_CUBICS).get(0, 1)).doubleValue(), DELTA);
		assertEquals( 1.77777777777777, ((Number) table.getRowStatistics(Statistics.SUM_OF_DIFF_CUBICS).get(0, 2)).doubleValue(), DELTA);
		// Vertical
		assertEquals( 17.90625000000000, ((Number) table.getColumnStatistics(Statistics.SUM_OF_DIFF_CUBICS).get(0, 0)).doubleValue(), DELTA);
		assertEquals(198.00000000000000, ((Number) table.getColumnStatistics(Statistics.SUM_OF_DIFF_CUBICS).get(1, 0)).doubleValue(), DELTA);
		assertEquals(  0.00000000000000, ((Number) table.getColumnStatistics(Statistics.SUM_OF_DIFF_CUBICS).get(2, 0)).doubleValue(), DELTA);
	}

	@Test
	public void testSumOfDiffQuads() {
		assertEquals(2723.1039496527756, stats.get(Statistics.SUM_OF_DIFF_QUADS), DELTA);
		// Horizontal
		assertEquals(   2.0000000000000, ((Number) table.getRowStatistics(Statistics.SUM_OF_DIFF_QUADS).get(0, 0)).doubleValue(), DELTA);
		assertEquals(   3.5555555555555, ((Number) table.getRowStatistics(Statistics.SUM_OF_DIFF_QUADS).get(0, 1)).doubleValue(), DELTA);
		assertEquals(   3.5555555555555, ((Number) table.getRowStatistics(Statistics.SUM_OF_DIFF_QUADS).get(0, 2)).doubleValue(), DELTA);
		// Vertical
		assertEquals( 104.2753906250000, ((Number) table.getColumnStatistics(Statistics.SUM_OF_DIFF_QUADS).get(0, 0)).doubleValue(), DELTA);
		assertEquals(1332.0000000000000, ((Number) table.getColumnStatistics(Statistics.SUM_OF_DIFF_QUADS).get(1, 0)).doubleValue(), DELTA);
		assertEquals( 388.5000000000000, ((Number) table.getColumnStatistics(Statistics.SUM_OF_DIFF_QUADS).get(2, 0)).doubleValue(), DELTA);
	}

	/**
	 * Tests skewness of a table, of its rows, and its columns for correctness.
	 * The results of R "moments" package are used for validation.
	 */
	@Test
	public void testSkewness() {
		assertEquals(  -2.1597406540506, stats.get(Statistics.SKEWNESS), DELTA);
		// Horizontal
		assertEquals(  -3.0000000000000, ((Number) table.getRowStatistics(Statistics.SKEWNESS).get(0, 0)).doubleValue(), DELTA);
		assertEquals(  -3.7071067811865, ((Number) table.getRowStatistics(Statistics.SKEWNESS).get(0, 1)).doubleValue(), DELTA);
		assertEquals(  -2.2928932188134, ((Number) table.getRowStatistics(Statistics.SKEWNESS).get(0, 2)).doubleValue(), DELTA);
		// Vertical
		assertEquals(  -2.3823830637406, ((Number) table.getColumnStatistics(Statistics.SKEWNESS).get(0, 0)).doubleValue(), DELTA);
		assertEquals(  -1.3159758018366, ((Number) table.getColumnStatistics(Statistics.SKEWNESS).get(1, 0)).doubleValue(), DELTA);
		assertEquals(  -3.0000000000000, ((Number) table.getColumnStatistics(Statistics.SKEWNESS).get(2, 0)).doubleValue(), DELTA);
	}

	/**
	 * Tests kurtosis of a table, of its rows, and its columns for correctness.
	 * The results of R "moments" package are used for validation.
	 */
	@Test
	public void testKurtosis() {
		assertEquals(  -0.3806690393420, stats.get(Statistics.KURTOSIS), DELTA);
		// Horizontal
		assertEquals(  -1.5000000000000, ((Number) table.getRowStatistics(Statistics.KURTOSIS).get(0, 0)).doubleValue(), DELTA);
		assertEquals(  -1.5000000000000, ((Number) table.getRowStatistics(Statistics.KURTOSIS).get(0, 1)).doubleValue(), DELTA);
		assertEquals(  -1.5000000000000, ((Number) table.getRowStatistics(Statistics.KURTOSIS).get(0, 2)).doubleValue(), DELTA);
		// Vertical
		assertEquals(  -0.6584798912328, ((Number) table.getColumnStatistics(Statistics.KURTOSIS).get(0, 0)).doubleValue(), DELTA);
		assertEquals(   1.6250000000000, ((Number) table.getColumnStatistics(Statistics.KURTOSIS).get(1, 0)).doubleValue(), DELTA);
		assertEquals(  -1.2380952380952, ((Number) table.getColumnStatistics(Statistics.KURTOSIS).get(2, 0)).doubleValue(), DELTA);
	}

	@Test
	public void testQuartiles() {
		// Quartile 1
		assertEquals(2.00, stats.get(Statistics.QUARTILE_1), DELTA);
		// Horizontal
		assertEquals(0.50, ((Number) table.getRowStatistics(Statistics.QUARTILE_1).get(0, 0)).doubleValue(), DELTA);
		assertEquals(2.00, ((Number) table.getRowStatistics(Statistics.QUARTILE_1).get(0, 1)).doubleValue(), DELTA);
		assertEquals(2.00, ((Number) table.getRowStatistics(Statistics.QUARTILE_1).get(0, 2)).doubleValue(), DELTA);
		// Vertical
		assertEquals(1.00, ((Number) table.getColumnStatistics(Statistics.QUARTILE_1).get(0, 0)).doubleValue(), DELTA);
		assertEquals(1.75, ((Number) table.getColumnStatistics(Statistics.QUARTILE_1).get(1, 0)).doubleValue(), DELTA);
		assertEquals(3.75, ((Number) table.getColumnStatistics(Statistics.QUARTILE_1).get(2, 0)).doubleValue(), DELTA);

		// Quartile 2
		assertEquals(2.50, stats.get(Statistics.QUARTILE_2), DELTA);
		// Horizontal
		assertEquals(1.00, ((Number) table.getRowStatistics(Statistics.QUARTILE_2).get(0, 0)).doubleValue(), DELTA);
		assertEquals(3.00, ((Number) table.getRowStatistics(Statistics.QUARTILE_2).get(0, 1)).doubleValue(), DELTA);
		assertEquals(2.00, ((Number) table.getRowStatistics(Statistics.QUARTILE_2).get(0, 2)).doubleValue(), DELTA);
		// Vertical
		assertEquals(2.00, ((Number) table.getColumnStatistics(Statistics.QUARTILE_2).get(0, 0)).doubleValue(), DELTA);
		assertEquals(2.00, ((Number) table.getColumnStatistics(Statistics.QUARTILE_2).get(1, 0)).doubleValue(), DELTA);
		assertEquals(5.50, ((Number) table.getColumnStatistics(Statistics.QUARTILE_2).get(2, 0)).doubleValue(), DELTA);

		// Quartile 3
		assertEquals(5.00, stats.get(Statistics.QUARTILE_3), DELTA);
		// Horizontal
		assertEquals(1.50, ((Number) table.getRowStatistics(Statistics.QUARTILE_3).get(0, 0)).doubleValue(), DELTA);
		assertEquals(3.00, ((Number) table.getRowStatistics(Statistics.QUARTILE_3).get(0, 1)).doubleValue(), DELTA);
		assertEquals(3.00, ((Number) table.getRowStatistics(Statistics.QUARTILE_3).get(0, 2)).doubleValue(), DELTA);
		// Vertical
		assertEquals(2.50, ((Number) table.getColumnStatistics(Statistics.QUARTILE_3).get(0, 0)).doubleValue(), DELTA);
		assertEquals(3.25, ((Number) table.getColumnStatistics(Statistics.QUARTILE_3).get(1, 0)).doubleValue(), DELTA);
		assertEquals(7.25, ((Number) table.getColumnStatistics(Statistics.QUARTILE_3).get(2, 0)).doubleValue(), DELTA);

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
		assertEquals(  3.0, ((Number) table.getRowStatistics(Statistics.N  ).get(0, 1)).doubleValue(), DELTA);
		assertEquals(-42.0, ((Number) table.getRowStatistics(Statistics.MIN).get(0, 1)).doubleValue(), DELTA);
		assertEquals(  3.0, ((Number) table.getRowStatistics(Statistics.MAX).get(0, 1)).doubleValue(), DELTA);
		assertEquals(-38.0, ((Number) table.getRowStatistics(Statistics.SUM).get(0, 1)).doubleValue(), DELTA);
		// Vertical
		assertEquals(  9.0, ((Number) table.getColumnStatistics(Statistics.N  ).get(1, 0)).doubleValue(), DELTA);
		assertEquals(-42.0, ((Number) table.getColumnStatistics(Statistics.MIN).get(1, 0)).doubleValue(), DELTA);
		assertEquals(  9.0, ((Number) table.getColumnStatistics(Statistics.MAX).get(1, 0)).doubleValue(), DELTA);
		assertEquals(-32.0, ((Number) table.getColumnStatistics(Statistics.SUM).get(1, 0)).doubleValue(), DELTA);
	}

	// TODO Add tests for dataAdded and dataRemoved
}

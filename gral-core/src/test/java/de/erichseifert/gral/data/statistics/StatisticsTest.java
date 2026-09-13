/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2026 Erich Seifert <dev[at]erichseifert.de>,
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;

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

	/**
	 * Returns the statistic of every row of the test table as a plain array,
	 * so that a test can assert all rows at once.
	 * @param key Statistical key.
	 * @return One value per row of the table.
	 */
	private double[] rowStatistics(String key) {
		DataSource stats = table.getRowStatistics(key);
		var values = new double[stats.getRowCount()];
		for (int row = 0; row < values.length; row++) {
			values[row] = ((Number) stats.get(0, row)).doubleValue();
		}
		return values;
	}

	/**
	 * Returns the statistic of every column of the test table as a plain
	 * array, so that a test can assert all columns at once.
	 * @param key Statistical key.
	 * @return One value per column of the table.
	 */
	private double[] columnStatistics(String key) {
		DataSource stats = table.getColumnStatistics(key);
		var values = new double[stats.getColumnCount()];
		for (int col = 0; col < values.length; col++) {
			values[col] = ((Number) stats.get(col, 0)).doubleValue();
		}
		return values;
	}

	@Test
	public void testSum() {
		assertEquals(85.0, stats.get(Statistics.SUM), DELTA);

		assertArrayEquals(new double[] {3.0, 7.0, 8.0, 9.0, 15.0, 10.0, 19.0, 14.0},
				rowStatistics(Statistics.SUM), DELTA);
		assertArrayEquals(new double[] {17.0, 24.0, 44.0},
				columnStatistics(Statistics.SUM), DELTA);
	}

	@Test
	public void testMean() {
		assertEquals(85.0/24.0, stats.get(Statistics.MEAN), DELTA);

		assertArrayEquals(new double[] {3.0/3.0, 7.0/3.0, 8.0/3.0, 9.0/3.0,
				15.0/3.0, 10.0/3.0, 19.0/3.0, 14.0/3.0},
				rowStatistics(Statistics.MEAN), DELTA);
		assertArrayEquals(new double[] {17.0/8.0, 24.0/8.0, 44.0/8.0},
				columnStatistics(Statistics.MEAN), DELTA);
	}

	@Test
	public void testMin() {
		assertEquals(0.0, stats.get(Statistics.MIN), DELTA);

		assertArrayEquals(new double[] {0.0, 1.0, 2.0, 2.0, 4.0, 1.0, 2.0, 1.0},
				rowStatistics(Statistics.MIN), DELTA);
		assertArrayEquals(new double[] {0.0, 1.0, 2.0},
				columnStatistics(Statistics.MIN), DELTA);
	}

	@Test
	public void testMax() {
		assertEquals(9.0, stats.get(Statistics.MAX), DELTA);

		assertArrayEquals(new double[] {2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 9.0, 9.0},
				rowStatistics(Statistics.MAX), DELTA);
		assertArrayEquals(new double[] {5.0, 9.0, 9.0},
				columnStatistics(Statistics.MAX), DELTA);
	}

	@Test
	public void testN() {
		assertEquals(24.0, stats.get(Statistics.N), DELTA);

		assertArrayEquals(new double[] {3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 3.0},
				rowStatistics(Statistics.N), DELTA);
		assertArrayEquals(new double[] {8.0, 8.0, 8.0},
				columnStatistics(Statistics.N), DELTA);
	}

	@Test
	public void testSumOfDiffSquares() {
		assertEquals(157.95833333333337, stats.get(Statistics.SUM_OF_DIFF_SQUARES), DELTA);

		double[] byRow = rowStatistics(Statistics.SUM_OF_DIFF_SQUARES);
		assertEquals(2.00000000000000, byRow[0], DELTA);
		assertEquals(2.66666666666666, byRow[1], DELTA);
		assertEquals(2.66666666666666, byRow[2], DELTA);

		double[] byColumn = columnStatistics(Statistics.SUM_OF_DIFF_SQUARES);
		assertEquals(18.87500000000000, byColumn[0], DELTA);
		assertEquals(48.00000000000000, byColumn[1], DELTA);
		assertEquals(42.00000000000000, byColumn[2], DELTA);
	}

	@Test
	public void testSumOfDiffCubics() {
		assertEquals(340.50347222222221, stats.get(Statistics.SUM_OF_DIFF_CUBICS), DELTA);

		double[] byRow = rowStatistics(Statistics.SUM_OF_DIFF_CUBICS);
		assertEquals( 0.00000000000000, byRow[0], DELTA);
		assertEquals(-1.77777777777777, byRow[1], DELTA);
		assertEquals( 1.77777777777777, byRow[2], DELTA);

		double[] byColumn = columnStatistics(Statistics.SUM_OF_DIFF_CUBICS);
		assertEquals( 17.90625000000000, byColumn[0], DELTA);
		assertEquals(198.00000000000000, byColumn[1], DELTA);
		assertEquals(  0.00000000000000, byColumn[2], DELTA);
	}

	@Test
	public void testSumOfDiffQuads() {
		assertEquals(2723.1039496527756, stats.get(Statistics.SUM_OF_DIFF_QUADS), DELTA);

		double[] byRow = rowStatistics(Statistics.SUM_OF_DIFF_QUADS);
		assertEquals(2.0000000000000, byRow[0], DELTA);
		assertEquals(3.5555555555555, byRow[1], DELTA);
		assertEquals(3.5555555555555, byRow[2], DELTA);

		double[] byColumn = columnStatistics(Statistics.SUM_OF_DIFF_QUADS);
		assertEquals( 104.2753906250000, byColumn[0], DELTA);
		assertEquals(1332.0000000000000, byColumn[1], DELTA);
		assertEquals( 388.5000000000000, byColumn[2], DELTA);
	}

	/**
	 * Tests skewness of a table, of its rows, and its columns for correctness.
	 * The results of R "moments" package are used for validation.
	 */
	@Test
	public void testSkewness() {
		assertEquals(0.8402593459494, stats.get(Statistics.SKEWNESS), DELTA);

		double[] byRow = rowStatistics(Statistics.SKEWNESS);
		assertEquals( 0.0000000000000, byRow[0], DELTA);
		assertEquals(-0.7071067811865, byRow[1], DELTA);
		assertEquals( 0.7071067811865, byRow[2], DELTA);

		double[] byColumn = columnStatistics(Statistics.SKEWNESS);
		assertEquals(0.6176169362594, byColumn[0], DELTA);
		assertEquals(1.6840241981634, byColumn[1], DELTA);
		assertEquals(0.0000000000000, byColumn[2], DELTA);
	}

	/**
	 * Tests that a symmetric distribution has a skewness of zero.
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testSkewnessOfSymmetricDistribution() {
		var symmetric = new DataTable(Integer.class);
		symmetric.add(1);
		symmetric.add(2);
		symmetric.add(3);
		symmetric.add(4);
		symmetric.add(5);

		assertEquals(0.0, symmetric.getStatistics().get(Statistics.SKEWNESS), DELTA);
	}

	/**
	 * Tests kurtosis of a table, of its rows, and its columns for correctness.
	 * The kurtosis is the excess kurtosis, i.e. the results of the R "moments"
	 * package minus three.
	 */
	@Test
	public void testKurtosis() {
		assertEquals(-0.3806690393420, stats.get(Statistics.KURTOSIS), DELTA);

		double[] byRow = rowStatistics(Statistics.KURTOSIS);
		assertEquals(-1.5000000000000, byRow[0], DELTA);
		assertEquals(-1.5000000000000, byRow[1], DELTA);
		assertEquals(-1.5000000000000, byRow[2], DELTA);

		double[] byColumn = columnStatistics(Statistics.KURTOSIS);
		assertEquals(-0.6584798912328, byColumn[0], DELTA);
		assertEquals( 1.6250000000000, byColumn[1], DELTA);
		assertEquals(-1.2380952380952, byColumn[2], DELTA);
	}

	@Test
	public void testQuartiles() {
		// Quartile 1
		assertEquals(2.00, stats.get(Statistics.QUARTILE_1), DELTA);
		assertArrayEquals(new double[] {0.50, 2.00, 2.00},
				Arrays.copyOf(rowStatistics(Statistics.QUARTILE_1), 3), DELTA);
		assertArrayEquals(new double[] {1.00, 1.75, 3.75},
				columnStatistics(Statistics.QUARTILE_1), DELTA);

		// Quartile 2
		assertEquals(2.50, stats.get(Statistics.QUARTILE_2), DELTA);
		assertArrayEquals(new double[] {1.00, 3.00, 2.00},
				Arrays.copyOf(rowStatistics(Statistics.QUARTILE_2), 3), DELTA);
		assertArrayEquals(new double[] {2.00, 2.00, 5.50},
				columnStatistics(Statistics.QUARTILE_2), DELTA);

		// Quartile 3
		assertEquals(5.00, stats.get(Statistics.QUARTILE_3), DELTA);
		assertArrayEquals(new double[] {1.50, 3.00, 3.00},
				Arrays.copyOf(rowStatistics(Statistics.QUARTILE_3), 3), DELTA);
		assertArrayEquals(new double[] {2.50, 3.25, 7.25},
				columnStatistics(Statistics.QUARTILE_3), DELTA);

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
		assertEquals(  3.0, rowStatistics(Statistics.N  )[1], DELTA);
		assertEquals(-42.0, rowStatistics(Statistics.MIN)[1], DELTA);
		assertEquals(  3.0, rowStatistics(Statistics.MAX)[1], DELTA);
		assertEquals(-38.0, rowStatistics(Statistics.SUM)[1], DELTA);
		// Vertical
		assertEquals(  9.0, columnStatistics(Statistics.N  )[1], DELTA);
		assertEquals(-42.0, columnStatistics(Statistics.MIN)[1], DELTA);
		assertEquals(  9.0, columnStatistics(Statistics.MAX)[1], DELTA);
		assertEquals(-32.0, columnStatistics(Statistics.SUM)[1], DELTA);
	}

	// TODO Add tests for dataAdded and dataRemoved
}

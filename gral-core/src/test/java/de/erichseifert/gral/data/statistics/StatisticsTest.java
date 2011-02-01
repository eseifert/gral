/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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
import de.erichseifert.gral.util.Orientation;

public class StatisticsTest {
	private static final double DELTA = 1e-10;
	private DataTable table;
	private Statistics stats;

	@Before
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
	public void testMeanDeviation() {
		assertEquals(0.0, stats.get(Statistics.MEAN_DEVIATION), DELTA);
		// Horizontal
		assertEquals(0.0, stats.get(Statistics.MEAN_DEVIATION, Orientation.HORIZONTAL, 0), DELTA);
		assertEquals(0.0, stats.get(Statistics.MEAN_DEVIATION, Orientation.HORIZONTAL, 1), DELTA);
		assertEquals(0.0, stats.get(Statistics.MEAN_DEVIATION, Orientation.HORIZONTAL, 2), DELTA);
		// Vertical
		assertEquals(0.0, stats.get(Statistics.MEAN_DEVIATION, Orientation.VERTICAL, 0), DELTA);
		assertEquals(0.0, stats.get(Statistics.MEAN_DEVIATION, Orientation.VERTICAL, 1), DELTA);
		assertEquals(0.0, stats.get(Statistics.MEAN_DEVIATION, Orientation.VERTICAL, 2), DELTA);
	}

	@Test
	public void testVariance() {
		assertEquals(157.95833333333337, stats.get(Statistics.VARIANCE), DELTA);
		// Horizontal
		assertEquals(  2.00000000000000, stats.get(Statistics.VARIANCE, Orientation.HORIZONTAL, 0), DELTA);
		assertEquals(  2.66666666666666, stats.get(Statistics.VARIANCE, Orientation.HORIZONTAL, 1), DELTA);
		assertEquals(  2.66666666666666, stats.get(Statistics.VARIANCE, Orientation.HORIZONTAL, 2), DELTA);
		// Vertical
		assertEquals( 18.87500000000000, stats.get(Statistics.VARIANCE, Orientation.VERTICAL, 0), DELTA);
		assertEquals( 48.00000000000000, stats.get(Statistics.VARIANCE, Orientation.VERTICAL, 1), DELTA);
		assertEquals( 42.00000000000000, stats.get(Statistics.VARIANCE, Orientation.VERTICAL, 2), DELTA);
	}

	@Test
	public void testSkewness() {
		assertEquals(340.5034722222222, stats.get(Statistics.SKEWNESS), DELTA);
		// Horizontal
		assertEquals(  0.0000000000000, stats.get(Statistics.SKEWNESS, Orientation.HORIZONTAL, 0), DELTA);
		assertEquals( -1.7777777777777, stats.get(Statistics.SKEWNESS, Orientation.HORIZONTAL, 1), DELTA);
		assertEquals(  1.7777777777777, stats.get(Statistics.SKEWNESS, Orientation.HORIZONTAL, 2), DELTA);
		// Vertical
		assertEquals( 17.9062500000000, stats.get(Statistics.SKEWNESS, Orientation.VERTICAL, 0), DELTA);
		assertEquals(198.0000000000000, stats.get(Statistics.SKEWNESS, Orientation.VERTICAL, 1), DELTA);
		assertEquals(  0.0000000000000, stats.get(Statistics.SKEWNESS, Orientation.VERTICAL, 2), DELTA);
	}

	@Test
	public void testKurtosis() {
		assertEquals(2723.1039496527756, stats.get(Statistics.KURTOSIS), DELTA);
		// Horizontal
		assertEquals(   2.0000000000000, stats.get(Statistics.KURTOSIS, Orientation.HORIZONTAL, 0), DELTA);
		assertEquals(   3.5555555555555, stats.get(Statistics.KURTOSIS, Orientation.HORIZONTAL, 1), DELTA);
		assertEquals(   3.5555555555555, stats.get(Statistics.KURTOSIS, Orientation.HORIZONTAL, 2), DELTA);
		// Vertical
		assertEquals( 104.2753906250000, stats.get(Statistics.KURTOSIS, Orientation.VERTICAL, 0), DELTA);
		assertEquals(1332.0000000000000, stats.get(Statistics.KURTOSIS, Orientation.VERTICAL, 1), DELTA);
		assertEquals( 388.5000000000000, stats.get(Statistics.KURTOSIS, Orientation.VERTICAL, 2), DELTA);
	}

	@Test
	public void testMedian() {
		assertEquals(2.5, stats.get(Statistics.MEDIAN), DELTA);
		// Horizontal
		assertEquals(1.0, stats.get(Statistics.MEDIAN, Orientation.HORIZONTAL, 0), DELTA);
		assertEquals(3.0, stats.get(Statistics.MEDIAN, Orientation.HORIZONTAL, 1), DELTA);
		assertEquals(2.0, stats.get(Statistics.MEDIAN, Orientation.HORIZONTAL, 2), DELTA);
		// Vertical
		assertEquals(2.0, stats.get(Statistics.MEDIAN, Orientation.VERTICAL, 0), DELTA);
		assertEquals(2.0, stats.get(Statistics.MEDIAN, Orientation.VERTICAL, 1), DELTA);
		assertEquals(5.5, stats.get(Statistics.MEDIAN, Orientation.VERTICAL, 2), DELTA);
	}

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

}

/* GRAL : a free graphing library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.tests.data.statistics;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.statistics.Statistics;

public class StatisticsTest {
	private static final double DELTA = 1e-10;
	private static DataTable table;
	private static Statistics stats;

	@BeforeClass
	public static void setUpBeforeClass() {
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
		assertEquals(17.0, stats.get(Statistics.SUM, 0), DELTA);
		assertEquals(24.0, stats.get(Statistics.SUM, 1), DELTA);
		assertEquals(44.0, stats.get(Statistics.SUM, 2), DELTA);
	}

	@Test
	public void testMean() {
		assertEquals(2.125, stats.get(Statistics.MEAN, 0), DELTA);
		assertEquals(3.000, stats.get(Statistics.MEAN, 1), DELTA);
		assertEquals(5.500, stats.get(Statistics.MEAN, 2), DELTA);
	}

	@Test
	public void testMin() {
		assertEquals(0.0, stats.get(Statistics.MIN, 0), DELTA);
		assertEquals(1.0, stats.get(Statistics.MIN, 1), DELTA);
		assertEquals(2.0, stats.get(Statistics.MIN, 2), DELTA);
	}

	@Test
	public void testMax() {
		assertEquals(5.0, stats.get(Statistics.MAX, 0), DELTA);
		assertEquals(9.0, stats.get(Statistics.MAX, 1), DELTA);
		assertEquals(9.0, stats.get(Statistics.MAX, 2), DELTA);
	}

	@Test
	public void testN() {
		assertEquals(8.0, stats.get(Statistics.N, 0), DELTA);
		assertEquals(8.0, stats.get(Statistics.N, 1), DELTA);
		assertEquals(8.0, stats.get(Statistics.N, 2), DELTA);
	}

	@Test
	public void testMeanDeviation() {
		assertEquals(0.0, stats.get(Statistics.MEAN_DEVIATION, 0), DELTA);
		assertEquals(0.0, stats.get(Statistics.MEAN_DEVIATION, 1), DELTA);
		assertEquals(0.0, stats.get(Statistics.MEAN_DEVIATION, 2), DELTA);
	}

	@Test
	public void testVariance() {
		assertEquals(18.875, stats.get(Statistics.VARIANCE, 0), DELTA);
		assertEquals(48.000, stats.get(Statistics.VARIANCE, 1), DELTA);
		assertEquals(42.000, stats.get(Statistics.VARIANCE, 2), DELTA);
	}

	@Test
	public void testSkewness() {
		assertEquals( 17.90625, stats.get(Statistics.SKEWNESS, 0), DELTA);
		assertEquals(198.00000, stats.get(Statistics.SKEWNESS, 1), DELTA);
		assertEquals(  0.00000, stats.get(Statistics.SKEWNESS, 2), DELTA);
	}

	@Test
	public void testKurtosis() {
		assertEquals( 104.275390625, stats.get(Statistics.KURTOSIS, 0), DELTA);
		assertEquals(1332.000000000, stats.get(Statistics.KURTOSIS, 1), DELTA);
		assertEquals( 388.500000000, stats.get(Statistics.KURTOSIS, 2), DELTA);
	}

	@Test
	public void testMedian() {
		// The loop ensures effects of randomized algorithm are reduced
		for (int i = 0; i < 10; i++) {
			assertEquals(2.0, stats.get(Statistics.MEDIAN, 0), DELTA);
			assertEquals(2.0, stats.get(Statistics.MEDIAN, 1), DELTA);
			assertEquals(5.5, stats.get(Statistics.MEDIAN, 2), DELTA);
		}
	}
}
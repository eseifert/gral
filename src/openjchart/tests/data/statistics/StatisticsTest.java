package openjchart.tests.data.statistics;

import static org.junit.Assert.assertEquals;
import openjchart.data.DataTable;
import openjchart.data.statistics.Statistics;

import org.junit.BeforeClass;
import org.junit.Test;

public class StatisticsTest {
	private static final double DELTA = 1e-10;
	private static DataTable table;
	private static Statistics stats;

	@BeforeClass
	public static void setUpBeforeClass() {
		table = new DataTable(Integer.class, Integer.class);
		table.add(0, 1); // 0
		table.add(1, 3); // 1
		table.add(2, 2); // 2
		table.add(2, 2); // 3
		table.add(5, 4); // 4
		table.add(1, 2); // 5
		table.add(2, 9); // 6
		table.add(4, 1); // 7

		stats = table.getStatistics();
	}

	@Test
	public void testSum() {
		assertEquals(17.0, stats.get(Statistics.SUM, 0), DELTA);
		assertEquals(24.0, stats.get(Statistics.SUM, 1), DELTA);
	}

	@Test
	public void testMean() {
		assertEquals(2.125, stats.get(Statistics.MEAN, 0), DELTA);
		assertEquals(3.000, stats.get(Statistics.MEAN, 1), DELTA);
	}

	@Test
	public void testMin() {
		assertEquals(0.0, stats.get(Statistics.MIN, 0), DELTA);
		assertEquals(1.0, stats.get(Statistics.MIN, 1), DELTA);
	}

	@Test
	public void testMax() {
		assertEquals(5.0, stats.get(Statistics.MAX, 0), DELTA);
		assertEquals(9.0, stats.get(Statistics.MAX, 1), DELTA);
	}

	@Test
	public void testN() {
		assertEquals(8.0, stats.get(Statistics.N, 0), DELTA);
		assertEquals(8.0, stats.get(Statistics.N, 1), DELTA);
	}

	@Test
	public void testMeanDeviation() {
		assertEquals(0.0, stats.get(Statistics.MEAN_DEVIATION, 0), DELTA);
		assertEquals(0.0, stats.get(Statistics.MEAN_DEVIATION, 1), DELTA);
	}

	@Test
	public void testVariance() {
		assertEquals(18.875, stats.get(Statistics.VARIANCE, 0), DELTA);
		assertEquals(48.000, stats.get(Statistics.VARIANCE, 1), DELTA);
	}

	@Test
	public void testSkewness() {
		assertEquals( 17.90625, stats.get(Statistics.SKEWNESS, 0), DELTA);
		assertEquals(198.00000, stats.get(Statistics.SKEWNESS, 1), DELTA);
	}

	@Test
	public void testKurtosis() {
		assertEquals( 104.275390625, stats.get(Statistics.KURTOSIS, 0), DELTA);
		assertEquals(1332.000000000, stats.get(Statistics.KURTOSIS, 1), DELTA);
	}

	@Test
	public void testMedian() {
		assertEquals(2.0, stats.get(Statistics.MEDIAN, 0), DELTA);
		assertEquals(2.0, stats.get(Statistics.MEDIAN, 1), DELTA);
	}
}
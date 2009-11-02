package openjchart.tests;

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
	public void testGeneration() {
		Histogram histogram = new Histogram(table, 4);

		long[] expected = {
			3, 5,  // 1.0-2.0, 1.0-3.0
			3, 2,  // 2.0-3.0, 3.0-5.0
			0, 0,  // 3.0-4.0, 5.0-7.0
			1, 0   // 4.0-5.0, 7.0-9.0
		};
		int i = 0;
		while (i < expected.length) {
			assertEquals(expected[i], histogram.get(i%2, i/2));
			i++;
		}
	}
}
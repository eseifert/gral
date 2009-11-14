package openjchart.tests.data;

import static org.junit.Assert.assertEquals;
import openjchart.data.DataSeries;
import openjchart.data.DataTable;

import org.junit.BeforeClass;
import org.junit.Test;

public class DataSeriesTest {
	private static DataTable table;
	private static DataSeries series;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		table = new DataTable(Integer.class, Integer.class, Integer.class);
		table.add(1, 3, 5); // 0
		table.add(2, 8, 2); // 1
		table.add(3, 5, 6); // 2
		table.add(4, 6, 2); // 3
		table.add(5, 4, 1); // 4
		table.add(6, 9, 5); // 5
		table.add(7, 8, 7); // 6
		table.add(8, 1, 9); // 7

		series = new DataSeries(table, 2, 1);
	}

	@Test
	public void testGetInt() {
		for (int i = 0; i < series.getRowCount(); i++) {
			Number[] rowTable = table.get(i);
			Number[] rowSeries = series.get(i);
			assertEquals(rowTable[2], rowSeries[0]);
			assertEquals(rowTable[1], rowSeries[1]);
			assertEquals(2, rowSeries.length);
		}
	}

	@Test
	public void testGetColumnCount() {
		assertEquals(2, series.getColumnCount());
	}

}

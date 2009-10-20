package openjchart.tests;

import static org.junit.Assert.assertEquals;
import openjchart.data.DataTable;

import org.junit.BeforeClass;
import org.junit.Test;

public class DataTableTest {
	private static DataTable table;

	@BeforeClass
	public static void setUpBeforeClass() {
		table = new DataTable(Integer.class, Integer.class);
		table.add(1, 1); // 0
		table.add(2, 3); // 1
		table.add(3, 2); // 2
		table.add(4, 6); // 3
		table.add(5, 4); // 4
		table.add(6, 8); // 5
		table.add(7, 9); // 6
		table.add(8, 11); // 7
	}

	@Test
	public void testGetIntInt() {
		assertEquals(6, table.get(1, 3));
		assertEquals(5, table.get(0, 4));
		assertEquals(1, table.get(0, 0));
		assertEquals(11, table.get(1, 7));
	}

	@Test
	public void testGetMin() {
		assertEquals(1.0, table.getMin(0));
		assertEquals(1.0, table.getMin(1));
	}

	@Test
	public void testGetMax() {
		assertEquals(8.0, table.getMax(0));
		assertEquals(11.0, table.getMax(1));
	}

}

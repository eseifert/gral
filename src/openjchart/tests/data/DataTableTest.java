/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.tests.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import openjchart.data.DataTable;
import openjchart.data.comparators.Ascending;
import openjchart.data.comparators.Descending;

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
	public void testIterator() {
		int rowNo = 0;
		for (Number[] row : table) {
			assertArrayEquals(table.get(rowNo), row);
			rowNo++;
		}
	}

	@Test
	public void testSort() {
		DataTable table = new DataTable(Integer.class, Integer.class, Integer.class);
		int[] original = {
				9,	1,	3,
				4,	4,	2,
				4,	2,	1,
				8,	1,	9,
				8,	1,	7,
				6,	2,	4,
				4,	6,	5,
				3,	3,	5
		};
		int i = 0;
		while (i < original.length) {
			table.add(original[i++], original[i++], original[i++]);
		}

		table.sort(new Ascending(1), new Descending(0), new Ascending(2));

		int[] expected = {
				9,	1,	3,
				8,	1,	7,
				8,	1,	9,
				6,	2,	4,
				4,	2,	1,
				3,	3,	5,
				4,	4,	2,
				4,	6,	5
		};
		i = 0;
		while (i < expected.length) {
			assertEquals(expected[i], table.get(i%3, i/3));
			i++;
		}
	}
}
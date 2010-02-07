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

package de.erichseifert.gral.tests.data;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.Row;

public class RowTest {
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
	public void testCreation() {
		Row row1 = new Row(table, 0);
		assertEquals(table, row1.getSource());
		assertEquals(0, row1.getRow());

		Row row2 = new Row(table, 1);
		assertEquals(table, row2.getSource());
		assertEquals(1, row2.getRow());
	}

	@Test
	public void testGet() {
		Row row1 = new Row(table, 1);
		assertEquals(table.get(0, 1), row1.get(0));
		assertEquals(table.get(1, 1), row1.get(1));

		Row row2 = new Row(null, 1);
		assertEquals(null, row2.get(0));
		assertEquals(null, row2.get(1));
	}

}

/**
 * GRAL: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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

package de.erichseifert.gral.data.filters;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.data.DataTable;

public class MedianTest {
	private static DataTable table;

	@BeforeClass
	public static void setUpBeforeClass() {
		table = new DataTable(Integer.class, Integer.class, Integer.class);
		table.add(1, 3, 5); // 0
		table.add(2, 8, 2); // 1
		table.add(3, 5, 6); // 2
		table.add(4, 6, 2); // 3
		table.add(5, 4, 1); // 4
		table.add(6, 9, 5); // 5
		table.add(7, 8, 7); // 6
		table.add(8, 1, 9); // 7
	}

	@Test
	public void testCreation() {
		Median filter = new Median(table, 3, 1, Filter.Mode.MODE_REPEAT, 0);

		assertEquals(table.getColumnCount(), filter.getColumnCount());
		assertEquals(table.getRowCount(), filter.getRowCount());
	}

	@Test
	public void testMode() {
		Median filter = new Median(table, 3, 1, Filter.Mode.MODE_REPEAT, 0);

		for (Filter.Mode mode : Filter.Mode.values()) {
			filter.setMode(mode);
			assertEquals(mode, filter.getMode());
		}
	}

	@Test
	public void testWindowSize() {
		Median filter = new Median(table, 3, 1, Filter.Mode.MODE_REPEAT, 0);
		assertEquals(3, filter.getWindowSize());

		filter.setWindowSize(1);
		assertEquals(1, filter.getWindowSize());
	}

	@Test
	public void testOffset() {
		Median filter = new Median(table, 3, 1, Filter.Mode.MODE_REPEAT, 0);
		assertEquals(1, filter.getOffset());

		filter.setOffset(0);
		assertEquals(0, filter.getOffset());
	}

}
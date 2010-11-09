/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2010 Erich Seifert <dev[at]richseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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

package de.erichseifert.gral.data;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RowSubsetTest {
	private static final double DELTA = 1e-15;
	private static DataTable table;
	private RowSubset data;

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

	@Before
	public void setUp() {
		data = new RowSubset(table) {
			@Override
			public boolean accept(Row row) {
				return (row.get(0).doubleValue() % 2.0) == 0.0;
			}
		};
	}

	@Test
	public void testCreation() {
		assertEquals(table.getColumnCount(), data.getColumnCount());
		assertEquals(table.getRowCount()/2, data.getRowCount());
	}

	@Test
	public void testGetIntInt() {
		assertEquals( 2.0, data.get(0, 0).doubleValue(), DELTA);
		assertEquals( 4.0, data.get(0, 1).doubleValue(), DELTA);
		assertEquals( 6.0, data.get(0, 2).doubleValue(), DELTA);
		assertEquals( 8.0, data.get(0, 3).doubleValue(), DELTA);
		assertEquals( 3.0, data.get(1, 0).doubleValue(), DELTA);
		assertEquals( 6.0, data.get(1, 1).doubleValue(), DELTA);
		assertEquals( 8.0, data.get(1, 2).doubleValue(), DELTA);
		assertEquals(11.0, data.get(1, 3).doubleValue(), DELTA);
	}

	@Test
	public void testGetInt() {
		assertEquals(table.getRow(1), data.getRow(0));
		assertEquals(table.getRow(3), data.getRow(1));
		assertEquals(table.getRow(5), data.getRow(2));
		assertEquals(table.getRow(7), data.getRow(3));
	}

}

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
package de.erichseifert.gral.plots.points;

import static org.junit.Assert.assertEquals;

import java.awt.Shape;

import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.Row;

public class SizeablePointsRendererTest {
	private static DataTable table;
	private static Row row;

	@BeforeClass
	public static void setUpBeforeClass() {
		table = new DataTable(Integer.class, Integer.class, Integer.class);
		table.add(1, 0, 1); // 0
		table.add(2, 0, 1); // 1
		table.add(3, 0, 1); // 2
		table.add(4, 0, 1); // 3
		table.add(5, 0, 1); // 4
		table.add(6, 0, 1); // 5
		table.add(7, 0, 1); // 6
		table.add(8, 0, 1); // 7

		row = new Row(table, 0);
	}

	@Test
	public void testPointPath() {
		// Get line
		PointRenderer r = new SizeablePointRenderer();
		Shape expected = r.<Shape>getSetting(PointRenderer.SHAPE);
		Shape path = r.getPointPath(row);
		assertEquals(expected.getBounds2D(), path.getBounds2D());
	}

}

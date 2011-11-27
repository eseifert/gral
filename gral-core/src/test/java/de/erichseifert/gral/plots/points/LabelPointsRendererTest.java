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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.awt.Shape;

import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.Row;

public class LabelPointsRendererTest {
	private static DataTable table;

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setUpBeforeClass() {
		table = new DataTable(Integer.class, Integer.class, Integer.class);
		table.add(1, 3, 1);              // 0
		table.add(2, (Integer) null, 2); // 1
	}

	@Test
	public void testPointPath() {
		PointRenderer r = new LabelPointRenderer();
		Shape path = r.getPointPath(new Row(table, 0));
		assertNotNull(path);
	}

	@Test
	public void testInvalidColumn() {
		PointRenderer r = new LabelPointRenderer();
		r.setSetting(LabelPointRenderer.COLUMN, table.getColumnCount());
		Shape path = r.getPointPath(new Row(table, 0));
		assertNull(path);
	}

	@Test
	public void testNullLabel() {
		PointRenderer r = new LabelPointRenderer();
		r.setSetting(LabelPointRenderer.COLUMN, 1);
		Row row = new Row(table, 1);
		assertNull(row.get(1));
		Shape path = r.getPointPath(row);
		assertNull(path);
	}
}

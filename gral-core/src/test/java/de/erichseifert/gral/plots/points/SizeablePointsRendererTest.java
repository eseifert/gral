/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2012 Erich Seifert <dev[at]erichseifert.de>,
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
import static org.junit.Assert.assertNull;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.Row;

public class SizeablePointsRendererTest {
	private static DataTable table;
	private static Shape shape;

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setUpBeforeClass() {
		table = new DataTable(Integer.class, Integer.class, Integer.class);
		table.add(1, 3, 1);              // 0
		table.add(2, 5, 2);              // 1
		table.add(3, 2, -1);             // 2
		table.add(4, 1, 0);              // 3
		table.add(5, 4, (Integer) null); // 4

		shape = new Rectangle2D.Double(-5.0, -5.0, 10.0, 10.0);
	}

	@Test
	public void testUnsized() {
		PointRenderer r = new SizeablePointRenderer();
		r.setSetting(PointRenderer.SHAPE, shape);
		Shape expected = shape;
		Shape path = r.getPointPath(new Row(table, 0));
		assertEquals(expected.getBounds2D(), path.getBounds2D());
	}

	@Test
	public void testSized() {
		PointRenderer r = new SizeablePointRenderer();
		r.setSetting(PointRenderer.SHAPE, shape);
		Shape expected = AffineTransform.getScaleInstance(2.0, 2.0)
				.createTransformedShape(shape);
		Shape path = r.getPointPath(new Row(table, 1));
		assertEquals(expected.getBounds2D(), path.getBounds2D());
	}

	@Test
	public void testNegativeSize() {
		PointRenderer r = new SizeablePointRenderer();
		r.setSetting(PointRenderer.SHAPE, shape);
		Shape path = r.getPointPath(new Row(table, 2));
		assertNull(path);
	}

	@Test
	public void testZeroSize() {
		PointRenderer r = new SizeablePointRenderer();
		r.setSetting(PointRenderer.SHAPE, shape);
		Shape path = r.getPointPath(new Row(table, 3));
		assertNull(path);
	}

	@Test
	public void testNullSize() {
		PointRenderer r = new SizeablePointRenderer();
		r.setSetting(PointRenderer.SHAPE, shape);
		Shape path = r.getPointPath(new Row(table, 4));
		assertNull(path);
	}

	@Test
	public void testInvalidColumn() {
		PointRenderer r = new SizeablePointRenderer();
		r.setSetting(PointRenderer.SHAPE, shape);
		Shape path;

		// Column index too big
		r.setSetting(SizeablePointRenderer.COLUMN, table.getColumnCount());
		path = r.getPointPath(new Row(table, 0));
		assertEquals(shape, path);

		// Column index too small
		r.setSetting(SizeablePointRenderer.COLUMN, -1);
		path = r.getPointPath(new Row(table, 0));
		assertEquals(shape, path);
	}
}

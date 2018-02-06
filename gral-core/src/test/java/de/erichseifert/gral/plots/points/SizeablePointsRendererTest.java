/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <mseifert[at]error-reports.org>
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
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.axes.LinearRenderer2D;

public class SizeablePointsRendererTest {
	private static DataTable table;
	private static Row row;
	private static Axis axis;
	private static AxisRenderer axisRenderer;
	private static PointData data;
	private static Shape shape;

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setUpBeforeClass() {
		table = new DataTable(Integer.class, Integer.class, Integer.class);
		table.add(1, 3, 1);    // 0
		table.add(2, 5, 2);    // 1
		table.add(3, 2, -1);   // 2
		table.add(4, 1, 0);    // 3
		table.add(5, 4, null); // 4

		row = new Row(table, 0);

		axis = new Axis(-1.0, 1.0);
		axisRenderer = new LinearRenderer2D();
		axisRenderer.setShape(new Line2D.Double(-5.0, 0.0, 5.0, 0.0));

		data = new PointData(
			Arrays.asList(null, axis),
			Arrays.asList(null, axisRenderer),
			row, row.getIndex(), 0);

		shape = new Rectangle2D.Double(-5.0, -5.0, 10.0, 10.0);
	}

	@Test
	public void testUnsized() {
		PointRenderer r = new SizeablePointRenderer();
		r.setShape(shape);
		Shape expected = shape;
		Shape path = r.getPointShape(data);
		assertEquals(expected.getBounds2D(), path.getBounds2D());
	}

	@Test
	public void testSized() {
		PointRenderer r = new SizeablePointRenderer();
		r.setShape(shape);
		Shape expected = AffineTransform.getScaleInstance(2.0, 2.0)
			.createTransformedShape(shape);
		Row row2 = new Row(table, 1);
		PointData data2 = new PointData(data.axes, data.axisRenderers, row2, row2.getIndex(), 0);
		Shape path = r.getPointShape(data2);
		assertEquals(expected.getBounds2D(), path.getBounds2D());
	}

	@Test
	public void testNegativeSize() {
		PointRenderer r = new SizeablePointRenderer();
		r.setShape(shape);
		Row row2 = new Row(table, 2);
		PointData data2 = new PointData(data.axes, data.axisRenderers, row2, row2.getIndex(), 0);
		Shape path = r.getPointShape(data2);
		assertNull(path);
	}

	@Test
	public void testZeroSize() {
		PointRenderer r = new SizeablePointRenderer();
		Row row2 = new Row(table, 3);
		PointData data2 = new PointData(data.axes, data.axisRenderers, row2, row2.getIndex(), 0);
		Shape path = r.getPointShape(data2);
		assertNull(path);
	}

	@Test
	public void testNullSize() {
		PointRenderer r = new SizeablePointRenderer();
		r.setShape(shape);
		Row row2 = new Row(table, 4);
		PointData data2 = new PointData(data.axes, data.axisRenderers, row2, row2.getIndex(), 0);
		Shape path = r.getPointShape(data2);
		assertNull(path);
	}

	@Test
	public void testInvalidColumn() {
		SizeablePointRenderer r = new SizeablePointRenderer();
		r.setShape(shape);
		Shape path;

		// Column index too big
		r.setColumn(table.getColumnCount());
		path = r.getPointShape(data);
		assertEquals(shape, path);

		// Column index too small
		r.setColumn(-1);
		path = r.getPointShape(data);
		assertEquals(shape, path);
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		SizeablePointRenderer original = new SizeablePointRenderer();
		SizeablePointRenderer deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(original.getColumn(), deserialized.getColumn());
	}
}

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
package de.erichseifert.gral.plots.areas;

import static de.erichseifert.gral.TestUtils.assertNotEmpty;
import static de.erichseifert.gral.TestUtils.createTestImage;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.plots.DataPoint;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.axes.LinearRenderer2D;
import de.erichseifert.gral.plots.points.PointData;
import de.erichseifert.gral.util.PointND;

public class LineAreaRendererTest {
	private PointData data;

	@Before
	public void setUp() {
		Axis axisX = new Axis(-5.0, 5.0);
		Axis axisY = new Axis(-5.0, 5.0);
		AxisRenderer axisRendererX = new LinearRenderer2D();
		AxisRenderer axisRendererY = new LinearRenderer2D();
		data = new PointData(
			Arrays.asList(axisX, axisY),
			Arrays.asList(axisRendererX, axisRendererY),
			null, 0, 0);
	}

	@Test
	public void testArea() {
		// Get line
		AreaRenderer r = new LineAreaRenderer2D();
		List<DataPoint> points = Arrays.asList(
			new DataPoint(data, new PointND<>(0.0, 0.0)),
			new DataPoint(data, new PointND<>(1.0, 1.0))
		);
		Shape shape = r.getAreaShape(points);
		Drawable area = r.getArea(points, shape);
		assertNotNull(area);

		// Draw area
		BufferedImage image = createTestImage();
		DrawingContext context = new DrawingContext((Graphics2D) image.getGraphics());
		area.draw(context);
		assertNotEmpty(image);
	}

	@Test
	public void testShapeNoPoints() {
		AreaRenderer r = new LineAreaRenderer2D();
		List<DataPoint> points = new LinkedList<>();
		Shape shape = r.getAreaShape(points);
		assertNull(shape);
	}

	@Test
	public void testShapeNullPoints() {
		AreaRenderer r = new LineAreaRenderer2D();
		List<DataPoint> points = Arrays.asList((DataPoint) null);
		Shape shape = r.getAreaShape(points);
		assertNull(shape);
	}

	@Test
	public void testShapeNullRenderer() {
		AreaRenderer r = new LineAreaRenderer2D();
		PointData data2 = new PointData(
			data.axes,
			Arrays.asList((AxisRenderer) null, null),
			null, 0, 0);
		List<DataPoint> points = Arrays.asList(
			new DataPoint(data2, new PointND<>(0.0, 0.0)),
			new DataPoint(data2, new PointND<>(1.0, 1.0))
		);
		Shape shape = r.getAreaShape(points);
		assertNotNull(shape);
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		AreaRenderer original = new DefaultAreaRenderer2D();
		@SuppressWarnings("unused")
		AreaRenderer deserialized = TestUtils.serializeAndDeserialize(original);
    }
}

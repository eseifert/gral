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

import static de.erichseifert.gral.TestUtils.assertEmpty;
import static de.erichseifert.gral.TestUtils.assertNotEmpty;
import static de.erichseifert.gral.TestUtils.assertNotEquals;
import static de.erichseifert.gral.TestUtils.createTestImage;
import static org.junit.Assert.assertNotNull;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.axes.LinearRenderer2D;
import de.erichseifert.gral.graphics.Location;

public class DefaultPointRenderer2DTest {
	private static DataTable table;
	private static Row row;
	private static Axis axis;
	private static AxisRenderer axisRenderer;
	private static PointData data;
	private PointRenderer r;

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setUpBeforeClass() {
		table = new DataTable(Integer.class, Integer.class, String.class);
		table.add(1, 9, "Jan"); // 0
		table.add(2, 8, "Feb"); // 1
		table.add(3, 7, "Mar"); // 2
		table.add(4, 6, "Apr"); // 3
		table.add(5, 5, "May"); // 4
		table.add(6, 4, "Jun"); // 5
		table.add(7, 3, "Jul"); // 6
		table.add(8, 1, "Aug"); // 7

		row = new Row(table, 4);

		axis = new Axis(0.0, 10.0);

		axisRenderer = new LinearRenderer2D();
		axisRenderer.setShape(new Line2D.Double(-5.0, 0.0, 5.0, 0.0));

		data = new PointData(
			Arrays.asList(null, axis),
			Arrays.asList(null, axisRenderer),
			row, row.getIndex(), 0);

	}

	@Before
	public void setUp() {
		r = new DefaultPointRenderer2D();
	}

	private static void layout(BufferedImage image, AxisRenderer axisRenderer) {
		Line2D axisShape = new Line2D.Double(
			image.getWidth()/2.0, 0.0,
			image.getWidth()/2.0, image.getHeight()
		);
		axisRenderer.setShape(axisShape);
	}

	private static void assertPointRenderer(PointRenderer r) {
		// Get point
		Drawable point = r.getPoint(data, r.getPointShape(data));
		assertNotNull(point);

		// Draw point
		BufferedImage image = createTestImage();
		DrawingContext context = new DrawingContext((Graphics2D) image.getGraphics());
		point.draw(context);
		assertNotEmpty(image);
	}

	@Test
	public void testPoint() {
		assertPointRenderer(r);
	}

	@Test
	public void testValueDisplayed() {
		DrawingContext context;
		Drawable point;

		// Draw without value labels
		BufferedImage unset = createTestImage();
		context = new DrawingContext((Graphics2D) unset.getGraphics());
		layout(unset, axisRenderer);
		r.setValueVisible(false);
		point = r.getValue(data, r.getPointShape(data));
		point.draw(context);

		// Draw with value labels
		BufferedImage set = createTestImage();
		context = new DrawingContext((Graphics2D) set.getGraphics());
		layout(set, axisRenderer);
		r.setValueVisible(true);
		point = r.getValue(data, r.getPointShape(data));
		point.draw(context);

		assertNotEquals(unset, set);
	}

	@Test
	public void testValueFormat() {
		List<Format> formats = Arrays.asList(
			(Format) null, NumberFormat.getInstance());

		r.setValueVisible(true);
		for (Format format : formats) {
			r.setValueFormat(format);
			Drawable point = r.getValue(data, r.getPointShape(data));
			assertNotNull(point);
			BufferedImage image = createTestImage();
			DrawingContext context = new DrawingContext((Graphics2D) image.getGraphics());
			layout(image, axisRenderer);
			point.draw(context);
			assertNotEmpty(image);
		}
	}

	@Test
	public void testValueDistance() {
		List<Double> distances = Arrays.asList(Double.NaN, 0.0, 1.0);

		r.setValueVisible(true);
		for (Double distance : distances) {
			r.setValueDistance(distance);
			Drawable point = r.getValue(data, r.getPointShape(data));
			assertNotNull(point);
			BufferedImage image = createTestImage();
			DrawingContext context = new DrawingContext((Graphics2D) image.getGraphics());
			layout(image, axisRenderer);
			point.draw(context);
			assertNotEmpty(image);
		}
	}

	@Test
	public void testValueLocation() {
		Location[] locations = new Location[Location.values().length + 1];
		System.arraycopy(Location.values(), 0, locations, 1, locations.length - 1);

		r.setValueVisible(true);
		r.setValueDistance(0.5);
		for (Location location : locations) {
			r.setValueLocation(location);
			Drawable point = r.getValue(data, r.getPointShape(data));
			assertNotNull(point);
			BufferedImage image = createTestImage();
			DrawingContext context = new DrawingContext((Graphics2D) image.getGraphics());
			layout(image, axisRenderer);
			AffineTransform txOld = context.getGraphics().getTransform();
			context.getGraphics().translate(image.getWidth()/2.0, image.getHeight()/2.0);
			point.draw(context);
			context.getGraphics().setTransform(txOld);
			assertNotEmpty(image);
		}
	}

	@Test
	public void testErrorDisplayed() {
		r.setErrorColumnTop(1);
		r.setErrorColumnBottom(1);

		DrawingContext context;
		Drawable point;

		// Draw without error bars
		r.setErrorVisible(false);
		BufferedImage unset = createTestImage();
		context = new DrawingContext((Graphics2D) unset.getGraphics());
		layout(unset, axisRenderer);
		point = r.getPoint(data, r.getPointShape(data));
		point.draw(context);

		// Draw with error bars
		r.setErrorVisible(true);
		BufferedImage set = createTestImage();
		context = new DrawingContext((Graphics2D) set.getGraphics());
		layout(set, axisRenderer);
		point = r.getPoint(data, r.getPointShape(data));
		point.draw(context);

		assertNotEquals(unset, set);
	}

	@Test
	public void testErrorNoAxisRenderer() {
		r.setShape(null);

		DrawingContext context;
		Drawable point;

		// Draw error bars
		r.setErrorVisible(true);
		BufferedImage image = createTestImage();
		context = new DrawingContext((Graphics2D) image.getGraphics());
		layout(image, axisRenderer);
		point = r.getPoint(data, r.getPointShape(data));
		point.draw(context);
		assertEmpty(image);
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		PointRenderer original = r;
		@SuppressWarnings("unused")
		PointRenderer deserialized = TestUtils.serializeAndDeserialize(original);
    }
}

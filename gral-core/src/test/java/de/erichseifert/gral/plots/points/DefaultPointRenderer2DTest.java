/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2012 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael[at]erichseifert.de>
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.axes.LinearRenderer2D;
import de.erichseifert.gral.plots.colors.ColorMapper;
import de.erichseifert.gral.plots.colors.SingleColor;
import de.erichseifert.gral.util.Location;
public class DefaultPointRenderer2DTest {
	private static DataTable table;
	private static Row row;
	private static Axis axis;
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

		axis = new Axis();
		axis.setRange(0.0, 10.0);
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
		axisRenderer.setSetting(AxisRenderer.SHAPE, axisShape);
	}

	private static void assertPointRenderer(PointRenderer r) {
		// Get point
		Drawable point = r.getPoint(null, null, row, 1);
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
	public void testSettings() {
		// Get
		assertTrue(r.getSetting(PointRenderer.COLOR) instanceof ColorMapper);
		assertEquals(Color.BLACK, r.<ColorMapper>getSetting(PointRenderer.COLOR).get(0));
		// Set
		r.setSetting(PointRenderer.COLOR, new SingleColor(Color.RED));
		assertTrue(r.getSetting(PointRenderer.COLOR) instanceof ColorMapper);
		assertEquals(Color.RED, r.<ColorMapper>getSetting(PointRenderer.COLOR).get(0));
		// Remove
		r.removeSetting(PointRenderer.COLOR);
		assertTrue(r.getSetting(PointRenderer.COLOR) instanceof ColorMapper);
		assertEquals(Color.BLACK, r.<ColorMapper>getSetting(PointRenderer.COLOR).get(0));
	}

	@Test
	public void testValueDisplayed() {
		AxisRenderer axisRenderer = new LinearRenderer2D();

		DrawingContext context;
		Drawable point;

		// Draw without value labels
		BufferedImage unset = createTestImage();
		context = new DrawingContext((Graphics2D) unset.getGraphics());
		layout(unset, axisRenderer);
		r.setSetting(PointRenderer.VALUE_DISPLAYED, false);
		point = r.getPoint(axis, axisRenderer, row, 0);
		point.draw(context);

		// Draw with value labels
		BufferedImage set = createTestImage();
		context = new DrawingContext((Graphics2D) set.getGraphics());
		layout(set, axisRenderer);
		r.setSetting(PointRenderer.VALUE_DISPLAYED, true);
		point = r.getPoint(axis, axisRenderer, row, 0);
		point.draw(context);

		assertNotEquals(unset, set);
	}

	@Test
	public void testValueFormat() {
		AxisRenderer axisRenderer = new LinearRenderer2D();

		List<Format> formats = Arrays.asList(
			(Format) null, NumberFormat.getInstance());

		r.setSetting(PointRenderer.VALUE_DISPLAYED, true);
		for (Format format : formats) {
			r.setSetting(PointRenderer.VALUE_FORMAT, format);
			int colIndex = 0;
			Drawable point = r.getPoint(axis, axisRenderer, row, colIndex);
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
		AxisRenderer axisRenderer = new LinearRenderer2D();

		List<Double> distances = Arrays.asList(
			(Double) null, Double.NaN,
			Double.valueOf(0.0), Double.valueOf(1.0));

		r.setSetting(PointRenderer.VALUE_DISPLAYED, true);
		for (Double distance : distances) {
			r.setSetting(PointRenderer.VALUE_DISTANCE, distance);
			Drawable point = r.getPoint(axis, axisRenderer, row, 0);
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
		AxisRenderer axisRenderer = new LinearRenderer2D();

		Location[] locations = new Location[Location.values().length + 1];
		System.arraycopy(Location.values(), 0, locations, 1, locations.length - 1);

		r.setSetting(PointRenderer.VALUE_DISPLAYED, true);
		for (Location location : locations) {
			r.setSetting(PointRenderer.VALUE_LOCATION, location);
			Drawable point = r.getPoint(axis, axisRenderer, row, 0);
			assertNotNull(point);
			BufferedImage image = createTestImage();
			DrawingContext context = new DrawingContext((Graphics2D) image.getGraphics());
			layout(image, axisRenderer);
			point.draw(context);
			assertNotEmpty(image);
		}
	}

	@Test
	public void testErrorDisplayed() {
		r.setSetting(PointRenderer.ERROR_COLUMN_TOP, 1);
		r.setSetting(PointRenderer.ERROR_COLUMN_BOTTOM, 1);

		AxisRenderer axisRenderer = new LinearRenderer2D();

		DrawingContext context;
		Drawable point;

		// Draw without error bars
		r.setSetting(PointRenderer.ERROR_DISPLAYED, false);
		BufferedImage unset = createTestImage();
		context = new DrawingContext((Graphics2D) unset.getGraphics());
		layout(unset, axisRenderer);
		point = r.getPoint(axis, axisRenderer, row, 0);
		point.draw(context);

		// Draw with error bars
		r.setSetting(PointRenderer.ERROR_DISPLAYED, true);
		BufferedImage set = createTestImage();
		context = new DrawingContext((Graphics2D) set.getGraphics());
		layout(set, axisRenderer);
		point = r.getPoint(axis, axisRenderer, row, 0);
		point.draw(context);

		assertNotEquals(unset, set);
	}

	@Test
	public void testErrorNoAxisRenderer() {
		r.setSetting(PointRenderer.SHAPE, null);

		AxisRenderer axisRenderer = new LinearRenderer2D();

		DrawingContext context;
		Drawable point;

		// Draw error bars
		r.setSetting(PointRenderer.ERROR_DISPLAYED, true);
		BufferedImage image = createTestImage();
		context = new DrawingContext((Graphics2D) image.getGraphics());
		layout(image, axisRenderer);
		point = r.getPoint(axis, null, row, 0);
		point.draw(context);
		assertEmpty(image);
	}
}

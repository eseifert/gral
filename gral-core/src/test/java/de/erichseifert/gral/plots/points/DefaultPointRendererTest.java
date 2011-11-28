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

import static de.erichseifert.gral.TestUtils.assertEquals;
import static de.erichseifert.gral.TestUtils.assertNotEmpty;
import static de.erichseifert.gral.TestUtils.assertNotEquals;
import static de.erichseifert.gral.TestUtils.createTestImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.DrawingContext;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.axes.LinearRenderer2D;
public class DefaultPointRendererTest {
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
		r = new DefaultPointRenderer();
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
		assertEquals(Color.BLACK, r.getSetting(PointRenderer.COLOR));
		// Set
		r.setSetting(PointRenderer.COLOR, Color.RED);
		assertEquals(Color.RED, r.getSetting(PointRenderer.COLOR));
		// Remove
		r.removeSetting(PointRenderer.COLOR);
		assertEquals(Color.BLACK, r.getSetting(PointRenderer.COLOR));
	}

	@Test
	public void testDisplayValue() {
		r.setSetting(PointRenderer.VALUE_DISPLAYED, true);
		assertPointRenderer(r);
	}

	@Test
	public void testDisplayError() {
		r.setSetting(PointRenderer.ERROR_DISPLAYED, true);
		assertPointRenderer(r);
	}

	@Test
	public void testErrorColumns() {
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
		for (int colIndex = -1; colIndex <= table.getColumnCount(); colIndex++) {
			r.setSetting(PointRenderer.ERROR_COLUMN_TOP, colIndex);
			r.setSetting(PointRenderer.ERROR_COLUMN_BOTTOM, colIndex);

			BufferedImage set = createTestImage();
			context = new DrawingContext((Graphics2D) set.getGraphics());
			layout(set, axisRenderer);
			point = r.getPoint(axis, axisRenderer, row, 0);
			point.draw(context);

			String message = String.format("Column %d:", colIndex);
			if (colIndex >= 0 && colIndex < table.getColumnCount() && table.isColumnNumeric(colIndex)) {
				assertNotEquals(message, unset, set);
			}  else {
				assertEquals(message, unset, set);
			}
		}
	}

}

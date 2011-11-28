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

import static de.erichseifert.gral.TestUtils.assertNonEmptyImage;
import static de.erichseifert.gral.TestUtils.assertNotEqual;
import static de.erichseifert.gral.TestUtils.createTestImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.AbstractDrawable;
import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.DrawingContext;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.axes.LinearRenderer2D;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.PointND;

public class AbstractPointRendererTest {
	private static DataTable table;
	private static Row row;
	private static Axis axis;
	private MockPointRenderer r;

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setUpBeforeClass() {
		table = new DataTable(Integer.class);
		table.add(1); // 0
		table.add(2); // 1
		table.add(3); // 2
		table.add(4); // 3
		table.add(5); // 4
		table.add(6); // 5
		table.add(7); // 6
		table.add(8); // 7

		row = new Row(table, 4);

		axis = new Axis();
		axis.setRange(0.0, 10.0);
	}

	private static final class MockPointRenderer extends AbstractPointRenderer {
		public Drawable getPoint(final Axis axis, final AxisRenderer axisRenderer,
				final Row row, final int col) {
			return new AbstractDrawable() {
				public void draw(DrawingContext context) {
					MockPointRenderer renderer = MockPointRenderer.this;
					Graphics2D g = context.getGraphics();

					Shape point = getPointPath(row);
					Comparable<?> cell = row.get(0);
					Number valueObj = (Number) cell;
					double value = valueObj.doubleValue();

					// Calculate positions
					PointND<Double> pointValue = axisRenderer.getPosition(
						axis, value, true, false);
					double posX = pointValue.get(PointND.X);
					double posY = pointValue.get(PointND.Y);

					g.translate(posX, posY);
					Paint paint = renderer.<Paint>getSetting(PointRenderer.COLOR);
					GraphicsUtils.fillPaintedShape(g, point, paint, null);

					boolean displayValueLabel = renderer.<Boolean>getSetting(PointRenderer.VALUE_DISPLAYED);
					if (displayValueLabel) {
						drawValue(context, point, valueObj);
					}

					boolean displayErrorBars = renderer.<Boolean>getSetting(PointRenderer.ERROR_DISPLAYED);
					if (displayErrorBars) {
						drawError(context, point, value, 2.0, 2.0, axis, axisRenderer);
					}

					g.translate(-posX, -posY);
				}
			};
		}

		public Shape getPointPath(Row row) {
			return new Rectangle2D.Double(-1.3, -1.3, 3.0, 3.0);
		}
	}

	@Before
	public void setUp() {
		r = new MockPointRenderer();
	}

	@Test
	public void testDraw() {
		AxisRenderer axisRenderer = new LinearRenderer2D();
		// TODO Layout axis by setting shape in renderer

		// Get line
		Drawable point = r.getPoint(axis, axisRenderer, row, 0);
		assertNotNull(point);

		// Draw line
		BufferedImage image = createTestImage();
		DrawingContext context = new DrawingContext((Graphics2D) image.getGraphics());
		layout(image, axisRenderer);
		point.draw(context);
		assertNonEmptyImage(image);
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
	public void testValueRendering() {
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

		assertNotEqual(unset, set);
	}

	@Test
	public void testErrorRendering() {
		AxisRenderer axisRenderer = new LinearRenderer2D();

		DrawingContext context;
		Drawable point;

		// Draw without value labels
		BufferedImage unset = createTestImage();
		context = new DrawingContext((Graphics2D) unset.getGraphics());
		layout(unset, axisRenderer);
		r.setSetting(PointRenderer.ERROR_DISPLAYED, false);
		point = r.getPoint(axis, axisRenderer, row, 0);
		point.draw(context);

		// Draw with value labels
		BufferedImage set = createTestImage();
		context = new DrawingContext((Graphics2D) set.getGraphics());
		layout(set, axisRenderer);
		r.setSetting(PointRenderer.ERROR_DISPLAYED, true);
		point = r.getPoint(axis, axisRenderer, row, 0);
		point.draw(context);

		assertNotEqual(unset, set);
	}

	private static void layout(BufferedImage image, AxisRenderer axisRenderer) {
		Line2D axisShape = new Line2D.Double(
			image.getWidth()/2.0, 0.0,
			image.getWidth()/2.0, image.getHeight()
		);
		axisRenderer.setSetting(AxisRenderer.SHAPE, axisShape);
	}
}

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

import static de.erichseifert.gral.TestUtils.assertNotEmpty;
import static de.erichseifert.gral.TestUtils.createTestImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.axes.LinearRenderer2D;
import de.erichseifert.gral.plots.colors.ColorMapper;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.PointND;

public class AbstractPointRendererTest {
	private static final double DELTA = TestUtils.DELTA;

	private static DataTable table;
	private static Row row;
	private static Axis axis;
	private static AxisRenderer axisRenderer;
	private static PointData data;
	private MockPointRenderer r;

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setUpBeforeClass() {
		table = new DataTable(Integer.class, String.class);
		table.add(1, "Jan"); // 0
		table.add(2, "Feb"); // 1
		table.add(3, "Mar"); // 2
		table.add(4, "Apr"); // 3
		table.add(5, "May"); // 4
		table.add(6, "Jun"); // 5
		table.add(7, "Jul"); // 6
		table.add(8, "Aug"); // 7

		row = new Row(table, 4);

		axis = new Axis(0.0, 10.0);

		axisRenderer = new LinearRenderer2D();
		axisRenderer.setShape(new Line2D.Double(-5.0, 0.0, 5.0, 0.0));

		data = new PointData(
			Arrays.asList(null, axis),
			Arrays.asList(null, axisRenderer),
			row, row.getIndex(), 0);
	}

	private static final class MockPointRenderer extends AbstractPointRenderer {
		private static final long serialVersionUID = -3361506388079000948L;

		@Override
		public Drawable getPoint(final PointData data, final Shape shape) {
			return new AbstractDrawable() {
				private static final long serialVersionUID = 8239109584500117586L;

				public void draw(DrawingContext context) {
					MockPointRenderer renderer = MockPointRenderer.this;
					Graphics2D g = context.getGraphics();

					Axis axis = data.axes.get(1);
					AxisRenderer axisRenderer = data.axisRenderers.get(1);
					Comparable<?> cell = data.row.get(0);
					Number valueObj = (Number) cell;
					double value = valueObj.doubleValue();

					double posX = 0.0;
					double posY = 0.0;

					// Calculate positions
					if (axisRenderer != null) {
						PointND<Double> pointValue = axisRenderer.getPosition(
							axis, value, true, false);
						posX = pointValue.get(PointND.X);
						posY = pointValue.get(PointND.Y);

						g.translate(posX, posY);
						ColorMapper colors = renderer.getColor();
						Paint paint = colors.get(data.index);
						GraphicsUtils.fillPaintedShape(g, shape, paint, null);
					}

					if (axisRenderer != null) {
						g.translate(-posX, -posY);
					}
				}
			};
		}

		public Shape getPointShape(PointData data) {
			return new Rectangle2D.Double(-1.3, -1.3, 3.0, 3.0);
		}

		public Drawable getValue(PointData data, Shape shape) {
			// TODO
			return null;
		}
	}

	@Before
	public void setUp() {
		r = new MockPointRenderer();

		r.setColor(Color.RED);

		r.setValueVisible(true);
		r.setValueAlignmentX(0.0);
		r.setValueAlignmentY(0.0);
		r.setValueRotation(90.0);
		r.setValueDistance(1.0);
		r.setValueColor(Color.BLUE);
		r.setValueFont(Font.decode(null).deriveFont(42f));
		r.setValueFormat(NumberFormat.getNumberInstance());

		r.setErrorVisible(true);
		r.setErrorShape(new Line2D.Double(-1.0, 0.0, 1.0, 0.0));
		r.setErrorColor(Color.BLACK);
		r.setErrorStroke(new BasicStroke(1.5f));
	}

	private static void layout(BufferedImage image, AxisRenderer axisRenderer) {
		Line2D axisShape = new Line2D.Double(
			image.getWidth()/2.0, 0.0,
			image.getWidth()/2.0, image.getHeight()
		);
		axisRenderer.setShape(axisShape);
	}

	@Test
	public void testDraw() {
		AxisRenderer axisRenderer = new LinearRenderer2D();

		// Get point
		Drawable point = r.getPoint(data, r.getPointShape(data));
		assertNotNull(point);

		// Draw point
		BufferedImage image = createTestImage();
		DrawingContext context = new DrawingContext((Graphics2D) image.getGraphics());
		layout(image, axisRenderer);
		point.draw(context);
		assertNotEmpty(image);
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		PointRenderer original = r;
		PointRenderer deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(original.getShape(), deserialized.getShape());
		assertEquals(original.getColor(), deserialized.getColor());

		assertEquals(original.isValueVisible(), deserialized.isValueVisible());
		assertEquals(original.getValueColumn(), deserialized.getValueColumn());
		assertEquals(original.getValueFormat(), deserialized.getValueFormat());
		assertEquals(original.getValueLocation(), deserialized.getValueLocation());
		assertEquals(original.getValueAlignmentX(), deserialized.getValueAlignmentX(), DELTA);
		assertEquals(original.getValueAlignmentY(), deserialized.getValueAlignmentY(), DELTA);
		assertEquals(original.getValueRotation(), deserialized.getValueRotation(), DELTA);
		assertEquals(original.getValueDistance(), deserialized.getValueDistance(), DELTA);
		assertEquals(original.getValueColor(), deserialized.getValueColor());
		assertEquals(original.getValueFont(), deserialized.getValueFont());

		assertEquals(original.isErrorVisible(), deserialized.isErrorVisible());
		assertEquals(original.getErrorColumnTop(), deserialized.getErrorColumnTop());
		assertEquals(original.getErrorColumnBottom(), deserialized.getErrorColumnBottom());
		assertEquals(original.getErrorColor(), deserialized.getErrorColor());
		TestUtils.assertEquals(original.getErrorShape(), deserialized.getErrorShape());
		assertEquals(original.getErrorStroke(), deserialized.getErrorStroke());
    }
}

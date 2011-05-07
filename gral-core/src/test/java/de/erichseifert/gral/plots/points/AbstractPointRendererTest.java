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
import static org.junit.Assert.assertNotNull;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

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

public class AbstractPointRendererTest {
	private static DataTable table;
	private static Row row;
	private TestPointRenderer r;

	@BeforeClass
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

		row = new Row(table, 0);
	}

	private static final class TestPointRenderer extends AbstractPointRenderer {
		public Drawable getPoint(final Axis axis, final AxisRenderer axisRenderer,
				final Row row) {
			return new AbstractDrawable() {
				public void draw(DrawingContext context) {
					Shape point = getPointPath(row);
					Number value = row.get(0);
					drawValue(context, point, value);
					drawError(context, point, value.doubleValue(),
							1.0, 0.0, axis, axisRenderer);
				}
			};
		}

		public Shape getPointPath(Row row) {
			return new Rectangle2D.Double(0.0, 0.0, 1.0, 1.0);
		}
	}

	@Before
	public void setUp() {
		r = new TestPointRenderer();
	}

	@Test
	public void testDraw() {
		Axis axis = new Axis(0.0, 1.0);
		AxisRenderer axisRenderer = new LinearRenderer2D();
		// Get line
		Drawable point = r.getPoint(axis, axisRenderer, row);
		assertNotNull(point);

		// Draw line
		BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);
		DrawingContext context = new DrawingContext((Graphics2D) image.getGraphics());
		point.draw(context);
		// TODO Assert something
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

}

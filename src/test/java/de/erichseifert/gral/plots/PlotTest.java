/* GRAL : a free graphing library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.plots;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;


import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.PlotArea2D;
import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.Label;
import de.erichseifert.gral.plots.Plot;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer2D;
import de.erichseifert.gral.plots.axes.LinearRenderer2D;
import de.erichseifert.gral.util.Insets2D;

public class PlotTest {
	private static final double DELTA = 1e-15;
	private static DataSeries series1, series2;
	private Plot plot;
	private boolean isDrawn;

	@BeforeClass
	public static void setUpBeforeClass() {
		DataTable table = new DataTable(Integer.class, Integer.class, Integer.class);
		table.add(1, 3, 5); // 0
		table.add(2, 8, 2); // 1
		table.add(3, 5, 6); // 2
		table.add(4, 6, 2); // 3
		table.add(5, 4, 1); // 4
		table.add(6, 9, 5); // 5
		table.add(7, 8, 7); // 6
		table.add(8, 1, 9); // 7
		
		series1 = new DataSeries("series1", table, 0, 1);
		series2 = new DataSeries("series2", table, 1, 2);
	}

	@Before
	public void setUp() {
		plot = new Plot(series1, series2) {
			@Override
			public void draw(Graphics2D g2d) {
				super.draw(g2d);
				isDrawn = true;
			}
		};
	}

	@Test
	public void testBounds() {
		Rectangle2D bounds = plot.getBounds();
		assertEquals(0.0, bounds.getX(), DELTA);
		assertEquals(0.0, bounds.getY(), DELTA);
		assertEquals(0.0, bounds.getWidth(), DELTA);
		assertEquals(0.0, bounds.getHeight(), DELTA);
		assertEquals(bounds.getX(), plot.getX(), DELTA);
		assertEquals(bounds.getY(), plot.getY(), DELTA);
		assertEquals(bounds.getWidth(), plot.getWidth(), DELTA);
		assertEquals(bounds.getHeight(), plot.getHeight(), DELTA);
	}

	@Test
	public void testInsets() {
		Insets2D insets = plot.getInsets();
		assertEquals(0.0, insets.getTop(), DELTA);
		assertEquals(0.0, insets.getLeft(), DELTA);
		assertEquals(0.0, insets.getBottom(), DELTA);
		assertEquals(0.0, insets.getRight(), DELTA);
	}

	@Test
	public void testTitle() {
		Label title = plot.getTitle();
		assertNotNull(title);
	}

	@Test
	public void testPlotArea() {
		PlotArea2D plotArea = plot.getPlotArea();
		assertNull(plotArea);
	}

	@Test
	public void testLegend() {
		// Get
		assertNull(plot.getLegend());
	}

	@Test
	public void testAxis() {
		// Get
		assertNull(plot.getAxis("a"));
		assertNull(plot.getAxis("b"));
		// Set
		AxisRenderer2D renderer = new LinearRenderer2D();
		Axis a = new Axis(0.0, 1.0);
		Axis b = new Axis(2.0, 3.0);
		plot.setAxis("a", a, renderer.getRendererComponent(a));
		plot.setAxis("b", b, renderer.getRendererComponent(b));
		assertEquals(a, plot.getAxis("a"));
		assertEquals(b, plot.getAxis("b"));
		// Remove
		plot.removeAxis("a");
		plot.setAxis("b", null, null);
		assertNull(plot.getAxis("a"));
		assertNull(plot.getAxis("b"));
	}

	@Test
	public void testSettings() {
		// Get
		assertNull(plot.getSetting(Plot.KEY_TITLE));

		// Set
		plot.setSetting(Plot.KEY_TITLE, "foobar");
		assertEquals("foobar", plot.<String>getSetting(Plot.KEY_TITLE));

		// Remove
		plot.removeSetting(Plot.KEY_TITLE);
		assertNull(plot.getSetting(Plot.KEY_TITLE));
	}

	@Test
	public void testDraw() {
		plot.setSetting(Plot.KEY_TITLE, "foobar");
		plot.setSetting(Plot.KEY_BACKGROUND, Color.WHITE);
		plot.setSetting(Plot.KEY_BORDER, new BasicStroke(1f));

		BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		plot.setBounds(0.0, 0.0, image.getWidth(), image.getHeight());
		plot.draw(g2d);
		assertTrue(isDrawn);
	}

}

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
package de.erichseifert.gral.plots;

import static de.erichseifert.gral.TestUtils.assertNotEmpty;
import static de.erichseifert.gral.TestUtils.createTestImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DummyData;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.plots.axes.AxisRenderer;

public class XYPlotTest {
	private List<MockXYPlot> plots;

	private static class MockXYPlot extends XYPlot {
		private boolean drawn;

		public MockXYPlot(DataSource... data) {
			super(data);
		}

		@Override
		public void draw(DrawingContext context) {
			super.draw(context);
			drawn = true;
		}

		public boolean isDrawn() {
			return drawn;
		}
	};

	@Before
	public void setUp() {
		DataSource data = new DummyData(2, 2, 1.0);

		plots = new LinkedList<MockXYPlot>();
		MockXYPlot plot;

		// XYPlot with all options turned on
		plot = new MockXYPlot(data);
		plot.setSetting(XYPlot.TITLE, "foobar");
		plot.setSetting(XYPlot.XYPlotArea2D.GRID_MAJOR_X, true);
		plot.setSetting(XYPlot.XYPlotArea2D.GRID_MINOR_X, true);
		plot.setSetting(XYPlot.LEGEND, true);
		plot.getAxisRenderer(XYPlot.AXIS_X).setSetting(AxisRenderer.TICKS_SPACING, 0.2);
		plot.getAxisRenderer(XYPlot.AXIS_Y).setSetting(AxisRenderer.TICKS_SPACING, 0.2);
		plots.add(plot);

		plot = new MockXYPlot(data);
		plot.setSetting(XYPlot.TITLE, null);
		plot.setSetting(XYPlot.XYPlotArea2D.GRID_MAJOR_X, false);
		plot.setSetting(XYPlot.XYPlotArea2D.GRID_MINOR_X, false);
		plot.setSetting(XYPlot.LEGEND, false);
		plot.getAxisRenderer(XYPlot.AXIS_X).setSetting(AxisRenderer.TICKS_SPACING, 0.0);
		plot.getAxisRenderer(XYPlot.AXIS_Y).setSetting(AxisRenderer.TICKS_SPACING, 0.0);
		plots.add(plot);
	}

	@Test
	public void testSettings() {
		for (MockXYPlot plot : plots) {
			// Get
			assertNull(plot.getSetting(Plot.BACKGROUND));
			// Set
			plot.setSetting(Plot.BACKGROUND, Color.WHITE);
			assertEquals(Color.WHITE, plot.<String>getSetting(Plot.BACKGROUND));
			// Remove
			plot.removeSetting(Plot.BACKGROUND);
			assertNull(plot.getSetting(Plot.BACKGROUND));
		}
	}

	@Test
	public void testDraw() {
		for (MockXYPlot plot : plots) {
			BufferedImage image = createTestImage();
			plot.setBounds(0.0, 0.0, image.getWidth(), image.getHeight());
			DrawingContext context = new DrawingContext((Graphics2D) image.getGraphics());
			plot.draw(context);
			assertTrue(plot.isDrawn());
			assertNotEmpty(image);
		}
	}

}

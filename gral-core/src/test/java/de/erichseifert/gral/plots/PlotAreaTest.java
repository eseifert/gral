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
package de.erichseifert.gral.plots;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.DrawingContext;
import de.erichseifert.gral.PlotArea;

public class PlotAreaTest {
	private TestPlotArea2D plotArea;

	private static final class TestPlotArea2D extends PlotArea {
		public boolean isDrawn;

		public void draw(DrawingContext context) {
			drawBackground(context);
			drawBorder(context);
			drawPlot(context);
		}
		@Override
		protected void drawPlot(DrawingContext context) {
			isDrawn = true;
		}
	};

	@Before
	public void setUp() {
		plotArea = new TestPlotArea2D();
	}

	@Test
	public void testSettings() {
		// Get
		assertEquals(Color.WHITE, plotArea.getSetting(PlotArea.BACKGROUND));
		// Set
		plotArea.setSetting(PlotArea.BACKGROUND, "foobar");
		assertEquals("foobar", plotArea.<String>getSetting(PlotArea.BACKGROUND));
		// Remove
		plotArea.removeSetting(PlotArea.BACKGROUND);
		assertNotNull(plotArea.getSetting(PlotArea.BACKGROUND));
	}

	@Test
	public void testDraw() {
		plotArea.setSetting(Plot.BACKGROUND, Color.WHITE);
		plotArea.setSetting(Plot.BORDER, new BasicStroke(1f));

		BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);
		plotArea.setBounds(0.0, 0.0, image.getWidth(), image.getHeight());
		DrawingContext context = new DrawingContext((Graphics2D) image.getGraphics());
		plotArea.draw(context);
		assertTrue(plotArea.isDrawn);
	}

}

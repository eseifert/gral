/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2026 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.gral.plots;

import static de.erichseifert.gral.TestUtils.assertNotEmpty;
import static de.erichseifert.gral.TestUtils.createTestImage;
import static org.junit.Assert.assertTrue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.graphics.DrawingContext;

public class PlotAreaTest {
	private MockPlotArea2D plotArea;

	private static final class MockPlotArea2D extends PlotArea {
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
	}

	@Before
	public void setUp() {
		plotArea = new MockPlotArea2D();
	}

	@Test
	public void testDraw() {
		plotArea.setBackground(Color.WHITE);
		plotArea.setBorderStroke(new BasicStroke(1f));

		BufferedImage image = createTestImage();
		plotArea.setBounds(0.0, 0.0, image.getWidth(), image.getHeight());
		var context = new DrawingContext((Graphics2D) image.getGraphics());
		plotArea.draw(context);
		assertTrue(plotArea.isDrawn);
		assertNotEmpty(image);
	}
}

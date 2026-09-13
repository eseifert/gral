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
package de.erichseifert.gral.plots.legends;

import static de.erichseifert.gral.TestUtils.assertNotEmpty;
import static de.erichseifert.gral.TestUtils.createTestImage;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DummyData;
import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;

public class LegendTest {
	private static final double DELTA = 1e-10;
	private MockLegend legend;

	private static class MockLegend extends SeriesLegend {
		private boolean isDrawn;

		@Override
		protected Drawable getSymbol(DataSource data) {
			return new AbstractDrawable() {
				public void draw(DrawingContext context) {
					isDrawn = true;
				}
			};
		}
	}

	@Before
	public void setUp() {
		legend = new MockLegend();
	}

	@Test
	public void testDataSources() {
		var source = new DummyData(1, 1, 1.0);
		assertFalse(legend.contains(source));
		legend.add(source);
		assertTrue(legend.contains(source));
		legend.remove(source);
		assertFalse(legend.contains(source));
	}

	@Test
	public void testDraw() {
		legend.setBackground(Color.WHITE);
		legend.setBorderStroke(new BasicStroke(1f));
		legend.add(new DummyData(1, 1, 1.0));

		BufferedImage image = createTestImage();
		legend.setBounds(0.0, 0.0, image.getWidth(), image.getHeight());
		var context = new DrawingContext((Graphics2D) image.getGraphics());
		legend.draw(context);
		assertTrue(legend.isDrawn);
		assertNotEmpty(image);
	}
}

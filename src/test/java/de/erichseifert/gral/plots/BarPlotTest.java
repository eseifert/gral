/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.DrawingContext;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DummyData;
import de.erichseifert.gral.plots.axes.Axis;

public class BarPlotTest {
	private BarPlot plot;
	private boolean isDrawn;

	@Before
	public void setUp() {
		DataSource data = new DummyData(2, 1, 1.0);
		plot = new BarPlot(data) {
			@Override
			public void draw(DrawingContext context) {
				super.draw(context);
				isDrawn = true;
			}
		};
	}

	@Test
	public void testSettings() {
		// Get
		assertNull(plot.getSetting(Plot.BACKGROUND));

		// Set
		plot.setSetting(Plot.BACKGROUND, Color.WHITE);
		assertEquals(Color.WHITE, plot.<String>getSetting(Plot.BACKGROUND));

		// Remove
		plot.removeSetting(Plot.BACKGROUND);
		assertNull(plot.getSetting(Plot.BACKGROUND));
	}

	@Test
	public void testDraw() {
		plot.getAxis(Axis.X).setRange(-1.0, 3.0);
		plot.getAxis(Axis.Y).setRange(-1.0, 2.0);
		BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);
		plot.setBounds(0.0, 0.0, image.getWidth(), image.getHeight());
		DrawingContext context = new DrawingContext((Graphics2D) image.getGraphics());
		plot.draw(context);
		assertTrue(isDrawn);
	}

}

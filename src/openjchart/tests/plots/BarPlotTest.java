/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.tests.plots;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import openjchart.data.DataSource;
import openjchart.data.DummyData;
import openjchart.plots.BarPlot;
import openjchart.plots.Plot;
import openjchart.plots.axes.Axis;

import org.junit.Before;
import org.junit.Test;

public class BarPlotTest {
	private BarPlot plot;
	private boolean isDrawn;

	@Before
	public void setUp() {
		DataSource data = new DummyData(2, 1, 1.0);
		plot = new BarPlot(data) {
			@Override
			public void draw(Graphics2D g2d) {
				super.draw(g2d);
				isDrawn = true;
			}
		};
	}

	@Test
	public void testSettings() {
		// Get
		assertNull(plot.getSetting(Plot.KEY_BACKGROUND));

		// Set
		plot.setSetting(Plot.KEY_BACKGROUND, Color.WHITE);
		assertEquals(Color.WHITE, plot.<String>getSetting(Plot.KEY_BACKGROUND));

		// Remove
		plot.removeSetting(Plot.KEY_BACKGROUND);
		assertNull(plot.getSetting(Plot.KEY_BACKGROUND));
	}

	@Test
	public void testDraw() {
		plot.getAxis(Axis.X).setRange(-1.0, 3.0);
		plot.getAxis(Axis.Y).setRange(-1.0, 2.0);
		BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		plot.setBounds(0.0, 0.0, image.getWidth(), image.getHeight());
		plot.draw(g2d);
		assertTrue(isDrawn);
	}

}

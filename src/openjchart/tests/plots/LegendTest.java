/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import openjchart.Drawable;
import openjchart.Legend;
import openjchart.data.DataSource;
import openjchart.data.DummyData;

import org.junit.Before;
import org.junit.Test;

public class LegendTest {
	private Legend legend;
	private boolean isDrawn;

	@Before
	public void setUp() {
		legend = new Legend() {
			@Override
			protected void drawSymbol(Graphics2D g2d, Drawable symbol, DataSource data) {
				isDrawn = true;
			}
		};
	}

	@Test
	public void testDataSources() {
		DataSource source = new DummyData(1, 1, 1.0);
		assertFalse(legend.contains(source));
		legend.add(source);
		assertTrue(legend.contains(source));
		legend.remove(source);
		assertFalse(legend.contains(source));
	}

	@Test
	public void testSettings() {
		// Get
		assertEquals(Color.WHITE, legend.getSetting(Legend.KEY_BACKGROUND));

		// Set
		legend.setSetting(Legend.KEY_BACKGROUND, Color.RED);
		assertEquals(Color.RED, legend.<String>getSetting(Legend.KEY_BACKGROUND));

		// Remove
		legend.removeSetting(Legend.KEY_BACKGROUND);
		assertEquals(Color.WHITE, legend.getSetting(Legend.KEY_BACKGROUND));
	}

	@Test
	public void testDraw() {
		legend.setSetting(Legend.KEY_BACKGROUND, Color.WHITE);
		legend.setSetting(Legend.KEY_BORDER, new BasicStroke(1f));
		legend.add(new DummyData(1, 1, 1.0));
		
		BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		legend.setBounds(0.0, 0.0, image.getWidth(), image.getHeight());
		legend.draw(g2d);
		assertTrue(isDrawn);
	}

}

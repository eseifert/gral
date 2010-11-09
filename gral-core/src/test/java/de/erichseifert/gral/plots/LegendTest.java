/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2010 Erich Seifert <dev[at]richseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.DrawingContext;
import de.erichseifert.gral.Legend;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DummyData;

public class LegendTest {
	private Legend legend;
	private boolean isDrawn;

	@Before
	public void setUp() {
		legend = new Legend() {
			@Override
			protected void drawSymbol(DrawingContext context,
					Drawable symbol, DataSource data) {
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
		assertEquals(Color.WHITE, legend.getSetting(Legend.BACKGROUND));

		// Set
		legend.setSetting(Legend.BACKGROUND, Color.RED);
		assertEquals(Color.RED, legend.<String>getSetting(Legend.BACKGROUND));

		// Remove
		legend.removeSetting(Legend.BACKGROUND);
		assertEquals(Color.WHITE, legend.getSetting(Legend.BACKGROUND));
	}

	@Test
	public void testDraw() {
		legend.setSetting(Legend.BACKGROUND, Color.WHITE);
		legend.setSetting(Legend.BORDER, new BasicStroke(1f));
		legend.add(new DummyData(1, 1, 1.0));

		BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);
		legend.setBounds(0.0, 0.0, image.getWidth(), image.getHeight());
		DrawingContext context = new DrawingContext((Graphics2D) image.getGraphics());
		legend.draw(context);
		assertTrue(isDrawn);
		assertFalse(isEmpty(image, true));
	}

	protected static boolean isEmpty(BufferedImage image, boolean checkAlpha) {
		int[] data = image.getRaster().getPixels(
				image.getMinX(), image.getMinY(),
				image.getWidth(), image.getHeight(),
				(int[])null);
		for (int i = 0; i < data.length; i++) {
			int argb = data[i];
			if (checkAlpha) {
				if (argb != 0) {
					return false;
				}
			} else {
				if ((argb & 0xFFFFFF) != 0) {
					return false;
				}
			}
		}
		return true;
	}

}

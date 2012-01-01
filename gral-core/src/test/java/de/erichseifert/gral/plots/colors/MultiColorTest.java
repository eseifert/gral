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
package de.erichseifert.gral.plots.colors;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.util.List;

import org.junit.Test;

public class MultiColorTest {
	@Test
	public void testCreation() {
		LinearGradient c = new LinearGradient(Color.WHITE, Color.BLACK);
		List<Color> colors = c.getColors();
		assertEquals(2, colors.size());
		assertEquals(Color.WHITE, colors.get(0));
		assertEquals(Color.BLACK, colors.get(1));
	}

	@Test
	public void testColor() {
		LinearGradient c = new LinearGradient(Color.RED, Color.GREEN, Color.BLUE);
		List<Color> colors = c.getColors();
		assertEquals(3, colors.size());
		assertEquals(new Color(255,   0,   0), c.get(0.00));
		assertEquals(new Color(128, 128,   0), c.get(0.25));
		assertEquals(new Color(  0, 255,   0), c.get(0.50));
		assertEquals(new Color(  0, 128, 128), c.get(0.75));
		assertEquals(new Color(  0,   0, 255), c.get(1.00));
	}
}

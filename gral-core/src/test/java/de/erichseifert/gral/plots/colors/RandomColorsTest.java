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
package de.erichseifert.gral.plots.colors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.awt.Color;

import org.junit.Test;

public class RandomColorsTest {
	private static final double DELTA = 1e-7;

	@Test
	public void testCreation() {
		RandomColors c = new RandomColors();
		float[] expected = new float[] {
			0.00f, 1.00f,  // Hue
			0.75f, 0.25f,  // Saturation
			0.25f, 0.75f   // Brightness
		};
		float[] actual = c.getColorVariance();
		assertEquals(expected.length, actual.length);
		for (int i = 0; i < actual.length; i++) {
			assertEquals(expected[i], actual[i], DELTA);
		}
	}

	@Test
	public void testCreationInt() {
		RandomColors c1 = new RandomColors(0);
		RandomColors c2 = new RandomColors(0);
		for (double x = 0.0; x <= 1.0; x += 0.1) {
			assertEquals(c1.get(x), c2.get(x));
		}
	}

	@Test
	public void testGet() {
		RandomColors c = new RandomColors();

		int STEPS = 10;
		Color[] actual = new Color[STEPS];
		
		// Test two runs in order to hit cache
		for (int run = 0; run < 2; run++) {
			Color prv = null;
			double x = 0.0;
			for (int i = 0; i < STEPS; i++) {
				Color cur = c.get(x);
				if (run == 0) {
					actual[i] = cur;
				} else {
					assertEquals(actual[i], cur);
				}
				assertNotNull(cur);
				assertFalse(cur.equals(prv));
				prv = cur;
				x += 1.0/(double)STEPS;
			}
		}
	}

}

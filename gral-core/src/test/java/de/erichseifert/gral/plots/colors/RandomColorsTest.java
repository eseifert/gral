/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.gral.plots.colors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.awt.Paint;
import java.io.IOException;

import org.junit.Test;

import de.erichseifert.gral.TestUtils;

public class RandomColorsTest {
	private static final float DELTA_FLOAT = 1e-7f;


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
			assertEquals(expected[i], actual[i], DELTA_FLOAT);
		}
	}

	@Test
	public void testCreationInt() {
		RandomColors c1 = new RandomColors(0);
		RandomColors c2 = new RandomColors(0);
		for (int i = 0; i <= 10; i++) {
			assertEquals(c1.get(i), c2.get(i));
		}
	}

	@Test
	public void testGet() {
		RandomColors c = new RandomColors();

		int STEPS = 10;
		Paint[] actual = new Paint[STEPS];

		// Test two runs in order to hit cache
		for (int run = 0; run < 2; run++) {
			Paint prv = null;
			for (int i = 0; i < STEPS; i++) {
				Paint cur = c.get(i);
				if (run == 0) {
					actual[i] = cur;
				} else {
					assertEquals(actual[i], cur);
				}
				assertNotNull(cur);
				assertFalse(cur.equals(prv));
				prv = cur;
			}
		}
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		QuasiRandomColors original = new QuasiRandomColors();
		QuasiRandomColors deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(original.getMode(), deserialized.getMode());
		assertArrayEquals(original.getColorVariance(), deserialized.getColorVariance(), DELTA_FLOAT);
    }
}

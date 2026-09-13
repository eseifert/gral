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
package de.erichseifert.gral.plots.colors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.awt.Color;
import java.awt.Paint;
import java.io.IOException;

import org.junit.Test;

import de.erichseifert.gral.TestUtils;

public class QuasiRandomColorsTest {
	private static final float DELTA_FLOAT = 1e-7f;

	@Test
	public void testGet() {
		var c = new QuasiRandomColors();
		final int STEPS = 10;

		Paint prv = null;
		for (int i = 0; i < STEPS; i++) {
			Paint cur = c.get(i);
			assertNotNull(cur);
			assertFalse(cur.equals(prv));
			prv = cur;
		}
	}

	@Test
	public void testGetIsIndependentOfQueryOrder() {
		var ascending = new QuasiRandomColors();
		var descending = new QuasiRandomColors();
		final int STEPS = 10;

		for (int i = STEPS - 1; i >= 0; i--) {
			descending.get(i);
		}
		for (int i = 0; i < STEPS; i++) {
			assertEquals(ascending.get(i), descending.get(i));
		}
	}

	@Test
	public void testSetHue() {
		var c = new QuasiRandomColors();
		c.setHue(0.25f, 0.75f);
		assertArrayEquals(new float[] {0.25f, 0.50f}, new float[] {
			c.getColorVariance()[0], c.getColorVariance()[1]}, DELTA_FLOAT);
	}

	@Test
	public void testSetSaturation() {
		var c = new QuasiRandomColors();
		c.setSaturation(0.10f, 0.40f);
		assertArrayEquals(new float[] {0.10f, 0.30f}, new float[] {
			c.getColorVariance()[2], c.getColorVariance()[3]}, DELTA_FLOAT);
	}

	@Test
	public void testSetBrightness() {
		var c = new QuasiRandomColors();
		c.setBrightness(0.20f, 0.20f);
		assertArrayEquals(new float[] {0.20f, 0.00f}, new float[] {
			c.getColorVariance()[4], c.getColorVariance()[5]}, DELTA_FLOAT);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetHueWithInvalidRange() {
		new QuasiRandomColors().setHue(0.75f, 0.25f);
	}

	@Test
	public void testSetBrightnessAffectsColorsThatHaveAlreadyBeenQueried() {
		var c = new QuasiRandomColors();
		Paint before = c.get(0);
		c.setBrightness(0.0f, 0.0f);
		assertEquals(Color.BLACK, c.get(0));
		assertFalse(before.equals(c.get(0)));
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		var original = new QuasiRandomColors();
		QuasiRandomColors deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(original.getMode(), deserialized.getMode());
		assertArrayEquals(original.getColorVariance(), deserialized.getColorVariance(), DELTA_FLOAT);
    }
}

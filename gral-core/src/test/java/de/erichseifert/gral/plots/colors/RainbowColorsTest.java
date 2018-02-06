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

import java.awt.Color;
import java.io.IOException;

import org.junit.Test;

import de.erichseifert.gral.TestUtils;

public class RainbowColorsTest {
	private static final float DELTA_FLOAT = 1e-7f;

	@Test
	public void testGet() {
		RainbowColors c = new RainbowColors();
		for (double i = 0.0; i <= 1.0f; i += 0.1f) {
			assertEquals(Color.getHSBColor((float) i, 1f, 1f), c.get(i));
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

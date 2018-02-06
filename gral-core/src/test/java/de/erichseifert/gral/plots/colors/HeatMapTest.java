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

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.io.IOException;

import org.junit.Test;

import de.erichseifert.gral.TestUtils;

public class HeatMapTest {
	@Test
	public void testGet() {
		HeatMap c = new HeatMap();
		assertEquals(new Color(  0,   0,   0), c.get(0.00));
		assertEquals(new Color( 67,  13, 109), c.get(0.25));
		assertEquals(new Color(175,  80,  80), c.get(0.50));
		assertEquals(new Color(242, 188,  93), c.get(0.75));
		assertEquals(new Color(255, 255, 255), c.get(1.00));
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		ScaledContinuousColorMapper original = new HeatMap();
		ScaledContinuousColorMapper deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(original.getMode(), deserialized.getMode());
		for (double x=0.0; x<=1.0; x+=0.5) {
			assertEquals(original.get(x), deserialized.get(x));
		}
    }
}

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

import org.junit.Test;

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
}

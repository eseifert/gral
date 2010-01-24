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

package openjchart.tests.plots.colors;

import static org.junit.Assert.assertEquals;

import java.awt.Color;

import openjchart.plots.colors.RainbowColors;

import org.junit.Test;

public class RainbowColorsTest {
	@Test
	public void testGet() {
		RainbowColors c = new RainbowColors();
		for (float i = 0.0f; i <= 1.0f; i += 0.1f) {
			assertEquals(Color.getHSBColor(i, 1f, 1f), c.get(i));
		}
	}

}

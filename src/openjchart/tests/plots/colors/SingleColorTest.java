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

package openjchart.tests.plots.colors;

import static org.junit.Assert.assertEquals;

import java.awt.Color;

import openjchart.plots.colors.SingleColor;

import org.junit.Test;

public class SingleColorTest {

	@Test
	public void testCreation() {
		SingleColor c = new SingleColor(Color.WHITE);
		assertEquals(Color.WHITE, c.getColor());
	}

	@Test
	public void testColor() {
		SingleColor c = new SingleColor(Color.BLUE);
		// Get
		assertEquals(Color.BLUE, c.getColor());
		// Set
		c.setColor(Color.RED);
		assertEquals(Color.RED, c.getColor());
	}

	@Test
	public void testGet() {
		SingleColor c = new SingleColor(Color.BLUE);
		for (double i = 0.0; i <= 1.0; i += 0.1) {
			assertEquals(Color.BLUE, c.get(i));
		}
	}

}

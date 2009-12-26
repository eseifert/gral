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

package openjchart.tests.plots.axes;

import static org.junit.Assert.assertEquals;
import openjchart.plots.axes.Axis;
import openjchart.plots.axes.LogarithmicRenderer2D;

import org.junit.Before;
import org.junit.Test;

public class LogarithmicRenderer2DTest {
	private Axis axis;
	private LogarithmicRenderer2D renderer;

	@Before
	public void setUp() {
		axis = new Axis(0, 10);
		renderer = new LogarithmicRenderer2D();
	}

	@Test
	public void testWorldToView() {
		double delta = 1e-5;
		double logMaxValue = Math.log(10);
		assertEquals(Double.NEGATIVE_INFINITY, renderer.worldToView(axis, 0, true), delta);
		assertEquals(1.0, renderer.worldToView(axis, 10, true), delta);
		assertEquals(Math.log(5) / logMaxValue, renderer.worldToView(axis, 5, true), delta);
		assertEquals(Math.log(1) / logMaxValue, renderer.worldToView(axis, 1, true), delta);
		assertEquals(Math.log(9) / logMaxValue, renderer.worldToView(axis, 9, true), delta);
		assertEquals(Math.log(0.1) / logMaxValue, renderer.worldToView(axis, 0.1, true), delta);
	}

}

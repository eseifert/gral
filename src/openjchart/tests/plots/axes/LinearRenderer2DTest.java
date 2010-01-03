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
import openjchart.plots.axes.LinearRenderer2D;

import org.junit.Before;
import org.junit.Test;

public class LinearRenderer2DTest {
	private static final double DELTA = 1e-15;
	private Axis axis;
	private LinearRenderer2D renderer;

	@Before
	public void setUp() {
		axis = new Axis(-5, 5);
		renderer = new LinearRenderer2D();
	}

	@Test
	public void testWorldToView() {
		assertEquals( 0.0, renderer.worldToView(axis,  -5.0, false), DELTA);
		assertEquals( 1.0, renderer.worldToView(axis,   5.0, false), DELTA);
		assertEquals( 0.5, renderer.worldToView(axis,   0.0, false), DELTA);
		assertEquals( 0.0, renderer.worldToView(axis, -10.0, false), DELTA);
		assertEquals( 1.0, renderer.worldToView(axis,  10.0, false), DELTA);
		assertEquals( 0.8, renderer.worldToView(axis,   3.0, false), DELTA);

		assertEquals( 0.0, renderer.worldToView(axis,  -5.0, true), DELTA);
		assertEquals( 1.0, renderer.worldToView(axis,   5.0, true), DELTA);
		assertEquals( 0.5, renderer.worldToView(axis,   0.0, true), DELTA);
		assertEquals(-0.5, renderer.worldToView(axis, -10.0, true), DELTA);
		assertEquals( 1.5, renderer.worldToView(axis,  10.0, true), DELTA);
		assertEquals( 0.8, renderer.worldToView(axis,   3.0, true), DELTA);
	}

	@Test
	public void testViewToWorld() {
		assertEquals( -5.0, renderer.viewToWorld(axis,  0.0, false));
		assertEquals(  0.0, renderer.viewToWorld(axis,  0.5, false));
		assertEquals(  3.0, renderer.viewToWorld(axis,  0.8, false));
		assertEquals(  5.0, renderer.viewToWorld(axis,  1.0, false));

		assertEquals(-10.0, renderer.viewToWorld(axis, -0.5, true));
		assertEquals( -5.0, renderer.viewToWorld(axis,  0.0, true));
		assertEquals(  0.0, renderer.viewToWorld(axis,  0.5, true));
		assertEquals(  3.0, renderer.viewToWorld(axis,  0.8, true));
		assertEquals(  5.0, renderer.viewToWorld(axis,  1.0, true));
		assertEquals( 10.0, renderer.viewToWorld(axis,  1.5, true));
	}

}

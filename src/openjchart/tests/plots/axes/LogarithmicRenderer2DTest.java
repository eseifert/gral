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

import java.util.List;

import openjchart.plots.DataPoint2D;
import openjchart.plots.axes.Axis;
import openjchart.plots.axes.LogarithmicRenderer2D;

import org.junit.Before;
import org.junit.Test;

public class LogarithmicRenderer2DTest {
	private static final double DELTA = 1e-13;
	private Axis axis;
	private LogarithmicRenderer2D renderer;

	@Before
	public void setUp() {
		axis = new Axis(0, 10);
		renderer = new LogarithmicRenderer2D();
	}

	@Test
	public void testWorldToView() {
		assertEquals(Double.NEGATIVE_INFINITY, renderer.worldToView(axis, 0.0, true), DELTA);
		assertEquals(Math.log10( 0.1), renderer.worldToView(axis,  0.1, true), DELTA);
		assertEquals(Math.log10( 1.0), renderer.worldToView(axis,  1.0, true), DELTA);
		assertEquals(Math.log10( 5.0), renderer.worldToView(axis,  5.0, true), DELTA);
		assertEquals(Math.log10( 9.0), renderer.worldToView(axis,  9.0, true), DELTA);
		assertEquals(Math.log10(10.0), renderer.worldToView(axis, 10.0, true), DELTA);
	}

	@Test
	public void testViewToWorld() {
		assertEquals( 0.00, renderer.viewToWorld(axis, Double.NEGATIVE_INFINITY, true).doubleValue(), DELTA);
		assertEquals( 0.01, renderer.viewToWorld(axis, Math.log10( 0.01), true).doubleValue(), DELTA);
		assertEquals( 0.10, renderer.viewToWorld(axis, Math.log10( 0.10), true).doubleValue(), DELTA);
		assertEquals( 1.00, renderer.viewToWorld(axis, Math.log10( 1.00), true).doubleValue(), DELTA);
		assertEquals( 5.00, renderer.viewToWorld(axis, Math.log10( 5.00), true).doubleValue(), DELTA);
		assertEquals( 9.00, renderer.viewToWorld(axis, Math.log10( 9.00), true).doubleValue(), DELTA);
		assertEquals(10.00, renderer.viewToWorld(axis, Math.log10(10.00), true).doubleValue(), DELTA);
		assertEquals(15.00, renderer.viewToWorld(axis, Math.log10(15.00), true).doubleValue(), DELTA);
	}

	@Test
	public void testTicks() {
		Axis axis = new Axis(0.2, 10);
		List<DataPoint2D> ticks = renderer.getTicks(axis);
		assertEquals(18, ticks.size());
	}

}

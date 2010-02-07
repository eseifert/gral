/* GRAL : a free graphing library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.plots.axes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;


import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.LogarithmicRenderer2D;
import de.erichseifert.gral.plots.axes.Tick2D;

public class LogarithmicRenderer2DTest {
	private static final double DELTA = 1e-14;
	private Axis axis;
	private LogarithmicRenderer2D renderer;

	@Before
	public void setUp() {
		axis = new Axis(0.0, 10.0);
		renderer = new LogarithmicRenderer2D();
	}

	@Test
	public void testDraw() {
		Axis axis = new Axis(0.1, 10.0);
		Drawable d = renderer.getRendererComponent(axis);
		assertNotNull(d);
		BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		d.draw(g2d);
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
		boolean extrapolate = true;
		assertEquals( 0.00, renderer.viewToWorld(axis, Double.NEGATIVE_INFINITY, extrapolate).doubleValue(), DELTA);
		assertEquals( 0.01, renderer.viewToWorld(axis, Math.log10( 0.01), extrapolate).doubleValue(), DELTA);
		assertEquals( 0.10, renderer.viewToWorld(axis, Math.log10( 0.10), extrapolate).doubleValue(), DELTA);
		assertEquals( 1.00, renderer.viewToWorld(axis, Math.log10( 1.00), extrapolate).doubleValue(), DELTA);
		assertEquals( 5.00, renderer.viewToWorld(axis, Math.log10( 5.00), extrapolate).doubleValue(), DELTA);
		assertEquals( 9.00, renderer.viewToWorld(axis, Math.log10( 9.00), extrapolate).doubleValue(), DELTA);
		assertEquals(10.00, renderer.viewToWorld(axis, Math.log10(10.00), extrapolate).doubleValue(), DELTA);
		assertEquals(15.00, renderer.viewToWorld(axis, Math.log10(15.00), extrapolate).doubleValue(), DELTA);

		extrapolate = false;
		assertEquals( 0.00, renderer.viewToWorld(axis, Double.NEGATIVE_INFINITY, extrapolate).doubleValue(), DELTA);
		assertEquals( 0.00, renderer.viewToWorld(axis, Math.log10( 0.01), extrapolate).doubleValue(), DELTA);
		assertEquals( 0.00, renderer.viewToWorld(axis, Math.log10( 0.10), extrapolate).doubleValue(), DELTA);
		assertEquals( 0.00, renderer.viewToWorld(axis, Math.log10( 1.00), extrapolate).doubleValue(), DELTA);
		assertEquals( 5.00, renderer.viewToWorld(axis, Math.log10( 5.00), extrapolate).doubleValue(), DELTA);
		assertEquals( 9.00, renderer.viewToWorld(axis, Math.log10( 9.00), extrapolate).doubleValue(), DELTA);
		assertEquals(10.00, renderer.viewToWorld(axis, Math.log10(10.00), extrapolate).doubleValue(), DELTA);
		assertEquals(10.00, renderer.viewToWorld(axis, Math.log10(15.00), extrapolate).doubleValue(), DELTA);
	}

	@Test
	public void testTicks() {
		Axis axis = new Axis(0.2, 10.0);
		List<Tick2D> ticks = renderer.getTicks(axis);
		assertEquals(36, ticks.size());  // 18 major ticks, 18 minor ticks
	}

}

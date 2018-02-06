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
package de.erichseifert.gral.plots.axes;

import static de.erichseifert.gral.TestUtils.assertNotEmpty;
import static de.erichseifert.gral.TestUtils.createTestImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;

public class LogarithmicRenderer2DTest {
	private static final double DELTA = 1e-14;
	private Axis axis;
	private AxisRenderer renderer;

	@Before
	public void setUp() {
		axis = new Axis(0.0, 10.0);
		renderer = new LogarithmicRenderer2D();
	}

	@Test
	public void testDraw() {
		Axis axis = new Axis();
		axis.setRange(0.1, 10.0);
		Drawable d = renderer.getRendererComponent(axis);
		assertNotNull(d);
		BufferedImage image = createTestImage();
		DrawingContext context = new DrawingContext((Graphics2D) image.getGraphics());
		d.draw(context);
		assertNotEmpty(image);
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
	public void testViewToView() {
		double[] values = {0.01, 0.10, 1.00, 5.00, 9.00, 10.00, 15.00};
		for (double v: values) {
			Number world = renderer.viewToWorld(axis, v, true);
			double view = renderer.worldToView(axis, world, true);
			assertEquals(v, view, DELTA);
		}
	}

	@Test
	public void testWorldToWorld() {
		double[] values = {0.01, 0.10, 1.00, 5.00, 9.00, 10.00, 15.00};
		for (double v: values) {
			double view = renderer.worldToView(axis, v, true);
			double world = renderer.viewToWorld(axis, view, true).doubleValue();
			assertEquals(v, world, DELTA);
		}
	}

	@Test
	public void testTicks() {
		Axis axis = new Axis(0.2, 10.0);
		List<Tick> ticks = renderer.getTicks(axis);
		assertEquals(36, ticks.size());  // 18 major ticks, 18 minor ticks
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		AxisRenderer original = renderer;
		@SuppressWarnings("unused")
		AxisRenderer deserialized = TestUtils.serializeAndDeserialize(original);
    }
}

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

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;

public class LinearRenderer2DTest {
	private static final double DELTA = 1e-15;
	private Axis axis;
	private AxisRenderer renderer;

	@Before
	public void setUp() {
		axis = new Axis();
		axis.setRange(-5.0, 5.0);
		renderer = new LinearRenderer2D();
	}

	@Test
	public void testDraw() {
		Drawable d = renderer.getRendererComponent(axis);
		assertNotNull(d);
		BufferedImage image = createTestImage();
		DrawingContext context = new DrawingContext((Graphics2D) image.getGraphics());
		d.draw(context);
		assertNotEmpty(image);
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

	@Test
	public void testViewToView() {
		double[] values = {-0.5, 0.0, 0.5, 0.8, 1.0, 1.5};
		for (double v: values) {
			Number world = renderer.viewToWorld(axis, v, true);
			double view = renderer.worldToView(axis, world, true);
			assertEquals(v, view, DELTA);
		}
	}

	@Test
	public void testWorldToWorld() {
		double[] values = {-0.5, 0.0, 0.5, 0.8, 1.0, 1.5};
		for (double v: values) {
			double view = renderer.worldToView(axis, v, true);
			double world = renderer.viewToWorld(axis, view, true).doubleValue();
			assertEquals(v, world, DELTA);
		}
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		AxisRenderer original = renderer;
		@SuppressWarnings("unused")
		AxisRenderer deserialized = TestUtils.serializeAndDeserialize(original);
    }
}

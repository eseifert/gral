/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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
package de.erichseifert.gral.plots.areas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.DrawingContext;
import de.erichseifert.gral.plots.DataPoint;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.axes.LinearRenderer2D;
import de.erichseifert.gral.util.PointND;

public class LineAreaRendererTest {
	private Axis axis;
	private AxisRenderer axisRenderer;

	@Before
	public void setUp() {
		axis = new Axis();
		axis.setRange(-5.0, 5.0);
		axisRenderer = new LinearRenderer2D();
	}

	@Test
	public void testArea() {
		// Get line
		AreaRenderer r = new LineAreaRenderer2D();
		List<DataPoint> points = Arrays.asList(
			new DataPoint(new PointND<Double>(0.0, 0.0), null, null),
			new DataPoint(new PointND<Double>(1.0, 1.0), null, null)
		);
		Drawable area = r.getArea(axis, axisRenderer, points);
		assertNotNull(area);

		// Draw area
		BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);
		DrawingContext context = new DrawingContext((Graphics2D) image.getGraphics());
		area.draw(context);
		// TODO Assert something
	}

	@Test
	public void testSettings() {
		// Get
		AreaRenderer r = new DefaultAreaRenderer2D();
		assertEquals(Color.GRAY, r.getSetting(AreaRenderer.COLOR));
		// Set
		r.setSetting(AreaRenderer.COLOR, Color.RED);
		assertEquals(Color.RED, r.getSetting(AreaRenderer.COLOR));
		// Remove
		r.removeSetting(AreaRenderer.COLOR);
		assertEquals(Color.GRAY, r.getSetting(AreaRenderer.COLOR));
	}

}

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
package de.erichseifert.gral.plots.areas;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.plots.DataPoint;

public class AbstractAreaRendererTest {
	private static final double DELTA = TestUtils.DELTA;

	private static class MockAbstractAreaRenderer extends AbstractAreaRenderer {
		public MockAbstractAreaRenderer() {
		}

		@Override
		public Shape getAreaShape(List<DataPoint> points) {
			return new Rectangle2D.Float(0, 0, 10, 10);
		}

		@Override
		public Drawable getArea(List<DataPoint> points, final Shape shape) {
			return new AbstractDrawable() {
				@Override
				public void draw(DrawingContext context) {
					context.getGraphics().draw(shape);
				}
			};
		}
	}

	@Test
	public void testProperties() {
		double gap = 1.23;
		boolean gapRounded = true;
		Color color = Color.RED;

		MockAbstractAreaRenderer r = new MockAbstractAreaRenderer();
		r.setGap(gap);
		r.setGapRounded(gapRounded);
		r.setColor(color);

		assertEquals(gap, r.getGap(), DELTA);
		assertEquals(gapRounded, r.isGapRounded());
		assertEquals(color, r.getColor());
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		AreaRenderer original = new MockAbstractAreaRenderer();
		AreaRenderer deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(original.getGap(), deserialized.getGap(), DELTA);
		assertEquals(original.isGapRounded(), deserialized.isGapRounded());
		assertEquals(original.getColor(), deserialized.getColor());
	}
}

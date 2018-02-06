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
package de.erichseifert.gral.graphics.layout;

import static org.junit.Assert.assertEquals;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.graphics.DrawingContext;
import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.graphics.Location;


public class EdgeLayoutTest {
	private static final double DELTA = 1e-15;
	private static final double GAP_H = 5.0;
	private static final double GAP_V = 10.0;
	private static final double COMP_WIDTH = 10.0;
	private static final double COMP_HEIGHT = 5.0;

	private DrawableContainer container;
	private EdgeLayout layout;
	private Drawable nn, nw, ww, sw, ss, se, ee, ne, ce;

	private static final class TestDrawable extends AbstractDrawable {
		/** Version id for serialization. */
		private static final long serialVersionUID = -8968220580916982445L;

		public void draw(DrawingContext context) {
		}

		@Override
		public Dimension2D getPreferredSize() {
			Dimension2D size = super.getPreferredSize();
			size.setSize(COMP_WIDTH, COMP_HEIGHT);
			return size;
		}
	}

	@Before
	public void setUp() {
		layout = new EdgeLayout(GAP_H, GAP_V);

		container = new DrawableContainer(null);

		nn = new TestDrawable();
		nw = new TestDrawable();
		ww = new TestDrawable();
		sw = new TestDrawable();
		ss = new TestDrawable();
		se = new TestDrawable();
		ee = new TestDrawable();
		ne = new TestDrawable();
		ce = new TestDrawable();

		container.add(nn, Location.NORTH);
		container.add(nw, Location.NORTH_WEST);
		container.add(ww, Location.WEST);
		container.add(sw, Location.SOUTH_WEST);
		container.add(ss, Location.SOUTH);
		container.add(se, Location.SOUTH_EAST);
		container.add(ee, Location.EAST);
		container.add(ne, Location.NORTH_EAST);
		container.add(ce, Location.CENTER);
	}

	@Test
	public void testCreate() {
		EdgeLayout noGap = new EdgeLayout();
		assertEquals(0.0, noGap.getGapX(), DELTA);
		assertEquals(0.0, noGap.getGapY(), DELTA);

		EdgeLayout gapped = new EdgeLayout(GAP_H, GAP_V);
		assertEquals(GAP_H, gapped.getGapX(), DELTA);
		assertEquals(GAP_V, gapped.getGapY(), DELTA);
	}

	@Test
	public void testPreferredSize() {
		Dimension2D size = layout.getPreferredSize(container);
		assertEquals(3.0*COMP_WIDTH + 2.0*GAP_H, size.getWidth(), DELTA);
		assertEquals(3.0*COMP_HEIGHT + 2.0*GAP_V, size.getHeight(), DELTA);
	}

	@Test
	public void testLayout() {
		Rectangle2D bounds = new Rectangle2D.Double(5.0, 5.0, 50.0, 50.0);
		container.setBounds(bounds);
		layout.layout(container);

		// Test x coordinates
		assertEquals(bounds.getMinX(), nw.getX(), DELTA);
		assertEquals(bounds.getMinX(), ww.getX(), DELTA);
		assertEquals(bounds.getMinX(), sw.getX(), DELTA);
		assertEquals(bounds.getMinX() + COMP_WIDTH + GAP_H, nn.getX(), DELTA);
		assertEquals(bounds.getMinX() + COMP_WIDTH + GAP_H, ce.getX(), DELTA);
		assertEquals(bounds.getMinX() + COMP_WIDTH + GAP_H, ss.getX(), DELTA);
		assertEquals(bounds.getMaxX() - COMP_WIDTH, ne.getX(), DELTA);
		assertEquals(bounds.getMaxX() - COMP_WIDTH, ee.getX(), DELTA);
		assertEquals(bounds.getMaxX() - COMP_WIDTH, se.getX(), DELTA);
		// Test y coordinates
		assertEquals(bounds.getMinY(), nw.getY(), DELTA);
		assertEquals(bounds.getMinY(), nn.getY(), DELTA);
		assertEquals(bounds.getMinY(), ne.getY(), DELTA);
		assertEquals(bounds.getMinY() + COMP_HEIGHT + GAP_V, ww.getY(), DELTA);
		assertEquals(bounds.getMinY() + COMP_HEIGHT + GAP_V, ce.getY(), DELTA);
		assertEquals(bounds.getMinY() + COMP_HEIGHT + GAP_V, ee.getY(), DELTA);
		assertEquals(bounds.getMaxY() - COMP_HEIGHT, sw.getY(), DELTA);
		assertEquals(bounds.getMaxY() - COMP_HEIGHT, ss.getY(), DELTA);
		assertEquals(bounds.getMaxY() - COMP_HEIGHT, se.getY(), DELTA);

		// TODO Test width and height
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		EdgeLayout original = layout;
		EdgeLayout deserialized = TestUtils.serializeAndDeserialize(original);
	}
}

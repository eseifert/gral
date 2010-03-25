/**
 * GRAL: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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

package de.erichseifert.gral;

import static org.junit.Assert.assertEquals;

import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;


import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.AbstractDrawable;
import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.DrawableContainer;
import de.erichseifert.gral.EdgeLayout;
import de.erichseifert.gral.Layout;
import de.erichseifert.gral.DrawableConstants.Location;

public class EdgeLayoutTest {
	private static final double DELTA = 1e-15;
	private static final double GAP_H = 5.0;
	private static final double GAP_V = 10.0;
	private static final double COMP_WIDTH = 10.0;
	private static final double COMP_HEIGHT = 5.0;

	private DrawableContainer container;
	private Layout layout;
	private Drawable nn, nw, ww, sw, ss, se, ee, ne, ce;

	private static final class TestDrawable extends AbstractDrawable {
		@Override
		public void draw(Graphics2D g2d) {
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
		// TODO: Test width and height
	}

}

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
 * Lesser GNU Lesser General Public License for more details.
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
import de.erichseifert.gral.Layout;
import de.erichseifert.gral.StackedLayout;
import de.erichseifert.gral.DrawableConstants.Orientation;

public class StackedLayoutTest {
	private static final double DELTA = 1e-15;
	private static final Dimension2D GAP = new de.erichseifert.gral.util.Dimension2D.Double(5.0, 10.0);
	private static final double COMP_WIDTH = 10.0;
	private static final double COMP_HEIGHT = 5.0;

	private DrawableContainer container;
	private Drawable a, b, c;

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
		container = new DrawableContainer(null);

		a = new TestDrawable();
		b = new TestDrawable();
		c = new TestDrawable();

		container.add(a);
		container.add(b);
		container.add(c);
	}

	@Test
	public void testPreferredSizeVertical() {
		Layout layout = new StackedLayout(Orientation.VERTICAL, GAP);
		Dimension2D size = layout.getPreferredSize(container);
		assertEquals(COMP_WIDTH, size.getWidth(), DELTA);
		assertEquals(3.0*COMP_HEIGHT + 2.0*GAP.getHeight(), size.getHeight(), DELTA);
	}

	@Test
	public void testPreferredSizeHorizontal() {
		Layout layout = new StackedLayout(Orientation.HORIZONTAL, GAP);
		Dimension2D size = layout.getPreferredSize(container);
		assertEquals(3.0*COMP_WIDTH + 2.0*GAP.getWidth(), size.getWidth(), DELTA);
		assertEquals(COMP_HEIGHT, size.getHeight(), DELTA);
	}

	@Test
	public void testLayoutVertical() {
		Layout layout = new StackedLayout(Orientation.VERTICAL, GAP);
		Rectangle2D bounds = new Rectangle2D.Double(5.0, 5.0, 50.0, 50.0);
		container.setBounds(bounds);
		layout.layout(container);

		// Test x coordinates
		assertEquals(bounds.getMinX(), a.getX(), DELTA);
		assertEquals(bounds.getMinX(), b.getX(), DELTA);
		assertEquals(bounds.getMinX(), c.getX(), DELTA);
		// Test y coordinates
		assertEquals(12.5, a.getY(), DELTA);
		assertEquals(27.5, b.getY(), DELTA);
		assertEquals(42.5, c.getY(), DELTA);
		// TODO: Test width and height
	}

	@Test
	public void testLayoutHorizontal() {
		Layout layout = new StackedLayout(Orientation.HORIZONTAL, GAP);
		Rectangle2D bounds = new Rectangle2D.Double(5.0, 5.0, 50.0, 50.0);
		container.setBounds(bounds);
		layout.layout(container);

		// Test x coordinates
		assertEquals(10.0, a.getX(), DELTA);
		assertEquals(25.0, b.getX(), DELTA);
		assertEquals(40.0, c.getX(), DELTA);
		// Test y coordinates
		assertEquals(bounds.getMinY(), a.getY(), DELTA);
		assertEquals(bounds.getMinY(), b.getY(), DELTA);
		assertEquals(bounds.getMinY(), c.getY(), DELTA);
		// TODO: Test width and height
	}

	@Test
	public void testOrientation() {
		StackedLayout layout;
		// Vertical
		layout = new StackedLayout(Orientation.VERTICAL);
		assertEquals(Orientation.VERTICAL, layout.getOrientation());
		// Horizontal
		layout = new StackedLayout(Orientation.HORIZONTAL);
		assertEquals(Orientation.HORIZONTAL, layout.getOrientation());
	}

	@Test
	public void testGap() {
		StackedLayout layout;
		// Vertical
		layout = new StackedLayout(Orientation.VERTICAL, GAP);
		assertEquals(GAP, layout.getGap());
		// Horizontal
		layout = new StackedLayout(Orientation.HORIZONTAL, GAP);
		assertEquals(GAP, layout.getGap());
	}

}

/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.gral.graphics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.util.Insets2D;


public class ContainerTest {
	private DrawableContainer container;

	private static final class MockDrawable extends AbstractDrawable {
		/** Version id for serialization. */
		private static final long serialVersionUID = 1802598562530415902L;

		private boolean isDrawn;
		private final Dimension2D preferredSize = new de.erichseifert.gral.util.Dimension2D.Double();

		public void draw(DrawingContext context) {
			isDrawn = true;
		}

		@Override
		public Dimension2D getPreferredSize() {
			return preferredSize;
		}
	}

	@Before
	public void setUp() {
		container = new DrawableContainer();
	}

	@Test
	public void testCreate() {
		assertEquals(new Rectangle2D.Double(), container.getBounds());
		assertEquals(new de.erichseifert.gral.util.Dimension2D.Double(), container.getPreferredSize());
		assertEquals(new Insets2D.Double(), container.getInsets());
		assertEquals(null, container.getLayout());
	}

	@Test
	public void testAdd() {
		assertEquals(0, container.size());

		Drawable d = new MockDrawable();
		container.add(d);
		assertEquals(1, container.size());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testAddSelf() {
		container.add(container);
	}

	@Test
	public void testConstraints() {
		Drawable d = new MockDrawable();
		container.add(d, "foo");
		assertEquals("foo", container.getConstraints(d));
	}

	@Test
	public void testRemove() {
		assertEquals(0, container.size());

		Drawable d = new MockDrawable();
		container.add(d);
		assertEquals(1, container.size());

		container.remove(d);
		assertEquals(0, container.size());
	}

	@Test
	public void testDraw() {
		Drawable d = new MockDrawable();
		container.add(d);

		for (Drawable c : container) {
			assertFalse(((MockDrawable) c).isDrawn);
		}

		container.draw(null);

		for (Drawable c : container) {
			assertTrue(((MockDrawable) c).isDrawn);
		}
	}

	@Test
	public void testInsets() {
		assertEquals(new Insets2D.Double(), container.getInsets());
		Insets2D insets = new Insets2D.Double(1.2, 3.4, 5.6, 7.8);
		container.setInsets(insets);
		assertEquals(insets, container.getInsets());
		container.setInsets(insets);
		assertEquals(insets, container.getInsets());
	}

	@Test
	public void testLayout() {
		assertEquals(null, container.getLayout());

		Layout layout = new EdgeLayout();
		container.setLayout(layout);
		assertEquals(layout, container.getLayout());
	}

	@Test
	public void testGetDrawableAt() {
		Point2D[] points = {
			new Point2D.Double(-0.5, -0.5),
			new Point2D.Double(0.0, 0.0),
			new Point2D.Double(0.5, 0.5),
			new Point2D.Double(1.0, 1.0),
			new Point2D.Double(1.5, 1.5)
		};

		for (Point2D point : points) {
			assertEquals(null, container.getDrawableAt(point));
		}

		MockDrawable d = new MockDrawable();
		d.setBounds(0.0, 0.0, 1.0, 1.0);
		container.add(d);

		Drawable[] expected = {
			null,
			d,
			d,
			null,
			null
		};
		for (int i = 0; i < points.length; i++) {
			assertEquals(String.format("Unexpected result at %s:", points[i]),
				expected[i], container.getDrawableAt(points[i]));
		}
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		DrawableContainer original = container;
		DrawableContainer deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(original.size(), deserialized.size());
		assertEquals(original.getPreferredSize(), deserialized.getPreferredSize());
		assertEquals(original.getInsets(), deserialized.getInsets());
		assertEquals(original.getLayout(), deserialized.getLayout());
	}
}

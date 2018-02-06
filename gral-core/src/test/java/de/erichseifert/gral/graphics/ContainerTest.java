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
package de.erichseifert.gral.graphics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.erichseifert.gral.graphics.layout.EdgeLayout;
import de.erichseifert.gral.graphics.layout.Layout;
import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;


public class ContainerTest {
	private DrawableContainer container;

	private static final class MockDrawable extends AbstractDrawable {
		/** Version id for serialization. */
		private static final long serialVersionUID = 1802598562530415902L;

		private boolean isDrawn;
		private final Dimension2D preferredSize = new de.erichseifert.gral.graphics.Dimension2D.Double();

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
		assertEquals(new de.erichseifert.gral.graphics.Dimension2D.Double(), container.getPreferredSize());
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
	public void testContains() {
		// TODO: Allow null values?
		assertFalse(container.contains(container));

		Drawable d1 = new MockDrawable();
		assertFalse(container.contains(d1));
		container.add(d1);
		assertTrue(container.contains(d1));

		Drawable d2 = new MockDrawable();
		container.add(d2);
		assertTrue(container.contains(d1));
		assertTrue(container.contains(d2));

		container.remove(d1);
		assertFalse(container.contains(d1));
		assertTrue(container.contains(d2));
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
			new Point2D.Double(1.5, 1.5),
			new Point2D.Double(2.0, 2.0)
		};

		for (Point2D point : points) {
			assertEquals(Collections.emptyList(), container.getDrawablesAt(point));
		}

		MockDrawable d = new MockDrawable();
		d.setBounds(0.0, 0.0, 1.0, 1.0);
		container.add(d);

		DrawableContainer nestedContainer = new DrawableContainer();
		nestedContainer.setBounds(1.0, 1.0, 1.0, 1.0);
		container.add(nestedContainer);
		MockDrawable nestedDrawable = new MockDrawable();
		nestedDrawable.setBounds(1.5, 1.5, 0.5, 0.5);
		nestedContainer.add(nestedDrawable);

		List<Drawable> dList = new ArrayList<>(1);
		dList.add(d);
		List<Drawable> dPlusNestedContainerList = new ArrayList<>(2);
		dPlusNestedContainerList.add(nestedContainer);
		dPlusNestedContainerList.add(d);
		List<Drawable> nestedContainerList = new ArrayList<>(1);
		nestedContainerList.add(nestedContainer);
		List<Drawable> nestedDrawableList = new ArrayList<>(1);
		nestedDrawableList.add(nestedDrawable);
		nestedDrawableList.add(nestedContainer);
		List[] expected = {
			Collections.emptyList(),
			dList,
			dList,
			nestedContainerList,
			nestedDrawableList,
			Collections.emptyList()
		};
		for (int i = 0; i < points.length; i++) {
			assertEquals(String.format("Unexpected result at %s:", points[i]),
				expected[i], container.getDrawablesAt(points[i]));
		}
	}

	@Test
	public void testGetDrawables() {
		assertNotNull(container.getDrawables());
		assertTrue(container.getDrawables().isEmpty());

		Drawable d1 = new MockDrawable();
		container.add(d1);
		List<Drawable> drawables = container.getDrawables();
		assertEquals(1, drawables.size());
		assertEquals(d1, drawables.get(0));

		Drawable d2 = new MockDrawable();
		container.add(d2);
		drawables = container.getDrawables();
		assertEquals(2, drawables.size());
		assertEquals(d1, drawables.get(0));
		assertEquals(d2, drawables.get(1));

		container.remove(d1);
		drawables = container.getDrawables();
		assertEquals(1, drawables.size());
		assertEquals(d2, drawables.get(0));
	}

	@Test
	public void testGetDrawableAtOrder() {
		// Create two overlapping drawables
		MockDrawable d1 = new MockDrawable();
		MockDrawable d2 = new MockDrawable();
		Rectangle2D bounds = new Rectangle2D.Double(0.0, 0.0, 1.0, 1.0);
		d1.setBounds(bounds);
		d2.setBounds(bounds);

		container.add(d1);
		container.add(d2);
		List<Drawable> resultList = new ArrayList<>(2);
		resultList.add(d2);
		resultList.add(d1);

		Point2D point = new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
		assertEquals(resultList, container.getDrawablesAt(point));

		// Clear container
		container.remove(d1);
		container.remove(d2);
		assertEquals(0, container.size());

		// Re-add drawables in inverse order
		container.add(d2);
		container.add(d1);
		Collections.reverse(resultList);
		assertEquals(resultList, container.getDrawablesAt(point));
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

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

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.erichseifert.gral.TestUtils;
import org.junit.Before;
import org.junit.Test;


public class DrawableTest {
	private static final double DELTA = TestUtils.DELTA;
	private static final Rectangle2D BOUNDS = new Rectangle2D.Double(1.2, 3.4, 56.7, 89.0);
	private static final Dimension2D PREFERRED_SIZE =
		new de.erichseifert.gral.graphics.Dimension2D.Double(12.3, 45.6);

	private MockDrawable drawable;

	private static final class MockDrawable extends AbstractDrawable {
		/** Version id for serialization. */
		private static final long serialVersionUID = 6148480638542875770L;

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
		drawable = new MockDrawable();
	}

	@Test
	public void testCreate() {
		assertEquals(new Rectangle2D.Double(), drawable.getBounds());
		assertEquals(drawable.getBounds().getX(), drawable.getX(), DELTA);
		assertEquals(drawable.getBounds().getY(), drawable.getY(), DELTA);
		assertEquals(drawable.getBounds().getWidth(), drawable.getWidth(), DELTA);
		assertEquals(drawable.getBounds().getHeight(), drawable.getHeight(), DELTA);
		assertEquals(new de.erichseifert.gral.graphics.Dimension2D.Double(), drawable.getPreferredSize());
	}

	@Test
	public void testBounds() {
		assertEquals(new Rectangle2D.Double(), drawable.getBounds());
		drawable.setBounds(BOUNDS);
		assertEquals(BOUNDS, drawable.getBounds());
	}

	@Test
	public void testPreferredSize() {
		assertEquals(new de.erichseifert.gral.graphics.Dimension2D.Double(), drawable.getPreferredSize());
		drawable.preferredSize.setSize(PREFERRED_SIZE);
		assertEquals(PREFERRED_SIZE, drawable.getPreferredSize());
	}

	@Test
	public void testDraw() {
		assertFalse(drawable.isDrawn);
		drawable.draw(null);
		assertTrue(drawable.isDrawn);
	}

	@Test
	public void testSetPosition() {
		drawable.setPosition(4.0, 2.0);
		assertEquals(4.0, drawable.getBounds().getX(), DELTA);
		assertEquals(2.0, drawable.getBounds().getY(), DELTA);

		drawable.setPosition(-4.0, -2.0);
		assertEquals(-4.0, drawable.getBounds().getX(), DELTA);
		assertEquals(-2.0, drawable.getBounds().getY(), DELTA);
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		MockDrawable original = drawable;
		MockDrawable deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(original.getBounds(), deserialized.getBounds());
		assertEquals(original.getPreferredSize(), deserialized.getPreferredSize());
	}
}

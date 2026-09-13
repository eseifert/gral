/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2026 Erich Seifert <dev[at]erichseifert.de>,
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

import static de.erichseifert.gral.TestUtils.assertNotEmpty;
import static de.erichseifert.gral.TestUtils.createTestImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;

public class LabelTest {
	private static final double DELTA = TestUtils.DELTA;

	private static class MockLabel extends Label {
		public boolean isDrawn;

		public MockLabel() {
			super();
		}

		public MockLabel(String text) {
			super(text);
		}

		@Override
		public void draw(DrawingContext context) {
			super.draw(context);
			isDrawn = true;
		}
	}

	@Before
	public void setUp() {
	}

	@Test
	public void testCreation() {
		var empty = new MockLabel();
		assertEquals("", empty.getText());
		assertEquals(0.0, empty.getX(), DELTA);
		assertEquals(0.0, empty.getY(), DELTA);
		assertEquals(0.0, empty.getWidth(), DELTA);
		assertEquals(0.0, empty.getHeight(), DELTA);
		assertEquals(new Dimension2D.Double(), empty.getPreferredSize());

		var text = new MockLabel("foobar");
		assertEquals("foobar", text.getText());
		assertEquals(0.0, text.getX(), DELTA);
		assertEquals(0.0, text.getY(), DELTA);
		assertTrue(text.getPreferredSize().getWidth() > 0.0);
		assertTrue(text.getPreferredSize().getHeight() > 0.0);
	}

	@Test
	public void testSettings() {
		var label = new MockLabel("foobar");
		assertEquals(0.5, label.getAlignmentX(), DELTA);
		assertEquals(0.5, label.getAlignmentY(), DELTA);
		assertEquals(Color.BLACK, label.getColor());
		assertEquals(Font.decode(null), label.getFont());
		assertEquals(0.0, label.getRotation(), DELTA);

		// Set
		label.setColor(Color.RED);
		assertEquals(Color.RED, label.getColor());
	}

	@Test
	public void testDraw() {
		var empty = new MockLabel();
		var text = new MockLabel("foobar");
		var rotated = new MockLabel("foobar");
		rotated.setRotation(45.0);

		MockLabel[] labels = { empty, text, rotated };

		for (MockLabel label : labels) {
			BufferedImage image = createTestImage();
			label.setBounds(0.0, 0.0, image.getWidth(), image.getHeight());
			var context = new DrawingContext((Graphics2D) image.getGraphics());
			label.draw(context);
			assertTrue(label.isDrawn);
			if (!label.getText().isEmpty()) {
				assertNotEmpty(image);
			}
		}
	}

	@Test
	public void testEqualsWithNullProperties() {
		var label = new Label();
		label.setText(null);
		label.setFont(null);
		label.setColor(null);
		label.setBackground(null);

		var other = new Label("foobar");
		other.setFont(Font.decode(null));
		other.setColor(Color.RED);
		other.setBackground(Color.BLUE);

		// Comparing null properties with non-null properties must not throw
		assertFalse(label.equals(other));
		assertFalse(other.equals(label));

		var emptyCopy = new Label();
		emptyCopy.setText(null);
		emptyCopy.setFont(null);
		emptyCopy.setColor(null);
		emptyCopy.setBackground(null);
		assertEquals(label, emptyCopy);
	}

	@Test
	public void testHashCode() {
		var label = new Label("foobar");
		label.setColor(Color.RED);
		var copy = new Label("foobar");
		copy.setColor(Color.RED);

		assertEquals(label, copy);
		assertEquals(label.hashCode(), copy.hashCode());

		var nullProperties = new Label();
		nullProperties.setText(null);
		nullProperties.setFont(null);
		nullProperties.setColor(null);
		nullProperties.setBackground(null);
		// Must not throw for null properties
		nullProperties.hashCode();
	}
}

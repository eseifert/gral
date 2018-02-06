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

import static de.erichseifert.gral.TestUtils.assertNotEmpty;
import static de.erichseifert.gral.TestUtils.createTestImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;

public class LabelTest {
	private static final double DELTA = TestUtils.DELTA;

	private static class MockLabel extends Label {
		/** Version id for serialization. */
		private static final long serialVersionUID = 7291791651477766692L;

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
		Label empty = new MockLabel();
		assertEquals("", empty.getText());
		assertEquals(0.0, empty.getX(), DELTA);
		assertEquals(0.0, empty.getY(), DELTA);
		assertEquals(0.0, empty.getWidth(), DELTA);
		assertEquals(0.0, empty.getHeight(), DELTA);
		assertEquals(new Dimension2D.Double(), empty.getPreferredSize());

		Label text = new MockLabel("foobar");
		assertEquals("foobar", text.getText());
		assertEquals(0.0, text.getX(), DELTA);
		assertEquals(0.0, text.getY(), DELTA);
		assertTrue(text.getPreferredSize().getWidth() > 0.0);
		assertTrue(text.getPreferredSize().getHeight() > 0.0);
	}

	@Test
	public void testSettings() {
		Label label = new MockLabel("foobar");
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
		MockLabel empty = new MockLabel();
		MockLabel text = new MockLabel("foobar");
		MockLabel rotated = new MockLabel("foobar");
		rotated.setRotation(45.0);

		MockLabel[] labels = { empty, text, rotated };

		for (MockLabel label : labels) {
			BufferedImage image = createTestImage();
			label.setBounds(0.0, 0.0, image.getWidth(), image.getHeight());
			DrawingContext context = new DrawingContext((Graphics2D) image.getGraphics());
			label.draw(context);
			assertTrue(label.isDrawn);
			if (!label.getText().isEmpty()) {
				assertNotEmpty(image);
			}
		}
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		Label original = new MockLabel("foobar");
		Label deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(original.getAlignmentX(), deserialized.getAlignmentX(), DELTA);
		assertEquals(original.getAlignmentY(), deserialized.getAlignmentY(), DELTA);
		assertEquals(original.getFont(), deserialized.getFont());
		assertEquals(original.getRotation(), deserialized.getRotation(), DELTA);
		assertEquals(original.getColor(), deserialized.getColor());
		assertEquals(original.getTextAlignment(), deserialized.getTextAlignment(), DELTA);
		assertEquals(original.isWordWrapEnabled(), deserialized.isWordWrapEnabled());
    }
}

/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2012 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael[at]erichseifert.de>
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
package de.erichseifert.gral.plots;

import static de.erichseifert.gral.TestUtils.assertNotEmpty;
import static de.erichseifert.gral.TestUtils.createTestImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.util.Dimension2D;

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
		assertEquals(0.5, label.<Number>getSetting(Label.ALIGNMENT_X).doubleValue(), DELTA);
		assertEquals(0.5, label.<Number>getSetting(Label.ALIGNMENT_Y).doubleValue(), DELTA);
		assertEquals(Color.BLACK, label.getSetting(Label.COLOR));
		assertEquals(Font.decode(null), label.getSetting(Label.FONT));
		assertEquals(0.0, label.<Number>getSetting(Label.ROTATION).doubleValue(), DELTA);

		// Set
		label.setSetting(Label.COLOR, Color.RED);
		assertEquals(Color.RED, label.<Paint>getSetting(Label.COLOR));

		// Remove
		label.removeSetting(Label.COLOR);
		assertEquals(Color.BLACK, label.<Paint>getSetting(Label.COLOR));
	}

	@Test
	public void testDraw() {
		MockLabel empty = new MockLabel();
		MockLabel text = new MockLabel("foobar");
		MockLabel rotated = new MockLabel("foobar");
		rotated.setSetting(Label.ROTATION, 45.0);

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

}

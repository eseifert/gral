/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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

import static de.erichseifert.gral.TestUtils.assertNonEmptyImage;
import static de.erichseifert.gral.TestUtils.createTestImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.junit.Test;

public class TestUtilsTest {
	@Test
	public void testCreateTestImage() {
		BufferedImage image = createTestImage();
		assertNotNull(image);
		assertTrue(image.getWidth() > 0);
		assertTrue(image.getHeight() > 0);
		assertEquals(BufferedImage.TYPE_INT_ARGB, image.getType());
	}

	@Test
	public void testAssertNonEmptyImage() {
		BufferedImage image = new BufferedImage(40, 30, BufferedImage.TYPE_INT_ARGB);

		// Assert must fail on empty image
		try {
			assertNonEmptyImage(image);
			fail();
		} catch (AssertionError e) {
		}

		// Assert must succeed on empty image
		Color color = Color.BLACK;
		image.setRGB(0, 0, color.getRGB());
		assertNonEmptyImage(image);
	}
}

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
package de.erichseifert.gral;

import static de.erichseifert.gral.TestUtils.assertNotEmpty;
import static de.erichseifert.gral.TestUtils.createTestImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
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
			assertNotEmpty(image);
			fail();
		} catch (AssertionError e) {
		}

		// Assert must succeed on empty image
		Color color = Color.BLACK;
		image.setRGB(0, 0, color.getRGB());
		assertNotEmpty(image);
	}

	@Test
	public void testAssertEqualsShape() {
		Rectangle2D s1 = new Rectangle2D.Double();
		try {
			TestUtils.assertEquals(s1, null);
			fail();
		} catch (AssertionError e) {
		}
		try {
			TestUtils.assertEquals(null, s1);
			fail();
		} catch (AssertionError e) {
		}
		Rectangle2D s2 = new Rectangle2D.Double();
		TestUtils.assertEquals(s1, s2);
		TestUtils.assertEquals(s2, s1);

		s1.setFrame(1.0, 1.0, 10.0, 10.0);
		try {
			TestUtils.assertEquals(s1, s2);
			fail();
		} catch (AssertionError e) {
		}
		try {
			TestUtils.assertEquals(s2, s1);
			fail();
		} catch (AssertionError e) {
		}

		s2.setFrame(1.0, 1.0, 10.0, 10.0);
		TestUtils.assertEquals(s1, s2);
		TestUtils.assertEquals(s2, s1);
	}

	@Test
	public void testAssertEqualsLine2D() {
		Line2D l1 = new Line2D.Double();
		try {
			TestUtils.assertEquals(l1, null);
			fail();
		} catch (AssertionError e) {
		}
		try {
			TestUtils.assertEquals(null, l1);
			fail();
		} catch (AssertionError e) {
		}
		Line2D l2 = new Line2D.Double();
		TestUtils.assertEquals(l1, l2);
		TestUtils.assertEquals(l2, l1);

		l2.setLine(0.0, 0.0, 1.0, 1.0);
		try {
			TestUtils.assertEquals(l1, l2);
			fail();
		} catch (AssertionError e) {
		}
		try {
			TestUtils.assertEquals(l2, l1);
			fail();
		} catch (AssertionError e) {
		}
		l1.setLine(0.0, 0.0, 1.0, 1.0);
		TestUtils.assertEquals(l1, l2);
		TestUtils.assertEquals(l2, l1);
	}
}

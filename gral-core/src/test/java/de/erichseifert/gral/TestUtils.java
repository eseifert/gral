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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class TestUtils {
	/** Default precision for unit tests. **/
	public static final double DELTA = 1e-15;

	/**
	 * Creates a new writable image for running a unit test.
	 * @return A writable image instance.
	 */
	public static final BufferedImage createTestImage() {
		return new BufferedImage(40, 30, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * Fails if the image is empty. An image is considered empty when it
	 * contains at least one non-transparent pixel (alpha &gt; 0).
	 * @param image Image to test.
	 */
	public static final void assertNonEmptyImage(BufferedImage image) {
		assertTrue(image.getWidth() > 0);
		assertTrue(image.getHeight() > 0);
		int[] data = ((DataBufferInt)(image).getRaster().getDataBuffer()).getData();
		for (int i = 0; i < data.length; i++) {
			int color = data[i];
			int alpha = color & 0xFF000000;
			if (alpha != 0) {
				return;
			}
		}
		fail();
	}
}

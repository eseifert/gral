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
		// An image without data is considered empty
		assertTrue(image.getWidth() > 0);
		assertTrue(image.getHeight() > 0);

		// Check whether there are non-transparent pixel values
		DataBufferInt buf = (DataBufferInt) image.getRaster().getDataBuffer();
		int[] data = buf.getData();
		for (int i = 0; i < data.length; i++) {
			int color = data[i];
			int alpha = color & 0xFF000000;
			if (alpha != 0) {
				return;
			}
		}

		fail();
	}

	/**
	 * Fails if two images are equal.
	 * @param image1 First image.
	 * @param image2 Second image.
	 */
	public static final void assertNotEqual(BufferedImage image1, BufferedImage image2) {
		DataBufferInt buf1 = (DataBufferInt) image1.getRaster().getDataBuffer();
		DataBufferInt buf2 = (DataBufferInt) image2.getRaster().getDataBuffer();

		// If the image dimensions are different, the images are considered as
		// different too
		if (buf1.getSize() != buf2.getSize()) {
			return;
		}

		// Check whether there are different pixel values
		int[] data1 = buf1.getData();
		int[] data2 = buf2.getData();
		for (int i = 0; i < data1.length; i++) {
			int color1 = data1[i];
			int color2 = data2[i];
			if (color1 != color2) {
				return;
			}
		}

		fail();
	}
}

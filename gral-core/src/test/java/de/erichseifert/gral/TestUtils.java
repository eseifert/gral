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

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import de.erichseifert.gral.util.GeometryUtils;
import de.erichseifert.gral.util.GeometryUtils.PathSegment;

public class TestUtils {
	/** Default precision for unit tests. **/
	public static final double DELTA = 1e-15;

	/**
	 * Creates a new writable image for running a unit test.
	 * @return A writable image instance.
	 */
	public static BufferedImage createTestImage() {
		return new BufferedImage(40, 30, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * Fails if the image is not empty and prints a specified message. The
	 * image is considered as empty when it contains only transparent pixels
	 * (alpha &lt; 0).
	 * @param message Custom message.
	 * @param image Image to test.
	 */
	public static void assertEmpty(String message, BufferedImage image) {
		if (!isEmpty(image)) {
			fail((String.valueOf(message) + " Image is not empty.").trim());
		}
	}

	/**
	 * Fails if the image is not empty. The image is considered as empty when
	 * it contains only transparent pixels (alpha &lt; 0).
	 * @param image Image to test.
	 */
	public static void assertEmpty(BufferedImage image) {
		assertEmpty("", image);
	}

	/**
	 * Fails if the image is empty and prints a specified message. The image is
	 * considered as not empty when it contains at least one transparent pixel
	 * (alpha &gt; 0).
	 * @param message Custom message.
	 * @param image Image to test.
	 */
	public static void assertNotEmpty(String message, BufferedImage image) {
		// An image without data is considered empty
		assertTrue(image.getWidth() > 0);
		assertTrue(image.getHeight() > 0);

		if (isEmpty(image)) {
			fail((String.valueOf(message) + " Image is empty.").trim());
		}
	}

	/**
	 * Fails if the image is empty. The image is considered as not empty when
	 * it contains at least one transparent pixel (alpha &gt; 0).
	 * @param image Image to test.
	 */
	public static void assertNotEmpty(BufferedImage image) {
		assertNotEmpty("", image);
	}

	/**
	 * Returns whether the specified image is empty. The image is considered
	 * as empty when it contains only transparent pixels (alpha &lt; 0).
	 * @param image Image to test.
	 * @return {@code true} when the image is empty, otherwise {@code false}
	 */
	private static boolean isEmpty(BufferedImage image) {
		// Check whether there are non-transparent pixel values
		DataBufferInt buf = (DataBufferInt) image.getRaster().getDataBuffer();
		int[] data = buf.getData();
		for (int color : data) {
			int alpha = color & 0xFF000000;
			if (alpha != 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Fails if the contents of two images aren't equal and prints a specified message.
	 * @param message Custom message.
	 * @param image1 First image.
	 * @param image2 Second image.
	 */
	public static void assertEquals(String message, BufferedImage image1, BufferedImage image2) {
		if (!isEqual(image1, image2)) {
			fail((String.valueOf(message) + " Image contents are different.").trim());
		}
	}

	/**
	 * Fails if the contents of two images aren't equal.
	 * @param image1 First image.
	 * @param image2 Second image.
	 */
	public static void assertEquals(BufferedImage image1, BufferedImage image2) {
		assertEquals("", image1, image2);
	}

	/**
	 * Fails if the contents of two images are equal and prints a specified message.
	 * @param message Custom message.
	 * @param image1 First image.
	 * @param image2 Second image.
	 */
	public static void assertNotEquals(String message, BufferedImage image1, BufferedImage image2) {
		if (isEqual(image1, image2)) {
			fail((String.valueOf(message) + " Image contents are identical.").trim());
		}
	}

	/**
	 * Fails if the contents of two images are equal.
	 * @param image1 First image.
	 * @param image2 Second image.
	 */
	public static void assertNotEquals(BufferedImage image1, BufferedImage image2) {
		assertNotEquals("", image1, image2);
	}

	/**
	 * Returns whether two images contain the same pixels.
	 * @param image1 First image.
	 * @param image2 Second image.
	 * @return {@code true} when the images are equal, otherwise {@code false}
	 */
	private static boolean isEqual(BufferedImage image1, BufferedImage image2) {
		DataBufferInt buf1 = (DataBufferInt) image1.getRaster().getDataBuffer();
		DataBufferInt buf2 = (DataBufferInt) image2.getRaster().getDataBuffer();

		// If the image dimensions are different, the images are considered as
		// not equal
		if (buf1.getSize() != buf2.getSize()) {
			return false;
		}

		// Check whether there are different pixel values
		int[] data1 = buf1.getData();
		int[] data2 = buf2.getData();
		for (int i = 0; i < data1.length; i++) {
			int color1 = data1[i];
			int color2 = data2[i];
			if (color1 != color2) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public static <T> T serializeAndDeserialize(T original)
			throws IOException, ClassNotFoundException {
		// Serialize
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (ObjectOutputStream oos = new ObjectOutputStream(out)) {
			oos.writeObject(original);
		} catch (NotSerializableException e) {
			e.printStackTrace();
		}
		assertTrue("Serialization failed.", out.size() > 0);

		// Deserialize
	    byte[] serializedData = out.toByteArray();
	    InputStream in = new ByteArrayInputStream(serializedData);
	    ObjectInputStream ois = new ObjectInputStream(in);
	    Object o = ois.readObject();
	    assertNotSame(original, o);

	    return (T) o;
	}

	public static void assertEquals(String message, Line2D expected, Line2D actual) {
		org.junit.Assert.assertEquals(message, expected.getP1(), actual.getP1());
		org.junit.Assert.assertEquals(message, expected.getP2(), actual.getP2());
	}

	public static <T> void assertSetting(String message, T expected, T actual) {
		// Line2D instances can't be compared. See Java bug 5057070
		// <http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5057070>
		if (expected instanceof Line2D) {
			assertEquals(message, (Line2D) expected, (Line2D) actual);
		} else if (expected instanceof Shape) {
			List<PathSegment> segsExpected =
				GeometryUtils.getSegments((Shape) expected);
			List<PathSegment> segsActual =
				GeometryUtils.getSegments((Shape) actual);
			org.junit.Assert.assertEquals(message,
				segsExpected.size(), segsActual.size());
			for (int i = 0; i < segsExpected.size(); i++) {
				PathSegment segExpected = segsExpected.get(i);
				PathSegment segActual = segsActual.get(i);
				org.junit.Assert.assertEquals(message,
					segExpected.type, segActual.type);
				org.junit.Assert.assertEquals(message,
					segExpected.start, segActual.start);
				org.junit.Assert.assertEquals(message,
					segExpected.end, segActual.end);
				org.junit.Assert.assertArrayEquals(message,
					segExpected.coords, segActual.coords, DELTA);
			}
		} else {
			org.junit.Assert.assertEquals(message, expected, actual);
		}
	}

	public static void assertEquals(Line2D expected, Line2D actual) {
		if (expected == null && actual == null) {
			return;
		}
		if (expected != null && expected.equals(actual)) {
			return;
		}
		if (expected == null || actual == null) {
			fail();
		}

		org.junit.Assert.assertEquals(expected.getP1(), actual.getP1());
		org.junit.Assert.assertEquals(expected.getP2(), actual.getP2());
	}

	public static void assertEquals(Shape expected, Shape actual) {
		if (expected instanceof Line2D && actual instanceof Line2D) {
			assertEquals((Line2D) expected, (Line2D) actual);
		} else {
			org.junit.Assert.assertEquals(expected, actual);
		}
	}
}

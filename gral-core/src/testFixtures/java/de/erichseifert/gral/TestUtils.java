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
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import de.erichseifert.gral.util.GeometryUtils;
import de.erichseifert.gral.util.GeometryUtils.PathSegment;

/**
 * <p>Shared helpers for the unit tests. Beyond saving typing, they define the
 * conventions the test suite follows.</p>
 *
 * <p><b>Rendering is checked by painting, not by comparison.</b> A test draws a
 * {@link de.erichseifert.gral.graphics.Drawable} into the image from
 * {@link #createTestImage()} and then asserts with {@link #assertNotEmpty(
 * java.awt.image.BufferedImage)} that something was painted. There are no
 * reference images anywhere in the suite, so the tests stay robust against
 * differences in font rendering and antialiasing between platforms and JDKs;
 * the price is that they catch "nothing was drawn", not "the wrong thing was
 * drawn".</p>
 *
 * <pre>
 * BufferedImage image = TestUtils.createTestImage();
 * drawable.setBounds(0.0, 0.0, image.getWidth(), image.getHeight());
 * drawable.draw(new DrawingContext((Graphics2D) image.getGraphics()));
 * TestUtils.assertNotEmpty(image);
 * </pre>
 *
 * <p><b>Anything serializable gets a round trip.</b> Serialization is a
 * supported feature of the plots and data sources, and it breaks silently when
 * a new field of a non-serializable AWT type is added without being handled in
 * {@code readObject} and {@code writeObject}. A new plot, renderer or data
 * source is therefore expected to be put through
 * {@link #serializeAndDeserialize(Object)} and compared property by
 * property.</p>
 *
 * <p>{@link #assertSetting(String, Object, Object)} exists because some AWT
 * types compare badly: {@code Line2D} has no {@code equals}, and shapes have to
 * be compared segment by segment.</p>
 */
public class TestUtils {
	/** Default precision for floating-point assertions in unit tests. **/
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
	 * (alpha == 0).
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
	 * it contains only transparent pixels (alpha == 0).
	 * @param image Image to test.
	 */
	public static void assertEmpty(BufferedImage image) {
		assertEmpty("", image);
	}

	/**
	 * Fails if the image is empty and prints a specified message. The image is
	 * considered as not empty when it contains at least one non-transparent
	 * pixel (alpha &gt; 0).
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
	 * it contains at least one non-transparent pixel (alpha &gt; 0).
	 * @param image Image to test.
	 */
	public static void assertNotEmpty(BufferedImage image) {
		assertNotEmpty("", image);
	}

	/**
	 * Returns whether the specified image is empty. The image is considered
	 * as empty when it contains only transparent pixels (alpha == 0).
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

	/**
	 * Serializes an object and deserializes it again, which is the standard way
	 * of checking that a class survives a round trip. Fails if nothing was
	 * written, and asserts that the result is a different instance.
	 * @param <T> Type of the object.
	 * @param original Object to be serialized.
	 * @return A deserialized copy of the object.
	 * @throws IOException if reading or writing the object failed.
	 * @throws ClassNotFoundException if a serialized class no longer exists.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T serializeAndDeserialize(T original)
			throws IOException, ClassNotFoundException {
		// Serialize
		var out = new ByteArrayOutputStream();
		try (ObjectOutputStream oos = new ObjectOutputStream(out)) {
			oos.writeObject(original);
		} catch (NotSerializableException e) {
			throw new AssertionError("Unable to serialize "
				+ original.getClass().getName() + ".", e);
		}
		assertTrue("Serialization failed.", out.size() > 0);

		// Deserialize
	    byte[] serializedData = out.toByteArray();
	    var in = new ByteArrayInputStream(serializedData);
	    var ois = new ObjectInputStream(in);
	    Object o = ois.readObject();
	    assertNotSame(original, o);

	    return (T) o;
	}

	/**
	 * Fails if two lines have different end points. {@code Line2D} does not
	 * implement {@code equals}, so it cannot be compared directly.
	 * @param message Custom message.
	 * @param expected Expected line.
	 * @param actual Actual line.
	 */
	public static void assertEquals(String message, Line2D expected, Line2D actual) {
		if (expected == null || actual == null) {
			org.junit.Assert.assertEquals(message, expected, actual);
			return;
		}
		org.junit.Assert.assertEquals(message, expected.getP1(), actual.getP1());
		org.junit.Assert.assertEquals(message, expected.getP2(), actual.getP2());
	}

	/**
	 * Fails if two lines have different end points.
	 * @param expected Expected line.
	 * @param actual Actual line.
	 */
	public static void assertEquals(Line2D expected, Line2D actual) {
		assertEquals(null, expected, actual);
	}

	/**
	 * Fails if two shapes describe different paths and prints a specified
	 * message. Most AWT shapes do not implement {@code equals}, so they are
	 * compared segment by segment.
	 * @param message Custom message.
	 * @param expected Expected shape.
	 * @param actual Actual shape.
	 */
	public static void assertEquals(String message, Shape expected, Shape actual) {
		// Line2D instances can't be compared. See Java bug 5057070
		// <http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5057070>
		if (expected instanceof Line2D && actual instanceof Line2D) {
			assertEquals(message, (Line2D) expected, (Line2D) actual);
			return;
		}
		if (expected == null || actual == null) {
			org.junit.Assert.assertEquals(message, expected, actual);
			return;
		}
		List<PathSegment> segsExpected = GeometryUtils.getSegments(expected);
		List<PathSegment> segsActual = GeometryUtils.getSegments(actual);
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
	}

	/**
	 * Fails if two shapes describe different paths.
	 * @param expected Expected shape.
	 * @param actual Actual shape.
	 */
	public static void assertEquals(Shape expected, Shape actual) {
		assertEquals(null, expected, actual);
	}

	/**
	 * Fails if two property values differ, comparing AWT shapes—which have no
	 * usable {@code equals}—by their path segments.
	 * @param <T> Type of the values.
	 * @param message Custom message.
	 * @param expected Expected value.
	 * @param actual Actual value.
	 */
	public static <T> void assertSetting(String message, T expected, T actual) {
		if (expected instanceof Shape) {
			assertEquals(message, (Shape) expected, (Shape) actual);
		} else {
			org.junit.Assert.assertEquals(message, expected, actual);
		}
	}

}

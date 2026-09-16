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
package de.erichseifert.gral.examples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.w3c.dom.Document;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.io.plots.DrawableWriterFactory;

/**
 * <p>Exports every entry of {@link Examples#createAll()} to PNG, SVG, PDF and
 * EPS with no display attached, and checks that the result is a document a
 * reader would accept. This is what the headless figure claim rests on, and it
 * is deliberately structural rather than visual: the assertions hold on any
 * machine, whatever its fonts, which is what {@link GoldenImageTest} cannot
 * promise.</p>
 *
 * <p>What is checked, per format:</p>
 * <ul>
 *   <li>the document is not a stub — issue #181 produced an empty file;</li>
 *   <li>it contains no {@code NaN} and no infinite coordinate — that is the
 *   signature of issue #142, and Java2D skips such a coordinate silently on
 *   screen, so a plot can look right as a bitmap and still export a file that
 *   no reader opens;</li>
 *   <li>it starts and ends the way its format requires, and SVG parses as
 *   well-formed XML;</li>
 *   <li>PNG decodes back to an image of the requested size that is not
 *   blank.</li>
 * </ul>
 */
@RunWith(Parameterized.class)
public class HeadlessExportTest {
	/** Width of the exported page. */
	private static final double WIDTH = 480.0;
	/** Height of the exported page. */
	private static final double HEIGHT = 360.0;

	/** Namespace the SVG elements are in. */
	private static final String SVG_NAMESPACE = "http://www.w3.org/2000/svg";

	/**
	 * Shortest document that is not considered a stub, in bytes. The smallest
	 * example here is a single label, whose SVG is under a kilobyte; issue #181
	 * produced files far below this.
	 */
	private static final int MINIMUM_LENGTH = 256;

	/** Name of the example, which the test report is keyed by. */
	@Parameter(0)
	public String name;
	/** Example that is exported. */
	@Parameter(1)
	public Example example;

	/**
	 * Returns one parameter set per example, named after its class.
	 * @return The examples to export.
	 */
	@Parameters(name = "{0}")
	public static Collection<Object[]> examples() {
		var parameters = new ArrayList<Object[]>();
		for (Example example : Examples.createAll()) {
			parameters.add(new Object[] {
				example.getClass().getSimpleName(), example});
		}
		return parameters;
	}

	/**
	 * Fails the whole class unless it really is running without a display.
	 * Running these under Xvfb would defeat their purpose, and issue #181 is
	 * exactly what goes unnoticed when they do.
	 */
	@BeforeClass
	public static void requireHeadless() {
		assertTrue("These tests have to run headless. Set "
			+ "-Djava.awt.headless=true; the Gradle test tasks of this module "
			+ "already do.", GraphicsEnvironment.isHeadless());
	}

	/**
	 * Checks the PNG export, which also proves that the example paints
	 * something without a display attached.
	 * @throws IOException if writing or decoding the image failed.
	 */
	@Test
	public void exportsPng() throws IOException {
		byte[] document = export("image/png");
		assertStructure("PNG", document);

		BufferedImage image = ImageIO.read(new ByteArrayInputStream(document));
		assertNotNull(name + " exported a PNG that cannot be decoded.", image);
		assertEquals(name + " exported a PNG of the wrong width.",
			(int) WIDTH, image.getWidth());
		assertEquals(name + " exported a PNG of the wrong height.",
			(int) HEIGHT, image.getHeight());

		/*
		 * TestUtils compares integer rasters, and ImageIO hands out whatever
		 * the file uses, so the image is repainted into one.
		 */
		var raster = new BufferedImage(
			image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		var graphics = raster.createGraphics();
		try {
			graphics.drawImage(image, 0, 0, null);
		} finally {
			graphics.dispose();
		}
		TestUtils.assertNotEmpty(name + " exported a blank PNG.", raster);
	}

	/**
	 * Checks that the SVG export is a well-formed SVG document.
	 * @throws Exception if writing or parsing the document failed.
	 */
	@Test
	public void exportsSvg() throws Exception {
		byte[] document = export("image/svg+xml");
		assertStructure("SVG", document);

		Document parsed = parseXml(document);
		assertEquals(name + " exported SVG with the wrong root element.",
			"svg", parsed.getDocumentElement().getLocalName());

		/*
		 * A gradient has to be rasterized, because no vector format can express
		 * what Java 2D means by one. Everything else has to survive as
		 * geometry, and for a while none of it did: one gradient made
		 * VectorGraphics2D rasterize every later fill, so all the text in every
		 * export came out as blocks of flat color. A document that is nothing
		 * but images is that bug again.
		 */
		assertTrue(name + " exported an SVG made only of raster images. "
			+ "Text and lines have to stay geometry.",
			parsed.getElementsByTagNameNS(SVG_NAMESPACE, "path").getLength() > 0);
	}

	/**
	 * Checks that the PDF export carries a header and a trailer.
	 * @throws IOException if writing the document failed.
	 */
	@Test
	public void exportsPdf() throws IOException {
		byte[] document = export("application/pdf");
		assertStructure("PDF", document);

		String text = text(document);
		assertTrue(name + " exported a PDF without a header.",
			text.startsWith("%PDF-"));
		assertTrue(name + " exported a PDF without a trailer.",
			text.contains("%%EOF"));
	}

	/**
	 * Checks that the EPS export carries a header and a bounding box.
	 * @throws IOException if writing the document failed.
	 */
	@Test
	public void exportsEps() throws IOException {
		byte[] document = export("application/postscript");
		assertStructure("EPS", document);

		String text = text(document);
		assertTrue(name + " exported PostScript without a header.",
			text.startsWith("%!PS-Adobe"));
		assertTrue(name + " exported EPS without a bounding box.",
			text.contains("%%BoundingBox:"));
		assertFalse(name + " exported an EPS bounding box that is not a number.",
			text.contains("%%BoundingBox: NaN"));
	}

	/**
	 * Exports the example of this parameter set to the specified format.
	 * @param mimeType Format to export to.
	 * @return The bytes that were written.
	 * @throws IOException if writing failed.
	 */
	private byte[] export(String mimeType) throws IOException {
		Drawable drawable = example.getDrawable();
		var destination = new ByteArrayOutputStream();
		DrawableWriterFactory.getInstance().get(mimeType)
			.write(drawable, destination, WIDTH, HEIGHT);
		return destination.toByteArray();
	}

	/**
	 * Fails when a document is a stub or holds a coordinate that no reader can
	 * make sense of.
	 * @param format Name of the format, for the failure message.
	 * @param document Exported document.
	 */
	private void assertStructure(String format, byte[] document) {
		String message = name + " exported to " + format;
		assertTrue(message + " is only " + document.length + " bytes long.",
			document.length >= MINIMUM_LENGTH);

		if ("PNG".equals(format)) {
			/*
			 * A bitmap holds no coordinates to be wrong, and looking for them
			 * in its compressed bytes finds nothing but coincidences.
			 */
			return;
		}
		String text = "PDF".equals(format) ? pdfText(document) : text(document);
		assertFalse(message + " contains NaN coordinates.", text.contains("NaN"));
		assertFalse(message + " contains infinite coordinates.",
			text.contains("Infinity"));
	}

	/**
	 * Returns a document as text, byte for byte. SVG and EPS are text-based, so
	 * this is the whole document; no character set may swallow a byte.
	 * @param document Exported document.
	 * @return The document as a string.
	 */
	private static String text(byte[] document) {
		return new String(document, StandardCharsets.ISO_8859_1);
	}

	/**
	 * <p>Returns a PDF with its content streams expanded, which is where its
	 * coordinates are.</p>
	 *
	 * <p>Searching the file as it stands would be wrong twice over: a
	 * coordinate inside a compressed stream would not be found, and the three
	 * bytes of {@code NaN} turn up in compressed data by chance often enough to
	 * fail a build for no reason — which is exactly what happened here.</p>
	 *
	 * @param document Exported document.
	 * @return The parts of the document that hold coordinates as text.
	 */
	private static String pdfText(byte[] document) {
		String raw = text(document);
		var expanded = new StringBuilder();
		int position = 0;
		while (true) {
			int start = raw.indexOf("stream", position);
			int end = (start < 0) ? -1 : raw.indexOf("endstream", start);
			if (end < 0) {
				expanded.append(raw, position, raw.length());
				return expanded.toString();
			}
			expanded.append(raw, position, start);
			int from = start + "stream".length();
			while (from < end && (raw.charAt(from) == '\r' || raw.charAt(from) == '\n')) {
				from++;
			}
			expanded.append(inflate(document, from, end));
			position = end;
		}
	}

	/**
	 * Returns a range of a document with the Flate compression undone, or an
	 * empty string when it is not compressed at all — an embedded image, for
	 * example, which carries no coordinates anyway.
	 * @param document Exported document.
	 * @param from First byte of the range.
	 * @param to Byte after the range.
	 * @return The expanded bytes as a string.
	 */
	private static String inflate(byte[] document, int from, int to) {
		var inflater = new Inflater();
		inflater.setInput(document, from, to - from);
		var expanded = new ByteArrayOutputStream();
		var buffer = new byte[4096];
		try {
			while (!inflater.finished()) {
				int count = inflater.inflate(buffer);
				if (count == 0) {
					break;
				}
				expanded.write(buffer, 0, count);
			}
		} catch (DataFormatException e) {
			// Not a Flate stream, so there is nothing to read out of it.
		} finally {
			inflater.end();
		}
		return text(expanded.toByteArray());
	}

	/**
	 * Parses a document as XML without reaching out to the network.
	 * @param document Exported document.
	 * @return The parsed document.
	 * @throws Exception if the document is not well-formed.
	 */
	private static Document parseXml(byte[] document) throws Exception {
		var factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		// Keep the test offline: the SVG document type is not resolved.
		factory.setFeature(
			"http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		return factory.newDocumentBuilder().parse(
			new ByteArrayInputStream(document));
	}
}

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
package de.erichseifert.gral.io.plots;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.BoxPlot;
import de.erichseifert.gral.plots.XYPlot;

/**
 * Checks that the vector formats receive geometry they can actually express.
 * A coordinate that is {@code NaN} is silently skipped by Java2D on screen, so
 * a plot can look correct as a bitmap and still produce a file that no reader
 * accepts (issue #142).
 */
public class VectorExportTest {
	/** Vector formats that are written through {@code VectorWriter}. */
	private static final String[] VECTOR_FORMATS = {
		"application/postscript",
		"application/pdf",
		"image/svg+xml"
	};

	/** Width of the exported page. */
	private static final double WIDTH = 320.0;
	/** Height of the exported page. */
	private static final double HEIGHT = 240.0;

	/**
	 * Returns a box plot whose observations are all identical, so that every
	 * quartile of every column collapses onto the same value.
	 * @return A box plot over degenerate data.
	 */
	private static Drawable createDegenerateBoxPlot() {
		var data = new DataTable(Double.class, Double.class);
		for (int i = 0; i < 16; i++) {
			data.add(0.0, 0.0);
		}
		return new BoxPlot(BoxPlot.createBoxData(data));
	}

	/**
	 * Returns a bar plot whose bars all sit at the same position, which
	 * collapses the horizontal axis.
	 * @return A bar plot over degenerate data.
	 */
	private static Drawable createDegenerateBarPlot() {
		var data = new DataTable(Double.class, Double.class);
		data.add(1.0, 1.0);
		return new BarPlot(data);
	}

	/**
	 * Returns an xy plot over a single point, which collapses both axes.
	 * @return An xy plot over degenerate data.
	 */
	private static Drawable createDegenerateXYPlot() {
		var data = new DataTable(Double.class, Double.class);
		data.add(2.0, 3.0);
		return new XYPlot(data);
	}

	/**
	 * Writes a drawable to the specified format and returns the result.
	 * @param drawable Drawable to export.
	 * @param mimeType Format to export to.
	 * @return The bytes that were written.
	 * @throws IOException if writing failed.
	 */
	private static byte[] export(Drawable drawable, String mimeType)
			throws IOException {
		var dest = new ByteArrayOutputStream();
		DrawableWriter writer = DrawableWriterFactory.getInstance().get(mimeType);
		writer.write(drawable, dest, WIDTH, HEIGHT);
		return dest.toByteArray();
	}

	/**
	 * Fails when a document contains a coordinate that no vector format can
	 * express.
	 * @param message Description of the exported drawable.
	 * @param document Exported document.
	 */
	private static void assertNoInvalidNumbers(String message, byte[] document) {
		String text = new String(document, StandardCharsets.ISO_8859_1);
		assertTrue(message + " is empty.", document.length > 256);
		assertFalse(message + " contains NaN coordinates.", text.contains("NaN"));
		assertFalse(message + " contains infinite coordinates.",
			text.contains("Infinity"));
	}

	@Test
	public void testDegenerateBoxPlot() throws IOException {
		for (String format : VECTOR_FORMATS) {
			assertNoInvalidNumbers("Box plot over constant data exported to " + format,
				export(createDegenerateBoxPlot(), format));
		}
	}

	@Test
	public void testDegenerateBarPlot() throws IOException {
		for (String format : VECTOR_FORMATS) {
			assertNoInvalidNumbers("Bar plot with a single bar exported to " + format,
				export(createDegenerateBarPlot(), format));
		}
	}

	@Test
	public void testDegenerateXYPlot() throws IOException {
		for (String format : VECTOR_FORMATS) {
			assertNoInvalidNumbers("Xy plot over a single point exported to " + format,
				export(createDegenerateXYPlot(), format));
		}
	}

	@Test
	public void testSvgIsWellFormed() throws Exception {
		byte[] document = export(createDegenerateBoxPlot(), "image/svg+xml");
		var factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		// The SVG document type is not resolved, so that the test stays offline
		factory.setFeature(
			"http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		factory.newDocumentBuilder().parse(
			new java.io.ByteArrayInputStream(document));
	}
}

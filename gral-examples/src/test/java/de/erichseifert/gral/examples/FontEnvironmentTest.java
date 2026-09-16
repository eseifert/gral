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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.util.GraphicsUtils;

/**
 * <p>Characterizes the one thing the otherwise self-contained core needs from
 * the machine it runs on: a font. Every label, tick and title goes through
 * {@link GraphicsUtils#getOutline(String, Font, float, double)}, and the seven
 * {@code Font.decode(null)} defaults in the library resolve through the
 * platform's font configuration — on Linux through fontconfig. A container
 * built from a slim JRE has neither fontconfig nor a font, and GRAL fails there
 * in a way that has nothing to do with GRAL.</p>
 *
 * <p>The test writes what it found to
 * {@code build/reports/fonts/font-environment.txt} whether it passes or fails,
 * so that the failure mode of such a runtime can be read off a CI log rather
 * than guessed at, and asserts the minimum the library needs: a default font
 * that lays text out with an extent. What the assertions require is what
 * {@code README.rst} documents as the runtime requirement.</p>
 */
public class FontEnvironmentTest {
	/** Font size the outline is measured at. */
	private static final float FONT_SIZE = 12f;
	/** Text the outline is measured from. */
	private static final String SAMPLE = "GRAL 123";

	/** Report that is written whether the assertions hold or not. */
	private static final String REPORT = "build/reports/fonts/font-environment.txt";

	/**
	 * Writes the report. It runs before the assertions, so that it exists even
	 * when they fail — which on a runtime without fonts is the interesting
	 * case.
	 * @throws IOException if the report could not be written.
	 */
	@BeforeClass
	public static void writeReport() throws IOException {
		var lines = new ArrayList<String>();
		lines.add("Font environment seen by the GRAL examples.");
		lines.add("");
		lines.addAll(RenderingEnvironment.describe());
		lines.add("");
		lines.add("Outline of \"" + SAMPLE + "\":");
		lines.add("  " + describeOutline());
		lines.add("");
		lines.add("All font families:");
		for (String family : RenderingEnvironment.fontFamilies()) {
			lines.add("  " + family);
		}

		var report = new File(REPORT);
		Files.createDirectories(report.getParentFile().toPath());
		Files.write(report.toPath(), lines, StandardCharsets.UTF_8);
	}

	/**
	 * Returns the bounds of the sample outline, or the failure that measuring
	 * it produced.
	 * @return A one-line description.
	 */
	private static String describeOutline() {
		try {
			Shape outline = GraphicsUtils.getOutline(
				SAMPLE, Font.decode(null).deriveFont(FONT_SIZE), 0f, 0.0);
			return String.valueOf(outline.getBounds2D());
		} catch (Error | RuntimeException e) {
			/*
			 * A runtime without fontconfig fails with an UnsatisfiedLinkError
			 * rather than an exception, so Exception alone would miss it.
			 */
			return "<unavailable: " + e + ">";
		}
	}

	/**
	 * Checks that the font every default in the library asks for exists.
	 */
	@Test
	public void hasADefaultFont() {
		Font font = Font.decode(null);
		assertNotNull("Font.decode(null) returned nothing. The library uses it "
			+ "as the default of every label.", font);
	}

	/**
	 * Checks that the graphics environment knows of any font at all.
	 */
	@Test
	public void hasFontFamilies() {
		List<String> families = RenderingEnvironment.fontFamilies();
		assertFalse("The graphics environment offers no font family. On Linux "
			+ "this means fontconfig or the fonts themselves are missing; see "
			+ REPORT + " and the runtime requirements in README.rst.",
			families.isEmpty());
	}

	/**
	 * Checks that text becomes a shape with an extent, which is what every
	 * label in a plot is made of.
	 */
	@Test
	public void laysTextOut() {
		Shape outline = GraphicsUtils.getOutline(
			SAMPLE, Font.decode(null).deriveFont(FONT_SIZE), 0f, 0.0);
		assertNotNull("No outline was produced for \"" + SAMPLE + "\".", outline);

		Rectangle2D bounds = outline.getBounds2D();
		assertTrue("The outline of \"" + SAMPLE + "\" has no width, so every "
			+ "label in a plot would be invisible and every axis would be laid "
			+ "out as if it had none. See " + REPORT + ".",
			bounds.getWidth() > 0.0);
		assertTrue("The outline of \"" + SAMPLE + "\" has no height. See "
			+ REPORT + ".", bounds.getHeight() > 0.0);
	}
}

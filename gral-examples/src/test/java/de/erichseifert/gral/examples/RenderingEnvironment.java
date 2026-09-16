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

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.PathIterator;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * <p>Describes the environment a plot is rendered in, as far as that
 * environment can change the pixels. GRAL turns every piece of text into a
 * {@code Shape} through
 * {@link de.erichseifert.gral.util.GraphicsUtils#getOutline(String,
 * java.awt.Font, float, double)}, so the glyphs of the logical font that
 * {@code Font.decode(null)} resolves to do not only decide how the labels look:
 * their advance widths decide where the axes end up, and with them everything
 * else in the plot.</p>
 *
 * <p>That is why a golden image is not portable. {@link #fingerprint()}
 * condenses what would move the pixels into one string, so that
 * {@link GoldenImageTest} can tell "the plot changed" from "the fonts are
 * different here" instead of failing on both.</p>
 */
final class RenderingEnvironment {
	/**
	 * Text the font outlines are taken from. It covers the characters the
	 * examples put on their axes, in their legends and in their titles.
	 */
	private static final String SAMPLE =
		"GRAL 0123456789 ,.-+% MiB Wj Memory Usage";

	/** Format of a single coordinate in the digested outline. */
	private static final String COORDINATE_FORMAT = "%.4f";

	/** The class is not meant to be instantiated. */
	private RenderingEnvironment() {
	}

	/**
	 * Returns the fonts the examples draw with: the default font that
	 * {@code Font.decode(null)} yields, and the two variants of it that they
	 * derive.
	 * @return The fonts that decide the layout of the plots.
	 */
	private static List<Font> fonts() {
		Font base = Font.decode(null);
		return Arrays.asList(
			base,
			base.deriveFont(Font.BOLD),
			base.deriveFont(20f)
		);
	}

	/**
	 * Returns a digest of everything that can move a pixel without the library
	 * changing: the font outlines, and the Java release whose rasterizer turns
	 * them into pixels.
	 * @return A hexadecimal digest.
	 */
	static String fingerprint() {
		var description = new StringBuilder();
		description.append("java=")
			.append(System.getProperty("java.specification.version"))
			.append('\n');
		for (Font font : fonts()) {
			describeOutline(description, font);
		}
		return digest(description.toString());
	}

	/**
	 * Appends the outline of the sample text in the specified font, coordinate
	 * by coordinate.
	 * @param out Buffer to append to.
	 * @param font Font to lay the text out in.
	 */
	private static void describeOutline(StringBuilder out, Font font) {
		/*
		 * The same font render context GraphicsUtils uses, so that this sees
		 * exactly the outlines the renderers will draw.
		 */
		var context = new FontRenderContext(null, true, true);
		Shape outline = new TextLayout(SAMPLE, font, context).getOutline(null);
		var segment = new double[6];
		for (PathIterator i = outline.getPathIterator(null); !i.isDone(); i.next()) {
			int type = i.currentSegment(segment);
			out.append(type);
			for (double coordinate : segment) {
				out.append(' ').append(
					String.format(Locale.ROOT, COORDINATE_FORMAT, coordinate));
			}
			out.append('\n');
		}
	}

	/**
	 * Returns a summary of the environment for a human to read, as a list of
	 * {@code key=value} lines. Unlike the fingerprint it is not compared
	 * against anything; it is what a failing build is diagnosed from.
	 *
	 * <p>It deliberately names no font. The summary is copied into the manifest
	 * that is checked in beside the golden images, and which fonts happen to be
	 * installed on the machine that regenerated them is nobody else's business.
	 * {@link #fontFamilies()} is for the report under {@code build}, which is
	 * not committed.</p>
	 * @return The lines of the summary.
	 */
	static List<String> describe() {
		var lines = new ArrayList<String>();
		for (String key : new String[] {
				"java.version", "java.vendor", "java.vm.name",
				"os.name", "os.arch", "user.language", "user.country",
				"user.timezone", "java.awt.headless"}) {
			lines.add(key + "=" + System.getProperty(key));
		}
		lines.add("java.awt.GraphicsEnvironment.isHeadless="
			+ GraphicsEnvironment.isHeadless());
		lines.add("fingerprint=" + fingerprint());
		for (Font font : fonts()) {
			/*
			 * The logical name, which is always Dialog here, not the physical
			 * font it resolves to: see the note above.
			 */
			lines.add("font=" + font.getName()
				+ " size=" + font.getSize2D()
				+ " style=" + font.getStyle());
		}
		return lines;
	}

	/**
	 * Returns the font families the graphics environment offers. An empty list
	 * is the symptom of a runtime without fontconfig or without any installed
	 * font.
	 * @return The available font families, sorted.
	 */
	static List<String> fontFamilies() {
		try {
			String[] families = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getAvailableFontFamilyNames(Locale.ROOT);
			var sorted = new ArrayList<>(Arrays.asList(families));
			sorted.sort(null);
			return sorted;
		} catch (Error | RuntimeException e) {
			/*
			 * A runtime without fontconfig fails here rather than returning
			 * nothing, and the failure is an Error often enough that catching
			 * Exception would miss it.
			 */
			return List.of("<unavailable: " + e + ">");
		}
	}

	/**
	 * Returns the SHA-256 digest of the specified text, as a hexadecimal
	 * string.
	 * @param text Text to digest.
	 * @return The digest.
	 */
	private static String digest(String text) {
		MessageDigest sha256;
		try {
			sha256 = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA-256 is not available.", e);
		}
		var hex = new StringBuilder();
		for (byte b : sha256.digest(text.getBytes(StandardCharsets.UTF_8))) {
			hex.append(String.format(Locale.ROOT, "%02x", b));
		}
		return hex.toString();
	}
}

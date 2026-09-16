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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;

/**
 * <p>Renders every entry of {@link Examples#createAll()} at a fixed size and
 * compares the result with an image checked in beside this test. This is the
 * regression net the library has been missing: the plot tests assert only that
 * something was painted, so a refactoring that moves every tick by a pixel
 * passes them.</p>
 *
 * <p><b>Regenerating.</b> {@code ./gradlew :gral-examples:updateGoldenImages}
 * writes the images and their manifest into
 * {@code src/test/resources/de/erichseifert/gral/examples/golden}. Look at the
 * result and at {@code git diff --stat} before committing it: a golden image
 * that is regenerated without being looked at asserts nothing.</p>
 *
 * <p><b>When it does not compare.</b> The images record the font outlines of
 * the machine that made them, and those decide the layout of the whole plot,
 * not just the labels. When {@link RenderingEnvironment#fingerprint()} does not
 * match the one in the manifest, the comparison is skipped rather than failed —
 * a different font is not a regression. Each example is still rendered and
 * checked for being non-empty everywhere, and
 * {@link HeadlessExportTest} checks the export formats everywhere.</p>
 *
 * <p>A mismatch leaves the rendered image and a difference image in
 * {@code build/reports/golden}, so that the change can be looked at rather than
 * guessed at.</p>
 */
@RunWith(Parameterized.class)
public class GoldenImageTest {
	/** Width the examples are rendered at. */
	private static final int WIDTH = 800;
	/** Height the examples are rendered at. */
	private static final int HEIGHT = 600;

	/** Folder of the golden images, relative to this package. */
	private static final String GOLDEN_FOLDER = "golden/";
	/** Name of the file describing the environment the images were made in. */
	private static final String MANIFEST_NAME = "manifest.properties";

	/**
	 * System property that holds the source folder to write new golden images
	 * into. It is set by the {@code updateGoldenImages} task and by nothing
	 * else; while it is set, nothing is compared.
	 */
	private static final String UPDATE_PROPERTY =
		"de.erichseifert.gral.examples.golden.update";
	/** System property that holds the folder failures are written to. */
	private static final String REPORT_PROPERTY =
		"de.erichseifert.gral.examples.golden.report";

	/** Key of the fingerprint in the manifest. */
	private static final String KEY_FINGERPRINT = "fingerprint";
	/** Key of the image width in the manifest. */
	private static final String KEY_WIDTH = "width";
	/** Key of the image height in the manifest. */
	private static final String KEY_HEIGHT = "height";
	/** Key of the seed the examples were pinned to in the manifest. */
	private static final String KEY_SEED = "seed";

	/**
	 * Images that have already been rendered, by example name. Rendering a
	 * hundred thousand scatter points is not something to do once per
	 * assertion, and the aggregating suite runs every test a second time.
	 */
	private static final Map<String, BufferedImage> RENDERED = new HashMap<>();

	/** Name of the example, which also names its image file. */
	@Parameter(0)
	public String name;
	/** Example that is rendered. */
	@Parameter(1)
	public Example example;

	/**
	 * Returns one parameter set per example, named after its class so that the
	 * test report and the image files say which plot broke.
	 * @return The examples to render.
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
	 * Writes the manifest that the images about to be written belong to. It is
	 * written once, before the first image, and only while regenerating.
	 * @throws IOException if the manifest could not be written.
	 */
	@BeforeClass
	public static void writeManifest() throws IOException {
		File target = updateFolder();
		if (target == null) {
			return;
		}
		Files.createDirectories(target.toPath());

		var manifest = new ArrayList<String>();
		manifest.add("# Environment the golden images beside this file were");
		manifest.add("# rendered in. GoldenImageTest compares the fingerprint");
		manifest.add("# and skips the comparison when it differs, because the");
		manifest.add("# font outlines it covers decide the layout of the plots.");
		manifest.add("# Regenerate with: gradlew :gral-examples:updateGoldenImages");
		manifest.add(KEY_FINGERPRINT + "=" + RenderingEnvironment.fingerprint());
		manifest.add(KEY_WIDTH + "=" + WIDTH);
		manifest.add(KEY_HEIGHT + "=" + HEIGHT);
		manifest.add(KEY_SEED + "=" + System.getProperty(Example.REPRODUCIBLE_PROPERTY));
		for (String line : RenderingEnvironment.describe()) {
			manifest.add("# " + line);
		}
		Files.write(new File(target, MANIFEST_NAME).toPath(), manifest,
			StandardCharsets.UTF_8);
	}

	/**
	 * Checks that the example paints something at the size the golden images
	 * use. Unlike the comparison below this runs in every environment, so a
	 * plot that fails to render headless is caught even where the fonts differ.
	 */
	@Test
	public void rendersSomething() {
		TestUtils.assertNotEmpty(name + " painted nothing.", image());
	}

	/**
	 * Compares the example with its golden image, or writes that image while
	 * regenerating.
	 * @throws IOException if an image could not be read or written.
	 */
	@Test
	public void matchesGoldenImage() throws IOException {
		BufferedImage image = image();

		File target = updateFolder();
		if (target != null) {
			Files.createDirectories(target.toPath());
			ImageIO.write(image, "png", new File(target, name + ".png"));
			return;
		}

		Properties manifest = readManifest();
		assertEquals("The golden images were rendered at a different size.",
			String.valueOf(WIDTH), manifest.getProperty(KEY_WIDTH));
		assertEquals("The golden images were rendered at a different size.",
			String.valueOf(HEIGHT), manifest.getProperty(KEY_HEIGHT));
		assertEquals("The golden images were rendered from a different seed. "
			+ "Set " + Example.REPRODUCIBLE_PROPERTY + " to the recorded value "
			+ "or regenerate the images.",
			manifest.getProperty(KEY_SEED),
			System.getProperty(Example.REPRODUCIBLE_PROPERTY));

		String fingerprint = RenderingEnvironment.fingerprint();
		assumeTrue("Skipping the comparison: this runtime lays text out "
			+ "differently from the one the golden images were made on "
			+ "(fingerprint " + fingerprint + ", images "
			+ manifest.getProperty(KEY_FINGERPRINT) + "). "
			+ "See build/reports/fonts for what differs.",
			fingerprint.equals(manifest.getProperty(KEY_FINGERPRINT)));

		BufferedImage golden = readGolden(name + ".png");
		if (!equals(golden, image)) {
			File report = writeFailureReport(image, golden);
			TestUtils.assertEquals(name + " no longer renders as its golden "
				+ "image does. The rendered image and a difference image are "
				+ "in " + report + ".", golden, image);
		}
	}

	/**
	 * Returns the rendered example, rendering it on first use.
	 * @return The image the example painted.
	 */
	private BufferedImage image() {
		return RENDERED.computeIfAbsent(name, key -> render(example));
	}

	/**
	 * Renders an example into a new image, with the rendering hints that
	 * {@link de.erichseifert.gral.io.plots.BitmapWriter} uses, so that the
	 * golden images are what a headless PNG export produces.
	 * @param example Example to render.
	 * @return The rendered image.
	 */
	private static BufferedImage render(Example example) {
		var image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		try {
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
			graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);

			Drawable drawable = example.getDrawable();
			drawable.setBounds(0.0, 0.0, WIDTH, HEIGHT);
			drawable.draw(new DrawingContext(graphics));
		} finally {
			graphics.dispose();
		}
		return image;
	}

	/**
	 * Returns the folder to write golden images into, or {@code null} when the
	 * test is comparing rather than regenerating.
	 * @return The source folder of the golden images, or {@code null}.
	 */
	private static File updateFolder() {
		String folder = System.getProperty(UPDATE_PROPERTY);
		return (folder == null || folder.isEmpty()) ? null : new File(folder);
	}

	/**
	 * Reads the manifest that belongs to the checked-in golden images.
	 * @return The manifest.
	 * @throws IOException if the manifest could not be read.
	 */
	private static Properties readManifest() throws IOException {
		var manifest = new Properties();
		try (InputStream in = open(MANIFEST_NAME)) {
			manifest.load(in);
		}
		return manifest;
	}

	/**
	 * Reads a golden image and converts it to the raster format that
	 * {@link TestUtils} compares.
	 * @param fileName Name of the image file.
	 * @return The golden image.
	 * @throws IOException if the image could not be read.
	 */
	private static BufferedImage readGolden(String fileName) throws IOException {
		BufferedImage stored;
		try (InputStream in = open(fileName)) {
			stored = ImageIO.read(in);
		}
		assertNotNull("Golden image " + fileName + " could not be decoded.", stored);

		/*
		 * ImageIO hands out whatever raster format the file happens to use, and
		 * TestUtils compares integer rasters, so the image is repainted into
		 * one. PNG is lossless, so this preserves every pixel.
		 */
		var image = new BufferedImage(
			stored.getWidth(), stored.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		try {
			graphics.drawImage(stored, 0, 0, null);
		} finally {
			graphics.dispose();
		}
		return image;
	}

	/**
	 * Opens a file from the golden folder on the class path.
	 * @param fileName Name of the file.
	 * @return A stream over the file.
	 * @throws IOException if there is no such file.
	 */
	private static InputStream open(String fileName) throws IOException {
		String resource = GOLDEN_FOLDER + fileName;
		InputStream in = GoldenImageTest.class.getResourceAsStream(resource);
		if (in == null) {
			throw new IOException("No golden image " + resource + ". Run "
				+ "'gradlew :gral-examples:updateGoldenImages' and check the "
				+ "result in before committing it.");
		}
		return in;
	}

	/**
	 * Returns whether two images contain the same pixels.
	 * @param expected First image.
	 * @param actual Second image.
	 * @return {@code true} when the images are equal.
	 */
	private static boolean equals(BufferedImage expected, BufferedImage actual) {
		if (expected.getWidth() != actual.getWidth()
				|| expected.getHeight() != actual.getHeight()) {
			return false;
		}
		for (int y = 0; y < expected.getHeight(); y++) {
			for (int x = 0; x < expected.getWidth(); x++) {
				if (expected.getRGB(x, y) != actual.getRGB(x, y)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Writes the rendered image and an image marking the pixels that differ,
	 * so that a failure can be looked at.
	 * @param actual Image that was rendered.
	 * @param expected Golden image it was compared with.
	 * @return The folder the images were written to.
	 */
	private File writeFailureReport(BufferedImage actual, BufferedImage expected) {
		var folder = new File(System.getProperty(
			REPORT_PROPERTY, "build/reports/golden"));
		try {
			Files.createDirectories(folder.toPath());
			ImageIO.write(actual, "png", new File(folder, name + "-actual.png"));
			ImageIO.write(expected, "png", new File(folder, name + "-expected.png"));
			ImageIO.write(difference(expected, actual), "png",
				new File(folder, name + "-diff.png"));
			Files.write(new File(folder, "environment.txt").toPath(),
				RenderingEnvironment.describe(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return folder;
	}

	/**
	 * Returns an image in which every pixel that differs is opaque red and
	 * every pixel that does not is a faint copy of the golden image.
	 * @param expected Golden image.
	 * @param actual Image that was rendered.
	 * @return The difference image.
	 */
	private static BufferedImage difference(
			BufferedImage expected, BufferedImage actual) {
		int width = Math.max(expected.getWidth(), actual.getWidth());
		int height = Math.max(expected.getHeight(), actual.getHeight());
		var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int before = pixel(expected, x, y);
				int after = pixel(actual, x, y);
				if (before == after) {
					// Keep the unchanged parts as a faint backdrop.
					image.setRGB(x, y, (before & 0x00FFFFFF) | 0x30000000);
				} else {
					image.setRGB(x, y, 0xFFFF0000);
				}
			}
		}
		return image;
	}

	/**
	 * Returns the pixel at the specified position, or a transparent pixel when
	 * the position lies outside the image.
	 * @param image Image to read from.
	 * @param x Horizontal position.
	 * @param y Vertical position.
	 * @return The pixel value.
	 */
	private static int pixel(BufferedImage image, int x, int y) {
		if (x >= image.getWidth() || y >= image.getHeight()) {
			return 0;
		}
		return image.getRGB(x, y);
	}
}

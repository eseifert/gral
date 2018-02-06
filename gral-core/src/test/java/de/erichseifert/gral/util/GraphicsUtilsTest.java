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
package de.erichseifert.gral.util;

import static de.erichseifert.gral.TestUtils.assertNotEmpty;
import static de.erichseifert.gral.TestUtils.createTestImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.junit.Test;

public class GraphicsUtilsTest {
	private static final double DELTA = 1e-15;

	@Test
	public void testGetOutline() {
		Shape outline = GraphicsUtils.getOutline(
			"foobar", Font.decode(null), 0f, 0.5);
		assertNotNull(outline);
		Rectangle2D bounds = outline.getBounds2D();
		assertTrue(bounds.getWidth() > 0.0);
		assertTrue(bounds.getHeight() > 0.0);
	}

	@Test
	public void testPaintedShape() {
		BufferedImage image;
		Shape shape = new Rectangle2D.Double(10.0, 10.0, 300.0, 220.0);
		Paint paint = Color.red;

		image = createTestImage();
		GraphicsUtils.fillPaintedShape((Graphics2D) image.getGraphics(), shape, paint, null);
		assertNotEmpty(image);

		image = createTestImage();
		Rectangle2D paintBounds = shape.getBounds2D();
		GraphicsUtils.fillPaintedShape((Graphics2D) image.getGraphics(), shape, paint, paintBounds);
		assertNotEmpty(image);

		image = createTestImage();
		GraphicsUtils.drawPaintedShape((Graphics2D) image.getGraphics(), shape, paint, paintBounds, null);
		assertNotEmpty(image);

		BasicStroke stroke = new BasicStroke(2f);
		image = createTestImage();
		GraphicsUtils.drawPaintedShape((Graphics2D) image.getGraphics(), shape, paint, paintBounds, stroke);
		assertNotEmpty(image);
	}

	private static void assertEqualsArray(double[] expected, double[] actual, double delta) {
		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], actual[i], delta);
		}
	}

	@Test
	public void testRgb2Xyz() {
		double[] xyz = new double[3];

		GraphicsUtils.rgb2xyz(new double[] { 0.0, 0.0, 0.0 }, xyz);
		assertEqualsArray(new double[] { 0.0, 0.0, 0.0}, xyz, DELTA);

		GraphicsUtils.rgb2xyz(new double[] { 0.0, 0.5, 1.0 }, xyz);
		assertEqualsArray(new double[] { 0.22551071734443490, 0.21406459587821420, 0.73496822304548560 }, xyz, DELTA);

		GraphicsUtils.rgb2xyz(new double[] { 0.5, 0.5, 0.5 }, xyz);
		assertEqualsArray(new double[] { 0.20638296936623524, 0.21404095726301628, 0.17662923071892103 }, xyz, DELTA);

		GraphicsUtils.rgb2xyz(new double[] { 1.0, 0.5, 0.0 }, xyz);
		assertEqualsArray(new double[] { 0.51847532834443490, 0.37593470787821420, 0.03471187504548562 }, xyz, DELTA);

		GraphicsUtils.rgb2xyz(new double[] { 1.0, 1.0, 1.0 }, xyz);
		assertEqualsArray(new double[] { 0.96422103200000000, 0.99999914400000000, 0.82521159400000000 }, xyz, DELTA);
	}

	@Test
	public void testLuv2Xyz() {
		double[] xyz = new double[3];

		GraphicsUtils.luv2xyz(new double[] { 0.0, 0.0, 0.0 }, xyz);
		assertEqualsArray(new double[] { 0.0000000000000000000, 0.0000000000000000000, 0.00000000000000000000 }, xyz, DELTA);

		GraphicsUtils.luv2xyz(new double[] { 0.0, 0.5, 1.0 }, xyz);
		assertEqualsArray(new double[] { 0.0000000000000000000, 0.0000000000000000000, 0.00000000000000000000 }, xyz, DELTA);

		GraphicsUtils.luv2xyz(new double[] { 0.5, 0.5, 0.5 }, xyz);
		assertEqualsArray(new double[] { 0.0006306220545989307, 0.0005535282299397269,-0.00003874159659988680 }, xyz, DELTA);

		GraphicsUtils.luv2xyz(new double[] { 1.0, 0.5, 0.0 }, xyz);
		assertEqualsArray(new double[] { 0.0012637351588200229, 0.0011070564598794539, 0.00084812581097405170 }, xyz, DELTA);

		GraphicsUtils.luv2xyz(new double[] { 1.0, 1.0, 1.0 }, xyz);
		assertEqualsArray(new double[] { 0.0012612441091978614, 0.0011070564598794539,-0.00007748319319977360 }, xyz, DELTA);

		GraphicsUtils.luv2xyz(new double[] { 100.00000000000000,   1.7759188446137468, -18.705788588473464 }, xyz);
		assertEqualsArray(new double[] { 1.0000000000000000000, 1.0000000000000000000, 1.00000000000000000000 }, xyz, DELTA);
	}

	@Test
	public void testXyz2Rgb() {
		double[] rgb = new double[3];

		GraphicsUtils.xyz2rgb(new double[] { 0.0, 0.0, 0.0 }, rgb);
		assertEqualsArray(new double[] {  0.0000000000000000, 0.0000000000000000, 0.0000000000000000 }, rgb, DELTA);

		GraphicsUtils.xyz2rgb(new double[] { 0.0, 0.5, 1.0 }, rgb);
		assertEqualsArray(new double[] {-16.7836995140000020, 0.9962651704966786, 1.1183734614873770 }, rgb, DELTA);

		GraphicsUtils.xyz2rgb(new double[] { 0.5, 0.5, 0.5 }, rgb);
		assertEqualsArray(new double[] {  0.7439767101458333, 0.7256668703074206, 0.8118445228904296 }, rgb, DELTA);

		GraphicsUtils.xyz2rgb(new double[] { 1.0, 0.5, 0.0 }, rgb);
		assertEqualsArray(new double[] {  1.4445522116235023,-0.2674136380000002,-0.5497511680000000 }, rgb, DELTA);

		GraphicsUtils.xyz2rgb(new double[] { 1.0, 1.0, 1.0 }, rgb);
		assertEqualsArray(new double[] {  1.0115059552563181, 0.9870652513165344, 1.1020986165231543 }, rgb, DELTA);

		GraphicsUtils.xyz2rgb(new double[] { 0.96422103200000000, 0.99999914400000000, 0.82521159400000000 }, rgb);
		assertEqualsArray(new double[] {  1.0000016663766877, 0.9999988622306825, 1.0000011147183525 }, rgb, DELTA);
	}

	@Test
	public void testXyz2Luv() {
		double[] luv = new double[3];

		GraphicsUtils.xyz2luv(new double[] { 0.0, 0.0, 0.0 }, luv);
		assertEqualsArray(new double[] {  0.00000000000000,   0.0000000000000000,   0.000000000000000 }, luv, DELTA);

		GraphicsUtils.xyz2luv(new double[] { 0.0, 0.5, 1.0 }, luv);
		assertEqualsArray(new double[] { 76.06926101415557,-206.8386281184853600, -58.841402958935040 }, luv, DELTA);

		GraphicsUtils.xyz2luv(new double[] { 0.5, 0.5, 0.5 }, luv);
		assertEqualsArray(new double[] { 76.06926101415557,   1.3509283413088070, -14.229355146122007 }, luv, DELTA);

		GraphicsUtils.xyz2luv(new double[] { 1.0, 0.5, 0.0 }, luv);
		assertEqualsArray(new double[] { 76.06926101415557, 258.5262627916428000,  40.879645093235310 }, luv, DELTA);

		GraphicsUtils.xyz2luv(new double[] { 1.0, 1.0, 1.0 }, luv);
		assertEqualsArray(new double[] {100.00000000000000,   1.7759188446137468, -18.705788588473464 }, luv, DELTA);

		GraphicsUtils.xyz2luv(new double[] { 0.0012612441091978614, 0.0011070564598794539,-0.00007748319319977360 }, luv);
		assertEqualsArray(new double[] {  1.00000000000000,   1.0000000000000000,   1.000000000000000 }, luv, DELTA);
	}

	@Test
	public void testRgb2Luv() {
		double[] luv = new double[3];

		GraphicsUtils.rgb2luv(new double[] { 0.0, 0.0, 0.0 }, luv);
		assertEqualsArray(new double[] {  0.00000000000000,   0.00000000000000000000,   0.000000000000000000000 }, luv, DELTA);

		GraphicsUtils.rgb2luv(new double[] { 0.0, 0.5, 1.0 }, luv);
		assertEqualsArray(new double[] { 53.39149927909138, -34.19278610160572000000,-101.728990843596360000000 }, luv, DELTA);

		GraphicsUtils.rgb2luv(new double[] { 0.5, 0.5, 0.5 }, luv);
		assertEqualsArray(new double[] { 53.38894494212407,   0.00009162075020892017,  -0.000087416607597709690 }, luv, DELTA);

		GraphicsUtils.rgb2luv(new double[] { 1.0, 0.5, 0.0 }, luv);
		assertEqualsArray(new double[] { 67.71991853084089, 107.44568793469313000000,  46.013808290473584000000 }, luv, DELTA);

		GraphicsUtils.rgb2luv(new double[] { 1.0, 1.0, 1.0 }, luv);
		assertEqualsArray(new double[] { 99.99996690132389,   0.00017160990909819560,  -0.000163735355247670150 }, luv, DELTA);
	}
}

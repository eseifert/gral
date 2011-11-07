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
package de.erichseifert.gral.util;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * Abstract class that contains utility functions for working with graphics.
 * For example, this includes font handling or color space conversion.
 */
public abstract class GraphicsUtils {
	/** Default font render context. */
	private static final FontRenderContext frc = new FontRenderContext(null, true, true);

	/** Constant for the CIE XYZ and CIE L*u*v* color spaces: (6/29)^3 **/
	private static final double CIE_EPSILON = 216.0/24389.0;
	/** Constant for the CIE XYZ and CIE L*u*v* color spaces: (29/3)^3 **/
	private static final double CIE_KAPPA = 24389.0/27.0;

	/** Xr, Yr, Zr constants with D50 white point used for CIE XYZ to
	CIE L*u*v* conversion **/
	private static final double[] XYZ_R_D50 = {
		0.964221, 1.000000, 0.825211
	};
	/** Precalculated u0 constant for CIE L*u*v* to CIE XYZ conversion. **/
	private static final double XYZ_R_D50_U0 =
		4.0*XYZ_R_D50[0]/(XYZ_R_D50[0] + 15.0*XYZ_R_D50[1] + 3.0*XYZ_R_D50[2]);
	/** Precalculated v0 constant for CIE L*u*v* to CIE XYZ conversion. **/
	private static final double XYZ_R_D50_V0 =
		9.0*XYZ_R_D50[1]/(XYZ_R_D50[0] + 15.0*XYZ_R_D50[1] + 3.0*XYZ_R_D50[2]);

	/** sRGB to CIE XYZ conversion matrix. See
	http://www.brucelindbloom.com/index.html?WorkingSpaceInfo.html#Specifications **/
	private static final double[] MATRIX_SRGB2XYZ_D50 = {
		0.436052025, 0.385081593, 0.143087414,
		0.222491598, 0.716886060, 0.060621486,
		0.013929122, 0.097097002, 0.714185470
	};

	/** CIE XYZ to sRGB conversion matrix. See
	http://www.brucelindbloom.com/index.html?WorkingSpaceInfo.html#Specifications **/
	private static final double[] MATRIX_XYZ2SRGB_D50 = {
		 3.1338561, -1.6168667, -0.4906146,
		-0.9787684,  1.9161415,  0.0334540,
		 0.0719453, -0.2289914,  1.4052427
	};

	/**
	 * Default constructor that prevents creation of class.
	 */
	protected GraphicsUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the layout for the specified text with the specified font.
	 * @param text Text to be displayed.
	 * @param font Font of the Text.
	 * @return TextLayout.
	 */
	public static TextLayout getLayout(String text, Font font) {
		TextLayout layout = new TextLayout(text, font, frc);
		return layout;
	}

	/**
	 * Fills a Shape with the specified Paint object.
	 * @param graphics Graphics to be painted into.
	 * @param shape Shape to be filled.
	 * @param paint Paint to be used.
	 * @param paintBounds Optional bounds describing the painted area.
	 */
	public static void fillPaintedShape(Graphics2D graphics, Shape shape,
			Paint paint, Rectangle2D paintBounds) {
		if (shape == null) {
			return;
		}
		if (paintBounds == null) {
			paintBounds = shape.getBounds2D();
		}
		AffineTransform txOrig = graphics.getTransform();
		graphics.translate(paintBounds.getX(), paintBounds.getY());
		graphics.scale(paintBounds.getWidth(), paintBounds.getHeight());
		Paint paintOld = null;
		if (paint != null) {
			paintOld = graphics.getPaint();
			graphics.setPaint(paint);
		}
		AffineTransform tx = AffineTransform.getScaleInstance(
				1.0/paintBounds.getWidth(), 1.0/paintBounds.getHeight());
		tx.translate(-paintBounds.getX(), -paintBounds.getY());
		graphics.fill(tx.createTransformedShape(shape));
		if (paintOld != null) {
			graphics.setPaint(paintOld);
		}
		graphics.setTransform(txOrig);
	}

	/**
	 * Draws a filled Shape with the specified Paint object.
	 * @param graphics Graphics to be painted into.
	 * @param shape Shape to be filled.
	 * @param paint Paint to be used.
	 * @param paintBounds Optional bounds describing the painted area.
	 * @param stroke Stroke to be used for outlines.
	 */
	public static void drawPaintedShape(Graphics2D graphics, Shape shape,
			Paint paint, Rectangle2D paintBounds, Stroke stroke) {
		if (shape == null) {
			return;
		}
		if (stroke == null) {
			stroke = graphics.getStroke();
		}
		shape = stroke.createStrokedShape(shape);
		fillPaintedShape(graphics, shape, paint, paintBounds);
	}

	/**
	 * Converts color components from the sRGB to the CIE XYZ color space.
	 * A D50 white point is assumed for the sRGB conversion. If the <i>xyz</i>
	 * array is <code>null</code>, a new one will be created with the same
	 * size as the <i>rgb</i> array.
	 *
	 * See http://www.brucelindbloom.com/index.html?Eqn_RGB_to_XYZ.html
	 *
	 * @param rgb Color components in the sRGB color space.
	 * @param xyz Optional array to store color components in the CIE XYZ color space.
	 * @return Color components in the CIE XYZ color space.
	 */
	public static double[] rgb2xyz(double[] rgb, double[] xyz) {
		if (xyz == null) {
			xyz = new double[rgb.length];
		}

		// Remove sRGB companding to make RGB components linear
		double[] rgbLin = new double[rgb.length];
		for (int i = 0; i < rgb.length; i++) {
			if (rgb[i] <= 0.04045) {
				rgbLin[i] = rgb[i]/12.92;
			} else {
				rgbLin[i] = Math.pow((rgb[i] + 0.055)/1.055, 2.4);
			}
		}

		// Convert linear sRGB with D50 white point to CIE XYZ
		for (int i = 0; i < xyz.length; i++) {
			xyz[i] = MATRIX_SRGB2XYZ_D50[i*3 + 0]*rgbLin[0] +
                     MATRIX_SRGB2XYZ_D50[i*3 + 1]*rgbLin[1] +
                     MATRIX_SRGB2XYZ_D50[i*3 + 2]*rgbLin[2];
		}

		return xyz;
	}

	/**
	 * Convert color components from the CIE L*u*v* to the CIE XYZ color space.
	 * If the <i>xyz</i> array is <code>null</code>, a new one will be created
	 * with the same size as the <i>luv</i> array.
	 *
	 * See http://www.brucelindbloom.com/index.html?Eqn_Luv_to_XYZ.html
	 *
	 * @param luv Color components in the CIE L*u*v* color space
	 * @param xyz Optional array to store color components in the CIE XYZ color
	 *            space.
	 * @return Color components in the CIE XYZ color space.
	 */
	public static double[] luv2xyz(double[] luv, double[] xyz) {
		if (xyz == null) {
			xyz = new double[luv.length];
		}

		if (luv[0] > CIE_KAPPA*CIE_EPSILON) {
			xyz[1] = (luv[0] + 16.0)/116.0;
			xyz[1] = xyz[1]*xyz[1]*xyz[1];
		} else {
			xyz[1] = luv[0]/CIE_KAPPA;
		}

		double a = (luv[0] != 0.0 || luv[1] != 0.0)
			? ((52.0*luv[0])/(luv[1] + 13.0*luv[0]*XYZ_R_D50_U0) - 1.0)/3.0
			: 0.0;
		double b = -5*xyz[1];
		double c = -1.0/3.0;
		double d = (luv[0] != 0.0 || luv[2] != 0.0)
			? xyz[1]*((39.0*luv[0])/(luv[2] + 13.0*luv[0]*XYZ_R_D50_V0) - 5.0)
			: 0.0;

		xyz[0] = (a != c) ? (d - b)/(a - c) : 0.0;
		xyz[2] = xyz[0]*a + b;

		return xyz;
	}

	/**
	 * Converts color components from the sRGB to the CIE XYZ color space.
	 * A D50 white point is assumed for the sRGB conversion. If the <i>rgb</i>
	 * array is <code>null</code>, a new one will be created with the same
	 * size as the <i>xyz</i> array.
	 *
	 * See http://www.brucelindbloom.com/index.html?Eqn_XYZ_to_RGB.html
	 *
	 * @param xyz Color components in the CIE XYZ color space.
	 * @param rgb Optional array for storing color components in the sRGB color
	 *            space.
	 * @return Color components in the sRGB color space.
	 */
	public static double[] xyz2rgb(double[] xyz, double[] rgb) {
		if (rgb == null) {
			rgb = new double[xyz.length];
		}

		// XYZ to linear sRGB with D50 white point
		for (int i = 0; i < xyz.length; i++) {
			rgb[i] = MATRIX_XYZ2SRGB_D50[i*3 + 0]*xyz[0] +
					 MATRIX_XYZ2SRGB_D50[i*3 + 1]*xyz[1] +
					 MATRIX_XYZ2SRGB_D50[i*3 + 2]*xyz[2];
		}

		// Apply sRGB companding
		for (int i = 0; i < rgb.length; i++) {
			if (rgb[i] <= 0.0031308) {
				rgb[i] = 12.92*rgb[i];
			} else {
				rgb[i] = 1.055*Math.pow(rgb[i], 1.0/2.4) - 0.055;
			}
		}

		return rgb;
	}

	/**
	 * Converts color components from the CIE XYZ to the CIE L*u*v* color
	 * space. If the <i>luv</i> array is <code>null</code>, a new one will be
	 * created with the same size as the <i>xyz</i> array.
	 *
	 * http://www.brucelindbloom.com/index.html?Eqn_XYZ_to_Luv.html
	 *
	 * @param xyz Color components in the CIE XYZ color space.
	 * @param luv Optional array for storing color components in the CIE L*u*v*
	 *            color space.
	 * @return Color components in the CIE L*u*v* color space.
	 */
	public static double[] xyz2luv(double[] xyz, double[] luv) {
		double tmp = xyz[0] + 15.0*xyz[1] + 3.0*xyz[2];
		if (tmp == 0.0) {
			tmp = 1.0;
		}
		double u1 = 4.0*xyz[0]/tmp;
		double v1 = 9.0*xyz[1]/tmp;

		// Relative luminance
		double yr = xyz[1]/XYZ_R_D50[1];
		double ur = 4.0*XYZ_R_D50[0]/(XYZ_R_D50[0] + 15.0*XYZ_R_D50[1] + 3.0*XYZ_R_D50[2]);
		double vr = 9.0*XYZ_R_D50[1]/(XYZ_R_D50[0] + 15.0*XYZ_R_D50[1] + 3.0*XYZ_R_D50[2]);

		// Mapping relative luminance to lightness
		if (luv == null) {
			luv = new double[xyz.length];
		}
		if (yr > CIE_EPSILON) {
			luv[0] = 116.0*Math.pow(yr, 1.0/3.0) - 16.0;
		} else {
			luv[0] = CIE_KAPPA*yr;
		}
		luv[1] = 13.0*luv[0]*(u1 - ur);
		luv[2] = 13.0*luv[0]*(v1 - vr);

		return luv;
	}

	/**
	 * Converts color components from the CIE L*u*v* to the sRGB color space.
	 * A D50 white point is assumed for the sRGB conversion. If the <i>luv</i>
	 * array is <code>null</code>, a new one will be created with the same
	 * size as the <i>rgb</i> array.
	 *
	 * @param rgb Color components in the sRGB color space.
	 * @param luv Optional array for storing color components in the CIE L*u*v*
	 *            color space.
	 * @return Color components in the CIE L*u*v* color space.
	 */
	public static double[] rgb2luv(double[] rgb, double[] luv) {
		double[] xyz = rgb2xyz(rgb, null);
		return xyz2luv(xyz, luv);
	}

	/**
	 * Converts color components from the CIE L*u*v* to the sRGB color space.
	 * A D50 white point is assumed for the sRGB conversion. If the <i>rgb</i>
	 * array is <code>null</code>, a new one will be created with the same size
	 * as the <i>luv</i> array.
	 *
	 * @param luv Color components in the CIE L*u*v* color space.
	 * @param rgb Optional array for storing color components in the sRGB color
	 *            space.
	 * @return Color components in sRGB color space.
	 */
	public static double[] luv2rgb(double[] luv, double[] rgb) {
		double[] xyz = luv2xyz(luv, null);
		return xyz2rgb(xyz, rgb);
	}
}

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
package de.erichseifert.gral.plots.colors;

import java.awt.Color;
import java.awt.Paint;

import de.erichseifert.gral.util.MathUtils;

/**
 * Class that generates different color shades for values between 0.0 and 1.0.
 */
public class HeatMap extends ScaledColorMapper {
	private static final Color[] COLORS = {
	    new Color(0.0f, 0.0f, 0.0f),
	    new Color(0.0f, 0.0f, 1.0f),
	    new Color(1.0f, 0.0f, 0.0f),
	    new Color(1.0f, 1.0f, 0.0f),
	    new Color(1.0f, 1.0f, 1.0f)
	};

	/**
	 * Creates a new instance.
	 */
	public HeatMap() {
	}

	/**
	 * Returns the Paint according to the specified value.
	 * @param value Value of color.
	 * @return Paint.
	 */
	public Paint get(double value) {
		double x = scale(value);
		double xInv = 1.0 - x;
		double xInv2 = xInv*xInv;
		double x2 = x*x;

		// Bernstein coefficients
		double[] coeffs = {
			xInv2*xInv2,
			4.0*x*xInv2*xInv,
			6.0*x2*xInv2,
			4.0*x*x2*xInv,
			x2*x2
		};

		double r = 0.0, g = 0.0, b = 0.0, a = 0.0;
		for (int i = 0; i < COLORS.length; i++) {
			r += coeffs[i]*COLORS[i].getRed();
			g += coeffs[i]*COLORS[i].getGreen();
			b += coeffs[i]*COLORS[i].getBlue();
			a += coeffs[i]*COLORS[i].getAlpha();
		}

		return new Color(
			(float) MathUtils.limit(r, 0.0, 255.0)/255f,
			(float) MathUtils.limit(g, 0.0, 255.0)/255f,
			(float) MathUtils.limit(b, 0.0, 255.0)/255f,
			(float) MathUtils.limit(a, 0.0, 255.0)/255f
		);
	}
}

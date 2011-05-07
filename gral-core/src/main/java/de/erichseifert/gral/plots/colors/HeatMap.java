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

/**
 * Class that generates shades of gray for values between 0.0 and 1.0.
 */
public class HeatMap implements ColorMapper {
	private static final Color COLOR1 = new Color(0.0f, 0.0f, 0.0f);
	private static final Color COLOR2 = new Color(0.0f, 0.0f, 1.0f);
	private static final Color COLOR3 = new Color(1.0f, 0.0f, 0.0f);
	private static final Color COLOR4 = new Color(1.0f, 1.0f, 0.0f);
	private static final Color COLOR5 = new Color(1.0f, 1.0f, 1.0f);

	/**
	 * Creates a new instance.
	 */
	public HeatMap() {
	}

	/**
	 * Returns the Color according to the specified value.
	 * @param value Value of color.
	 * @return Color.
	 */
	public Color get(double value) {
		double x = 1.0 - value;
		double x2 = x*x;
		double value2 = value*value;

		// Bernstein coefficients
		double f1 = x2*x2;
		double f2 = 4.0*value*x2*x;
		double f3 = 6.0*value2*x2;
		double f4 = 4.0*value*value2*x;
		double f5 = value2*value2;

		return new Color(
			(float)(f1*COLOR1.getRed()   + f2*COLOR2.getRed()   + f3*COLOR3.getRed()   + f4*COLOR4.getRed()   + f5*COLOR5.getRed())  /255f,
			(float)(f1*COLOR1.getGreen() + f2*COLOR2.getGreen() + f3*COLOR3.getGreen() + f4*COLOR4.getGreen() + f5*COLOR5.getGreen())/255f,
			(float)(f1*COLOR1.getBlue()  + f2*COLOR2.getBlue()  + f3*COLOR3.getBlue()  + f4*COLOR4.getBlue()  + f5*COLOR5.getBlue()) /255f,
			(float)(f1*COLOR1.getAlpha() + f2*COLOR2.getAlpha() + f3*COLOR3.getAlpha() + f4*COLOR4.getAlpha() + f5*COLOR5.getAlpha())/255f
		);
	}
}

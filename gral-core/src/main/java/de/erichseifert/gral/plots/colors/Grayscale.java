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
public class Grayscale implements ColorMapper {

	/**
	 * Creates a new instance.
	 */
	public Grayscale() {
	}

	/**
	 * Returns the Color according to the specified value.
	 * @param value Value of color.
	 * @return Color.
	 */
	public Color get(double value) {
		return Color.getHSBColor(0f, 0f, (float) value);
	}
}

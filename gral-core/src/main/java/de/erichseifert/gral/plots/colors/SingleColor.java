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
 * Class that represents a ColorMapper with a single color.
 */
public class SingleColor implements ColorMapper {
	private Color color;

	/**
	 * Creates a new instance with the specified color.
	 * @param color Color to use.
	 */
	public SingleColor(Color color) {
		this.color = color;
	}

	@Override
	public Color get(double value) {
		return color;
	}

	/**
	 * Returns the color of this ColorMapper.
	 * @return Color.
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Sets the color of this ColorMapper.
	 * @param color Color to be set.
	 */
	public void setColor(Color color) {
		this.color = color;
	}

}

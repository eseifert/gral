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
package de.erichseifert.gral.plots.colors;

import java.awt.Paint;

/**
 * <p>A {@link ColorMapper} that returns the same paint for every value. This is
 * what a renderer stores when a plain color is set on it:
 * {@code pointRenderer.setColor(Color.RED)} is shorthand for
 * {@code pointRenderer.setColor(new SingleColor(Color.RED))}.</p>
 *
 * <p>The paint need not be a flat color &mdash; any {@code java.awt.Paint}
 * will do, so a gradient or a texture can be applied uniformly to every point
 * of a series.</p>
 */
public class SingleColor extends IndexedColorMapper {
	/** The color that will be returned in any case. */
	private Paint color;

	/**
	 * Creates a new instance with the specified color.
	 * @param color Color to use.
	 */
	public SingleColor(Paint color) {
		this.color = color;
	}

	/**
	 * Returns the Paint according to the specified value.
	 * @param value Numeric index.
	 * @return Paint.
	 */
	@Override
	public Paint get(int value) {
		return getColor();
	}

	/**
	 * Returns the color of this ColorMapper.
	 * @return Color.
	 */
	public Paint getColor() {
		return color;
	}

	/**
	 * Sets the color of this ColorMapper.
	 * @param color Color to be set.
	 */
	public void setColor(Paint color) {
		this.color = color;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SingleColor)) {
			return false;
		}
		SingleColor cm = (SingleColor) obj;
		return color.equals(cm.color) && getMode() == cm.getMode();
	}

	@Override
	public int hashCode() {
		long bits = getColor().hashCode();
		bits ^= getMode().hashCode() * 31;
		return ((int) bits) ^ ((int) (bits >> 32));
	}
}

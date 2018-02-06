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
package de.erichseifert.gral.plots.colors;

import java.awt.Paint;

import de.erichseifert.gral.util.MathUtils;

/**
 * Class that maps floating point numbers to Paint objects. This can be used to
 * generate colors or gradients for various elements in a plot, e.g. lines,
 * areas, etc.
 */
public abstract class ContinuousColorMapper extends AbstractColorMapper<Double> {
	/** Version id for serialization. */
	private static final long serialVersionUID = 4616781244057993699L;

	/**
	 * Returns the Paint object according to the specified value.
	 * @param value Numeric value.
	 * @return Paint object.
	 */
	public abstract Paint get(double value);

	/**
	 * Returns the Paint object according to the specified value. The specified
	 * value will be handled like a double value.
	 * @param value Numeric value object.
	 * @return Paint object.
	 */
	public Paint get(Number value) {
		return get(value.doubleValue());
	}

	@Override
	protected Double applyMode(Double value, Double rangeMin, Double rangeMax) {
		if (value >= rangeMin && value <= rangeMax) {
			return value;
		}
		Mode mode = getMode();
		if (mode == Mode.REPEAT) {
			return MathUtils.limit(value, rangeMin, rangeMax);
		} else if (mode == Mode.CIRCULAR) {
			double range = rangeMax - rangeMin;
			double i = value%range;
			if (i < 0.0) {
				i += range;
			}
			return i + rangeMin;
		}
		return null;
	}
}

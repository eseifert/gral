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

import java.awt.Color;
import java.awt.Paint;

import de.erichseifert.gral.util.MathUtils;

/**
 * <p>A gradient that sweeps through the hues of the color wheel for values
 * between 0.0 and 1.0, at full saturation and brightness.</p>
 *
 * <pre>
 * RainbowColors colors = new RainbowColors();
 * colors.setRange(0.0, 1.0);
 * </pre>
 *
 * <p>The hues are easy to tell apart but their perceived brightness does not
 * rise with the value, so the ordering of the colors is not obvious to a
 * reader and is lost in grayscale. Prefer {@link HeatMap} or
 * {@link Grayscale} where the values are to be compared rather than merely
 * distinguished.</p>
 */
public class RainbowColors extends ScaledContinuousColorMapper {
	/**
	 * Returns the Paint according to the specified value.
	 * @param value Value of color.
	 * @return Paint.
	 */
	@Override
	public Paint get(double value) {
		Double v = scale(value);
		v = applyMode(v, 0.0, 1.0);
		if (!MathUtils.isCalculatable(v)) {
			return null;
		}

		float hue = v.floatValue();
		return Color.getHSBColor(hue, 1f, 1f);
	}

	@Override
	public void setMode(Mode mode) {
		super.setMode(mode);
	}
}

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

import java.awt.Color;
import java.awt.Paint;

import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.MathUtils;

/**
 * Class that generates shades of gray for values between 0.0 and 1.0.
 */
public class Grayscale extends ScaledContinuousColorMapper {
	/** Version id for serialization. */
	private static final long serialVersionUID = -1005712209663359529L;

	/**
	 * Returns the Paint object according to the specified value.
	 * @param value Value of color.
	 * @return Paint object.
	 */
	@Override
	public Paint get(double value) {
		Double v = scale(value);
		v = applyMode(v, 0.0, 1.0);
		if (!MathUtils.isCalculatable(v)) {
			return null;
		}
		double lightness = 100.0*v;
		double[] rgb = GraphicsUtils.luv2rgb(new double[] {lightness, 0.0, 0.0}, null);
		return new Color(
			(float) MathUtils.limit(rgb[0], 0.0, 1.0),
			(float) MathUtils.limit(rgb[1], 0.0, 1.0),
			(float) MathUtils.limit(rgb[2], 0.0, 1.0)
		);
	}

	@Override
	public void setMode(Mode mode) {
		super.setMode(mode);
	}
}

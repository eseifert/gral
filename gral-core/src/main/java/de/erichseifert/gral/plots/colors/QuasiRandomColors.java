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
import java.util.HashMap;
import java.util.Map;

import de.erichseifert.gral.util.HaltonSequence;
import de.erichseifert.gral.util.MathUtils;

/**
 * Class that generates seemingly random colors for specified index values.
 */
public class QuasiRandomColors extends IndexedColorMapper {
	/** Version id for serialization. */
	private static final long serialVersionUID = 3320256963368776894L;

	/** Object for mapping a plot value to a hue. */
	private final HaltonSequence seqHue = new HaltonSequence(3);
	/** Object for mapping a plot value to a saturation. */
	private final HaltonSequence seqSat = new HaltonSequence(5);
	/** Object for mapping a plot value to a brightness. */
	private final HaltonSequence seqBrightness = new HaltonSequence(2);
	/** Cache for colors that have already been generated. */
	private final Map<Integer, Color> colorCache;
	/** Variance settings for hue, saturation and brightness. */
	//FIXME duplicate code! See RandomColors
	private float[] colorVariance;

	/**
	 * Creates a new QuasiRandomColors object with default color variance.
	 */
	public QuasiRandomColors() {
		colorCache = new HashMap<>();
		colorVariance = new float[] {
			0.00f, 1.00f,  // Hue
			0.75f, 0.25f,  // Saturation
			0.25f, 0.75f   // Brightness
		};
	}

	/**
	 * Returns the Paint associated to the specified index value.
	 * @param index Numeric index.
	 * @return Paint object.
	 */
	@Override
	public Paint get(int index) {
		Integer key = index;
		if (colorCache.containsKey(key)) {
			return colorCache.get(key);
		}
		float[] colorVariance = getColorVariance();
		float hue = colorVariance[0] + colorVariance[1]*seqHue.next().floatValue();
		float saturation = colorVariance[2] + colorVariance[3]*seqSat.next().floatValue();
		float brightness = colorVariance[4] + colorVariance[5]*seqBrightness.next().floatValue();
		Color color = Color.getHSBColor(
			hue,
			MathUtils.limit(saturation, 0f, 1f),
			MathUtils.limit(brightness, 0f, 1f)
		);
		colorCache.put(key, color);
		return color;
	}

	/**
	 * Returns the current color variance.
	 * @return Range of hue, saturation and brightness a color can have.
	 */
	public float[] getColorVariance() {
		return colorVariance;
	}

	/**
	 * Sets the current color variance.
	 * @param colorVariance Range of hue, saturation and brightness a color
	 *        can have.
	 */
	public void setColorVariance(float[] colorVariance) {
		this.colorVariance = colorVariance;
	}
}

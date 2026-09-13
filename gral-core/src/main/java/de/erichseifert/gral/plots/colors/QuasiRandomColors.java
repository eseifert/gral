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
import java.util.HashMap;
import java.util.Map;

import de.erichseifert.gral.util.HaltonSequence;
import de.erichseifert.gral.util.MathUtils;

/**
 * <p>An {@link IndexedColorMapper} that spreads hues using a
 * {@link de.erichseifert.gral.util.HaltonSequence}. The colors look arbitrary
 * but are evenly distributed, so each new index lands in a gap left by the
 * previous ones and consecutive series stay easy to tell apart.</p>
 *
 * <pre>
 * QuasiRandomColors colors = new QuasiRandomColors();
 * pointRenderer.setColor(colors);
 * </pre>
 *
 * <p>The sequence is deterministic: the same index always yields the same
 * color, so a plot looks the same on every run. That is the practical
 * difference to {@link RandomColors}. The permitted spread of hue, saturation
 * and brightness is set with {@link #setHue(float, float)},
 * {@link #setSaturation(float, float)} and {@link #setBrightness(float, float)},
 * or with {@link #setColorVariance(float[])} in one go.</p>
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
		return colorCache.computeIfAbsent(index, key -> {
			float[] colorVariance = getColorVariance();
			float hue = colorVariance[0] + colorVariance[1]*seqHue.get(key).floatValue();
			float saturation = colorVariance[2] + colorVariance[3]*seqSat.get(key).floatValue();
			float brightness = colorVariance[4] + colorVariance[5]*seqBrightness.get(key).floatValue();
			return Color.getHSBColor(
				hue,
				MathUtils.limit(saturation, 0f, 1f),
				MathUtils.limit(brightness, 0f, 1f)
			);
		});
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
		colorCache.clear();
	}

	/**
	 * Sets the range of hues a color can have. A hue is an angle on the color
	 * wheel expressed as a fraction of a full turn, so {@code 0.0} and
	 * {@code 1.0} both stand for red.
	 * @param min Smallest hue.
	 * @param max Largest hue.
	 */
	public void setHue(float min, float max) {
		setRange(0, min, max);
	}

	/**
	 * Sets the range of saturations a color can have, from {@code 0.0} for
	 * gray to {@code 1.0} for a fully saturated color.
	 * @param min Smallest saturation.
	 * @param max Largest saturation.
	 */
	public void setSaturation(float min, float max) {
		setRange(2, min, max);
	}

	/**
	 * Sets the range of brightnesses a color can have, from {@code 0.0} for
	 * black to {@code 1.0} for a fully lit color.
	 * @param min Smallest brightness.
	 * @param max Largest brightness.
	 */
	public void setBrightness(float min, float max) {
		setRange(4, min, max);
	}

	/**
	 * Stores a range as the offset and the extent the color variance is made
	 * of.
	 * @param offset Index of the offset in the color variance.
	 * @param min Smallest value of the range.
	 * @param max Largest value of the range.
	 */
	private void setRange(int offset, float min, float max) {
		if (max < min) {
			throw new IllegalArgumentException(
				"The upper bound must not be smaller than the lower bound."); //$NON-NLS-1$
		}
		colorVariance[offset] = min;
		colorVariance[offset + 1] = max - min;
		colorCache.clear();
	}
}

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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import de.erichseifert.gral.util.MathUtils;

/**
 * Class that generates pseudo-random colors for specified index values.
 */
public class RandomColors extends IndexedColorMapper {
	/** Version id for serialization. */
	private static final long serialVersionUID = -4518470000665474457L;

	/** Number of comparisons that will be done before accepting two similar
	random values. */
	private static final int NUM_COMPARISONS = 4;
	/** Minimal distance the causes a random values to be to re-generated. */
	private static final double MIN_DIST = 0.3;

	/** Cache for colors that have already been generated. */
	private final Map<Integer, Color> colorCache;
	/** Object for generating random values. */
	private final Random random;
	/** Variance settings for hue, saturation and brightness. */
	//FIXME duplicate code! See QuasiRandomColors
	private final float[] colorVariance;

	/**
	 * Creates a new RandomColors object with default seed.
	 */
	public RandomColors() {
		random = new Random();
		colorCache = new LinkedHashMap<>();
		colorVariance = new float[] {
			0.00f, 1.00f,  // Hue
			0.75f, 0.25f,  // Saturation
			0.25f, 0.75f   // Brightness
		};
	}

	/**
	 * Creates a new instances with the specified seed.
	 * @param seed Random number seed.
	 */
	public RandomColors(long seed) {
		this();
		random.setSeed(seed);
	}

	/**
	 * Returns the Paint associated to the specified index value.
	 * @param index Numeric index.
	 * @return Paint.
	 */
	@Override
	public Paint get(int index) {
		Integer key = index;
		if (colorCache.containsKey(key)) {
			return colorCache.get(key);
		}

		// Use the same random numbers for the same input value
		//long seed = Double.doubleToRawLongBits(value);
		//random.setSeed(seed);

		// Generate a new color that is distant enough from previous colors
		boolean match;
		Color r;
		do {
			r = getRandomColor();
			match = true;
			Iterator<Color> colors = colorCache.values().iterator();
			for (int i=0; i<NUM_COMPARISONS && colors.hasNext(); i++) {
				Color prev = colors.next();
				if (distanceSq(r, prev) < MIN_DIST*MIN_DIST) {
					match = false;
					break;
				}
			}
		} while (!match);

		// Remember previous colors to avoid similarities
		colorCache.put(key, r);

		return r;
	}

	/**
	 * Generates a random color with the current color variance.
	 * @return Color.
	 */
	private Color getRandomColor() {
		float[] colorVariance = getColorVariance();
		float hue = colorVariance[0] + colorVariance[1]*random.nextFloat();
		float saturation = colorVariance[2] + colorVariance[3]*random.nextFloat();
		float brightness = colorVariance[4] + colorVariance[5]*random.nextFloat();
		return Color.getHSBColor(
			hue,
			MathUtils.limit(saturation, 0f, 1f),
			MathUtils.limit(brightness, 0f, 1f)
		);
	}

	/**
	 * Calculates the square of the distance between the two specified
	 * Colors.
	 * @param a Color.
	 * @param b Color.
	 * @return Square of distance.
	 */
	private static double distanceSq(Color a, Color b) {
		double rMean = (a.getRed() + b.getRed()) / 256.0 / 2.0;
		double dr = (a.getRed()   - b.getRed())   / 256.0;
		double dg = (a.getGreen() - b.getGreen()) / 256.0;
		double db = (a.getBlue()  - b.getBlue())  / 256.0;
		double d = (2.0 + rMean)*dr*dr + 4.0*dg*dg + (2.0 + 1.0-rMean)*db*db;
		return d / 9.0;
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
		System.arraycopy(colorVariance, 0, this.colorVariance, 0, this.colorVariance.length);
	}
}

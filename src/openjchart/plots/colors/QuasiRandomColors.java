/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.plots.colors;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import openjchart.util.HaltonSequence;

/**
 * Class that generates seemingly random colors.
 */
public class QuasiRandomColors implements ColorMapper {
	private HaltonSequence seqHue = new HaltonSequence(3);
	private HaltonSequence seqSat = new HaltonSequence(5);
	private HaltonSequence seqBrightness = new HaltonSequence(2);
	private final Map<Double, Color> colorCache;
	//FIXME : duplicate code! See openjchart.plots.colors.RandomColors
	private float[] colorVariance;

	/**
	 * Creates a new QuasiRandomColors object with default color variance.
	 */
	public QuasiRandomColors() {
		colorCache = new HashMap<Double, Color>();
		colorVariance = new float[] {
			0.00f, 1.00f,  // Hue
			0.75f, 0.25f,  // Saturation
			0.25f, 0.75f   // Brightness
		};
	}

	@Override
	public Color get(double value) {
		if (colorCache.containsKey(value)) {
			return colorCache.get(value);
		}
		float hue        = colorVariance[0] + colorVariance[1]*(float)(double)seqHue.next();
		float saturation = colorVariance[2] + colorVariance[3]*(float)(double)seqSat.next();
		float brightness = colorVariance[4] + colorVariance[5]*(float)(double)seqBrightness.next();
		Color color = Color.getHSBColor(hue, saturation, brightness);
		colorCache.put(value, color);
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
	 * @param Range of hue, saturation and brightness a color can have.
	 */
	public void setColorVariance(float[] colorVariance) {
		this.colorVariance = colorVariance;
	}

}

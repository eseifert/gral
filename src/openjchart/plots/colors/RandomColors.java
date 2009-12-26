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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class RandomColors implements ColorMapper {
	private static final int NUM_COMPARISONS = 4;
	private static final double MIN_DIST = 0.3;

	private final Map<Double, Color> colorCache;
	private Random random;
	private float[] colorVariance;

	public RandomColors() {
		random = new Random();
		colorCache = new LinkedHashMap<Double, Color>();
	}

	public RandomColors(long seed) {
		this();
		random = new Random(seed);
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
		
		// Use the same random numbers for the same input value
		//long seed = Double.doubleToRawLongBits(value);
		//random.setSeed(seed);

		// Generate a new color that is distant enough from previous colors
		boolean match = false;
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
		colorCache.put(value, r);

		return r;
	}

	private Color getRandomColor() {
		float hue        = colorVariance[0] + colorVariance[1]*random.nextFloat();
		float saturation = colorVariance[2] + colorVariance[3]*random.nextFloat();
		float brightness = colorVariance[4] + colorVariance[5]*random.nextFloat();
		return Color.getHSBColor(hue, saturation, brightness);
	}

	private static double distanceSq(Color a, Color b) {
		double rMean = (a.getRed() + b.getRed()) / 256.0 / 2.0;
		double dr = (a.getRed()   - b.getRed())   / 256.0;
		double dg = (a.getGreen() - b.getGreen()) / 256.0;
		double db = (a.getBlue()  - b.getBlue())  / 256.0;
		double d = (2.0 + rMean)*dr*dr + (4.0)*dg*dg + (2.0 + 1.0-rMean)*db*db;
		return d / 9.0;
	}

	public float[] getColorVariance() {
		return colorVariance;
	}

	public void setColorVariance(float[] colorVariance) {
		this.colorVariance = colorVariance;
	}

}

package openjchart.charts.colors;

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

	public RandomColors() {
		random = new Random();
		colorCache = new LinkedHashMap<Double, Color>();
	}

	public RandomColors(long seed) {
		this();
		random = new Random(seed);
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
		float hue        = 0.0f + 1.0f*random.nextFloat();
		float saturation = 0.7f + 0.3f*random.nextFloat();
		float brightness = 0.3f + 0.7f*random.nextFloat();
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

}

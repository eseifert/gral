package openjchart.charts.colors;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class RandomColors implements ColorMapper {
	private static final int NUM_COMPARISONS = 3;
	private static final float MIN_DIST = 0.2f;
	private Random random = new Random();
	private Deque<Color> previousColors;

	public RandomColors() {
		this(1l);
	}

	public RandomColors(long seed) {
		random.setSeed(seed);
		previousColors = new ArrayDeque<Color>(NUM_COMPARISONS);
	}

	@Override
	public Color get(double value) {
		boolean match = false;
		Color r;
		do {
			r = getRandomColor();
			match = true;
			for (Color prev : previousColors) {
				if (distanceSq(r, prev) < MIN_DIST*MIN_DIST) {
					match = false;
					break;
				}
			}
		} while (!match);
		previousColors.addFirst(r);
		if (previousColors.size() > NUM_COMPARISONS) {
			previousColors.removeLast();
		}
		return r;
	}

	private Color getRandomColor() {
		float hue        = 0.0f + 1.0f*random.nextFloat();
		float saturation = 0.7f + 0.3f*random.nextFloat();
		float brightness = 0.3f + 0.7f*random.nextFloat();
		return Color.getHSBColor(hue, saturation, brightness);
	}

	private static float distanceSq(Color a, Color b) {
		float[] hsbA = new float[3];
		float[] hsbB = new float[3];
		Color.RGBtoHSB(a.getRed(), a.getGreen(), a.getBlue(), hsbA);
		Color.RGBtoHSB(b.getRed(), b.getGreen(), b.getBlue(), hsbB);
		float dh = hsbA[0]-hsbB[0];
		float ds = hsbA[1]-hsbB[1];
		float db = hsbA[2]-hsbB[2];
		return dh*dh + ds*ds + db*db;
	}

}

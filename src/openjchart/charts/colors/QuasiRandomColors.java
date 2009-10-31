package openjchart.charts.colors;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import openjchart.util.HaltonSequence;

public class QuasiRandomColors implements ColorMapper {
	private HaltonSequence seqHue = new HaltonSequence(3);
	private HaltonSequence seqSat = new HaltonSequence(5);
	private HaltonSequence seqBrightness = new HaltonSequence(2);
	private final Map<Double, Color> colorCache;
	private float[] colorVariance;

	public float[] getColorVariance() {
		return colorVariance;
	}

	public void setColorVariance(float[] colorVariance) {
		this.colorVariance = colorVariance;
	}

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
}

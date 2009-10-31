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

	public QuasiRandomColors() {
		colorCache = new HashMap<Double, Color>();
	}

	@Override
	public Color get(double value) {
		if (colorCache.containsKey(value)) {
			return colorCache.get(value);
		}
		float hue        = 0.00f + 1.00f*(float)(double)seqHue.next();
		float saturation = 0.75f + 0.25f*(float)(double)seqSat.next();
		float brightness = 0.25f + 0.75f*(float)(double)seqBrightness.next();
		Color color = Color.getHSBColor(hue, saturation, brightness);
		colorCache.put(value, color);
		return color;
	}
}

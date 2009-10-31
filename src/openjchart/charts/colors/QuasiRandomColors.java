package openjchart.charts.colors;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import openjchart.util.HaltonSequence;

public class QuasiRandomColors implements ColorMapper {
	private HaltonSequence seqX = new HaltonSequence(2);
	private HaltonSequence seqY = new HaltonSequence(3);
	private HaltonSequence seqZ = new HaltonSequence(5);
	private final Map<Double, Color> colorCache;

	public QuasiRandomColors() {
		colorCache = new HashMap<Double, Color>();
	}
	
	@Override
	public Color get(double value) {
		if (colorCache.containsKey(value)) {
			return colorCache.get(value);
		}
		float hue        = 0.0f + 1.0f*(float)(double)seqX.next();
		float saturation = 0.7f + 0.3f*(float)(double)seqY.next();
		float brightness = 0.3f + 0.7f*(float)(double)seqZ.next();
		Color color = Color.getHSBColor(hue, saturation, brightness);
		colorCache.put(value, color);
		return color;
	}
}

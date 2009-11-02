package openjchart.plots.colors;

import java.awt.Color;

public class RainbowColors implements ColorMapper {
	@Override
	public Color get(double value) {
		return Color.getHSBColor((float)value, 1f, 1f);
	}
}

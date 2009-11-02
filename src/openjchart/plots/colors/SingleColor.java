package openjchart.plots.colors;

import java.awt.Color;

public class SingleColor implements ColorMapper {
	private Color color;
	
	public SingleColor(Color color) {
		this.color = color;
	}

	@Override
	public Color get(double value) {
		return color;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}

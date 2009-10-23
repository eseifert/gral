package openjchart.charts.axes;

import java.awt.Color;
import java.text.Format;

import openjchart.Drawable;

public abstract class AbstractAxisRenderer2D implements AxisRenderer2D {
	protected double tickSpacing;
	protected int tickLength;
	protected Color axisColor;
	protected Format labelFormat;

	/**
	 * Default constructor.
	 * The renderer is initialized with the following default values:
	 * ticks appear every <code>1</code> numbers and have a length of
	 * <code>10px</code>. The axis, the ticks and the labels are colored
	 * black.
	 */
	public AbstractAxisRenderer2D() {
		tickSpacing = 1d;
		tickLength = 10;
		axisColor = Color.BLACK;
	}

	@Override
	public abstract Drawable getRendererComponent(Axis axis, openjchart.charts.axes.AxisRenderer2D.Orientation orientation);

	public double getTickSpacing() {
		return tickSpacing;
	}

	public void setTickSpacing(double tickSpacing) {
		this.tickSpacing = tickSpacing;
	}

	public int getTickLength() {
		return tickLength;
	}

	public void setTickLength(int tickLength) {
		this.tickLength = tickLength;
	}

	public double getMinTick(double axisMin) {
		return Math.ceil(axisMin/tickSpacing) * tickSpacing;
	}

	public double getMaxTick(double axisMax) {
		return Math.floor(axisMax/tickSpacing) * tickSpacing;
	}

	public Color getAxisColor() {
		return axisColor;
	}

	public void setAxisColor(Color axisColor) {
		this.axisColor = axisColor;
	}

	public Format getLabelFormat() {
		return labelFormat;
	}

	public void setLabelFormat(Format labelFormat) {
		this.labelFormat = labelFormat;
	}

}
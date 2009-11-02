package openjchart.plots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;

import openjchart.data.DataSource;
import openjchart.data.DataTable;
import openjchart.plots.colors.ColorMapper;
import openjchart.plots.colors.QuasiRandomColors;

public class PiePlot extends Plot {
	public static final String KEY_RADIUS = "pieplot.radius";
	public static final String KEY_COLORS = "pieplot.colorlist";
	public static final String KEY_CLOCKWISE = "pieplot.clockwise";
	public static final String KEY_START = "pieplot.start";

	private DataSource data;
	private double degreesPerValue;
	private double[] startValues;

	public PiePlot(DataSource data) {
		setSettingDefault(KEY_RADIUS, 1.0);
		setSettingDefault(KEY_COLORS, new QuasiRandomColors());
		setSettingDefault(KEY_CLOCKWISE, true);
		setSettingDefault(KEY_START, 0.0);

		this.data = data;
		dataChanged(this.data);
		this.data.addDataListener(this);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform txOld = g2d.getTransform();

		// Paint pie
		Color colorOld = g2d.getColor();
		Insets insets = getInsets();
		double w = getWidth() - insets.left - insets.right;
		double h = getHeight() - insets.top - insets.bottom;
		double size = Math.min(w, h) * this.<Double>getSetting(KEY_RADIUS);
		g2d.translate(getWidth()/2d, getHeight()/2d);
		startValues[0] = getSetting(KEY_START);
		startValues[startValues.length-1] = Math.signum(degreesPerValue) * 360.0 + startValues[0];
		ColorMapper colorList = getSetting(KEY_COLORS);
		for (int i = 1; i < startValues.length;  i++) {
			g2d.setColor(colorList.get(i-1/(double)startValues.length));
			g2d.fill(new Arc2D.Double(-size/2d, -size/2d, size, size, startValues[i-1], startValues[i]-startValues[i-1], Arc2D.PIE));
		}
		g2d.setTransform(txOld);
		g2d.setColor(colorOld);
	}

	@Override
	public void dataChanged(DataSource data) {
		super.dataChanged(data);

		// Calculate sum of all values
		double colYSum = 0.0;
		for (int i = 0; i < data.getRowCount();  i++) {
			double val = data.get(0, i).doubleValue();
			// Ignore negative values
			if (val <= 0.0) {
				continue;
			}
			colYSum += val;
		}

		if (getSetting(KEY_CLOCKWISE)) {
			degreesPerValue = -360.0/colYSum;
		}
		else {
			degreesPerValue = 360.0/colYSum;
		}

		// Calculate starting angles
		startValues = new double[data.getRowCount()+1];
		for (int i = 1; i < data.getRowCount(); i++) {
			startValues[i] = startValues[i-1] + data.get(0, i-1).doubleValue() * degreesPerValue;
		}
	}
}

package openjchart.charts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.List;

import openjchart.data.DataTable;

public class PieChart extends Chart {
	public static final String KEY_RADIUS = "piechart.radius";
	public static final String KEY_COLOR_LIST = "piechart.colorlist";
	public static final String KEY_CLOCKWISE = "piechart.clockwise";
	public static final String KEY_START = "piechart.start";

	private DataTable data;
	private double degreesPerValue;

	public PieChart(DataTable data) {
		setSettingDefault(KEY_RADIUS, 1.0);
		setSettingDefault(KEY_COLOR_LIST, generateColors(data.getRowCount()));
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
		g2d.translate(getWidth()/2, getHeight()/2);
		double angleStart = getSetting(KEY_START);
		double angleStop = angleStart;
		for (int i = 0; i < data.getRowCount();  i++) {
			angleStop = data.get(0, i).doubleValue() * degreesPerValue;
			List<Color> colorList = getSetting(KEY_COLOR_LIST);
			g2d.setColor(colorList.get(i));
			g2d.fill(new Arc2D.Double(-size/2, -size/2, size, size, angleStart, angleStop, Arc2D.PIE));
			angleStart += angleStop;
		}
		g2d.setTransform(txOld);
		g2d.setColor(colorOld);
	}

	protected List<Color> generateColors(int count) {
		List<Color> colors = new ArrayList<Color>(count);
		float hueStep = 1f / count;
		for (int i = 0; i < count; i++) {
			float h;
			if ((i & 1) == 0) {
				h = i/2f * hueStep;
			}
			else {
				h = 0.5f + i/2f * hueStep;
			}
			Color color = Color.getHSBColor(h, 1f, 1f);
			colors.add(color);
		}

		return colors;
	}

	@Override
	public void dataChanged(DataTable data) {
		super.dataChanged(data);

		// Calculate sum of all values
		double colYSum = 0.0;
		for (Number[] row : data) {
			colYSum += row[0].doubleValue();
		}

		if (getSetting(KEY_CLOCKWISE)) {
			degreesPerValue = -360.0/colYSum;
		}
		else {
			degreesPerValue = 360.0/colYSum;
		}
	}
}

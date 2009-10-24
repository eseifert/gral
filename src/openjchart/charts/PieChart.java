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
	private DataTable data;
	private double radius;
	private double degreesPerValue;
	private List<Color> colorList;
	private boolean clockwise;
	private int start;

	public PieChart(DataTable data) {
		this.data = data;
		radius = 1.0;
		start = 0;
		clockwise = true;
		colorList = new ArrayList<Color>();
		colorList.add(Color.RED);
		colorList.add(Color.BLUE);
		colorList.add(Color.CYAN);
		colorList.add(Color.YELLOW);
		colorList.add(Color.MAGENTA);
		colorList.add(Color.GREEN);
		colorList.add(Color.GRAY);
		colorList.add(Color.DARK_GRAY);

		// Calculate sum of all values
		double colYSum = 0.0;
		for (Number[] row : data) {
			colYSum += row[0].doubleValue();
		}

		if (clockwise) {
			degreesPerValue = -360.0/colYSum;
		}
		else {
			degreesPerValue = 360.0/colYSum;
		}
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
		double size = Math.min(w, h) * radius;
		g2d.translate(getWidth()/2, getHeight()/2);
		double angleStart = start;
		double angleStop = angleStart;
		for (int i = 0; i < data.getRowCount();  i++) {
			angleStop = data.get(0, i).doubleValue() * degreesPerValue;
			g2d.setColor(colorList.get(i));
			g2d.fill(new Arc2D.Double(-size/2, -size/2, size, size, angleStart, angleStop, Arc2D.PIE));
			angleStart += angleStop;
		}
		g2d.setTransform(txOld);
		g2d.setColor(colorOld);
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public List<Color> getColorList() {
		return colorList;
	}

	public void setColorList(List<Color> colorList) {
		this.colorList = colorList;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public boolean isClockwise() {
		return clockwise;
	}

	public void setClockwise(boolean clockwise) {
		this.clockwise = clockwise;
	}

}

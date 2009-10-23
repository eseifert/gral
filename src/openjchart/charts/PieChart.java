package openjchart.charts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import openjchart.data.DataMapper;
import openjchart.data.DataTable;

public class PieChart implements Chart {
	private double radius;
	private List<Color> colorList;
	private boolean clockwise;
	private int start;

	public PieChart() {
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
	}

	@Override
	public JComponent getChartRenderer(final DataTable data, final DataMapper mapper) {
		// Calculate sum of all values
		double colYSum = 0.0;
		for (Number[] row : data) {
			colYSum += row[0].doubleValue();
		}

		final double degreesPerValue;
		if (clockwise) {
			degreesPerValue = -360.0/colYSum;
		}
		else {
			degreesPerValue = 360.0/colYSum;
		}

		PlotArea plotArea = new PlotArea() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				Graphics2D g2d = (Graphics2D) g;
				AffineTransform txOld = g2d.getTransform();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				// Paint pie
				Color colorOld = g2d.getColor();
				double size = Math.min(getWidth(), getHeight()) * radius;
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
		};

		return plotArea;
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

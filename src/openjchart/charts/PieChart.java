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
	public static enum Rotation {
		CLOCKWISE, COUNTER_CLOCKWISE
	}

	private int radius;
	private List<Color> colorList;
	private Rotation rotation;
	private int start;

	public PieChart() {
		radius = 200;
		start = 0;
		rotation = Rotation.CLOCKWISE;
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
		for (int i = 0; i < data.getRowCount(); i++) {
			colYSum += data.get(0, i).doubleValue();
		}

		final double degreesPerValue;
		if (rotation == Rotation.CLOCKWISE) {
			degreesPerValue = -360.0/colYSum;
			//start = start-360;
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
				g2d.translate((getWidth()-radius) / 2, (getHeight()-radius) / 2);
				double angleStart = 0.0d;
				double angleStop = 0.0d;
				for (int i = 0; i < data.getRowCount();  i++) {
					angleStop = data.get(0, i).doubleValue() * degreesPerValue;
					g2d.setColor(colorList.get(i));
					g2d.fill(new Arc2D.Double(0, 0, radius, radius, angleStart+start, angleStop+start, Arc2D.PIE));
					angleStart += angleStop;
				}
				g2d.setTransform(txOld);
				g2d.setColor(colorOld);
			}
		};

		return plotArea;
	}

}

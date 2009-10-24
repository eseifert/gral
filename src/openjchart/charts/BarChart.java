package openjchart.charts;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import openjchart.Drawable;
import openjchart.charts.axes.Axis;
import openjchart.charts.axes.AxisRenderer2D;
import openjchart.charts.axes.LinearRenderer2D;
import openjchart.charts.axes.AxisRenderer2D.Orientation;
import openjchart.data.DataSeries;
import openjchart.data.DataTable;

public class BarChart extends Chart {
	private DataTable data;
	private DataSeries series;

	private double minY;
	private double maxY;

	private Drawable axisYComp;

	public BarChart(DataTable data, DataSeries series) {
		this.data = data;
		this.series = series;

		minY = Double.MAX_VALUE;
		maxY = -Double.MAX_VALUE;
		for (Integer col : series.values()) {
			maxY = Math.max(maxY, data.getMax(col).doubleValue());
			minY = Math.min(minY, data.getMin(col).doubleValue());
		}

		Axis axisY = new Axis(minY, maxY);
		AxisRenderer2D axisYRenderer = new LinearRenderer2D();
		axisYComp = axisYRenderer.getRendererComponent(axisY, Orientation.VERTICAL);
		addAxis(axisY, axisYComp);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		AffineTransform txOld = g2d.getTransform();

		double w = getWidth() - axisYComp.getWidth()/2.0;
		double h = getHeight();
		double barGap = w / series.size();
		double plotXMin = axisYComp.getWidth() / 2;
		double plotXMax = plotXMin + w;
		Rectangle2D bar = new Rectangle2D.Double();
		Iterator<Integer> cols = series.values().iterator();
		for (int i = 0; cols.hasNext(); i++) {
			double barWidth = barGap / 2.0;
			double barHeight = h * data.getMax(cols.next()).doubleValue() / maxY;
			double barX = plotXMin - barWidth/2.0;
			double barY = h - barHeight;
			bar.setFrame(barX, barY, barWidth, barHeight);

			double transformX = barGap*i + barGap/2;
			g2d.translate(transformX, 0.0);
			g2d.fill(bar);
			g2d.setTransform(txOld);
		}

		g2d.setTransform(txOld);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);

		double w = 100.0;
		double h = height;
		double yCompX = 0.0;
		double yCompY = 0.0;
		axisYComp.setBounds(new Rectangle2D.Double(yCompX, yCompY, w, h));
	}
}

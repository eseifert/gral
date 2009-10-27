package openjchart.charts;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import openjchart.Drawable;
import openjchart.charts.axes.AbstractAxisRenderer2D;
import openjchart.charts.axes.Axis;
import openjchart.charts.axes.LinearRenderer2D;
import openjchart.data.DataSeries;
import openjchart.data.DataTable;

public class BarChart extends Chart {
	private DataTable data;
	private DataSeries series;

	private double minY;
	private double maxY;

	private AbstractAxisRenderer2D axisYRenderer;
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
		axisYRenderer = new LinearRenderer2D();
		axisYRenderer.setNormalOrientationClockwise(false);
		axisYComp = axisYRenderer.getRendererComponent(axisY);
		addAxis(axisY, axisYComp);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		AffineTransform txOld = g2d.getTransform();

		Insets insets = getInsets();
		double w = getWidth() - axisYComp.getWidth()/2.0 - insets.left - insets.right;
		double h = getHeight() - insets.top - insets.bottom;
		double barGap = w / series.size();
		double plotXMin = axisYComp.getWidth()/2 + insets.left;
		double plotXMax = plotXMin + w;
		double plotYMin = insets.top;
		double plotYMax = plotYMin + h;
		Rectangle2D bar = new Rectangle2D.Double();
		Iterator<Integer> cols = series.values().iterator();
		for (int i = 0; cols.hasNext(); i++) {
			double barWidth = barGap / 2.0;
			double barHeight = h * data.getMax(cols.next()).doubleValue() / maxY;
			double barX = plotXMin - barWidth/2.0;
			double barY = plotYMax - barHeight;
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

		Insets insets = getInsets();
		double w = axisYComp.getPreferredSize().getWidth();
		double h = height - insets.top - insets.bottom;
		double axisYCompX = insets.left;
		double axisYCompY = insets.top;
		axisYComp.setBounds(axisYCompX, axisYCompY, w, h);
		axisYRenderer.setShape(new Line2D.Double(w, h, w, 0.0));
	}
}

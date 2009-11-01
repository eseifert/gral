package openjchart.plots;

import java.awt.Color;
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
import openjchart.charts.axes.AxisRenderer2D;
import openjchart.charts.axes.LinearRenderer2D;
import openjchart.data.DataSeries;
import openjchart.data.DataTable;

public class BarPlot extends Plot {
	private DataTable data;
	private DataSeries series;

	private double minY;
	private double maxY;

	private Axis axisY;
	private AbstractAxisRenderer2D axisYRenderer;
	private Drawable axisYComp;

	public BarPlot(DataTable data, DataSeries series) {
		this.data = data;
		this.series = series;
		dataChanged(this.data);
		this.data.addDataListener(this);

		axisY = new Axis(minY, maxY);
		axisYRenderer = new LinearRenderer2D();
		axisYRenderer.setSetting(AxisRenderer2D.KEY_SHAPE_NORMAL_ORIENTATION_CLOCKWISE, true);
		axisYComp = axisYRenderer.getRendererComponent(axisY);
		setAxis(Axis.Y, axisY, axisYComp);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		Color colorOld = g2d.getColor();
		AffineTransform txOld = g2d.getTransform();

		Insets insets = getInsets();
		double w = getWidth() - axisYComp.getWidth() - insets.left - insets.right;
		double h = getHeight() - insets.top - insets.bottom;
		double barGap = w / series.size();
		double plotXMin = axisYComp.getWidth() + insets.left;
		double plotXMax = plotXMin + w;
		double plotYMin = insets.top;
		double plotYMax = plotYMin + h;
		// Draw gridY
		double minTick = axisYRenderer.getMinTick(axisY);
		double maxTick = axisYRenderer.getMaxTick(axisY);
		double tickSpacing = axisYRenderer.getSetting(AxisRenderer2D.KEY_TICK_SPACING);
		double tickOffset =
			axisYRenderer.<Double>getSetting(AxisRenderer2D.KEY_TICK_ALIGNMENT) *
			axisYRenderer.<Double>getSetting(AxisRenderer2D.KEY_TICK_LENGTH);
		Line2D gridLineHoriz = new Line2D.Double(plotXMin + tickOffset, 0.0, plotXMax, 0.0);
		g2d.setColor(Color.LIGHT_GRAY);
		for (double i = minTick; i <= maxTick; i += tickSpacing) {
			double translateY = plotYMax - axisYRenderer.worldToView(axisY, i);
			g2d.translate(0.0, translateY);
			g2d.draw(gridLineHoriz);
			g2d.setTransform(txOld);
		}

		// Draw bars
		g2d.setColor(Color.BLACK);
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

		g2d.setColor(colorOld);
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
		axisYRenderer.setSetting(AxisRenderer2D.KEY_SHAPE, new Line2D.Double(w, h, w, 0.0));
	}

	@Override
	public void dataChanged(DataTable data) {
		super.dataChanged(data);

		minY = Double.MAX_VALUE;
		maxY = -Double.MAX_VALUE;
		for (Integer col : series.values()) {
			maxY = Math.max(maxY, data.getMax(col).doubleValue());
			minY = Math.min(minY, data.getMin(col).doubleValue());
		}
	}
}

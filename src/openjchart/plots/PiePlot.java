package openjchart.plots;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;

import openjchart.AbstractDrawable;
import openjchart.data.DataSource;
import openjchart.plots.colors.ColorMapper;
import openjchart.plots.colors.QuasiRandomColors;
import openjchart.util.GraphicsUtils;

public class PiePlot extends Plot {
	public static final String KEY_RADIUS = "pieplot.radius";
	public static final String KEY_COLORS = "pieplot.colorlist";
	public static final String KEY_CLOCKWISE = "pieplot.clockwise";
	public static final String KEY_START = "pieplot.start";

	private DataSource data;
	private double degreesPerValue;
	private double[] startValues;

	protected final PlotArea2D plotArea;
	
	private class PlotArea2D extends AbstractDrawable {
		@Override
		public void draw(Graphics2D g2d) {
			Paint bg = getSetting(KEY_PLOTAREA_BACKGROUND);
			if (bg != null) {
				GraphicsUtils.fillPaintedShape(g2d, getBounds(), bg, null);
			}

			Stroke borderStroke = getSetting(KEY_PLOTAREA_BORDER);
			if (borderStroke != null) {
				Stroke strokeOld = g2d.getStroke();
				g2d.setStroke(borderStroke);
				g2d.draw(getBounds());
				g2d.setStroke(strokeOld);
			}

			drawPlot(g2d);
		}

		protected void drawPlot(Graphics2D g2d) {
			AffineTransform txOrig = g2d.getTransform();
			g2d.translate(getX(), getY());
			AffineTransform txOffset = g2d.getTransform();

			// Paint pie
			double w = getWidth();
			double h = getHeight();
			double size = Math.min(w, h) * PiePlot.this.<Double>getSetting(KEY_RADIUS);
			g2d.translate(w/2d, h/2d);
			startValues[0] = getSetting(KEY_START);
			startValues[startValues.length-1] = Math.signum(degreesPerValue) * 360.0 + startValues[0];
			ColorMapper colorList = getSetting(KEY_COLORS);
			for (int i = 1; i < startValues.length;  i++) {
				Paint paint = colorList.get(i-1/(double)startValues.length);
				Shape shape = new Arc2D.Double(-size/2d, -size/2d, size, size, startValues[i-1], startValues[i]-startValues[i-1], Arc2D.PIE);
				GraphicsUtils.fillPaintedShape(g2d, shape, paint, null);
			}
			g2d.setTransform(txOffset);
			g2d.setTransform(txOrig);
		}
	}
	
	public PiePlot(DataSource data) {
		setSettingDefault(KEY_RADIUS, 0.9);
		setSettingDefault(KEY_COLORS, new QuasiRandomColors());
		setSettingDefault(KEY_CLOCKWISE, true);
		setSettingDefault(KEY_START, 0.0);
		plotArea = new PlotArea2D();
		add(plotArea, PlotLayout.CENTER);
		
		this.data = data;
		dataChanged(this.data);
		this.data.addDataListener(this);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		drawComponents(g2d);
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

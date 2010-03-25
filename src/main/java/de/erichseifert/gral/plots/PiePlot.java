/**
 * GRAL: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.plots;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;

import de.erichseifert.gral.PlotArea2D;
import de.erichseifert.gral.data.DataListener;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.plots.colors.ColorMapper;
import de.erichseifert.gral.plots.colors.QuasiRandomColors;
import de.erichseifert.gral.util.GraphicsUtils;


/**
 * Class that displays data in a pie plot.
 */
public class PiePlot extends Plot implements DataListener {
	/** Key for specifying the relative radius of the pie. */
	public static final String KEY_RADIUS = "pieplot.radius";
	/** Key for specifying the {@link de.erichseifert.gral.plots.colors.ColorMapper} instance used for the segments. */
	public static final String KEY_COLORS = "pieplot.colorlist";
	/** Key for specifying whether the segments should be ordered clockwise or counterclockwise. */
	public static final String KEY_CLOCKWISE = "pieplot.clockwise";
	/** Key for specifying the starting angle of the first segment in degrees. */
	public static final String KEY_START = "pieplot.start";

	private double degreesPerValue;
	private double[] startValues;

	/**
	 * Class that represents the drawing area of a <code>PiePlot</code>.
	 */
	public class PiePlotArea2D extends PlotArea2D {
		@Override
		public void draw(Graphics2D g2d) {
			drawBackground(g2d);
			drawBorder(g2d);
			drawPlot(g2d);
		}

		@Override
		protected void drawPlot(Graphics2D g2d) {
			AffineTransform txOrig = g2d.getTransform();
			g2d.translate(getX(), getY());
			AffineTransform txOffset = g2d.getTransform();

			// Paint pie
			double w = getWidth();
			double h = getHeight();
			double size = Math.min(w, h) * PiePlot.this.<Double>getSetting(KEY_RADIUS);
			g2d.translate(w/2d, h/2d);
			startValues[0] = PiePlot.this.<Double>getSetting(KEY_START);
			startValues[startValues.length-1] = Math.signum(degreesPerValue) * 360.0 + startValues[0];
			ColorMapper colorList = PiePlot.this.getSetting(KEY_COLORS);
			for (int i = 1; i < startValues.length;  i++) {
				Paint paint = colorList.get(i-1/(double)startValues.length);
				Shape shape = new Arc2D.Double(-size/2d, -size/2d, size, size, startValues[i-1], startValues[i]-startValues[i-1], Arc2D.PIE);
				GraphicsUtils.fillPaintedShape(g2d, shape, paint, null);
			}
			g2d.setTransform(txOffset);
			g2d.setTransform(txOrig);
		}
	}

	/**
	 * Creates a new <code>PiePlot</code> object with the specified <code>DataSource</code>.
	 * @param data Data to be displayed.
	 */
	public PiePlot(DataSource data) {
		super(data);

		setSettingDefault(KEY_RADIUS, 0.9);
		setSettingDefault(KEY_COLORS, new QuasiRandomColors());
		setSettingDefault(KEY_CLOCKWISE, true);
		setSettingDefault(KEY_START, 0.0);

		dataChanged(data);
		data.addDataListener(this);

		setPlotArea(new PiePlotArea2D());
	}

	@Override
	public void dataChanged(DataSource data) {
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

		if (this.<Boolean>getSetting(KEY_CLOCKWISE)) {
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

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
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import de.erichseifert.gral.PlotArea2D;
import de.erichseifert.gral.data.DataListener;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.plots.colors.ColorMapper;
import de.erichseifert.gral.plots.colors.QuasiRandomColors;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Settings.Key;


/**
 * Class that displays data in a pie plot.
 */
public class PiePlot extends Plot implements DataListener {
	/** Key for specifying the radius of the pie relative to the plot area size. */
	public static final Key RADIUS = new Key("pieplot.radius");
	/** Key for specifying the inner radius of the pie relative to the outer radius. */
	public static final Key RADIUS_INNER = new Key("pieplot.radius.inner");
	/** Key for specifying the {@link de.erichseifert.gral.plots.colors.ColorMapper} instance used for the segments. */
	public static final Key COLORS = new Key("pieplot.colorlist");
	/** Key for specifying whether the segments should be ordered clockwise or counterclockwise. */
	public static final Key CLOCKWISE = new Key("pieplot.clockwise");
	/** Key for specifying the starting angle of the first segment in degrees. */
	public static final Key START = new Key("pieplot.start");

	/**
	 * Class that represents the drawing area of a <code>PiePlot</code>.
	 */
	public static class PiePlotArea2D extends PlotArea2D implements DataListener {
		private final PiePlot plot;
		private double degreesPerValue;
		private ArrayList<double[]> slices;

		public PiePlotArea2D(PiePlot plot) {
			this.plot = plot;
		}

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
			if (w <= 0.0 || h <= 0.0) {
				return;
			}
			g2d.translate(w/2d, h/2d);
			ColorMapper colorList = plot.getSetting(PiePlot.COLORS);

			double sizeRel = plot.<Double>getSetting(PiePlot.RADIUS);
			double size = Math.min(w, h) * sizeRel;

			double sizeRelInner = plot.<Double>getSetting(PiePlot.RADIUS_INNER);
			double sizeInner = size * sizeRelInner;
			Ellipse2D inner = new Ellipse2D.Double(
					-sizeInner/2d, -sizeInner/2d, sizeInner, sizeInner);
			Area whole = new Area(inner);

			double sliceOffset = plot.<Double>getSetting(PiePlot.START);
			int sliceNo = 0;
			for (double[] slice : slices) {
				double sliceStart = sliceOffset + slice[0];
				double sliceSpan = slice[1];
				sliceNo++;
				if (Double.isNaN(sliceSpan)) {
					continue;
				}

				// Paint slice
				Paint paint = colorList.get(sliceNo - 1.0/slices.size());
				Arc2D pieSlice = new Arc2D.Double(-size/2d, -size/2d, size, size,
						sliceStart, sliceSpan, Arc2D.PIE);
				Area doughnutSlice = new Area(pieSlice);
				if (sizeRelInner > 0.0) {
					doughnutSlice.subtract(whole);
				}
				GraphicsUtils.fillPaintedShape(g2d, doughnutSlice, paint, null);
			}
			g2d.setTransform(txOffset);
			g2d.setTransform(txOrig);
		}

		@Override
		public void dataChanged(DataSource data) {
			// Calculate sum of all values
			double colYSum = 0.0;
			for (int i = 0; i < data.getRowCount();  i++) {
				double val = data.get(0, i).doubleValue();
				// Negative values cause "empty" slices
				colYSum += Math.abs(val);
			}

			if (plot.<Boolean>getSetting(PiePlot.CLOCKWISE)) {
				degreesPerValue = -360.0/colYSum;
			}
			else {
				degreesPerValue = 360.0/colYSum;
			}

			// Calculate starting angles
			slices = new ArrayList<double[]>(data.getRowCount());
			double sliceStart = 0.0;
			for (int i = 0; i < data.getRowCount(); i++) {
				double val = data.get(0, i).doubleValue();
				double[] slice = new double[] { sliceStart, Double.NaN };
				slices.add(slice);

				if (Double.isNaN(val) || Double.isInfinite(val)) {
					continue;
				}

				slice[0] = sliceStart;
				// Negative values cause "empty" slices
				slice[1] = (val >= 0.0) ? (val * degreesPerValue) : (Double.NaN);

				sliceStart += Math.abs(val) * degreesPerValue;
			}
		}
	}

	/**
	 * Creates a new <code>PiePlot</code> object with the specified <code>DataSource</code>.
	 * @param data Data to be displayed.
	 */
	public PiePlot(DataSource data) {
		super(data);

		setSettingDefault(RADIUS, 1.0);
		setSettingDefault(RADIUS_INNER, 0.0);
		setSettingDefault(COLORS, new QuasiRandomColors());
		setSettingDefault(CLOCKWISE, true);
		setSettingDefault(START, 0.0);

		setPlotArea(new PiePlotArea2D(this));

		dataChanged(data);
		data.addDataListener(this);

	}

	@Override
	public void dataChanged(DataSource data) {
		((PiePlotArea2D) getPlotArea()).dataChanged(data);
	}
}

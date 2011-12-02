/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import de.erichseifert.gral.DrawingContext;
import de.erichseifert.gral.PlotArea;
import de.erichseifert.gral.data.Column;
import de.erichseifert.gral.data.DataChangeEvent;
import de.erichseifert.gral.data.DataListener;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.plots.colors.ColorMapper;
import de.erichseifert.gral.plots.colors.ContinuousColorMapper;
import de.erichseifert.gral.plots.colors.QuasiRandomColors;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.MathUtils;


/**
 * <p>Class that displays data as segments of a pie plot. Empty segments are
 * displayed for negative values.</p>
 * <p>To create a new {@code PiePlot} simply create a new instance using
 * a data source. Example:</p>
 * <pre>
 * DataTable data = new DataTable(Integer.class, Double.class);
 * data.add(2005, -23.50);
 * data.add(2006, 100.00);
 * data.add(2007,  60.25);
 *
 * PiePlot plot = new PiePlot(data);
 * </pre>
 */
public class PiePlot extends Plot implements DataListener {
	/** Key for specifying the radius of the pie relative to the
	plot area size. */
	public static final Key RADIUS =
		new Key("pieplot.radius"); //$NON-NLS-1$
	/** Key for specifying a {@link java.lang.Number} value for the inner
	radius of the pie relative to the outer radius. */
	public static final Key RADIUS_INNER =
		new Key("pieplot.radius.inner"); //$NON-NLS-1$
	/** Key for specifying an instance of
	{@link de.erichseifert.gral.plots.colors.ColorMapper} used for coloring
	the segments. */
	public static final Key COLORS =
		new Key("pieplot.colorlist"); //$NON-NLS-1$
	/** Key for specifying a {@link java.lang.Boolean} value which decides
	whether the segments should be ordered clockwise ({@code true}) or
	counterclockwise ({@code false}). */
	public static final Key CLOCKWISE =
		new Key("pieplot.clockwise"); //$NON-NLS-1$
	/** Key for specifying a {@link java.lang.Number} value for the starting
	angle of the first segment in degrees. */
	public static final Key START =
		new Key("pieplot.start"); //$NON-NLS-1$
	/** Key for specifying a {@link java.lang.Number} value for the width of
	gaps between the segments. */
	public static final Key GAP =
		new Key("pieplot.gap"); //$NON-NLS-1$

	/**
	 * Class that represents the drawing area of a {@code PiePlot}.
	 */
	public static class PiePlotArea2D extends PlotArea
			implements DataListener {
		/** Pie plot that this renderer is associated to. */
		private final PiePlot plot;
		/** Factor that stores the degrees per data value. */
		private double degreesPerValue;
		/** Interval boundaries of the pie slices. */
		private final ArrayList<double[]> slices;

		/**
		 * Constructor that creates a new instance and initializes it with a
		 * plot acting as data provider.
		 * @param plot Data provider.
		 */
		public PiePlotArea2D(PiePlot plot) {
			this.plot = plot;
			slices = new ArrayList<double[]>();
		}

		/**
		 * Draws the {@code Drawable} with the specified drawing context.
		 * @param context Environment used for drawing
		 */
		public void draw(DrawingContext context) {
			drawBackground(context);
			drawBorder(context);
			drawPlot(context);
		}

		@Override
		protected void drawPlot(DrawingContext context) {
			Graphics2D graphics = context.getGraphics();
			AffineTransform txOrig = graphics.getTransform();
			graphics.translate(getX(), getY());
			AffineTransform txOffset = graphics.getTransform();

			// TODO Use real font size instead of fixed value
			final double fontSize = 10.0;

			Shape clipBoundsOld = graphics.getClip();
			Insets2D clipOffset = getSetting(CLIPPING);
			if (clipOffset != null) {
				// Perform clipping
				Shape clipBounds = new Rectangle2D.Double(
						clipOffset.getLeft()*fontSize,
						clipOffset.getTop()*fontSize,
						getWidth() - clipOffset.getHorizontal()*fontSize,
						getHeight() - clipOffset.getVertical()*fontSize
				);
				// Take care of old clipping region. This is used when getting
				// scrolled in a JScrollPane for example.
				if (clipBoundsOld != null) {
					Area clipBoundsNew = new Area(clipBoundsOld);
					clipBoundsNew.intersect(new Area(clipBounds));
					clipBounds = clipBoundsNew;
				}
				graphics.setClip(clipBounds);
			}

			// Paint pie
			double w = getWidth();
			double h = getHeight();
			if (w <= 0.0 || h <= 0.0) {
				return;
			}
			graphics.translate(w/2d, h/2d);
			ColorMapper colorMapper = plot.getSetting(PiePlot.COLORS);

			double sizeRel = plot.<Number>getSetting(PiePlot.RADIUS)
				.doubleValue();
			double size = Math.min(w, h) * sizeRel;

			double sizeRelInner = plot.<Number>getSetting(PiePlot.RADIUS_INNER)
				.doubleValue();
			double sizeInner = size * sizeRelInner;
			Ellipse2D inner = new Ellipse2D.Double(
				-sizeInner/2d, -sizeInner/2d, sizeInner, sizeInner);
			Area whole = new Area(inner);

			double gap = plot.<Number>getSetting(PiePlot.GAP).doubleValue();

			double sliceOffset = plot.<Number>getSetting(PiePlot.START)
				.doubleValue();

			boolean isClockwise = plot.<Boolean>getSetting(PiePlot.CLOCKWISE);

			int sliceIndex = 0;
			for (double[] slice : slices) {
				double sliceStart = sliceOffset + slice[0];
				double sliceSpan = slice[1];

				if (!MathUtils.isCalculatable(sliceSpan)) {
					continue;
				}

				// Construct slice
				Arc2D pieSlice = new Arc2D.Double(-size/2d, -size/2d,
					size, size, sliceStart, sliceSpan, Arc2D.PIE);
				Area doughnutSlice = new Area(pieSlice);
				if (gap > 0.0) {
					Stroke sliceStroke =
						new BasicStroke((float) (gap*fontSize));
					Area sliceContour =
						new Area(sliceStroke.createStrokedShape(pieSlice));
					doughnutSlice.subtract(sliceContour);
				}
				if (sizeRelInner > 0.0) {
					doughnutSlice.subtract(whole);
				}

				// Paint slice
				Paint paint;
				if (colorMapper instanceof ContinuousColorMapper) {
					double coloringRel = 0.0;
					if (slices.size() > 1) {
						double sliceStartRel, sliceEndRel;
						if (isClockwise) {
							sliceStartRel = MathUtils.normalizeDegrees(-slice[0])/360.0;
							sliceEndRel = sliceStartRel - slice[1]/360.0;
						} else {
							sliceStartRel = MathUtils.normalizeDegrees(slice[0])/360.0;
							sliceEndRel = sliceStartRel + slice[1]/360.0;
						}
						double posRel = sliceIndex / ((double) slices.size() - 1);
						coloringRel =
							(1.0 - posRel)*sliceStartRel + posRel*sliceEndRel;
					}
					paint = ((ContinuousColorMapper) colorMapper).get(coloringRel);
				} else {
					Integer index = Integer.valueOf(sliceIndex);
					paint = colorMapper.get(index);
				}
				GraphicsUtils.fillPaintedShape(
					graphics, doughnutSlice, paint, null);

				sliceIndex++;
			}

			if (clipOffset != null) {
				// Reset clipping
				graphics.setClip(clipBoundsOld);
			}

			graphics.setTransform(txOffset);
			graphics.setTransform(txOrig);
		}

		/**
		 * Method that is invoked when data has been added.
		 * This method is invoked by objects that provide support for
		 * {@code DataListener}s and should not be called manually.
		 * @param source Data source that has changed
		 * @param events Optional event object describing the data values that
		 *        have been added
		 */
		public void dataAdded(DataSource source, DataChangeEvent... events) {
			update(source);
		}

		/**
		 * Method that is invoked when data has been updated.
		 * This method is invoked by objects that provide support for
		 * {@code DataListener}s and should not be called manually.
		 * @param source Data source that has changed
		 * @param events Optional event object describing the data values that
		 *        have been added
		 */
		public void dataUpdated(DataSource source, DataChangeEvent... events) {
			update(source);
		}

		/**
		 * Method that is invoked when data has been added.
		 * This method is invoked by objects that provide support for
		 * {@code DataListener}s and should not be called manually.
		 * @param source Data source that has changed
		 * @param events Optional event object describing the data values that
		 *        have been added
		 */
		public void dataRemoved(DataSource source, DataChangeEvent... events) {
			update(source);
		}

		/**
		 * Updates the data that is necessary to render the pie slices.
		 * @param data Data source which has changed
		 */
		private void update(DataSource data) {
			slices.clear();

			int colIndex = 0;
			Column col = new Column(data, colIndex);

			if (!col.isNumeric()) {
				return;
			}

			// Calculate sum of all values
			double colYSum = 0.0;
			for (Comparable<?> cell : col) {
				Number numericCell = (Number) cell;
				double val = numericCell.doubleValue();
				// Negative values cause "empty" slices
				colYSum += Math.abs(val);
			}

			boolean isClockwise = plot.<Boolean>getSetting(PiePlot.CLOCKWISE);
			if (isClockwise) {
				degreesPerValue = -360.0/colYSum;
			} else {
				degreesPerValue = 360.0/colYSum;
			}

			// Calculate starting angles
			double sliceStart = 0.0;
			for (Comparable<?> cell : col) {
				Number numericCell = (Number) cell;
				double val = numericCell.doubleValue();
				double[] slice = new double[] { sliceStart, Double.NaN };
				slices.add(slice);

				if (!MathUtils.isCalculatable(val)) {
					continue;
				}

				slice[0] = sliceStart;
				// Negative values cause "empty" slices
				slice[1] = Double.NaN;
				if (val >= 0.0) {
					slice[1] = val * degreesPerValue;
				}

				sliceStart += Math.abs(val) * degreesPerValue;
			}
		}
	}

	/**
	 * Initializes a new pie plot with the specified data source.
	 * @param data Data to be displayed.
	 */
	public PiePlot(DataSource data) {
		super(data);

		setSettingDefault(RADIUS, 1.0);
		setSettingDefault(RADIUS_INNER, 0.0);
		setSettingDefault(COLORS, new QuasiRandomColors());
		setSettingDefault(CLOCKWISE, true);
		setSettingDefault(START, 0.0);
		setSettingDefault(GAP, 0.0);

		setPlotArea(new PiePlotArea2D(this));

		dataUpdated(data);
		data.addDataListener(this);
	}

	@Override
	public void add(int index, DataSource source, boolean visible) {
		if (getData().size() != 0) {
			throw new IllegalArgumentException(
				"This plot type only supports a single data source."); //$NON-NLS-1$
		}
		super.add(index, source, visible);
	}

	@Override
	public void dataAdded(DataSource data, DataChangeEvent... events) {
		((DataListener) getPlotArea()).dataAdded(data, events);
	}

	@Override
	public void dataUpdated(DataSource data, DataChangeEvent... events) {
		((DataListener) getPlotArea()).dataUpdated(data, events);
	}

	@Override
	public void dataRemoved(DataSource data, DataChangeEvent... events) {
		((DataListener) getPlotArea()).dataRemoved(data, events);
	}
}

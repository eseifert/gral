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

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import de.erichseifert.gral.AbstractDrawable;
import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.DrawingContext;
import de.erichseifert.gral.data.DataChangeEvent;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.colors.ColorMapper;
import de.erichseifert.gral.plots.colors.Grayscale;
import de.erichseifert.gral.plots.points.AbstractPointRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.PointND;


/**
 * <p>Class that displays the values in the columns and rows of a data source
 * as a raster of boxes.</p>
 * <p>To create a new <code>RasterPlot</code> simply create a new instance using
 * a data source. Example:</p>
 * <pre>
 * DataTable data = new DataTable(Double.class, Double.class);
 * data.add(10.98, -12.34);
 * data.add( 7.65,  45.67);
 * data.add(43.21,  89.01);
 *
 * RasterPlot plot = new RasterPlot(data);
 * </pre>
 */
public class RasterPlot extends XYPlot {
	/** Key for specifying a {@link java.awt.Point2D} instance which defines
	the horizontal and vertical offset of the raster from the origin. */
	public static final Key OFFSET =
		new Key("rasterplot.offset"); //$NON-NLS-1$
	/** Key for specifying a {@link java.awt.Dimension2D} instance which
	defines the horizontal and vertical distance of the raster pixels. */
	public static final Key DISTANCE =
		new Key("rasterplot.distance"); //$NON-NLS-1$
	/** Key for specifying an instance of
	{@link de.erichseifert.gral.plots.colors.ColorMapper} used for mapping the
	pixel values to colors. */
	public static final Key COLORS =
		new Key("rasterplot.color"); //$NON-NLS-1$

	private final DataSource data;
	private final DataTable pixels;

	/**
	 * Class that renders a box and its whiskers in a box-and-whisker plot.
	 */
	protected static class RasterRenderer extends AbstractPointRenderer {
		/** Bar plot this renderer is associated to. */
		private final RasterPlot plot;

		/**
		 * Constructor that creates a new instance and initializes it with a
		 * plot as data provider.
		 * @param plot Data provider.
		 */
		public RasterRenderer(RasterPlot plot) {
			this.plot = plot;
		}

		/**
		 * Returns the graphical representation to be drawn for the specified data
		 * value.
		 * @param axis that is used to project the point.
		 * @param axisRenderer Renderer for the axis.
		 * @param row Data row containing the point.
		 * @return Component that can be used to draw the point
		 */
		public Drawable getPoint(final Axis axis,
				final AxisRenderer axisRenderer, final Row row) {
			//final Drawable plotArea = BarPlot.this.plotArea;
			return new AbstractDrawable() {
				private final Rectangle2D pixel = new Rectangle2D.Double();
				private final Axis axisX = plot.getAxis(AXIS_X);
				private final Axis axisY = plot.getAxis(AXIS_Y);
				private final AxisRenderer axisXRenderer = plot.getAxisRenderer(AXIS_X);
				private final AxisRenderer axisYRenderer = plot.getAxisRenderer(AXIS_Y);

				public void draw(DrawingContext context) {
					double valueX = row.get(0).doubleValue();
					double valueY = row.get(1).doubleValue();
					double value = row.get(2).doubleValue();

					// Pixel dimensions
					double xMin = axisXRenderer
						.getPosition(axisX, valueX - 0.5, true, false)
						.get(PointND.X);
					double xMax = axisXRenderer
						.getPosition(axisX, valueX + 0.5, true, false)
						.get(PointND.X);
					double width = Math.abs(xMax - xMin) + 1.0;
					double yMin = axisYRenderer
						.getPosition(axisY, valueY - 0.5, true, false)
						.get(PointND.Y);
					double yMax = axisYRenderer
						.getPosition(axisY, valueY + 0.5, true, false)
						.get(PointND.Y);
					double height = Math.abs(yMax - yMin) + 1.0;

					// Create shapes
					// The origin of all shapes is (boxX, boxY)
					pixel.setFrame(0.0, 0.0, width, height);

					// Paint pixel
					Graphics2D graphics = context.getGraphics();
					ColorMapper colorMapper = plot.getSetting(COLORS);
					Paint paint = colorMapper.get(value);
					GraphicsUtils.fillPaintedShape(
							graphics, pixel, paint, pixel.getBounds2D());
				}
			};
		}

		/**
		 * Returns a <code>Shape</code> instance that can be used
		 * for further calculations.
		 * @param row Data row containing the point.
		 * @return Outline that describes the point's shape.
		 */
		public Shape getPointPath(Row row) {
			return null;
		}
	}

	/**
	 * Initializes a new box-and-whisker plot with the specified data source.
	 * @param data Data to be displayed.
	 */
	public RasterPlot(DataSource data) {
		setSettingDefault(COLORS, new Grayscale());

		getPlotArea().setSettingDefault(XYPlotArea2D.GRID_MAJOR_X, false);
		getPlotArea().setSettingDefault(XYPlotArea2D.GRID_MAJOR_Y, false);
		//getAxisRenderer(AXIS_X).setSetting(AxisRenderer.TICKS, false);
		//getAxisRenderer(AXIS_Y).setSetting(AxisRenderer.TICKS, false);
		getAxisRenderer(AXIS_X).setSetting(AxisRenderer.INTERSECTION,
			-Double.MAX_VALUE);
		getAxisRenderer(AXIS_Y).setSetting(AxisRenderer.INTERSECTION,
			-Double.MAX_VALUE);

		// Store original data source
		this.data = data;
		data.addDataListener(this);

		// Generated new data series
		pixels = new DataTable(
			Double.class, Double.class, Double.class);
		updatePixelData();
		add(pixels);

		// Adjust axes to generated data series
		getAxis(AXIS_X).setRange(0.0, data.getColumnCount());
		getAxis(AXIS_Y).setRange(-data.getRowCount(), 0.0);

		// Adjust rendering
		PointRenderer pointRenderer = new RasterRenderer(this);
		setLineRenderer(pixels, null);
		setPointRenderer(pixels, pointRenderer);
	}

	/**
	 * Utility method to update generated data series.
	 */
	private void updatePixelData() {
		// Remove old entries
		pixels.clear();

		// Generate pixel data with (x, y, value)
		Statistics stats = data.getStatistics();
		double min = stats.get(Statistics.MIN);
		double max = stats.get(Statistics.MAX);
		double range = max - min;
		int i = 0;
		for (Number value : data) {
			double x =  i%data.getColumnCount();
			double y = -i/data.getColumnCount();
			double v = (value.doubleValue() - min) / range;
			pixels.add(x, y, v);
			i++;
		}
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
	public void dataAdded(DataSource source, DataChangeEvent... events) {
		super.dataAdded(source, events);
		if (source == data) {
			updatePixelData();
		}
	}

	@Override
	public void dataUpdated(DataSource source, DataChangeEvent... events) {
		super.dataUpdated(source, events);
		if (source == data) {
			updatePixelData();
		}
	}

	@Override
	public void dataRemoved(DataSource source, DataChangeEvent... events) {
		super.dataRemoved(source, events);
		if (source == data) {
			updatePixelData();
		}
	}
}

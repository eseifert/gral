/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2012 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael[at]erichseifert.de>
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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.colors.ColorMapper;
import de.erichseifert.gral.plots.colors.ContinuousColorMapper;
import de.erichseifert.gral.plots.colors.Grayscale;
import de.erichseifert.gral.plots.points.AbstractPointRenderer;
import de.erichseifert.gral.plots.settings.Key;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.PointND;


/**
 * <p>Class that displays two coordinate values and a value as a raster of
 * boxes. The data source must provide at least three columns:</p>
 * <ul>
 *   <li>x coordinate</li>
 *   <li>y coordinate</li>
 *   <li>value</li>
 * </ul>
 * <p>The method {@link #createRasterData(DataSource)} can be used to convert
 * a matrix of values to the (coordinates, value) format.</p>
 *
 * <p>To create a new {@code RasterPlot} simply create a new instance using
 * a suitable data source. Example:</p>
 * <pre>
 * DataTable data = new DataTable(Double.class, Double.class);
 * data.add(10.98, -12.34);
 * data.add( 7.65,  45.67);
 * data.add(43.21,  89.01);
 * DataSource rasterData = RasterPlot.createRasterData(data);
 * RasterPlot plot = new RasterPlot(rasterData);
 * </pre>
 */
public class RasterPlot extends XYPlot {
	/** Key for specifying a {@link java.awt.geom.Point2D} instance which defines
	the horizontal and vertical offset of the raster from the origin. */
	public static final Key OFFSET =
		new Key("rasterplot.offset"); //$NON-NLS-1$
	/** Key for specifying a {@link java.awt.geom.Dimension2D} instance which
	defines the horizontal and vertical distance of the raster pixels. */
	public static final Key DISTANCE =
		new Key("rasterplot.distance"); //$NON-NLS-1$
	/** Key for specifying an instance of
	{@link de.erichseifert.gral.plots.colors.ColorMapper} used for mapping the
	pixel values to colors. */
	public static final Key COLORS =
		new Key("rasterplot.color"); //$NON-NLS-1$

	/**
	 * Class that renders a box and its whiskers in a box-and-whisker plot.
	 */
	protected static class RasterRenderer extends AbstractPointRenderer {
		/** Bar plot this renderer is associated to. */
		private final RasterPlot plot;
		/** Key for specifying the {@link Integer} which specifies the
		index of the column that is used for the x coordinate of a point. */
		public static final Key COLUMN_X =
			new Key("rasterplot.columnX"); //$NON-NLS-1$
		/** Key for specifying the {@link Integer} which specifies the
		index of the column that is used for the y coordinate of a point. */
		public static final Key COLUMN_Y =
			new Key("rasterplot.columnY"); //$NON-NLS-1$
		/** Key for specifying the {@link Integer} which specifies the
		index of the column that is used for the value of a point. */
		public static final Key COLUMN_VALUE =
			new Key("rasterplot.columnValue"); //$NON-NLS-1$

		/**
		 * Constructor that creates a new instance and initializes it with a
		 * plot as data provider. The default columns for (x, y, value) are set
		 * to (0, 1, 2)
		 * @param plot Data provider.
		 */
		public RasterRenderer(RasterPlot plot) {
			this.plot = plot;
			setSettingDefault(COLUMN_X, 0);
			setSettingDefault(COLUMN_Y, 1);
			setSettingDefault(COLUMN_VALUE, 2);
		}

		/**
		 * Returns the graphical representation to be drawn for the specified data
		 * value.
		 * @param axis that is used to project the point.
		 * @param axisRenderer Renderer for the axis.
		 * @param row Data row containing the point.
		 * @param col Index of the column that will be projected on the axis.
		 * @return Component that can be used to draw the point
		 */
		public Drawable getPoint(final Axis axis,
				final AxisRenderer axisRenderer, final Row row, final int col) {
			return new AbstractDrawable() {
				private final Rectangle2D pixel = new Rectangle2D.Double();
				private final Axis axisX = plot.getAxis(AXIS_X);
				private final Axis axisY = plot.getAxis(AXIS_Y);
				private final AxisRenderer axisXRenderer =
					plot.getAxisRenderer(AXIS_X);
				private final AxisRenderer axisYRenderer =
					plot.getAxisRenderer(AXIS_Y);

				public void draw(DrawingContext context) {
					RasterRenderer renderer = RasterRenderer.this;
					int colX = renderer.<Integer>getSetting(COLUMN_X);
					if (colX < 0 || colX >= row.size() || !row.isColumnNumeric(colX)) {
						return;
					}
					int colY = renderer.<Integer>getSetting(COLUMN_Y);
					if (colY < 0 || colY >= row.size() || !row.isColumnNumeric(colY)) {
						return;
					}
					int colValue = renderer.<Integer>getSetting(COLUMN_VALUE);
					if (colValue < 0 || colValue >= row.size() || !row.isColumnNumeric(colValue)) {
						return;
					}

					double valueX = ((Number) row.get(colX)).doubleValue();
					double valueY = ((Number) row.get(colY)).doubleValue();
					Number value = (Number) row.get(colValue);

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
					Paint paint;
					if (colorMapper instanceof ContinuousColorMapper) {
						paint = ((ContinuousColorMapper) colorMapper)
							.get(value.doubleValue());
					} else if (colorMapper != null) {
						Integer index = value.intValue();
						paint = colorMapper.get(index);
					} else {
						paint = Color.BLACK;
					}
					GraphicsUtils.fillPaintedShape(
						graphics, pixel, paint, pixel.getBounds2D());
				}
			};
		}

		/**
		 * Returns a {@code Shape} instance that can be used
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
		setSettingDefault(OFFSET, new Point2D.Double());
		setSettingDefault(DISTANCE, new de.erichseifert.gral.util.Dimension2D.Double(1.0, 1.0));
		setSettingDefault(COLORS, new Grayscale());

		getPlotArea().setSettingDefault(XYPlotArea2D.GRID_MAJOR_X, false);
		getPlotArea().setSettingDefault(XYPlotArea2D.GRID_MAJOR_Y, false);
		//getAxisRenderer(AXIS_X).setSetting(AxisRenderer.TICKS, false);
		//getAxisRenderer(AXIS_Y).setSetting(AxisRenderer.TICKS, false);
		getAxisRenderer(AXIS_X).setSetting(AxisRenderer.INTERSECTION,
			-Double.MAX_VALUE);
		getAxisRenderer(AXIS_Y).setSetting(AxisRenderer.INTERSECTION,
			-Double.MAX_VALUE);

		// Store data
		add(data);

		// Adjust axes to the data series
		autoScaleAxes();
	}

	@Override
	protected void autoScaleAxes() {
		Dimension2D dist = getSetting(DISTANCE);
		// In case we get called before settings defaults have been set,
		// just set distance to a sane default
		if (dist == null) {
			dist = new de.erichseifert.gral.util.Dimension2D.Double(1.0, 1.0);
		}
		double xMin = getAxisMin(AXIS_X);
		double xMax = getAxisMax(AXIS_X);
		getAxis(AXIS_X).setRange(xMin, xMax + dist.getWidth());
		double yMin = getAxisMin(AXIS_Y);
		double yMax = getAxisMax(AXIS_Y);
		getAxis(AXIS_Y).setRange(yMin - dist.getHeight(), yMax);
	}

	/**
	 * Takes a matrix of values and creates a new data source that stores the
	 * values in (x, y, value) format.
	 * @param data Original data source with values in each cell.
	 * @return New data source with (x, y, value) columns
	 */
	@SuppressWarnings("unchecked")
	public static DataSource createRasterData(DataSource data) {
		if (data == null) {
			throw new NullPointerException("Cannot convert null data source.");
		}

		DataTable coordsValueData =
			new DataTable(Double.class, Double.class, Double.class);

		// Generate pixel data with (x, y, value)
		Statistics stats = data.getStatistics();
		double min = stats.get(Statistics.MIN);
		double max = stats.get(Statistics.MAX);
		double range = max - min;
		int i = 0;
		for (Comparable<?> cell : data) {
			int x =  i%data.getColumnCount();
			int y = -i/data.getColumnCount();
			double v = Double.NaN;
			if (cell instanceof Number) {
				Number numericCell = (Number) cell;
				v = (numericCell.doubleValue() - min) / range;
			}
			coordsValueData.add((double) x, (double) y, v);
			i++;
		}
		return coordsValueData;
	}

	@Override
	public void add(int index, DataSource source, boolean visible) {
		if (getData().size() > 0) {
			throw new IllegalArgumentException(
				"This plot type only supports a single data source."); //$NON-NLS-1$
		}
		// Add data source
		super.add(index, source, visible);
		// Adjust rendering
		setLineRenderer(source, null);
		setPointRenderer(source, new RasterRenderer(this));
	}
}

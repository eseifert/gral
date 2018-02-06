/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <mseifert[at]error-reports.org>
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
import java.awt.geom.AffineTransform;
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
import de.erichseifert.gral.plots.points.PointData;
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
	/** Version id for serialization. */
	private static final long serialVersionUID = 5844862286358250831L;

	/** Offset of the raster pixels to the origin. */
	private final Point2D offset;
	/** Size of the raster pixels. */
	private final Dimension2D distance;
	/** Color mapping to fill the raster pixels. */
	private ColorMapper colors;

	/**
	 * Class that renders the grid points of a {@code RasterPlot}.
	 */
	protected static class RasterRenderer extends AbstractPointRenderer {
		/** Version id for serialization. */
		private static final long serialVersionUID = 1266585364126459761L;

		/** Plot specific settings. */
		private final RasterPlot plot;

		/** Horizontal position of the current raster pixel. */
		private int xColumn;
		/** Vertical position of the current raster pixel. */
		private int yColumn;
		/** Value of the current raster pixel. */
		private int valueColumn;

		/**
		 * Constructor that creates a new instance and initializes it with a
		 * plot as data provider. The default columns for (x, y, value) are set
		 * to (0, 1, 2)
		 * @param plot Plot storing global settings.
		 */
		public RasterRenderer(RasterPlot plot) {
			this.plot = plot;
			xColumn = 0;
			yColumn = 1;
			valueColumn = 2;
		}

		/**
		 * Returns the index of the column which is used for the x coordinate
		 * of a point.
		 * @return Index of the column for the x coordinate of a point.
		 */
		public int getXColumn() {
			return xColumn;
		}

		/**
		 * Sets the index of the column which will be used for the x coordinate
		 * of a point.
		 * @param columnIndex Index of the column for the x coordinate of a point.
		 */
		public void setXColumn(int columnIndex) {
			this.xColumn = columnIndex;
		}

		/**
		 * Returns the index of the column which is used for the y coordinate
		 * of a point.
		 * @return Index of the column for the y coordinate of a point.
		 */
		public int getYColumn() {
			return yColumn;
		}

		/**
		 * Sets the index of the column which will be used for the y coordinate
		 * of a point.
		 * @param columnIndex Index of the column for the y coordinate of a point.
		 */
		public void setYColumn(int columnIndex) {
			this.yColumn = columnIndex;
		}

		/**
		 * Returns the index of the column which is used for the value of a
		 * point.
		 * @return Index of the column for the value of a point.
		 */
		@Override
		public int getValueColumn() {
			return valueColumn;
		}

		/**
		 * Sets the index of the column which will be used for the value of a
		 * point.
		 * @param columnIndex Index of the column for the value of a point.
		 */
		@Override
		public void setValueColumn(int columnIndex) {
			this.valueColumn = columnIndex;
		}

		@Override
		public Drawable getPoint(final PointData data, final Shape shape) {
			return new AbstractDrawable() {
				/** Version id for serialization. */
				private static final long serialVersionUID = -1136689797647794969L;

				public void draw(DrawingContext context) {
					RasterRenderer renderer = RasterRenderer.this;

					Axis axisX = data.axes.get(0);
					Axis axisY = data.axes.get(1);
					AxisRenderer axisXRenderer = data.axisRenderers.get(0);
					AxisRenderer axisYRenderer = data.axisRenderers.get(1);
					Row row = data.row;

					int colX = renderer.getXColumn();
					if (colX < 0 || colX >= row.size() || !row.isColumnNumeric(colX)) {
						return;
					}
					int colY = renderer.getYColumn();
					if (colY < 0 || colY >= row.size() || !row.isColumnNumeric(colY)) {
						return;
					}
					int colValue = renderer.getValueColumn();
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

					// Create shape for pixel
					// The origin of all shapes is (boxX, boxY)
					Rectangle2D shapeBounds = shape.getBounds2D();
					AffineTransform tx = new AffineTransform();
					tx.scale(width/shapeBounds.getWidth(), height/shapeBounds.getHeight());
					tx.translate(-shapeBounds.getMinX(), -shapeBounds.getMinY());
					Shape pixel = tx.createTransformedShape(shape);

					// Paint pixel
					Graphics2D graphics = context.getGraphics();
					ColorMapper colorMapper = plot.getColors();
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
		 * Returns a {@code Shape} instance that can be used for further
		 * calculations.
		 * @param data Information on axes, renderers, and values.
		 * @return Outline that describes the point's shape.
		 */
		public Shape getPointShape(PointData data) {
			return getShape();
		}

		/**
		 * Returns a graphical representation of the value label to be drawn for
		 * the specified data value.
		 * @param data Information on axes, renderers, and values.
		 * @param shape Outline that describes the bounds for the value label.
		 * @return Component that can be used to draw the value label.
		 */
		public Drawable getValue(final PointData data, final Shape shape) {
			return new AbstractDrawable() {
				/** Version id for serialization. */
				private static final long serialVersionUID1 = -8402945980942955359L;

				public void draw(DrawingContext context) {
					// TODO Implement rendering of value label
				}
			};
		}
	}

	/**
	 * Initializes a new box-and-whisker plot with the specified data source.
	 * @param data Data to be displayed.
	 */
	public RasterPlot(DataSource data) {
		offset = new Point2D.Double();
		distance = new de.erichseifert.gral.graphics.Dimension2D.Double(1.0, 1.0);
		colors = new Grayscale();

		((XYPlotArea2D) getPlotArea()).setMajorGridX(false);
		((XYPlotArea2D) getPlotArea()).setMajorGridY(false);
		//getAxisRenderer(AXIS_X).setSetting(AxisRenderer.TICKS, false);
		//getAxisRenderer(AXIS_Y).setSetting(AxisRenderer.TICKS, false);
		getAxisRenderer(AXIS_X).setIntersection(-Double.MAX_VALUE);
		getAxisRenderer(AXIS_Y).setIntersection(-Double.MAX_VALUE);

		// Store data
		add(data);

		// Adjust axes to the data series
		autoscaleAxes();
	}

	@Override
	public void autoscaleAxis(String axisName) {
		if (AXIS_X.equals(axisName) || AXIS_Y.equals(axisName)) {
			Dimension2D dist = getDistance();
			// In case we get called before settings defaults have been set,
			// just set distance to a sane default
			if (dist == null) {
				dist = new de.erichseifert.gral.graphics.Dimension2D.Double(1.0, 1.0);
			}

			Axis axis = getAxis(axisName);
			if (axis == null || !axis.isAutoscaled()) {
				return;
			}

			double min = getAxisMin(axisName);
			double max = getAxisMax(axisName);
			if (AXIS_X.equals(axisName)) {
				axis.setRange(min, max + dist.getWidth());
			} else if (AXIS_Y.equals(axisName)) {
				axis.setRange(min - dist.getHeight(), max);
			}
		} else {
			super.autoscaleAxis(axisName);
		}
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
		double min = ((Number) data.getRowStatistics(Statistics.MIN).
				getColumnStatistics(Statistics.MIN).get(0, 0)).doubleValue();
		double max = ((Number) data.getRowStatistics(Statistics.MAX).
				getColumnStatistics(Statistics.MAX).get(0, 0)).doubleValue();
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
		// FIXME: Overwrites possible present point and line renderers
		setLineRenderers(source, null);
		setPointRenderers(source, new RasterRenderer(this));
	}

	/**
	 * Returns the horizontal and vertical offset of the raster from the
	 * origin.
	 * @return Horizontal and vertical offset of the raster from the origin.
	 */
	public Point2D getOffset() {
		return offset;
	}

	/**
	 * Sets the horizontal and vertical offset of the raster from the
	 * origin.
	 * @param offset Horizontal and vertical offset of the raster from the
	 * origin.
	 */
	public void setOffset(Point2D offset) {
		this.offset.setLocation(offset);
	}

	/**
	 * Returns the horizontal and vertical distance of the raster elements.
	 * @return Horizontal and vertical distance of the raster elements.
	 */
	public Dimension2D getDistance() {
		return distance;
	}

	/**
	 * Returns the horizontal and vertical distance of the raster elements.
	 * @param distance Horizontal and vertical distance of the raster elements.
	 */
	public void setDistance(Dimension2D distance) {
		this.distance.setSize(distance);
	}

	/**
	 * Returns the object which is used to map pixel values to colors.
	 * @return Object which is used to map pixel values to colors.
	 */
	public ColorMapper getColors() {
		return colors;
	}

	/**
	 * Sets the object which will be used to map pixel values to colors.
	 * @param colors Object which will be used to map pixel values to colors.
	 */
	public void setColors(ColorMapper colors) {
		this.colors = colors;
	}
}

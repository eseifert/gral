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

import static java.util.Arrays.asList;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.Format;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.erichseifert.gral.data.AbstractDataSource;
import de.erichseifert.gral.data.Column;
import de.erichseifert.gral.data.DataChangeEvent;
import de.erichseifert.gral.data.DataListener;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.data.filters.Accumulation;
import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.graphics.Location;
import de.erichseifert.gral.navigation.AbstractNavigator;
import de.erichseifert.gral.navigation.Navigable;
import de.erichseifert.gral.navigation.Navigator;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.axes.LinearRenderer2D;
import de.erichseifert.gral.plots.colors.ColorMapper;
import de.erichseifert.gral.plots.colors.ContinuousColorMapper;
import de.erichseifert.gral.plots.colors.QuasiRandomColors;
import de.erichseifert.gral.plots.legends.AbstractLegend;
import de.erichseifert.gral.plots.legends.ValueLegend;
import de.erichseifert.gral.plots.points.AbstractPointRenderer;
import de.erichseifert.gral.plots.points.PointData;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.util.GeometryUtils;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.PointND;


/**
 * <p>Class that displays data as segments of a pie plot. Empty segments are
 * displayed for negative values.</p>
 * <p>To create a new {@code PiePlot} simply create a new instance using
 * a data source. Example:</p>
 * <pre>
 * DataTable data = new DataTable(Integer.class, Double.class);
 * data.add(-23.50);
 * data.add(100.00);
 * data.add( 60.25);
 *
 * PiePlot plot = new PiePlot(data);
 * </pre>
 */
public class PiePlot extends AbstractPlot implements Navigable {
	/** Version id for serialization. */
	private static final long serialVersionUID = 5486418164040578150L;

	/** Key for specifying the tangential axis of a pie plot. */
	public static final String AXIS_TANGENTIAL = "tangential"; //$NON-NLS-1$

	/** Mapping from data source to point renderer. */
	private final Map<DataSource, PointRenderer> pointRenderers;
	/** Cache for the {@code Navigator} implementation. */
	private transient PiePlotNavigator navigator;

	/** Position of the pie center. */
	private final Point2D center;
	/** Radius of the the pie. */
	private double radius;
	/** Starting angle in degrees. */
	private double start;
	/** Decides whether pie slices are drawn in clockwise direction. */
	private boolean clockwise;

	/**
	 * Navigator implementation for pie plots. Zooming changes the
	 * {@code RADIUS} setting and panning the {@code CENTER} setting.
	 */
	public static class PiePlotNavigator extends AbstractNavigator {
		/** Pie plot that will be navigated. */
		private final PiePlot plot;
		/** Location of center in default state. */
		private PointND<? extends Number> centerOriginal;
		/** Zoom level in default state. */
		private double zoomOriginal;
		/** Current zoom level. */
		private double zoom;

		/**
		 * Initializes a new instance with a pie plot to be navigated.
		 * @param plot Pie plot.
		 */
		public PiePlotNavigator(PiePlot plot) {
			this.plot = plot;
			this.zoom = 1.0;
			setDefaultState();
		}

		/**
		 * Returns the current zoom level of the associated object.
		 * @return Current zoom level.
		 */
		public double getZoom() {
			return zoom;
		}

		/**
		 * Sets the zoom level of the associated object to the specified value.
		 * @param zoomNew New zoom level.
		 */
		public void setZoom(double zoomNew) {
			if (!isZoomable() || (zoomNew <= 0.0) ||
					!MathUtils.isCalculatable(zoomNew)) {
				return;
			}
			double zoomOld = getZoom();
			zoomNew = MathUtils.limit(zoomNew, getZoomMin(), getZoomMax());
			if (zoomOld == zoomNew) {
				return;
			}
			zoom = zoomNew;
			plot.setRadius(zoomOriginal*getZoom());
		}

		/**
		 * Returns the current center point. The returned point contains value in
		 * world units.
		 * @return Center point in world units.
		 */
		public PointND<? extends Number> getCenter() {
			Point2D center = plot.getCenter();
			return new PointND<Number>(center.getX(), center.getY());
		}

		/**
		 * Sets a new center point. The values of the point are in world units.
		 * @param center New center point in world units.
		 */
		public void setCenter(PointND<? extends Number> center) {
			if (center == null || !isPannable()) {
				return;
			}
			Point2D center2d = center.getPoint2D();
			plot.setCenter(center2d);
		}

		/**
		 * Moves the center by the relative values of the specified point.
		 * The values of the point are in screen units.
		 * @param deltas Relative values to use for panning.
		 */
		@SuppressWarnings("unchecked")
		public void pan(PointND<? extends Number> deltas) {
			PlotArea plotArea = plot.getPlotArea();
			PointND<Number> center = (PointND<Number>) getCenter();
			double x = center.get(0).doubleValue();
			x += deltas.get(0).doubleValue()/plotArea.getWidth();
			double y = center.get(1).doubleValue();
			y += deltas.get(1).doubleValue()/plotArea.getHeight();
			center.set(0, x);
			center.set(1, y);
			setCenter(center);
		}

		/**
		 * Sets the object's position and zoom level to the default state.
		 */
		public void reset() {
			setCenter(centerOriginal);
			setZoom(1.0);
		}

		/**
		 * Sets the current state as the default state of the object.
		 * Resetting the navigator will then return to the default state.
		 */
		public void setDefaultState() {
			centerOriginal = getCenter();
			zoomOriginal = plot.getRadius();
		}
	}

	/**
	 * Class that represents the drawing area of a {@code PiePlot}.
	 */
	public static class PiePlotArea2D extends PlotArea {
		/** Version id for serialization. */
		private static final long serialVersionUID = 5646816099037852271L;

		/** Pie plot that this renderer is associated to. */
		private final PiePlot plot;

		/**
		 * Constructor that creates a new instance and initializes it with a
		 * plot acting as data provider.
		 * @param plot Data provider.
		 */
		public PiePlotArea2D(PiePlot plot) {
			this.plot = plot;
		}

		/**
		 * Draws the {@code Drawable} with the specified drawing context.
		 * @param context Environment used for drawing
		 */
		public void draw(DrawingContext context) {
			drawBackground(context);
			drawBorder(context);
			drawPlot(context);
			plot.drawLegend(context);
		}

		@Override
		protected void drawPlot(DrawingContext context) {
			Graphics2D graphics = context.getGraphics();

			Shape clipBoundsOld = graphics.getClip();
			Insets2D clipOffset = getClippingOffset();
			if (clipOffset != null) {
				final double fontSize = getBaseFont().getSize2D();

				// Perform clipping
				Shape clipBounds = new Rectangle2D.Double(
					getX() + clipOffset.getLeft()*fontSize,
					getY() + clipOffset.getTop()*fontSize,
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

			AffineTransform txOrig = graphics.getTransform();
			graphics.translate(getX(), getY());

			// Get width and height of the plot area for relative sizes
			Rectangle2D bounds = getBounds();

			// Move to center, so origin for point renderers will be (0, 0)
			Point2D center = plot.getCenter();
			if (center == null) {
				center = new Point2D.Double(0.5, 0.5);
			}
			graphics.translate(
				center.getX()*bounds.getWidth(),
				center.getY()*bounds.getHeight()
			);

			// Paint points and lines
			for (DataSource s : plot.getVisibleData()) {
				// Skip empty data source
				if (s.getColumnCount() == 0) {
					continue;
				}

				// TODO Use property for column index
				int colIndex = 0;
				if (colIndex < 0 || colIndex >= s.getColumnCount() ||
						!s.isColumnNumeric(colIndex)) {
					continue;
				}

				PointRenderer pointRenderer = plot.getPointRenderer(s);

				String[] axisNames = plot.getMapping(s);
				// TODO Use loop to get all axes instead of direct access
				Axis axis = plot.getAxis(axisNames[0]);
				if (!axis.isValid()) {
					continue;
				}
				AxisRenderer axisRenderer = plot.getAxisRenderer(axisNames[0]);

				List<Axis> axes = asList(axis);
				List<AxisRenderer> axisRenderers = asList(axisRenderer);
				// Draw graphics
				for (int rowIndex = 0; rowIndex < s.getRowCount(); rowIndex++) {
					Row row = s.getRow(rowIndex);
					PointData pointData = new PointData(
						axes, axisRenderers, row, row.getIndex(), 0);
					Shape shape = pointRenderer.getPointShape(pointData);
					Drawable point = pointRenderer.getPoint(pointData, shape);
					point.setBounds(bounds);
					point.draw(context);
				}
				// Draw labels
				for (int rowIndex = 0; rowIndex < s.getRowCount(); rowIndex++) {
					Row row = s.getRow(rowIndex);
					PointData pointData = new PointData(
						axes, axisRenderers, row, row.getIndex(), 0);
					Shape shape = pointRenderer.getPointShape(pointData);
					Drawable point = pointRenderer.getValue(pointData, shape);
					point.setBounds(bounds);
					point.draw(context);
				}
			}

			graphics.setTransform(txOrig);

			if (clipOffset != null) {
				// Reset clipping
				graphics.setClip(clipBoundsOld);
			}
		}
	}

	/**
	 * Data class for storing slice information in world units.
	 */
	protected static final class Slice {
		/** Value where the slice starts. */
		public final double start;
		/** Value where the slice ends. */
		public final double end;
		/** Whether the slice is visible. */
		public final boolean visible;

		/**
		 * Initializes a new slice with start and end value.
		 * @param start Value where the slice starts.
		 * @param end Value where the slice ends.
		 * @param visible Visibility of the slice.
		 */
		public Slice(double start, double end, boolean visible) {
			this.start = start;
			this.end = end;
			this.visible = visible;
		}
	}

	/**
	 * A point renderer for a single slice in a pie plot.
	 */
	public static class PieSliceRenderer extends AbstractPointRenderer {
		/** Version id for serialization. */
		private static final long serialVersionUID = 1135636437801090607L;

		/** Pie plot this renderer is attached to. */
		private final PiePlot plot;

		/** Relative outer radius of the current pie slice,
		 * in percentage of the total radius. */
		private double outerRadius;
		/** Relative inner radius of the current pie slice,
		 * in percentage of the total radius. */
		private double innerRadius;
		/** Gap of the current pie slice, in pixels. */
		private double gap;

		/**
		 * Initializes a new instance with a pie plot object.
		 * @param plot Pie plot.
		 */
		public PieSliceRenderer(PiePlot plot) {
			this.plot =  plot;

			setValueColumn(0);
			setErrorColumnTop(1);
			setErrorColumnBottom(2);

			setColor(new QuasiRandomColors());
			outerRadius = 1.0;
			innerRadius = 0.0;
			gap = 0.0;
		}

		private Slice getSlice(PointData pointData) {
			double sliceStart = (Double) pointData.row.get(0);
			double sliceEnd = (Double) pointData.row.get(1);
			boolean sliceVisible = (Boolean) pointData.row.get(2);
			return new Slice(sliceStart, sliceEnd, sliceVisible);
		}

		/**
		 * Returns the value for the outer radius of a pie relative to the
		 * radius set in the plot.
		 * @return Outer radius of a pie relative to the radius of the plot.
		 */
		public double getOuterRadius() {
			return outerRadius;
		}

		/**
		 * Sets the value for the outer radius of a pie relative to the radius
		 * set in the plot.
		 * @param radius Outer radius of a pie relative to the radius of the
		 * plot.
		 */
		public void setOuterRadius(double radius) {
			this.outerRadius = radius;
		}

		/**
		 * Returns the value for the inner radius of a pie relative to the
		 * radius set in the plot.
		 * @return Inner radius of a pie relative to the radius of the plot.
		 */
		public double getInnerRadius() {
			return innerRadius;
		}

		/**
		 * Sets the value for the inner radius of a pie relative to the radius
		 * set in the plot.
		 * @param radius Inner radius of a pie relative to the radius of the
		 * plot.
		 */
		public void setInnerRadius(double radius) {
			this.innerRadius = radius;
		}

		/**
		 * Returns the width of gaps between the segments relative to the font
		 * size.
		 * @return Width of gaps between the segments relative to the font
		 * size.
		 */
		public double getGap() {
			return gap;
		}

		/**
		 * Sets the width of gaps between the segments relative to the font
		 * size.
		 * @param gap Width of gaps between the segments relative to the font
		 * size.
		 */
		public void setGap(double gap) {
			this.gap = gap;
		}

		@Override
		public Drawable getPoint(final PointData data, final Shape shape) {
			return new AbstractDrawable() {
				/** Version id for serialization. */
				private static final long serialVersionUID = -1783451355453643712L;

				public void draw(DrawingContext context) {
					PointRenderer renderer = PieSliceRenderer.this;

					Row row = data.row;
					if (shape == null) {
						return;
					}

					Slice slice = getSlice(data);
					if (!slice.visible) {
						return;
					}

					// Paint slice
					ColorMapper colorMapper = renderer.getColor();
					Paint paint;
					if (colorMapper instanceof ContinuousColorMapper) {
						double sum = plot.getSum(row.getSource());
						if (sum == 0.0) {
							return;
						}
						double sliceStartRel = slice.start/sum;
						double sliceEndRel = slice.end/sum;

						double coloringRel = 0.0;
						int rows = row.getSource().getRowCount();
						if (rows > 1) {
							double posRel = data.index / (double)(rows - 1);
							double posRelInv = 1.0 - posRel;
							coloringRel =
								posRelInv*sliceStartRel + posRel*sliceEndRel;
						}
						paint = ((ContinuousColorMapper) colorMapper).get(coloringRel);
					} else {
						paint = colorMapper.get(data.index);
					}
					GraphicsUtils.fillPaintedShape(
						context.getGraphics(), shape, paint, null);
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
			Slice slice = getSlice(data);
			if (!slice.visible) {
				return null;
			}

			Font font = getValueFont();
			double fontSize = font.getSize2D();

			PlotArea plotArea = plot.getPlotArea();
			double plotAreaSize = Math.min(
				plotArea.getWidth(), plotArea.getHeight())/2.0;
			double radiusRel = plot.getRadius();
			double radius = plotAreaSize*radiusRel;
			double radiusRelOuter = getOuterRadius();
			double radiusOuter = radius*radiusRelOuter;

			// Construct slice
			Row row = data.row;
			double sum = plot.getSum(row.getSource());
			if (sum == 0.0) {
				return null;
			}
			double sliceStartRel = slice.start/sum;
			double sliceEndRel = slice.end/sum;

			double start = plot.getStart();

			double sliceSpan = (sliceEndRel - sliceStartRel)*360.0;
			double sliceStart;
			if (plot.isClockwise()) {
				sliceStart = start - sliceEndRel*360.0;
			} else {
				sliceStart = start + sliceStartRel*360.0;
			}
			start = MathUtils.normalizeDegrees(start);

			Arc2D pieSlice = new Arc2D.Double(
				-radiusOuter, -radiusOuter,
				2.0*radiusOuter, 2.0*radiusOuter,
				sliceStart, sliceSpan,
				Arc2D.PIE
			);
			Area doughnutSlice = new Area(pieSlice);

			double gap = getGap();
			if (gap > 0.0) {
				Stroke sliceStroke =
					new BasicStroke((float) (gap*fontSize));
				Area sliceContour =
					new Area(sliceStroke.createStrokedShape(pieSlice));
				doughnutSlice.subtract(sliceContour);
			}

			double radiusRelInner = getInnerRadius();
			if (radiusRelInner > 0.0 && radiusRelInner < radiusRelOuter) {
				double radiusInner = radius*radiusRelInner;
				Ellipse2D inner = new Ellipse2D.Double(
					-radiusInner, -radiusInner,
					2.0*radiusInner, 2.0*radiusInner
				);
				Area hole = new Area(inner);
				doughnutSlice.subtract(hole);
			}

			return doughnutSlice;
		}

		/**
		 * Draws the specified value label for the specified shape.
		 * @param context Environment used for drawing.
		 * @param slice Pie slice to draw.
		 * @param radius Radius of pie slice in view units (e.g. pixels).
		 * @param row Data row containing the point.
		 * @param rowIndex Index number used for coloring.
		 */
		protected void drawValueLabel(DrawingContext context, Slice slice,
				double radius, Row row, int rowIndex) {
			Comparable<?> value = slice.end - slice.start;

			// Formatting
			Format format = getValueFormat();
			if ((format == null) && (value instanceof Number)) {
				format = NumberFormat.getInstance();
			}

			// Text to display
			String text = (format != null) ? format.format(value) : value.toString();

			// Visual settings
			ColorMapper colors = getValueColor();
			Paint paint = colors.get(rowIndex);
			Font font = getValueFont();
			double fontSize = font.getSize2D();

			// Layout settings
			Location location = getValueLocation();
			double alignX = getValueAlignmentX();
			double alignY = getValueAlignmentY();
			double rotation = getValueRotation();
			double distance = getValueDistance();
			if (MathUtils.isCalculatable(distance)) {
				distance *= fontSize;
			} else {
				distance = 0.0;
			}

			// Vertical layout
			double radiusRelOuter = getOuterRadius();
			double radiusRelInner = getInnerRadius();
			double radiusOuter = radius*radiusRelOuter;
			double radiusInner = radius*radiusRelInner;
			double distanceV = distance;
			double labelPosV;
			if (location == Location.NORTH) {
				labelPosV = radiusOuter + distanceV;
			} else if (location == Location.SOUTH) {
				labelPosV = Math.max(radiusInner - distanceV, 0);
			} else {
				double sliceHeight = radiusOuter - radiusInner;
				if (2.0*distance >= sliceHeight) {
					alignY = 0.5;
					distanceV = 0.0;
				}
				labelPosV = radiusInner + distanceV +
					alignY*(sliceHeight - 2.0*distanceV);
			}

			// Horizontal layout
			double sum = plot.getSum(row.getSource());
			if (sum == 0.0) {
				return;
			}
			double sliceStartRel = slice.start/sum;
			double sliceEndRel = slice.end/sum;
			double circumference = 2.0*labelPosV*Math.PI;
			double distanceRelH = distance/circumference;
			double sliceWidthRel = sliceEndRel - sliceStartRel;
			if (2.0*distanceRelH >= sliceWidthRel) {
				alignX = 0.5;
				distanceRelH = 0.0;
			}
			double labelPosRelH = sliceStartRel + distanceRelH +
				alignX*(sliceWidthRel - 2.0*distanceRelH);

			double start = plot.getStart();

			double angleStart = Math.toRadians(-start);
			double direction = 1.0;
			if (!plot.isClockwise()) {
				direction = -1.0;
			}
			double angle = angleStart + direction*labelPosRelH*2.0*Math.PI;
			double dirX = Math.cos(angle);
			double dirY = Math.sin(angle);

			// Create a label with the settings
			Label label = new Label(text);
			label.setAlignmentX(1.0 - 0.5*dirX - 0.5);
			label.setAlignmentY(0.5*dirY + 0.5);
			label.setRotation(rotation);
			label.setColor(paint);
			label.setFont(font);

			// Calculate label position
			Dimension2D sizeLabel = label.getPreferredSize();
			double anchorX = 0.5;
			double anchorY = 0.5;
			if (location == Location.NORTH || location == Location.SOUTH) {
				anchorX = dirX*sizeLabel.getWidth()/2.0;
				anchorY = dirY*sizeLabel.getHeight()/2.0;
				if (location == Location.SOUTH) {
					anchorX = -anchorX;
					anchorY = -anchorY;
				}
			}

			// Resize label component
			double x = labelPosV*dirX + anchorX - sizeLabel.getWidth()/2.0;
			double y = labelPosV*dirY + anchorY - sizeLabel.getHeight()/2.0;
			double w = sizeLabel.getWidth();
			double h = sizeLabel.getHeight();
			label.setBounds(x, y, w, h);

			label.draw(context);
		}

		@Override
		public Drawable getValue(final PointData data, final Shape shape) {
			return new AbstractDrawable() {
				/** Version id for serialization. */
				private static final long serialVersionUID1 = 8389872806138135038L;

				public void draw(DrawingContext context) {
					PointRenderer renderer = PieSliceRenderer.this;

					Row row = data.row;
					if (shape == null) {
						return;
					}

					Slice slice = getSlice(data);
					if (!slice.visible) {
						return;
					}

					PlotArea plotArea = plot.getPlotArea();
					double plotAreaSize = Math.min(
						plotArea.getWidth(), plotArea.getHeight())/2.0;
					double radiusRel = plot.getRadius();
					double radius1 = plotAreaSize*radiusRel;

					if (renderer.isValueVisible()) {
						drawValueLabel(context, slice, radius1, row, data.index);
					}
				}
			};
		}
	}

	/**
	 * A legend implementation for pie plots that displays items for each data
	 * value of a data source.
	 */
	public static class PiePlotLegend extends ValueLegend {
		/** Version id for serialization. */
		private static final long serialVersionUID = 309673490751330686L;

		/** Plot that contains settings and renderers. */
		private final PiePlot plot;

		/**
		 * Initializes a new instance with a specified plot.
		 * @param plot Plot.
		 */
		public PiePlotLegend(PiePlot plot) {
			this.plot = plot;
		}

		@Override
		protected Iterable<Row> getEntries(DataSource source) {
			Iterable<Row> slicesAndGaps = super.getEntries(source);
			List<Row> slices = new LinkedList<>();
			for (Row row : slicesAndGaps) {
				if (!row.isColumnNumeric(0)) {
					continue;
				}
				boolean isVisible = (Boolean) row.get(2);
				if (isVisible) {
					slices.add(row);
				}
			}
			return slices;
		}

		@Override
		protected Drawable getSymbol(final Row row) {
			return new LegendSymbol(row, plot.getPointRenderer(row.getSource()),
					plot.getFont(), plot.getLegend().getSymbolSize());
		}

		@Override
		protected String getLabel(Row row) {
			Number sliceStart = (Number) row.get(0);
			Number sliceEnd = (Number) row.get(1);
			Number sliceWidth = sliceEnd.doubleValue() - sliceStart.doubleValue();
			Format format = getLabelFormat();
			if ((format == null)) {
				format = NumberFormat.getInstance();
			}
			return format.format(sliceWidth);
		}
	}

	private static class LegendSymbol extends AbstractLegend.AbstractSymbol {
		private final Row row;
		private final PointRenderer pointRenderer;

		public LegendSymbol(Row row, PointRenderer pointRenderer, Font font, Dimension2D symbolSize) {
			super(font, symbolSize);
			this.row = row;
			this.pointRenderer = pointRenderer;
		}

		@Override
		public void draw(DrawingContext context) {
			Rectangle2D bounds = getBounds();

			Shape shape = new Rectangle2D.Double(
					0.0, 0.0, bounds.getWidth(), bounds.getHeight());

			PointData pointData = new PointData(
					asList((Axis) null),
					asList((AxisRenderer) null),
					row, row.getIndex(), 0);

			Drawable drawable = pointRenderer.getPoint(pointData, shape);

			Graphics2D graphics = context.getGraphics();
			AffineTransform txOrig = graphics.getTransform();
			graphics.translate(bounds.getX(), bounds.getY());
			drawable.draw(context);
			graphics.setTransform(txOrig);
		}
	}

	/**
	 * Initializes a new pie plot with the specified data source.
	 * @param data Data to be displayed.
	 */
	public PiePlot(DataSource data) {
		super();

		center = new Point2D.Double(0.5, 0.5);
		radius = 1.0;
		start = 0.0;
		clockwise = true;

		pointRenderers = new HashMap<>();

		setPlotArea(new PiePlotArea2D(this));
		setLegend(new PiePlotLegend(this));

		add(data);

		createDefaultAxes();
		createDefaultAxisRenderers();

		dataUpdated(data);
	}

	@Override
	protected void createDefaultAxes() {
		// Create x axis and y axis by default
		Axis axisPie = new Axis();
		setAxis(AXIS_TANGENTIAL, axisPie);
	}

	@Override
	public void autoscaleAxis(String axisName) {
		if (!AXIS_TANGENTIAL.equals(axisName)) {
			super.autoscaleAxis(axisName);
			return;
		}

		List<DataSource> sources = getVisibleData();
		if (sources.isEmpty()) {
			return;
		}

		DataSource data = sources.get(0);
		if (data.getRowCount() == 0) {
			return;
		}

		double sum = getSum(data);
		if (sum == 0.0) {
			return;
		}

		Axis axis = getAxis(axisName);
		if (axis == null || !axis.isAutoscaled()) {
			return;
		}
		axis.setRange(0.0, sum);
	}

	@Override
	protected void createDefaultAxisRenderers() {
		// Create a linear renderer for the pie slices by default
		AxisRenderer renderer = new LinearRenderer2D();
		// Create a circle with radius 1.0 as shape for the axis
		Shape shape = new Ellipse2D.Double(-1.0, -1.0, 2.0, 2.0);
		renderer.setShape(shape);
		// Don't show axis
		renderer.setShapeVisible(false);

		setAxisRenderer(AXIS_TANGENTIAL, renderer);
	}

	@Override
	public void add(int index, DataSource source, boolean visible) {
		if (getData().size() != 0) {
			throw new IllegalArgumentException(
				"This plot type only supports a single data source."); //$NON-NLS-1$
		}

		PointRenderer pointRendererDefault = new PieSliceRenderer(this);
		setPointRenderer(source, pointRendererDefault);

		super.add(index, source, visible);
		setMapping(source, AXIS_TANGENTIAL);
	}

	/**
	 * Returns the {@code PointRenderer} for the specified data source.
	 * @param s Data source.
	 * @return PointRenderer.
	 */
	public PointRenderer getPointRenderer(DataSource s) {
		return pointRenderers.get(s);
	}

	/**
	 * Sets the {@code PointRenderer} for a certain data source to the
	 * specified value.
	 * @param s Data source.
	 * @param pointRenderer PointRenderer to be set.
	 */
	public void setPointRenderer(DataSource s, PointRenderer pointRenderer) {
		this.pointRenderers.put(s, pointRenderer);
	}

	/**
	 * Returns a navigator instance that can control the current object.
	 * @return A navigator instance.
	 */
	public Navigator getNavigator() {
		if (navigator == null) {
			navigator = new PiePlotNavigator(this);
		}
		return navigator;
	}

	/**
	 * Returns the sum of all absolute values in the data column of a specified
	 * data source.
	 * @param source Data source.
	 * @return Sum of all absolute values for the specified data source.
	 */
	protected double getSum(DataSource source) {
		double sum;
		synchronized (source) {
			sum = (Double) source.get(1, source.getRowCount() - 1);
		}
		return sum;
	}

	private static class PieData extends AbstractDataSource {
		private final DataSource data;

		public PieData(DataSource data) {
			this.data = data;
			data.addDataListener(new DataListener() {
				@Override
				public void dataAdded(DataSource source, DataChangeEvent... events) {
					notifyDataAdded(events);
				}

				@Override
				public void dataUpdated(DataSource source, DataChangeEvent... events) {
					notifyDataUpdated(events);
				}

				@Override
				public void dataRemoved(DataSource source, DataChangeEvent... events) {
					notifyDataRemoved(events);
				}
			});

			setColumnTypes(getColumnTypesFor(data).toArray(new Class[] {}));
		}

		private List<Class<? extends Comparable<?>>> getColumnTypesFor(DataSource data) {
			List<Class<? extends Comparable<?>>> columnTypes = new LinkedList<>();
			for (int colIndex = 0; colIndex < data.getColumnCount(); colIndex++) {
				Column<?> column = data.getColumn(colIndex);
				if (column.isNumeric()) {
					columnTypes.add(Double.class);
					columnTypes.add(Double.class);
					columnTypes.add(Boolean.class);
				} else {
					columnTypes.add(column.getType());
				}
			}
			return columnTypes;
		}

		@Override
		public Comparable<?> get(int col, int row) {
			Iterable<Double> accumulatedColumnData = new Accumulation(data.getColumn(0));
			if (col == 0) {
				if (row == 0) {
					return 0.0;
				}
				return get(accumulatedColumnData, row - 1);
			} else if (col == 1) {
				return get(accumulatedColumnData, row);
			} else if (col == 2) {
				return ((Number) data.get(0, row)).doubleValue() > 0.0;
			}
			return null;
		}

		@Override
		public int getRowCount() {
			return data.getRowCount();
		}

		private static <T> T get(Iterable<T> iterable, int index) {
			T element = null;
			int elementIndex = 0;
			for (T e : iterable) {
				if (elementIndex == index) {
					element = e;
					break;
				}
				elementIndex++;
			}
			return element;
		}
	}

	public static DataSource createPieData(DataSource data) {
		return new PieData(data);
	}

	@Override
	protected void dataChanged(DataSource source, DataChangeEvent... events) {
		super.dataChanged(source, events);
		autoscaleAxes();
	}

	/**
	 * Custom deserialization method.
	 * @param in Input stream.
	 * @throws ClassNotFoundException if a serialized class doesn't exist anymore.
	 * @throws IOException if there is an error while reading data from the
	 *         input stream.
	 */
	private void readObject(ObjectInputStream in)
			throws ClassNotFoundException, IOException {
		// Default deserialization
		in.defaultReadObject();

		// Update caches
		for (DataSource source : getData()) {
			dataUpdated(source);
		}
	}

	/**
	 * Returns a point which defines the center of the pie. The coordinates
	 * are relative to the plot area dimensions, i.e. 0.0 means left/top,
	 * 0.5 means the center, and 1.0 means right/bottom.
	 * @return Point which defines the center of the pie.
	 */
	public Point2D getCenter() {
		return center;
	}

	/**
	 * Sets the center of the pie. The coordinates must be relative to the plot
	 * area dimensions, i.e. 0.0 means left/top, 0.5 means the center, and 1.0
	 * means right/bottom.
	 * @param center Point which defines the center of the pie.
	 */
	public void setCenter(Point2D center) {
		this.center.setLocation(center);
	}

	/**
	 * Returns the radius of the pie relative to the plot area size.
	 * @return Radius of the pie relative to the plot area size.
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * Sets the radius of the pie relative to the plot area size.
	 * @param radius Radius of the pie relative to the plot area size.
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}

	/**
	 * Returns the starting angle of the first segment. The angle is
	 * counterclockwise.
	 * @return Starting angle of the first segment in degrees.
	 */
	public double getStart() {
		return start;
	}

	/**
	 * Sets the starting angle of the first segment. The angle is always
	 * applied counterclockwise.
	 * @param start Starting angle of the first segment in degrees.
	 */
	public void setStart(double start) {
		double startOld = this.start;

		this.start = start;

		AxisRenderer axisRenderer = getAxisRenderer(PiePlot.AXIS_TANGENTIAL);
		if (axisRenderer != null) {
			Shape shape = axisRenderer.getShape();
			if (shape != null) {
				double delta = Math.toRadians(startOld - start);
				AffineTransform tx = AffineTransform.getRotateInstance(delta);
				shape = tx.createTransformedShape(shape);
				axisRenderer.setShape(shape);
			}
		}
	}

	/**
	 * Returns whether the segments are in clockwise or counterclockwise order.
	 * @return {@code true} if segments are in clockwise order,
	 * otherwise {@code false}.
	 */
	public boolean isClockwise() {
		return clockwise;
	}

	/**
	 * Sets whether the segments will be in clockwise or counterclockwise order.
	 * @param clockwise {@code true} if segments should be in clockwise order,
	 * otherwise {@code false}.
	 */
	public void setClockwise(boolean clockwise) {
		this.clockwise = clockwise;

		AxisRenderer axisRenderer = getAxisRenderer(PiePlot.AXIS_TANGENTIAL);
		if (axisRenderer != null) {
			Shape shape = axisRenderer.getShape();
			if (shape != null) {
				shape = GeometryUtils.reverse(shape);
				axisRenderer.setShape(shape);
			}
		}
	}
}

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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DummyData;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.graphics.Orientation;
import de.erichseifert.gral.navigation.Navigable;
import de.erichseifert.gral.navigation.NavigationDirection;
import de.erichseifert.gral.navigation.Navigator;
import de.erichseifert.gral.plots.areas.AreaRenderer;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisListener;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.axes.LinearRenderer2D;
import de.erichseifert.gral.plots.axes.Tick;
import de.erichseifert.gral.plots.axes.Tick.TickType;
import de.erichseifert.gral.plots.legends.AbstractLegend;
import de.erichseifert.gral.plots.legends.SeriesLegend;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.plots.points.DefaultPointRenderer2D;
import de.erichseifert.gral.plots.points.PointData;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.util.GeometryUtils;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.PointND;


/**
 * <p>Class that displays data in an two dimensional coordinate system
 * (x-y plot). It also serves as a base class for many other plot types.</p>
 * <p>To create a new {@code XYPlot} simply create a new instance
 * using one or more data sources. Example:</p>
 * <pre>
 * DataTable data = new DataTable(Integer.class, Integer.class);
 * data.add( 1, 2);
 * data.add(-5, 0);
 *
 * XYPlot plot = new XYPlot(data);
 * </pre>
 */
public class XYPlot extends AbstractPlot implements Navigable, AxisListener {
	/** Version id for serialization. */
	private static final long serialVersionUID = 4501074701747572783L;

	/** Key for specifying the x-axis of an xy-plot. */
	public static final String AXIS_X = "x"; //$NON-NLS-1$
	/** Key for specifying the secondary x-axis of an xy-plot. */
	public static final String AXIS_X2 = "x2"; //$NON-NLS-1$
	/** Key for specifying the y-axis of an xy-plot. */
	public static final String AXIS_Y = "y"; //$NON-NLS-1$
	/** Key for specifying the secondary y-axis of an xy-plot. */
	public static final String AXIS_Y2 = "y2"; //$NON-NLS-1$

	/** Mapping from data source to point renderers. */
	private final Map<DataSource, List<PointRenderer>> pointRenderersByDataSource;
	/** Mapping from data source to line renderers. */
	private final Map<DataSource, List<LineRenderer>> lineRenderersByDataSource;
	/** Mapping from data source to area renderers. */
	private final Map<DataSource, List<AreaRenderer>> areaRenderersByDataSource;

	/** Cache for the {@code Navigator} implementation. */
	private transient XYPlotNavigator navigator;
	/** A flag that shows whether the navigator has been properly
	initialized. */
	private transient boolean navigatorInitialized;

	/**
	 * Constants which determine the direction of zoom and pan actions.
	 */
	public enum XYNavigationDirection implements NavigationDirection {
		/** Value for zooming and panning horizontally. */
		HORIZONTAL(XYPlot.AXIS_X, XYPlot.AXIS_X2),
		/** Value for zooming and panning vertically. */
		VERTICAL(XYPlot.AXIS_Y, XYPlot.AXIS_Y2),
		/** Value for zooming and panning in all direction. */
		ARBITRARY(XYPlot.AXIS_X, XYPlot.AXIS_Y, XYPlot.AXIS_X2, XYPlot.AXIS_Y2);

		/** Names of the axes that have the same direction. */
		private final String[] axesNames;

		/**
		 * Initializes a new instance with the names of the axes that have the
		 * same direction.
		 * @param axesNames Names of the axes that have the same direction.
		 */
		XYNavigationDirection(String... axesNames) {
			this.axesNames = axesNames;
		}

		/**
		 * Returns the names of the axes that have the direction described by
		 * this object.
		 * @return Names of the axes that have the same direction.
		 */
		public String[] getAxesNames() {
			return axesNames;
		}
	}

	/**
	 * Navigator implementation for two-dimensional plots.
	 */
	public static class XYPlotNavigator extends PlotNavigator {
		/**
		 * Initializes a new Navigator for two-dimensional plots with the
		 * default axes.
		 * @param plot Two-dimensional plot that should be controlled.
		 */
		public XYPlotNavigator(XYPlot plot) {
			super(plot, XYNavigationDirection.ARBITRARY.getAxesNames());
		}

		@Override
		public void setDirection(NavigationDirection direction) {
			if (direction == getDirection()) {
				return;
			}
			if (!(direction instanceof XYNavigationDirection)) {
				throw new IllegalArgumentException("Unknown direction.");
			}
			String[] axesNames = ((XYNavigationDirection)direction).getAxesNames();
			setAxes(axesNames);
			super.setDirection(direction);
		}

		@Override
		protected Number getDimensionValue(String axisName,
				PointND<? extends Number> values) {
			if (XYPlot.AXIS_Y.equals(axisName) || XYPlot.AXIS_Y2.equals(axisName)) {
				return -values.get(1).doubleValue();
			}
			return values.get(0);
		}

		@Override
		protected int getDimensions() {
			return 2;
		}
	}

	/**
	 * Class that represents the drawing area of an {@code XYPlot}.
	 */
	public static class XYPlotArea2D extends PlotArea {
		/** Version id for serialization. */
		private static final long serialVersionUID = -3673157774425536428L;

		/** x-y plot this plot area is associated to. */
		private final XYPlot plot;

		/** Decides whether the horizontal grid lines at major ticks are drawn. */
		private boolean majorGridX;
		/** Decides whether the vertical grid lines at major ticks are drawn. */
		private boolean majorGridY;
		/** Paint to fill the grid lines at major ticks. */
		private Paint majorGridColor;

		/** Decides whether the horizontal grid lines at minor ticks are drawn. */
		private boolean minorGridX;
		/** Decides whether the vertical grid lines at minor ticks are drawn. */
		private boolean minorGridY;
		/** Paint to fill the grid lines at minor ticks. */
		private Paint minorGridColor;

		/**
		 * Creates a new instance with default settings and initializes it with
		 * a plot serving as data provider.
		 * @param plot Data provider.
		 */
		public XYPlotArea2D(XYPlot plot) {
			this.plot = plot;

			majorGridX = true;
			majorGridY = true;
			majorGridColor = new Color(0.0f, 0.0f, 0.0f, 0.1f);

			minorGridX = false;
			minorGridY = false;
			minorGridColor = new Color(0.0f, 0.0f, 0.0f, 0.05f);
		}

		/**
		 * Draws the {@code Drawable} with the specified {@code Graphics2D}
		 * object.
		 * @param context Environment used for drawing
		 */
		public void draw(DrawingContext context) {
			drawBackground(context);
			drawGrid(context);
			drawBorder(context);
			drawPlot(context);
			plot.drawAxes(context);
			plot.drawLegend(context);
		}

		/**
		 * Draws the grid using the specified drawing context.
		 * @param context Environment used for drawing.
		 */
		protected void drawGrid(DrawingContext context) {
			Graphics2D graphics = context.getGraphics();

			AffineTransform txOrig = graphics.getTransform();
			graphics.translate(getX(), getY());
			AffineTransform txOffset = graphics.getTransform();
			Rectangle2D bounds = getBounds();

			// Draw gridX
			if (isMajorGridX() || isMinorGridX()) {
				AxisRenderer axisXRenderer = plot.getAxisRenderer(AXIS_X);
				Axis axisX = plot.getAxis(AXIS_X);
				if (axisXRenderer != null && axisX != null && axisX.isValid()) {
					Shape shapeX = axisXRenderer.getShape();
					Rectangle2D shapeBoundsX = shapeX.getBounds2D();
					List<Tick> ticksX = axisXRenderer.getTicks(axisX);
					Line2D gridLineVert = new Line2D.Double(
						-shapeBoundsX.getMinX(),
						-shapeBoundsX.getMinY(),
						-shapeBoundsX.getMinX(),
						bounds.getHeight() - shapeBoundsX.getMinY()
					);
					for (Tick tick : ticksX) {
						if ((tick.type == TickType.MAJOR && !isMajorGridX()) ||
								(tick.type == TickType.MINOR && !isMinorGridX())) {
							continue;
						}
						Point2D tickPoint = tick.position.getPoint2D();
						if (tickPoint == null) {
							continue;
						}

						Paint paint = majorGridColor;
						if (tick.type == TickType.MINOR) {
							paint = getMinorGridColor();
						}
						graphics.translate(tickPoint.getX(), tickPoint.getY());
						GraphicsUtils.drawPaintedShape(
							graphics, gridLineVert, paint, null, null);
						graphics.setTransform(txOffset);
					}
				}
			}

			// Draw gridY
			if (isMajorGridY() || isMinorGridY()) {
				Axis axisY = plot.getAxis(AXIS_Y);
				AxisRenderer axisYRenderer = plot.getAxisRenderer(AXIS_Y);
				if (axisY != null && axisY.isValid() && axisYRenderer != null) {
					Shape shapeY = axisYRenderer.getShape();
					Rectangle2D shapeBoundsY = shapeY.getBounds2D();
					List<Tick> ticksY = axisYRenderer.getTicks(axisY);
					Line2D gridLineHoriz = new Line2D.Double(
						-shapeBoundsY.getMinX(), -shapeBoundsY.getMinY(),
						bounds.getWidth() - shapeBoundsY.getMinX(), -shapeBoundsY.getMinY()
					);
					for (Tick tick : ticksY) {
						boolean isMajorTick = tick.type == TickType.MAJOR;
						boolean isMinorTick = tick.type == TickType.MINOR;
						if ((isMajorTick && !isMajorGridY()) ||
								(isMinorTick && !isMinorGridY())) {
							continue;
						}
						Point2D tickPoint = tick.position.getPoint2D();
						if (tickPoint == null) {
							continue;
						}

						Paint paint = majorGridColor;
						if (isMinorTick) {
							paint = getMinorGridColor();
						}
						graphics.translate(tickPoint.getX(), tickPoint.getY());
						GraphicsUtils.drawPaintedShape(
							graphics, gridLineHoriz, paint, null, null);
						graphics.setTransform(txOffset);
					}
				}
			}

			graphics.setTransform(txOrig);
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
			AffineTransform txOffset = graphics.getTransform();


			// Paint points and lines
			for (DataSource s : plot.getVisibleData()) {
				// Skip empty data source
				if (s.getColumnCount() == 0) {
					continue;
				}

				int colX = 0;
				if (colX < 0 || colX >= s.getColumnCount() || !s.isColumnNumeric(colX)) {
					continue;
				}
				int colY = 1;
				if (colY < 0 || colY >= s.getColumnCount() || !s.isColumnNumeric(colY)) {
					continue;
				}

				String[] axisNames = plot.getMapping(s);
				Axis axisX = plot.getAxis(axisNames[0]);
				Axis axisY = plot.getAxis(axisNames[1]);
				if (!axisX.isValid() || !axisY.isValid()) {
					continue;
				}
				AxisRenderer axisXRenderer = plot.getAxisRenderer(axisNames[0]);
				AxisRenderer axisYRenderer = plot.getAxisRenderer(axisNames[1]);

				List<DataPoint> points = new LinkedList<>();
				for (int i = 0; i < s.getRowCount(); i++) {
					Row row = new Row(s, i);
					Number valueX = (Number) row.get(colX);
					Number valueY = (Number) row.get(colY);

					PointND<Double> axisPosX = (axisXRenderer != null)
						? axisXRenderer.getPosition(axisX, valueX, true, false)
						: new PointND<>(0.0, 0.0);
					PointND<Double> axisPosY = (axisYRenderer != null)
						? axisYRenderer.getPosition(axisY, valueY, true, false)
						: new PointND<>(0.0, 0.0);
					if (axisPosX == null || axisPosY == null) {
						continue;
					}

					PointND<Double> pos = new PointND<>(
							axisPosX.get(PointND.X), axisPosY.get(PointND.Y));

					PointData pointData = new PointData(
						Arrays.asList(axisX, axisY),
						Arrays.asList(axisXRenderer, axisYRenderer),
						row, row.getIndex(), colY);

					DataPoint dataPoint = new DataPoint(pointData, pos);
					points.add(dataPoint);
				}

				List<PointRenderer> pointRenderers = new ArrayList<>(plot.getPointRenderers(s));
				Collections.reverse(pointRenderers);

				List<AreaRenderer> areaRenderers = new ArrayList<>(plot.getAreaRenderers(s));
				Collections.reverse(areaRenderers);
				for (AreaRenderer areaRenderer : areaRenderers) {
					Shape punchedArea = areaRenderer.getAreaShape(points);
					for (PointRenderer pointRenderer : pointRenderers) {
						List<Shape> punchShapes = new ArrayList<>(points.size());
						for (DataPoint point : points) {
							Shape punchShape = pointRenderer.getPointShape(point.data);
							punchShapes.add(punchShape);
						}
						punchedArea = punch(punchedArea, points, punchShapes, areaRenderer.getGap(), areaRenderer.isGapRounded());
					}
					Drawable drawable = areaRenderer.getArea(points, punchedArea);
					drawable.draw(context);
				}

				List<LineRenderer> lineRenderers = new ArrayList<>(plot.getLineRenderers(s));
				Collections.reverse(lineRenderers);
				for (LineRenderer lineRenderer : lineRenderers) {
					Shape punchedLine = lineRenderer.getLineShape(points);
					for (PointRenderer pointRenderer : pointRenderers) {
						List<Shape> punchShapes = new ArrayList<>(points.size());
						for (DataPoint point : points) {
							Shape punchShape = pointRenderer.getPointShape(point.data);
							punchShapes.add(punchShape);
						}
						punchedLine = punch(punchedLine, points, punchShapes, lineRenderer.getGap(), lineRenderer.isGapRounded());
					}
					Drawable drawable = lineRenderer.getLine(points, punchedLine);
					drawable.draw(context);
				}
				if (!plot.getPointRenderers(s).isEmpty()) {
					// Draw graphics
					for (DataPoint point : points) {
						PointND<Double> pos = point.position;
						double pointX = pos.get(PointND.X);
						double pointY = pos.get(PointND.Y);
						graphics.translate(pointX, pointY);
						for (PointRenderer pointRenderer : plot.getPointRenderers(s)) {
							Shape pointShape = pointRenderer.getPointShape(point.data);
							Drawable pointDrawable = pointRenderer.getPoint(point.data, pointShape);
							pointDrawable.draw(context);
						}
						graphics.setTransform(txOffset);
					}
					// Draw labels
					for (DataPoint point : points) {
						PointND<Double> pos = point.position;
						double pointX = pos.get(PointND.X);
						double pointY = pos.get(PointND.Y);
						graphics.translate(pointX, pointY);
						for (PointRenderer pointRenderer : plot.getPointRenderers(s)) {
							Shape pointShape = pointRenderer.getPointShape(point.data);
							Drawable labelDrawable = pointRenderer.getValue(point.data, pointShape);
							labelDrawable.draw(context);
						}
						graphics.setTransform(txOffset);
					}
				}
			}

			// Reset transformation (offset)
			graphics.setTransform(txOrig);

			if (clipOffset != null) {
				// Reset clipping
				graphics.setClip(clipBoundsOld);
			}
		}

		/**
		 * Returns the shape from which the shapes of the specified points are subtracted.
		 * @param shape Shape to be modified.
		 * @param dataPoints Data points on the line.
		 * @param punchShapes Shape used for punching.
		 * @param gap Gap between shape and point shapes.
		 * @param roundedGaps {@code true} if the shape gaps are rounded.
		 * @return Punched shape.
		 */
		protected static Shape punch(Shape shape, List<DataPoint> dataPoints, List<Shape> punchShapes, double gap, boolean roundedGaps) {
			if (!MathUtils.isCalculatable(gap) || gap == 0.0) {
				return shape;
			}

			// Subtract shapes of data points from the line to yield gaps.
			Area punched = new Area(shape);
			for (int pointIndex = 0; pointIndex < dataPoints.size(); pointIndex++) {
				DataPoint p = dataPoints.get(pointIndex);
				punched = GeometryUtils.punch(punched, gap, roundedGaps,
						p.position.getPoint2D(), punchShapes.get(pointIndex));
			}
			return punched;
		}

		/**
		 * Returns whether horizontal grid lines at major ticks along the
		 * x-axis are drawn.
		 * @return {@code true} if horizontal grid lines at major ticks along
		 * the x-axis are drawn, otherwise {@code false}.
		 */
		public boolean isMajorGridX() {
			return majorGridX;
		}

		/**
		 * Sets whether horizontal grid lines at major ticks along the x-axis
		 * will be drawn.
		 * @param gridMajorX {@code true} if horizontal grid lines at major
		 * ticks along the x-axis should be drawn, otherwise {@code false}.
		 */
		public void setMajorGridX(boolean gridMajorX) {
			this.majorGridX = gridMajorX;
		}

		/**
		 * Returns whether vertical grid lines at major ticks along the y-axis
		 * are drawn.
		 * @return {@code true} if vertical grid lines at major ticks along the
		 * y-axis are drawn, otherwise {@code false}.
		 */
		public boolean isMajorGridY() {
			return majorGridY;
		}

		/**
		 * Sets whether vertical grid lines at major ticks along the y-axis
		 * will be drawn.
		 * @param gridMajorY {@code true} if vertical grid lines at major ticks
		 * along the y-axis should be drawn, otherwise {@code false}.
		 */
		public void setMajorGridY(boolean gridMajorY) {
			this.majorGridY = gridMajorY;
		}

		/**
		 * Returns the paint which is used to paint the grid lines at major
		 * ticks.
		 * @return Paint which is used to paint the grid lines at major ticks.
		 */
		public Paint getMajorGridColor() {
			return majorGridColor;
		}

		/**
		 * Sets the paint which will be used to paint the grid lines at major
		 * ticks.
		 * @param color Paint which should be used to paint the grid lines at
		 * major ticks.
		 */
		public void setMajorGridColor(Color color) {
			this.majorGridColor = color;
		}

		/**
		 * Returns whether horizontal grid lines at minor ticks along the
		 * x-axis are drawn.
		 * @return {@code true} if horizontal grid lines at minor ticks along
		 * the x-axis are drawn, otherwise {@code false}.
		 */
		public boolean isMinorGridX() {
			return minorGridX;
		}

		/**
		 * Sets whether horizontal grid lines at minor ticks along the x-axis
		 * will be drawn.
		 * @param gridMinorX {@code true} if horizontal grid lines at minor
		 * ticks along the x-axis should be drawn, otherwise {@code false}.
		 */
		public void setMinorGridX(boolean gridMinorX) {
			this.minorGridX = gridMinorX;
		}

		/**
		 * Returns whether vertical grid lines at minor ticks along the y-axis
		 * are drawn.
		 * @return {@code true} if vertical grid lines at minor ticks along the
		 * y-axis are drawn, otherwise {@code false}.
		 */
		public boolean isMinorGridY() {
			return minorGridY;
		}

		/**
		 * Sets whether vertical grid lines at minor ticks along the y-axis
		 * will be drawn.
		 * @param gridMinorY {@code true} if vertical grid lines at minor ticks
		 * along the y-axis should be drawn, otherwise {@code false}.
		 */
		public void setMinorGridY(boolean gridMinorY) {
			this.minorGridY = gridMinorY;
		}

		/**
		 * Returns the paint which is used to paint the grid lines at minor
		 * ticks.
		 * @return Paint which is used to paint the grid lines at minor ticks.
		 */
		public Paint getMinorGridColor() {
			return minorGridColor;
		}

		/**
		 * Sets the paint which will be used to paint the grid lines at minor
		 * ticks.
		 * @param color Paint which should be used to paint the grid lines at
		 * minor ticks.
		 */
		public void setMinorGridColor(Color color) {
			this.minorGridColor = color;
		}
	}

	/**
	 * Class that displays a legend in an {@code XYPlot}.
	 */
	public static class XYLegend extends SeriesLegend {
		/** Version id for serialization. */
		private static final long serialVersionUID = -4629928754001372002L;

		/** Plot that contains settings and renderers. */
		private final XYPlot plot;

		/**
		 * Constructor that initializes the instance with a plot acting as a
		 * provider for settings and renderers.
		 * @param plot Plot.
		 */
		public XYLegend(XYPlot plot) {
			this.plot = plot;
		}

		@Override
		protected Drawable getSymbol(DataSource data) {
			return new LegendSymbol(plot, data, plot.getFont(), plot.getLegend().getSymbolSize());
		}
	}

	private static class LegendSymbol extends AbstractLegend.AbstractSymbol {
		/** Source for dummy data. */
		private static final DataSource DUMMY_DATA = new DummyData(2, Integer.MAX_VALUE, 0.5);
		private final XYPlot plot;
		private final DataSource data;

		public LegendSymbol(XYPlot plot, DataSource data, Font font, Dimension2D symbolSize) {
			super(font, symbolSize);
			this.plot = plot;
			this.data = data;
		}

		@Override
		public void draw(DrawingContext context) {
			Row symbolRow = new Row(DUMMY_DATA, 0);
			Rectangle2D bounds = getBounds();

			Axis axisX = new Axis(0.0, 1.0);
			AxisRenderer axisRendererX = new LinearRenderer2D();
			axisRendererX.setShape(new Line2D.Double(
					bounds.getMinX(), bounds.getCenterY(),
					bounds.getMaxX(), bounds.getCenterY()));
			Axis axisY = new Axis(0.0, 1.0);
			AxisRenderer axisRendererY = new LinearRenderer2D();
			axisRendererY.setShape(new Line2D.Double(
					bounds.getCenterX(), bounds.getMaxY(),
					bounds.getCenterX(), bounds.getMinY()));

			PointData pointData = new PointData(
					Arrays.asList(axisX, axisY),
					Arrays.asList(axisRendererX, axisRendererY),
					symbolRow, symbolRow.getIndex(), 0);

			DataPoint p1 = new DataPoint(
					pointData,
					new PointND<>(bounds.getMinX(), bounds.getCenterY())
			);
			DataPoint p2 = new DataPoint(
					pointData,
					new PointND<>(bounds.getCenterX(), bounds.getCenterY())
			);
			DataPoint p3 = new DataPoint(
					pointData,
					new PointND<>(bounds.getMaxX(), bounds.getCenterY())
			);
			List<DataPoint> points = Arrays.asList(p1, p2, p3);

			// TODO: Provide a means to set the AreaRenderer used for the Legend
			AreaRenderer areaRenderer = null;
			List<AreaRenderer> areaRenderers = plot.getAreaRenderers(data);
			if (!areaRenderers.isEmpty()) {
				areaRenderer = areaRenderers.get(0);
			}
			if (areaRenderer != null) {
				Shape area = areaRenderer.getAreaShape(points);
				Drawable drawable = areaRenderer.getArea(points, area);
				drawable.draw(context);
			}

			// TODO: Provide a means to set the LineRenderer used for the Legend
			LineRenderer lineRenderer = null;
			List<LineRenderer> lineRenderers = plot.getLineRenderers(data);
			if (!lineRenderers.isEmpty()) {
				lineRenderer = lineRenderers.get(0);
			}
			if (lineRenderer != null) {
				Shape line = lineRenderer.getLineShape(points);
				Drawable drawable = lineRenderer.getLine(points, line);
				drawable.draw(context);
			}

			// TODO: Provide a means to set the PointRenderer used for the Legend
			PointRenderer pointRenderer = null;
			List<PointRenderer> pointRenderers = plot.getPointRenderers(data);
			if (!pointRenderers.isEmpty()) {
				pointRenderer = pointRenderers.get(0);
			}
			if (pointRenderer != null) {
				Graphics2D graphics = context.getGraphics();
				Point2D pos = p2.position.getPoint2D();
				AffineTransform txOrig = graphics.getTransform();
				graphics.translate(pos.getX(), pos.getY());
				Shape shape = pointRenderer.getPointShape(pointData);
				Drawable drawable = pointRenderer.getPoint(pointData, shape);
				drawable.draw(context);
				graphics.setTransform(txOrig);
			}
		}
	}

	/**
	 * Initializes a new instance object with the specified data sources and
	 * reasonable default settings.
	 * @param data Data to be displayed.
	 */
	public XYPlot(DataSource... data) {
		super();

		pointRenderersByDataSource = new HashMap<>(data.length);
		lineRenderersByDataSource = new HashMap<>(data.length);
		areaRenderersByDataSource = new HashMap<>(data.length);

		setPlotArea(new XYPlotArea2D(this));
		setLegend(new XYLegend(this));

		// Handle data sources after the renderer lists are initialized
		for (DataSource source : data) {
			add(source);
		}

		createDefaultAxes();
		autoscaleAxes();
		createDefaultAxisRenderers();

		// Listen for changes of the axis range
		for (String axisName : getAxesNames()) {
			getAxis(axisName).addAxisListener(this);
		}
	}

	@Override
	protected void createDefaultAxes() {
		// Create x axis and y axis by default
		Axis axisX = new Axis();
		Axis axisY = new Axis();
		setAxis(AXIS_X, axisX);
		setAxis(AXIS_Y, axisY);
	}

	@Override
	protected void createDefaultAxisRenderers() {
		// Create renderers for x and y axes by default
		AxisRenderer axisXRenderer = new LinearRenderer2D();
		AxisRenderer axisYRenderer = new LinearRenderer2D();
		setAxisRenderer(AXIS_X, axisXRenderer);
		setAxisRenderer(AXIS_Y, axisYRenderer);
	}

	@Override
	protected void layoutAxes() {
		if (getPlotArea() == null) {
			return;
		}

		// Set the new shapes first to allow for correct positioning
		layoutAxisShape(AXIS_X, Orientation.HORIZONTAL);
		layoutAxisShape(AXIS_X2, Orientation.HORIZONTAL);
		layoutAxisShape(AXIS_Y, Orientation.VERTICAL);
		layoutAxisShape(AXIS_Y2, Orientation.VERTICAL);

		// Set bounds with new axis shapes
		layoutAxisComponent(AXIS_X, Orientation.HORIZONTAL);
		layoutAxisComponent(AXIS_X2, Orientation.HORIZONTAL);
		layoutAxisComponent(AXIS_Y, Orientation.VERTICAL);
		layoutAxisComponent(AXIS_Y2, Orientation.VERTICAL);
	}

	private void layoutAxisShape(String axisName, Orientation orientation) {
		Rectangle2D plotBounds = getPlotArea().getBounds();

		Drawable comp = getAxisComponent(axisName);
		AxisRenderer renderer = getAxisRenderer(axisName);

		if (comp == null || renderer == null) {
			return;
		}

		Dimension2D size = comp.getPreferredSize();

		Shape shape;
		if (orientation == Orientation.HORIZONTAL) {
			shape = new Line2D.Double(
				0.0, 0.0,
				plotBounds.getWidth(), 0.0
			);
		} else {
			shape = new Line2D.Double(
				size.getWidth(), plotBounds.getHeight(),
				size.getWidth(), 0.0
			);
		}
		renderer.setShape(shape);
	}

	private void layoutAxisComponent(String axisName, Orientation orientation) {
		Drawable comp = getAxisComponent(axisName);
		AxisRenderer renderer = getAxisRenderer(axisName);
		if (comp == null || renderer == null) {
			return;
		}

		String nameSecondary;
		if (orientation == Orientation.HORIZONTAL) {
			nameSecondary = AXIS_Y;
		} else {
			nameSecondary = AXIS_X;
		}
		Axis axisSecondary = getAxis(nameSecondary);
		AxisRenderer rendererSecondary = getAxisRenderer(nameSecondary);
		if (axisSecondary == null || !axisSecondary.isValid() ||
				rendererSecondary == null) {
			return;
		}

		Number intersection = renderer.getIntersection();
		PointND<Double> pos = rendererSecondary.getPosition(
				axisSecondary, intersection, false, false);

		if (pos == null) {
			pos = new PointND<>(0.0, 0.0);
		}

		Rectangle2D plotBounds = getPlotArea().getBounds();
		Dimension2D size = comp.getPreferredSize();

		if (orientation == Orientation.HORIZONTAL) {
			comp.setBounds(
				plotBounds.getMinX(),
				pos.get(1) + plotBounds.getMinY(),
				plotBounds.getWidth(),
				size.getHeight()
			);
		} else {
			comp.setBounds(
				plotBounds.getMinX() - size.getWidth() + pos.get(0),
				plotBounds.getMinY(),
				size.getWidth(),
				plotBounds.getHeight()
			);
		}
	}

	/**
	 * Returns all {@code PointRenderer}s that display the data of the specified data source.
	 * @param s Data source in question.
	 * @return Renderers being applied on the specified data source.
	 */
	public List<PointRenderer> getPointRenderers(DataSource s) {
		List<PointRenderer> pointRenderers = pointRenderersByDataSource.get(s);
		if (pointRenderers != null) {
			return Collections.unmodifiableList(pointRenderers);
		}
		return Collections.emptyList();
	}

	/**
	 * Adds a {@code PointRenderer} for the specified data source.
	 * @param s Data to be rendered.
	 * @param pointRenderer PointRenderer to be used.
	 */
	public void addPointRenderer(DataSource s, PointRenderer pointRenderer) {
		List<PointRenderer> pointRenderers = pointRenderersByDataSource.get(s);
		if (pointRenderers == null) {
			pointRenderers = new ArrayList<>();
			pointRenderersByDataSource.put(s, pointRenderers);
		}
		pointRenderers.add(pointRenderer);
	}

	/**
	 * Decouples the specified {@code PointRenderer} from the rendering of the specified data source.
	 * @param s Data to be rendered no longer.
	 * @param pointRenderer PointRenderer to be removed.
	 */
	public void removePointRenderer(DataSource s, PointRenderer pointRenderer) {
		List<PointRenderer> pointRenderers = pointRenderersByDataSource.get(s);
		if (pointRenderers != null) {
			pointRenderers.remove(pointRenderer);
		}
	}

	/**
	 * Sets the {@code PointRenderer}s for a certain data source to the specified value.
	 * @param s Data source.
	 * @param pointRenderers PointRenderers to be set.
	 */
	public void setPointRenderers(DataSource s, List<PointRenderer> pointRenderers) {
		this.pointRenderersByDataSource.put(s, pointRenderers);
	}

	/**
	 * Sets the {@code PointRenderer}s for a certain data source to the specified value.
	 * @param s Data source.
	 * @param pointRendererFirst First PointRenderer.
	 * @param pointRenderers Remaining PointRenderers to be set.
	 */
	public void setPointRenderers(DataSource s, PointRenderer pointRendererFirst, PointRenderer... pointRenderers) {
		List<PointRenderer> pointRendererList = null;
		if (pointRendererFirst == null) {
			setPointRenderers(s, pointRendererList);
			return;
		}
		pointRendererList = new ArrayList<>(pointRenderers.length + 1);
		pointRendererList.add(pointRendererFirst);
		for (PointRenderer pointRenderer : pointRenderers) {
			if (pointRenderer == null) {
				throw new IllegalArgumentException("A PointRenderer for a DataSource cannot be null.");
			}
			pointRendererList.add(pointRenderer);
		}
		setPointRenderers(s, pointRendererList);
	}

	/**
	 * Returns all {@code LineRenderer}s that display the data of the specified data source.
	 * @param s Data source in question.
	 * @return Renderers being applied on the specified data source.
	 */
	public List<LineRenderer> getLineRenderers(DataSource s) {
		List<LineRenderer> lineRenderers = lineRenderersByDataSource.get(s);
		if (lineRenderers != null) {
			return Collections.unmodifiableList(lineRenderers);
		}
		return Collections.emptyList();
	}

	/**
	 * Sets the {@code LineRenderer}s for a certain data source to the specified
	 * value.
	 * @param s Data source.
	 * @param lineRenderers {@code LineRenderer}s to be set.
	 */
	public void setLineRenderers(DataSource s, List<LineRenderer> lineRenderers) {
		lineRenderersByDataSource.put(s, lineRenderers);
	}

	/**
	 * Sets the {@code LineRenderer}s for a certain data source to the specified
	 * value.
	 * @param s Data source.
	 * @param lineRendererFirst First {@code LineRenderer} to be set.
	 * @param lineRenderers Remaining {@code LineRenderer}s to be set.
	 */
	public void setLineRenderers(DataSource s, LineRenderer lineRendererFirst, LineRenderer... lineRenderers) {
		List<LineRenderer> lineRendererList = null;
		if (lineRendererFirst == null) {
			setLineRenderers(s, lineRendererList);
			return;
		}
		lineRendererList = new ArrayList<>(lineRenderers.length + 1);
		lineRendererList.add(lineRendererFirst);
		for (LineRenderer lineRenderer : lineRenderers) {
			if (lineRenderer == null) {
				throw new IllegalArgumentException("A LineRenderer for a DataSource cannot be null.");
			}
			lineRendererList.add(lineRenderer);
		}
		setLineRenderers(s, lineRendererList);
	}

	/**
	 * Returns all {@code AreaRenderer}s for the specified data source.
	 * @param s Data source.
	 * @return {@code AreaRenderer}s used to render the {@code DataSource}.
	 */
	public List<AreaRenderer> getAreaRenderers(DataSource s) {
		List<AreaRenderer> areaRenderers = areaRenderersByDataSource.get(s);
		if (areaRenderers != null) {
			return Collections.unmodifiableList(areaRenderers);
		}
		return Collections.emptyList();
	}

	/**
	 * Sets the {@code AreaRenderer}s for a certain data source to the specified
	 * value.
	 * @param s Data source.
	 * @param areaRenderers {@code AreaRenderer}s to be set.
	 */
	public void setAreaRenderers(DataSource s, List<AreaRenderer> areaRenderers) {
		areaRenderersByDataSource.put(s, areaRenderers);
	}

	/**
	 * Sets the {@code AreaRenderer}s for a certain data source to the specified
	 * value.
	 * @param s Data source.
	 * @param areaRendererFirst First {@code AreaRenderer} to be set.
	 * @param areaRenderers Remaining {@code AreaRenderer}s to be set.
	 */
	public void setAreaRenderers(DataSource s, AreaRenderer areaRendererFirst, AreaRenderer... areaRenderers) {
		List<AreaRenderer> areaRendererList = null;
		if (areaRendererFirst == null) {
			setAreaRenderers(s, areaRendererList);
			return;
		}
		areaRendererList = new ArrayList<>(areaRenderers.length + 1);
		areaRendererList.add(areaRendererFirst);
		for (AreaRenderer areaRenderer : areaRenderers) {
			if (areaRenderer == null) {
				throw new IllegalArgumentException("An AreaRenderer for a DataSource cannot be null.");
			}
			areaRendererList.add(areaRenderer);
		}
		setAreaRenderers(s, areaRendererList);
	}

	@Override
	public void setAxisRenderer(String axisName, AxisRenderer renderer) {
		if (renderer != null) {
			if (AXIS_X2.equals(axisName) || AXIS_Y.equals(axisName)) {
				renderer.setShapeNormalOrientationClockwise(true);
			}
			if (AXIS_Y.equals(axisName)) {
				renderer.getLabel().setRotation(90.0);
			}
		}
		super.setAxisRenderer(axisName, renderer);
	}

	@Override
	public void add(int index, DataSource source, boolean visible) {
		super.add(index, source, visible);

		// Set axis mapping
		setMapping(source, AXIS_X, AXIS_Y);
		// The mapping from columns to axes has changed, so scaling has to be
		// refreshed
		autoscaleAxes();

		// Assign default renderers
		PointRenderer pointRendererDefault = new DefaultPointRenderer2D();
		LineRenderer lineRendererDefault = null;
		AreaRenderer areaRendererDefault = null;
		// FIXME: Overwrites possible present point and line renderers
		setPointRenderers(source, pointRendererDefault);
		setLineRenderers(source, lineRendererDefault);
		setAreaRenderers(source, areaRendererDefault);
	}

	/**
	 * Returns a navigator instance that can control the current object.
	 * @return A navigator instance.
	 */
	public Navigator getNavigator() {
		if (navigator == null) {
			navigator = new XYPlotNavigator(this);
		}
		return navigator;
	}

	@Override
	public void draw(DrawingContext context) {
		super.draw(context);
		if (!navigatorInitialized) {
			getNavigator().setDefaultState();
			navigatorInitialized = true;
		}
	}

	/**
	 * Notified if the range of an axis has changed.
	 * @param axis Axis instance that has changed.
	 * @param min New minimum value.
	 * @param max New maximum value.
	 */
	public void rangeChanged(Axis axis, Number min, Number max) {
		layoutAxes();
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
		// Normal deserialization
		in.defaultReadObject();

		// Restore listeners
		for (String axisName : getAxesNames()) {
			getAxis(axisName).addAxisListener(this);
		}
	}
}

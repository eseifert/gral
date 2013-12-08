/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
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
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DummyData;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;
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
import de.erichseifert.gral.plots.legends.SeriesLegend;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.plots.points.DefaultPointRenderer2D;
import de.erichseifert.gral.plots.points.PointData;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.Orientation;
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

	/** Mapping from data source to point renderer. */
	private final Map<DataSource, PointRenderer> pointRenderers;
	/** Mapping from data source to line renderer. */
	private final Map<DataSource, LineRenderer> lineRenderers;
	/** Mapping from data source to area renderer. */
	private final Map<DataSource, AreaRenderer> areaRenderers;

	/** Cache for the {@code Navigator} implementation. */
	private transient XYPlotNavigator navigator;
	/** A flag that shows whether the navigator has been properly
	initialized. */
	private transient boolean navigatorInitialized;

	/**
	 * Constants which determine the direction of zoom and pan actions.
	 */
	public static enum XYNavigationDirection implements NavigationDirection {
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
		private XYNavigationDirection(String... axesNames) {
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
				// TODO Use real font size instead of fixed value
				final double fontSize = 10.0;

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

				PointRenderer pointRenderer = plot.getPointRenderer(s);
				LineRenderer lineRenderer = plot.getLineRenderer(s);
				AreaRenderer areaRenderer = plot.getAreaRenderer(s);

				String[] axisNames = plot.getMapping(s);
				Axis axisX = plot.getAxis(axisNames[0]);
				Axis axisY = plot.getAxis(axisNames[1]);
				if (!axisX.isValid() || !axisY.isValid()) {
					continue;
				}
				AxisRenderer axisXRenderer = plot.getAxisRenderer(axisNames[0]);
				AxisRenderer axisYRenderer = plot.getAxisRenderer(axisNames[1]);

				List<DataPoint> points = new LinkedList<DataPoint>();
				for (int i = 0; i < s.getRowCount(); i++) {
					Row row = new Row(s, i);
					Number valueX = (Number) row.get(colX);
					Number valueY = (Number) row.get(colY);

					PointND<Double> axisPosX = (axisXRenderer != null)
						? axisXRenderer.getPosition(axisX, valueX, true, false)
						: new PointND<Double>(0.0, 0.0);
					PointND<Double> axisPosY = (axisYRenderer != null)
						? axisYRenderer.getPosition(axisY, valueY, true, false)
						: new PointND<Double>(0.0, 0.0);
					if (axisPosX == null || axisPosY == null) {
						continue;
					}

					PointND<Double> pos = new PointND<Double>(
						axisPosX.get(PointND.X), axisPosY.get(PointND.Y));

					PointData pointData = new PointData(
						Arrays.asList(axisX, axisY),
						Arrays.asList(axisXRenderer, axisYRenderer),
						row, colY);

					Shape shape = null;
					Drawable drawable = null;
					Drawable labelDrawable = null;
					if (pointRenderer != null) {
						shape = pointRenderer.getPointShape(
							pointData);
						drawable = pointRenderer.getPoint(
							pointData, shape);
						labelDrawable = pointRenderer.getValue(
							pointData, shape);
					}

					DataPoint dataPoint = new DataPoint(
						pointData, pos, drawable, shape, labelDrawable);
					points.add(dataPoint);
				}

				if (areaRenderer != null) {
					Shape area = areaRenderer.getAreaShape(points);
					Drawable drawable = areaRenderer.getArea(points, area);
					drawable.draw(context);
				}
				if (lineRenderer != null) {
					Shape line = lineRenderer.getLineShape(points);
					Drawable drawable = lineRenderer.getLine(points, line);
					drawable.draw(context);
				}
				if (pointRenderer != null) {
					// Draw graphics
					for (DataPoint point : points) {
						PointND<Double> pos = point.position;
						double pointX = pos.get(PointND.X);
						double pointY = pos.get(PointND.Y);
						graphics.translate(pointX, pointY);
						point.drawable.draw(context);
						graphics.setTransform(txOffset);
					}
					// Draw labels
					for (DataPoint point : points) {
						PointND<Double> pos = point.position;
						double pointX = pos.get(PointND.X);
						double pointY = pos.get(PointND.Y);
						graphics.translate(pointX, pointY);
						point.labelDrawable.draw(context);
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

		/** Source for dummy data. */
		private static final DataSource DUMMY_DATA = new DummyData(2, Integer.MAX_VALUE, 0.5);

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

		/**
		 * Returns a symbol for rendering a legend item.
		 * @param row Data row.
		 * @return A drawable object that can be used to display the symbol.
		 */
		public Drawable getSymbol(final Row row) {
			return new AbstractSymbol(this) {
				/** Version id for serialization. */
				private static final long serialVersionUID = 5744026898590787285L;

				public void draw(DrawingContext context) {
					DataSource data = row.getSource();
					PointRenderer pointRenderer = plot.getPointRenderer(data);
					LineRenderer lineRenderer = plot.getLineRenderer(data);
					AreaRenderer areaRenderer = plot.getAreaRenderer(data);

					Row symbolRow = new Row(DUMMY_DATA, row.getIndex());
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
						symbolRow, 0);

					Shape shape = null;
					if (pointRenderer != null) {
						shape = pointRenderer.getPointShape(pointData);
					}

					DataPoint p1 = new DataPoint(
						pointData,
						new PointND<Double>(bounds.getMinX(), bounds.getCenterY()),
						null, null, null);
					DataPoint p2 = new DataPoint(
						pointData,
						new PointND<Double>(bounds.getCenterX(), bounds.getCenterY()),
						null, shape, null);
					DataPoint p3 = new DataPoint(
						pointData,
						new PointND<Double>(bounds.getMaxX(), bounds.getCenterY()),
						null, null, null);
					List<DataPoint> points = Arrays.asList(p1, p2, p3);

					if (areaRenderer != null) {
						Shape area = areaRenderer.getAreaShape(points);
						Drawable drawable = areaRenderer.getArea(points, area);
						drawable.draw(context);
					}
					if (lineRenderer != null) {
						Shape line = lineRenderer.getLineShape(points);
						Drawable drawable = lineRenderer.getLine(points, line);
						drawable.draw(context);
					}
					if (pointRenderer != null) {
						Graphics2D graphics = context.getGraphics();
						Point2D pos = p2.position.getPoint2D();
						AffineTransform txOrig = graphics.getTransform();
						graphics.translate(pos.getX(), pos.getY());
						Drawable drawable = pointRenderer.getPoint(pointData, p2.shape);
						drawable.draw(context);
						graphics.setTransform(txOrig);
					}
				}
			};
		}
	}

	/**
	 * Initializes a new instance object with the specified data sources and
	 * reasonable default settings.
	 * @param data Data to be displayed.
	 */
	public XYPlot(DataSource... data) {
		super();

		pointRenderers = new HashMap<DataSource, PointRenderer>(data.length);
		lineRenderers = new HashMap<DataSource, LineRenderer>(data.length);
		areaRenderers = new HashMap<DataSource, AreaRenderer>(data.length);

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
			pos = new PointND<Double>(0.0, 0.0);
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
	 * Returns the {@code LineRenderer} for the specified data source.
	 * @param s Data source.
	 * @return {@code LineRenderer}.
	 */
	public LineRenderer getLineRenderer(DataSource s) {
		return lineRenderers.get(s);
	}

	/**
	 * Sets the {@code LineRenderer} for a certain data source to the specified
	 * value.
	 * @param s Data source.
	 * @param lineRenderer {@code LineRenderer} to be set.
	 */
	public void setLineRenderer(DataSource s, LineRenderer lineRenderer) {
		lineRenderers.put(s, lineRenderer);
	}

	/**
	 * Returns the {@code AreaRenderer} for the specified data source.
	 * @param s Data source.
	 * @return {@code AreaRenderer}.
	 */
	public AreaRenderer getAreaRenderer(DataSource s) {
		return areaRenderers.get(s);
	}

	/**
	 * Sets the {@code AreaRenderer} for a certain data source to the specified
	 * value.
	 * @param s Data source.
	 * @param areaRenderer {@code AreaRenderer} to be set.
	 */
	public void setAreaRenderer(DataSource s, AreaRenderer areaRenderer) {
		areaRenderers.put(s, areaRenderer);
	}

	@Override
	public void setAxisRenderer(String axisName, AxisRenderer renderer) {
		if (renderer != null) {
			if (AXIS_X2.equals(axisName) || AXIS_Y.equals(axisName)) {
				renderer.setShapeNormalOrientationClockwise(true);
			}
			if (AXIS_Y.equals(axisName)) {
				renderer.setLabelRotation(90.0);
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
		setPointRenderer(source, pointRendererDefault);
		setLineRenderer(source, lineRendererDefault);
		setAreaRenderer(source, areaRendererDefault);
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

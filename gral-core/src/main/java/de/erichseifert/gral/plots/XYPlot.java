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
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DummyData;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.graphics.Container;
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
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.plots.points.DefaultPointRenderer2D;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.plots.settings.Key;
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
public class XYPlot extends AbstractPlot implements Navigable {
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
	private XYPlotNavigator navigator;
	/** A flag that shows whether the navigator has been properly
	initialized. */
	private boolean navigatorInitialized;

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

		/** Key for specifying a {@link Boolean} value which decides whether
		horizontal grid lines at major ticks along x-axis are drawn. */
		public static final Key GRID_MAJOR_X =
			new Key("xyplot.grid.major.x"); //$NON-NLS-1$
		/** Key for specifying a {@link Boolean} value which decides whether
		vertical grid lines at major ticks along y-axis are drawn. */
		public static final Key GRID_MAJOR_Y =
			new Key("xyplot.grid.major.y"); //$NON-NLS-1$
		/** Key for specifying the {@link java.awt.Paint} instance to be used
		to paint the grid lines of major ticks. */
		public static final Key GRID_MAJOR_COLOR =
			new Key("xyplot.grid.major.color"); //$NON-NLS-1$

		/** Key for specifying a {@link Boolean} value which decides whether
		horizontal grid lines at minor ticks along x-axis are drawn. */
		public static final Key GRID_MINOR_X =
			new Key("xyplot.grid.minor.x"); //$NON-NLS-1$
		/** Key for specifying a {@link Boolean} value which decides whether
		vertical grid lines at minor ticks along y-axis are drawn. */
		public static final Key GRID_MINOR_Y =
			new Key("xyplot.grid.minor.y"); //$NON-NLS-1$
		/** Key for specifying the {@link java.awt.Paint} instance to be used
		to paint the grid lines of minor ticks. */
		public static final Key GRID_MINOR_COLOR =
			new Key("xyplot.grid.minor.color"); //$NON-NLS-1$

		/** x-y plot this plot area is associated to. */
		private final XYPlot plot;

		/**
		 * Creates a new instance with default settings and initializes it with
		 * a plot serving as data provider.
		 * @param plot Data provider.
		 */
		public XYPlotArea2D(XYPlot plot) {
			this.plot = plot;

			setSettingDefault(GRID_MAJOR_X, true);
			setSettingDefault(GRID_MAJOR_Y, true);
			setSettingDefault(GRID_MAJOR_COLOR,
				new Color(0.0f, 0.0f, 0.0f, 0.1f));

			setSettingDefault(GRID_MINOR_X, false);
			setSettingDefault(GRID_MINOR_Y, false);
			setSettingDefault(GRID_MINOR_COLOR,
				new Color(0.0f, 0.0f, 0.0f, 0.05f));
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
			boolean isGridMajorX = this.<Boolean>getSetting(GRID_MAJOR_X);
			boolean isGridMajorY = this.<Boolean>getSetting(GRID_MAJOR_Y);
			boolean isGridMinorX = this.<Boolean>getSetting(GRID_MINOR_X);
			boolean isGridMinorY = this.<Boolean>getSetting(GRID_MINOR_Y);

			AffineTransform txOrig = graphics.getTransform();
			graphics.translate(getX(), getY());
			AffineTransform txOffset = graphics.getTransform();
			Rectangle2D bounds = getBounds();

			// Draw gridX
			if (isGridMajorX || isGridMinorX) {
				AxisRenderer axisXRenderer = plot.getAxisRenderer(AXIS_X);
				Axis axisX = plot.getAxis(AXIS_X);
				if (axisXRenderer != null && axisX != null && axisX.isValid()) {
					Shape shapeX =
						axisXRenderer.getSetting(AxisRenderer.SHAPE);
					Rectangle2D shapeBoundsX = shapeX.getBounds2D();
					List<Tick> ticksX = axisXRenderer.getTicks(axisX);
					Line2D gridLineVert = new Line2D.Double(
						-shapeBoundsX.getMinX(),
						-shapeBoundsX.getMinY(),
						-shapeBoundsX.getMinX(),
						bounds.getHeight() - shapeBoundsX.getMinY()
					);
					for (Tick tick : ticksX) {
						if ((TickType.MAJOR.equals(tick.getType()) && !isGridMajorX) ||
								(TickType.MINOR.equals(tick.getType()) && !isGridMinorX)) {
							continue;
						}
						Point2D tickPoint = tick.getPosition().getPoint2D();
						if (tickPoint == null) {
							continue;
						}

						Paint paint = getSetting(GRID_MAJOR_COLOR);
						if (TickType.MINOR.equals(tick.getType())) {
							paint = getSetting(GRID_MINOR_COLOR);
						}
						graphics.translate(tickPoint.getX(), tickPoint.getY());
						GraphicsUtils.drawPaintedShape(
							graphics, gridLineVert, paint, null, null);
						graphics.setTransform(txOffset);
					}
				}
			}

			// Draw gridY
			if (isGridMajorY || isGridMinorY) {
				Axis axisY = plot.getAxis(AXIS_Y);
				AxisRenderer axisYRenderer = plot.getAxisRenderer(AXIS_Y);
				if (axisY != null && axisY.isValid() && axisYRenderer != null) {
					Shape shapeY = axisYRenderer.getSetting(AxisRenderer.SHAPE);
					Rectangle2D shapeBoundsY = shapeY.getBounds2D();
					List<Tick> ticksY = axisYRenderer.getTicks(axisY);
					Line2D gridLineHoriz = new Line2D.Double(
						-shapeBoundsY.getMinX(), -shapeBoundsY.getMinY(),
						bounds.getWidth() - shapeBoundsY.getMinX(), -shapeBoundsY.getMinY()
					);
					for (Tick tick : ticksY) {
						boolean isMajorTick = tick.getType() == TickType.MAJOR;
						boolean isMinorTick = tick.getType() == TickType.MINOR;
						if ((isMajorTick && !isGridMajorY) ||
								(isMinorTick && !isGridMinorY)) {
							continue;
						}
						Point2D tickPoint = tick.getPosition().getPoint2D();
						if (tickPoint == null) {
							continue;
						}

						Paint paint = getSetting(GRID_MAJOR_COLOR);
						if (tick.getType() == TickType.MINOR) {
							paint = getSetting(GRID_MINOR_COLOR);
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

				List<DataPoint> dataPoints = new LinkedList<DataPoint>();
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

					Drawable drawable = null;
					Shape point = null;
					if (pointRenderer != null) {
						drawable = pointRenderer.getPoint(
							axisY, axisYRenderer, row, colY);
						point = pointRenderer.getPointPath(row);
					}
					DataPoint dataPoint = new DataPoint(pos, drawable, point);
					dataPoints.add(dataPoint);
				}

				if (areaRenderer != null) {
					Drawable drawable = areaRenderer.getArea(
							axisY, axisYRenderer, dataPoints);
					drawable.draw(context);
				}
				if (lineRenderer != null) {
					Drawable drawable = lineRenderer.getLine(dataPoints);
					drawable.draw(context);
				}
				if (pointRenderer != null) {
					for (DataPoint point : dataPoints) {
						PointND<Double> pos = point.getPosition();
						double pointX = pos.get(PointND.X);
						double pointY = pos.get(PointND.Y);
						graphics.translate(pointX, pointY);
						Drawable drawable = point.getDrawable();
						drawable.draw(context);
						graphics.setTransform(txOffset);
					}
				}
			}

			if (clipOffset != null) {
				// Reset clipping
				graphics.setClip(clipBoundsOld);
			}

			// Reset transformation (offset)
			graphics.setTransform(txOrig);
		}
	}

	/**
	 * Class that displays a legend in an {@code XYPlot}.
	 */
	public static class XYLegend extends Legend {
		/** Version id for serialization. */
		private static final long serialVersionUID = -4629928754001372002L;

		/** Source for dummy data. */
		private final DataSource DUMMY_DATA = new DummyData(2, 1, 0.5);

		private final XYPlot plot;

		/**
		 * Constructor that initializes the instance with a plot acting as data
		 * provider.
		 * @param plot Data provider.
		 */
		public XYLegend(XYPlot plot) {
			this.plot = plot;
		}

		@Override
		protected void drawSymbol(DrawingContext context,
				Drawable symbol, DataSource data) {
			PointRenderer pointRenderer = plot.getPointRenderer(data);
			LineRenderer lineRenderer = plot.getLineRenderer(data);
			AreaRenderer areaRenderer = plot.getAreaRenderer(data);

			Row row = new Row(DUMMY_DATA, 0);
			Rectangle2D bounds = symbol.getBounds();

			DataPoint p1 = new DataPoint(
				new PointND<Double>(bounds.getMinX(), bounds.getCenterY()), null, null
			);
			DataPoint p2 = new DataPoint(
				new PointND<Double>(bounds.getCenterX(), bounds.getCenterY()),
				null, (pointRenderer != null) ? pointRenderer.getPointPath(row) : null
			);
			DataPoint p3 = new DataPoint(
				new PointND<Double>(bounds.getMaxX(), bounds.getCenterY()), null, null
			);
			List<DataPoint> dataPoints = Arrays.asList(p1, p2, p3);

			Axis axis = new Axis();
			axis.setRange(0.0, 1.0);
			LinearRenderer2D axisRenderer = new LinearRenderer2D();
			axisRenderer.setSetting(LinearRenderer2D.SHAPE, new Line2D.Double(
					bounds.getCenterX(), bounds.getMaxY(),
					bounds.getCenterX(), bounds.getMinY()));

			if (areaRenderer != null) {
				areaRenderer.getArea(axis, axisRenderer, dataPoints).draw(context);
			}
			if (lineRenderer != null) {
				lineRenderer.getLine(dataPoints).draw(context);
			}
			if (pointRenderer != null) {
				Graphics2D graphics = context.getGraphics();
				Point2D pos = p2.getPosition().getPoint2D();
				AffineTransform txOrig = graphics.getTransform();
				graphics.translate(pos.getX(), pos.getY());
				pointRenderer.getPoint(axis, axisRenderer, row, 0).draw(context);
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

		pointRenderers = new HashMap<DataSource, PointRenderer>();
		lineRenderers = new HashMap<DataSource, LineRenderer>(data.length);
		areaRenderers = new HashMap<DataSource, AreaRenderer>(data.length);

		setPlotArea(new XYPlotArea2D(this));
		setLegend(new XYLegend(this));

		// Handle data sources after the renderer lists are initialized
		for (DataSource source : data) {
			add(source);
		}

		createDefaultAxes();
		autoScaleAxes();
		createDefaultAxisRenderers();

		// Listen for changes of the axis range
		AxisListener axisListener = new AxisListener() {
			public void rangeChanged(Axis axis, Number min, Number max) {
				layoutAxes();
			}
		};
		for (String axisName : getAxesNames()) {
			getAxis(axisName).addAxisListener(axisListener);
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
	public void layout() {
		super.layout();
		layoutAxes();
		layoutLegend();
	}

	/**
	 * Calculates the bounds of the axes.
	 */
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
		renderer.setSetting(AxisRenderer.SHAPE, shape);
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

		Number intersection =
			renderer.<Number>getSetting(AxisRenderer.INTERSECTION);
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
	 * Calculates the bounds of the legend component.
	 */
	protected void layoutLegend() {
		if (getPlotArea() == null) {
			return;
		}
		Container legendContainer = getLegendContainer();
		Rectangle2D plotBounds = getPlotArea().getBounds();
		legendContainer.setBounds(plotBounds);
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
				renderer.setSetting(AxisRenderer.SHAPE_NORMAL_ORIENTATION_CLOCKWISE, true);
			}
			if (AXIS_Y.equals(axisName)) {
				renderer.setSetting(AxisRenderer.LABEL_ROTATION, 90.0);
			}
		}
		super.setAxisRenderer(axisName, renderer);
	}

	@Override
	public void add(int index, DataSource source, boolean visible) {
		super.add(index, source, visible);
		PointRenderer pointRendererDefault = new DefaultPointRenderer2D();
		LineRenderer lineRendererDefault = null;
		AreaRenderer areaRendererDefault = null;
		setPointRenderer(source, pointRendererDefault);
		setLineRenderer(source, lineRendererDefault);
		setAreaRenderer(source, areaRendererDefault);
		setMapping(source, AXIS_X, AXIS_Y);
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
}

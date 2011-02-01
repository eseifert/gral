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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.DrawingContext;
import de.erichseifert.gral.Legend;
import de.erichseifert.gral.PlotArea;
import de.erichseifert.gral.data.Column;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DummyData;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.plots.areas.AreaRenderer;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisListener;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.axes.LinearRenderer2D;
import de.erichseifert.gral.plots.axes.Tick;
import de.erichseifert.gral.plots.axes.Tick.TickType;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.plots.points.DefaultPointRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.PointND;


/**
 * <p>Class that displays data in an two dimensional coordinate system
 * (x-y plot). It also serves as a base class for many other plot types.</p>
 * <p>To create a new <code>XYPlot</code> simply create a new instance
 * using one or more data sources. Example:</p>
 * <pre>
 * DataTable data = new DataTable(Integer.class, Integer.class);
 * data.add( 1, 2);
 * data.add(-5, 0);
 *
 * XYPlot plot = new XYPlot(data);
 * </pre>
 */
public class XYPlot extends Plot  {
	/** Key for specifying the x-axis of an xy-plot. */
	public static String AXIS_X = "x"; //$NON-NLS-1$
	/** Key for specifying the secondary x-axis of an xy-plot. */
	public static String AXIS_X2 = "x2"; //$NON-NLS-1$
	/** Key for specifying the y-axis of an xy-plot. */
	public static String AXIS_Y = "y"; //$NON-NLS-1$
	/** Key for specifying the secondary y-axis of an xy-plot. */
	public static String AXIS_Y2 = "y2"; //$NON-NLS-1$

	/** Minimum value in x direction. */
	private double minX;
	/** Maximum value in x direction. */
	private double maxX;
	/** Minimum value in y direction. */
	private double minY;
	/** Maximum value in y direction. */
	private double maxY;

	/** Mapping from data source to point renderer. */
	private final Map<DataSource, PointRenderer> pointRenderers;
	/** Mapping from data source to line renderer. */
	private final Map<DataSource, LineRenderer> lineRenderers;
	/** Mapping from data source to area renderer. */
	private final Map<DataSource, AreaRenderer> areaRenderers;

	/**
	 * Class that represents the drawing area of an <code>XYPlot</code>.
	 */
	public static class XYPlotArea2D extends PlotArea {
		/** Key for specifying a {@link java.lang.Boolean} value which decides
		whether horizontal grid lines at major ticks along x-axis are drawn. */
		public static final Key GRID_MAJOR_X =
			new Key("xyplot.grid.major.x"); //$NON-NLS-1$
		/** Key for specifying a {@link java.lang.Boolean} value which decides
		whether vertical grid lines at major ticks along y-axis are drawn. */
		public static final Key GRID_MAJOR_Y =
			new Key("xyplot.grid.major.y"); //$NON-NLS-1$
		/** Key for specifying the {@link java.awt.Paint} instance to be used
		to paint the grid lines of major ticks. */
		public static final Key GRID_MAJOR_COLOR =
			new Key("xyplot.grid.major.color"); //$NON-NLS-1$

		/** Key for specifying a {@link java.lang.Boolean} value which decides
		whether horizontal grid lines at minor ticks along x-axis are drawn. */
		public static final Key GRID_MINOR_X =
			new Key("xyplot.grid.minor.x"); //$NON-NLS-1$
		/** Key for specifying a {@link java.lang.Boolean} value which decides
		whether  vertical grid lines at minor ticks along y-axis are drawn. */
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

		@Override
		public void draw(DrawingContext context) {
			drawBackground(context);
			drawGrid(context);
			drawBorder(context);
			drawPlot(context);
			plot.drawAxes(context);
			plot.drawLegend(context);
		}

		/**
		 * Draws the grid into the specified <code>Graphics2D</code> object.
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
				if (axisXRenderer != null) {
					Axis axisX = plot.getAxis(AXIS_X);
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
				AxisRenderer axisYRenderer = plot.getAxisRenderer(AXIS_Y);
				if (axisYRenderer != null) {
					Axis axisY = plot.getAxis(AXIS_Y);
					Shape shapeY = axisYRenderer.getSetting(AxisRenderer.SHAPE);
					Rectangle2D shapeBoundsY = shapeY.getBounds2D();
					List<Tick> ticksY = axisYRenderer.getTicks(axisY);
					Line2D gridLineHoriz = new Line2D.Double(
						-shapeBoundsY.getMinX(), -shapeBoundsY.getMinY(),
						bounds.getWidth() - shapeBoundsY.getMinX(), -shapeBoundsY.getMinY()
					);
					for (Tick tick : ticksY) {
						boolean isMajorTick =
							TickType.MAJOR.equals(tick.getType());
						boolean isMinorTick =
							TickType.MINOR.equals(tick.getType());
						if ((isMajorTick && !isGridMajorY) ||
								(isMinorTick && !isGridMinorY)) {
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

			Axis axisX = plot.getAxis(AXIS_X);
			Axis axisY = plot.getAxis(AXIS_Y);
			AxisRenderer axisXRenderer = plot.getAxisRenderer(AXIS_X);
			AxisRenderer axisYRenderer = plot.getAxisRenderer(AXIS_Y);

			// TODO Use real font size instead of fixed value
			final double fontSize = 10.0;

			Insets2D clipOffset = getSetting(CLIPPING);
			if (clipOffset != null) {
				// Perform clipping
				Rectangle2D clipBounds = new Rectangle2D.Double(
						clipOffset.getLeft()*fontSize,
						clipOffset.getTop()*fontSize,
						getWidth() - clipOffset.getHorizontal()*fontSize,
						getHeight() - clipOffset.getVertical()*fontSize
				);
				graphics.setClip(clipBounds);
			}

			// Paint points and lines
			for (DataSource s : plot.getVisibleData()) {
				PointRenderer pointRenderer = plot.getPointRenderer(s);
				LineRenderer lineRenderer = plot.getLineRenderer(s);
				AreaRenderer areaRenderer = plot.getAreaRenderer(s);

				List<DataPoint> dataPoints = new LinkedList<DataPoint>();
				for (int i = 0; i < s.getRowCount(); i++) {
					Row row = new Row(s, i);
					Number valueX = row.get(0);
					Number valueY = row.get(1);
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
								axisY, axisYRenderer, row);
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
				graphics.setClip(null);
			}

			// Reset transformation (offset)
			graphics.setTransform(txOrig);
		}
	}

	/**
	 * Class that displays a legend in an <code>XYPlot</code>.
	 */
	public static class XYLegend extends Legend {
		/** Source for dummy data. */
		private final DataSource DUMMY_DATA = new DummyData(1, 1, 0.5);

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

			Axis axis = new Axis(0.0, 1.0);
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
				pointRenderer.getPoint(axis, axisRenderer, row).draw(context);
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
		pointRenderers = new HashMap<DataSource, PointRenderer>();
		lineRenderers = new HashMap<DataSource, LineRenderer>(data.length);
		areaRenderers = new HashMap<DataSource, AreaRenderer>(data.length);

		setPlotArea(new XYPlotArea2D(this));
		setLegend(new XYLegend(this));

		// Add data sources after the renderer lists are initialized
		for (DataSource source : data) {
			add(source);
		}

		// Create axes
		Axis axisX = new Axis(minX, maxX);
		Axis axisY = new Axis(minY, maxY);
		setAxis(AXIS_X, axisX);
		setAxis(AXIS_Y, axisY);

		AxisRenderer axisXRenderer = new LinearRenderer2D();
		AxisRenderer axisYRenderer = new LinearRenderer2D();
		setAxisRenderer(AXIS_X, axisXRenderer);
		setAxisRenderer(AXIS_Y, axisYRenderer);

		// Listen for changes of the axis range
		AxisListener axisListener = new AxisListener() {
			@Override
			public void rangeChanged(Axis axis, Number min, Number max) {
				layoutAxes();
			}
		};
		axisX.addAxisListener(axisListener);
		axisY.addAxisListener(axisListener);
	}

	@Override
	protected void layout() {
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

		Rectangle2D plotBounds = getPlotArea().getBounds();
		AxisRenderer axisXRenderer = getAxisRenderer(AXIS_X);
		AxisRenderer axisYRenderer = getAxisRenderer(AXIS_Y);
		Drawable axisXComp = getAxisComponent(AXIS_X);
		Drawable axisYComp = getAxisComponent(AXIS_Y);
		Dimension2D axisXSize = null;
		Dimension2D axisYSize = null;

		// Set the new shapes first to allow for correct positioning
		if (axisXComp != null && axisXRenderer != null) {
			axisXSize = axisXComp.getPreferredSize();
			axisXRenderer.setSetting(AxisRenderer.SHAPE, new Line2D.Double(
				0.0, 0.0,
				plotBounds.getWidth(), 0.0
			));
		}
		if (axisYComp != null && axisYRenderer != null) {
			axisYSize = axisYComp.getPreferredSize();
			axisYRenderer.setSetting(AxisRenderer.SHAPE, new Line2D.Double(
				axisYSize.getWidth(), plotBounds.getHeight(),
				axisYSize.getWidth(), 0.0
			));
		}

		// Set bounds with new axis shapes
		if (axisXRenderer != null && axisXComp != null) {
			PointND<Double> axisXPos = null;
			if (axisYRenderer != null) {
				Axis axisY = getAxis(AXIS_Y);
				Double axisXIntersection =
					axisXRenderer.<Number>getSetting(AxisRenderer.INTERSECTION)
					.doubleValue();
				axisXPos = axisYRenderer.getPosition(
						axisY, axisXIntersection, false, false);
			}
			if (axisXPos == null) {
				axisXPos = new PointND<Double>(0.0, 0.0);
			}
			axisXComp.setBounds(
				plotBounds.getMinX(),
				axisXPos.get(1) + plotBounds.getMinY(),
				plotBounds.getWidth(),
				axisXSize.getHeight()
			);
		}

		if (axisYRenderer != null && axisYComp != null) {
			PointND<Double> axisYPos = null;
			if (axisXRenderer != null) {
				Axis axisX = getAxis(AXIS_X);
				Double axisYIntersection =
					axisYRenderer.<Number>getSetting(AxisRenderer.INTERSECTION)
					.doubleValue();
				axisYPos = axisXRenderer.getPosition(
						axisX, axisYIntersection, false, false);
			}
			if (axisYPos == null) {
				axisYPos = new PointND<Double>(0.0, 0.0);
			}
			axisYComp.setBounds(
				plotBounds.getMinX() - axisYSize.getWidth() + axisYPos.get(0),
				plotBounds.getMinY(),
				axisYSize.getWidth(),
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
		Rectangle2D plotBounds = getPlotArea().getBounds();
		getLegendContainer().setBounds(plotBounds);
	}

	/**
	 * Returns the <code>PointRenderer</code> for the specified
	 * data source.
	 * @param s data source.
	 * @return PointRenderer.
	 */
	public PointRenderer getPointRenderer(DataSource s) {
		return pointRenderers.get(s);
	}

	/**
	 * Sets the <code>PointRenderer</code> for a certain data source
	 * to the specified instance.
	 * @param s data source.
	 * @param pointRenderer PointRenderer to be set.
	 */
	public void setPointRenderer(DataSource s, PointRenderer pointRenderer) {
		this.pointRenderers.put(s, pointRenderer);
	}

	/**
	 * Returns the <code>LineRenderer</code> for the specified data source.
	 * @param s data source.
	 * @return <code>LineRenderer</code>.
	 */
	public LineRenderer getLineRenderer(DataSource s) {
		return lineRenderers.get(s);
	}

	/**
	 * Sets the <code>LineRenderer</code> for a certain data source
	 * to the specified value.
	 * @param s <code>DataSource</code>.
	 * @param lineRenderer <code>LineRenderer</code> to be set.
	 */
	public void setLineRenderer(DataSource s, LineRenderer lineRenderer) {
		lineRenderers.put(s, lineRenderer);
	}

	/**
	 * Returns the <code>AreaRenderer</code> for the specified data source.
	 * @param s <code>DataSource</code>.
	 * @return <code>AreaRenderer</code>.
	 */
	public AreaRenderer getAreaRenderer(DataSource s) {
		return areaRenderers.get(s);
	}

	/**
	 * Sets the <code>AreaRenderer</code> for a certain <code>DataSource</code>
	 * to the specified value.
	 * @param s <code>DataSource</code>.
	 * @param areaRenderer <code>AreaRenderer</code> to be set.
	 */
	public void setAreaRenderer(DataSource s, AreaRenderer areaRenderer) {
		areaRenderers.put(s, areaRenderer);
	}

	@Override
	public void setAxisRenderer(String axisName, AxisRenderer renderer) {
		if ((renderer != null) && AXIS_Y.equals(axisName)) {
			renderer.setSetting(AxisRenderer.SHAPE_NORMAL_ORIENTATION_CLOCKWISE, true);
			renderer.setSetting(AxisRenderer.LABEL_ROTATION, 90.0);
		}
		super.setAxisRenderer(axisName, renderer);
	}

	@Override
	public void add(int index, DataSource source, boolean visible) {
		super.add(index, source, visible);
		PointRenderer pointRendererDefault = new DefaultPointRenderer();
		LineRenderer lineRendererDefault = null;
		AreaRenderer areaRendererDefault = null;
		setPointRenderer(source, pointRendererDefault);
		setLineRenderer(source, lineRendererDefault);
		setAreaRenderer(source, areaRendererDefault);
	}

	@Override
	public void refresh() {
		super.refresh();

		minX =  Double.MAX_VALUE;
		maxX = -Double.MAX_VALUE;
		minY =  Double.MAX_VALUE;
		maxY = -Double.MAX_VALUE;
		for (DataSource s : getVisibleData()) {
			// Set the minimal and maximal value of the axes
			Column colX = s.getColumn(0);
			Column colY = s.getColumn(1);
			minX = Math.min(minX, colX.getStatistics(Statistics.MIN));
			maxX = Math.max(maxX, colX.getStatistics(Statistics.MAX));
			minY = Math.min(minY, colY.getStatistics(Statistics.MIN));
			maxY = Math.max(maxY, colY.getStatistics(Statistics.MAX));
		}
	}
}

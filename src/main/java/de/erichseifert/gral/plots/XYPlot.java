/*
 * GRAL: GRAphing Library for Java(R)
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
import de.erichseifert.gral.Legend;
import de.erichseifert.gral.PlotArea;
import de.erichseifert.gral.data.DataListener;
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
import de.erichseifert.gral.util.PointND;
import de.erichseifert.gral.util.Settings.Key;


/**
 * Class that displays data in an XY-Plot.
 */
public class XYPlot extends Plot implements DataListener  {
	private double minX;
	private double maxX;
	private double minY;
	private double maxY;

	private final Map<DataSource, PointRenderer> pointRenderers;
	private final Map<DataSource, LineRenderer> lineRenderers;
	private final Map<DataSource, AreaRenderer> areaRenderers;

	/**
	 * Class that represents the drawing area of an <code>XYPlot</code>.
	 */
	public static class XYPlotArea2D extends PlotArea {
		/** Key for specifying whether the horizontal grid lines at major ticks
		along the x-axis are drawn. */
		public static final Key GRID_MAJOR_X = new Key("xyplot.grid.major.x");
		/** Key for specifying whether the vertical grid lines at major ticks
		along the y-axis are drawn. */
		public static final Key GRID_MAJOR_Y = new Key("xyplot.grid.major.y");
		/** Key for specifying the {@link java.awt.Paint} instance to be used
		to paint the grid lines of major ticks. */
		public static final Key GRID_MAJOR_COLOR = new Key("xyplot.grid.major.color");

		/** Key for specifying whether the horizontal grid lines at minor ticks
		along the x-axis are drawn. */
		public static final Key GRID_MINOR_X = new Key("xyplot.grid.minor.x");
		/** Key for specifying whether the vertical grid lines at minor ticks
		along the y-axis are drawn. */
		public static final Key GRID_MINOR_Y = new Key("xyplot.grid.minor.y");
		/** Key for specifying the {@link java.awt.Paint} instance to be used
		to paint the grid lines of minor ticks. */
		public static final Key GRID_MINOR_COLOR = new Key("xyplot.grid.minor.color");

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
			setSettingDefault(GRID_MAJOR_COLOR, new Color(0.0f, 0.0f, 0.0f, 0.1f));

			setSettingDefault(GRID_MINOR_X, false);
			setSettingDefault(GRID_MINOR_Y, false);
			setSettingDefault(GRID_MINOR_COLOR, new Color(0.0f, 0.0f, 0.0f, 0.05f));
		}

		@Override
		public void draw(final Graphics2D g2d) {
			drawBackground(g2d);
			drawGrid(g2d);
			drawBorder(g2d);
			drawPlot(g2d);
			plot.drawAxes(g2d);
			plot.drawLegend(g2d);
		}

		/**
		 * Draws the grid into the specified <code>Graphics2D</code> object.
		 * @param g2d Graphics to be used for drawing.
		 */
		protected void drawGrid(Graphics2D g2d) {
			boolean isGridMajorX = this.<Boolean>getSetting(GRID_MAJOR_X);
			boolean isGridMajorY = this.<Boolean>getSetting(GRID_MAJOR_Y);
			boolean isGridMinorX = this.<Boolean>getSetting(GRID_MINOR_X);
			boolean isGridMinorY = this.<Boolean>getSetting(GRID_MINOR_Y);

			AffineTransform txOrig = g2d.getTransform();
			g2d.translate(getX(), getY());
			AffineTransform txOffset = g2d.getTransform();
			Rectangle2D bounds = getBounds();

			// Draw gridX
			if (isGridMajorX || isGridMinorX) {
				Axis axisX = plot.getAxis(Axis.X);
				AxisRenderer axisXRenderer = plot.getAxisRenderer(axisX);
				if (axisXRenderer != null) {
					Shape shapeX = axisXRenderer.getSetting(AxisRenderer.SHAPE);
					Rectangle2D shapeBoundsX = shapeX.getBounds2D();
					List<Tick> ticksX = axisXRenderer.getTicks(axisX);
					Line2D gridLineVert = new Line2D.Double(
						-shapeBoundsX.getMinX(), -shapeBoundsX.getMinY(),
						-shapeBoundsX.getMinX(), bounds.getHeight() - shapeBoundsX.getMinY()
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
						g2d.translate(tickPoint.getX(), tickPoint.getY());
						GraphicsUtils.drawPaintedShape(g2d, gridLineVert, paint, null, null);
						g2d.setTransform(txOffset);
					}
				}
			}

			// Draw gridY
			if (isGridMajorY || isGridMinorY) {
				Axis axisY = plot.getAxis(Axis.Y);
				AxisRenderer axisYRenderer = plot.getAxisRenderer(axisY);
				if (axisYRenderer != null) {
					Shape shapeY = axisYRenderer.getSetting(AxisRenderer.SHAPE);
					Rectangle2D shapeBoundsY = shapeY.getBounds2D();
					List<Tick> ticksY = axisYRenderer.getTicks(axisY);
					Line2D gridLineHoriz = new Line2D.Double(
						-shapeBoundsY.getMinX(), -shapeBoundsY.getMinY(),
						bounds.getWidth() - shapeBoundsY.getMinX(), -shapeBoundsY.getMinY()
					);
					for (Tick tick : ticksY) {
						if ((TickType.MAJOR.equals(tick.getType()) && !isGridMajorY) ||
								(TickType.MINOR.equals(tick.getType()) && !isGridMinorY)) {
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
						g2d.translate(tickPoint.getX(), tickPoint.getY());
						GraphicsUtils.drawPaintedShape(g2d, gridLineHoriz, paint, null, null);
						g2d.setTransform(txOffset);
					}
				}
			}

			g2d.setTransform(txOrig);
		}

		@Override
		protected void drawPlot(final Graphics2D g2d) {
			AffineTransform txOrig = g2d.getTransform();
			g2d.translate(getX(), getY());
			AffineTransform txOffset = g2d.getTransform();

			Axis axisX = plot.getAxis(Axis.X);
			Axis axisY = plot.getAxis(Axis.Y);
			AxisRenderer axisXRenderer = plot.getAxisRenderer(axisX);
			AxisRenderer axisYRenderer = plot.getAxisRenderer(axisY);

			// Paint points and lines
			for (DataSource s : plot.getData()) {
				PointRenderer pointRenderer = plot.getPointRenderer(s);
				LineRenderer lineRenderer = plot.getLineRenderer(s);
				AreaRenderer areaRenderer = plot.getAreaRenderer(s);

				List<DataPoint> dataPoints = new LinkedList<DataPoint>();
				for (int i = 0; i < s.getRowCount(); i++) {
					Row row = new Row(s, i);
					Number valueX = row.get(0);
					Number valueY = row.get(1);
					PointND axisPosX = axisXRenderer.getPosition(axisX, valueX, true, false);
					PointND axisPosY = axisYRenderer.getPosition(axisY, valueY, true, false);
					if (axisPosX == null || axisPosY == null) {
						continue;
					}
					PointND pos = new PointND(axisPosX.get(0).doubleValue(), axisPosY.get(1).doubleValue());

					Drawable drawable = null;
					Shape point = null;
					if (pointRenderer != null) {
						drawable = pointRenderer.getPoint(axisY, axisYRenderer, row);
						point = pointRenderer.getPointPath(row);
					}
					DataPoint dataPoint = new DataPoint(pos, drawable, point);
					dataPoints.add(dataPoint);
				}

				if (areaRenderer != null) {
					Drawable drawable = areaRenderer.getArea(axisY, axisYRenderer, dataPoints);
					drawable.draw(g2d);
				}
				if (lineRenderer != null) {
					Drawable drawable = lineRenderer.getLine(dataPoints);
					drawable.draw(g2d);
				}
				if (pointRenderer != null) {
					for (DataPoint point : dataPoints) {
						double pointX = point.getPosition().get(0).doubleValue();
						double pointY = point.getPosition().get(1).doubleValue();
						g2d.translate(pointX, pointY);
						Drawable drawable = point.getDrawable();
						drawable.draw(g2d);
						g2d.setTransform(txOffset);
					}
				}
			}
			g2d.setTransform(txOrig);
		}
	}

	/**
	 * Class that displays a legend in an <code>XYPlot</code>.
	 */
	public static class XYLegend extends Legend {
		/** Source for dummy data. */
		protected final DataSource DUMMY_DATA = new DummyData(1, 1, 0.5);

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
		protected void drawSymbol(Graphics2D g2d, Drawable symbol, DataSource data) {
			PointRenderer pointRenderer = plot.getPointRenderer(data);
			LineRenderer lineRenderer = plot.getLineRenderer(data);
			AreaRenderer areaRenderer = plot.getAreaRenderer(data);

			Row row = new Row(DUMMY_DATA, 0);
			Rectangle2D bounds = symbol.getBounds();

			DataPoint p1 = new DataPoint(
				new PointND(bounds.getMinX(), bounds.getCenterY()), null, null
			);
			DataPoint p2 = new DataPoint(
				new PointND(bounds.getCenterX(), bounds.getCenterY()),
				null, (pointRenderer != null) ? pointRenderer.getPointPath(row) : null
			);
			DataPoint p3 = new DataPoint(
				new PointND(bounds.getMaxX(), bounds.getCenterY()), null, null
			);
			List<DataPoint> dataPoints = Arrays.asList(p1, p2, p3);

			Axis axis = new Axis(0.0, 1.0);
			LinearRenderer2D axisRenderer = new LinearRenderer2D();
			axisRenderer.setSetting(LinearRenderer2D.SHAPE, new Line2D.Double(
					bounds.getCenterX(), bounds.getMaxY(), bounds.getCenterX(), bounds.getMinY()));

			if (areaRenderer != null) {
				areaRenderer.getArea(axis, axisRenderer, dataPoints).draw(g2d);
			}
			if (lineRenderer != null) {
				lineRenderer.getLine(dataPoints).draw(g2d);
			}
			if (pointRenderer != null) {
				Point2D pos = p2.getPosition().getPoint2D();
				AffineTransform txOrig = g2d.getTransform();
				g2d.translate(pos.getX(), pos.getY());
				pointRenderer.getPoint(axis, axisRenderer, row).draw(g2d);
				g2d.setTransform(txOrig);
			}
		}

	}

	/**
	 * Creates a new <code>XYPlot</code> object with the specified
	 * <code>DataSource</code>s and default settings.
	 * @param data Data to be displayed.
	 */
	public XYPlot(DataSource... data) {
		super(data);

		pointRenderers = new HashMap<DataSource, PointRenderer>();
		lineRenderers = new HashMap<DataSource, LineRenderer>(data.length);
		areaRenderers = new HashMap<DataSource, AreaRenderer>(data.length);

		setPlotArea(new XYPlotArea2D(this));
		setLegend(new XYLegend(this));

		PointRenderer pointRendererDefault = new DefaultPointRenderer();
		for (DataSource source : data) {
			getLegend().add(source);
			setPointRenderer(source, pointRendererDefault);
			source.addDataListener(this);
			dataChanged(source);
		}

		// Create axes
		Axis axisX = new Axis(minX, maxX);
		Axis axisY = new Axis(minY, maxY);
		setAxis(Axis.X, axisX);
		setAxis(Axis.Y, axisY);

		AxisRenderer axisXRenderer = new LinearRenderer2D();
		AxisRenderer axisYRenderer = new LinearRenderer2D();
		setAxisRenderer(axisX, axisXRenderer);
		setAxisRenderer(axisY, axisYRenderer);

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
		Axis axisX = getAxis(Axis.X);
		Axis axisY = getAxis(Axis.Y);
		AxisRenderer axisXRenderer = getAxisRenderer(axisX);
		AxisRenderer axisYRenderer = getAxisRenderer(axisY);
		Drawable axisXComp = getAxisComponent(axisX);
		Drawable axisYComp = getAxisComponent(axisY);
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
		if (axisXComp != null && axisXRenderer != null) {
			Double axisXIntersection = axisXRenderer.getSetting(AxisRenderer.INTERSECTION);
			Point2D axisXPos = axisYRenderer.getPosition(
					axisY, axisXIntersection, false, false).getPoint2D();
			axisXComp.setBounds(
				plotBounds.getMinX(), axisXPos.getY() + plotBounds.getMinY(),
				plotBounds.getWidth(), axisXSize.getHeight()
			);
		}

		if (axisYComp != null && axisYRenderer != null) {
			Double axisYIntersection = axisYRenderer.getSetting(AxisRenderer.INTERSECTION);
			Point2D axisYPos = axisXRenderer.getPosition(
					axisX, axisYIntersection, false, false).getPoint2D();
			axisYComp.setBounds(
				plotBounds.getMinX() - axisYSize.getWidth() + axisYPos.getX(), plotBounds.getMinY(),
				axisYSize.getWidth(), plotBounds.getHeight()
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

	@Override
	public void dataChanged(DataSource data) {
		minX =  Double.MAX_VALUE;
		maxX = -Double.MAX_VALUE;
		minY =  Double.MAX_VALUE;
		maxY = -Double.MAX_VALUE;
		final int colX = 0;
		final int colY = 1;
		for (DataSource s : this.data) {
			Statistics stats = s.getStatistics();
			// Set the minimal and maximal value of the axes
			minX = Math.min(minX, stats.get(Statistics.MIN, colX));
			maxX = Math.max(maxX, stats.get(Statistics.MAX, colX));
			minY = Math.min(minY, stats.get(Statistics.MIN, colY));
			maxY = Math.max(maxY, stats.get(Statistics.MAX, colY));
		}
	}

	/**
	 * Returns the <code>PointRenderer</code> for the specified <code>DataSource</code>.
	 * @param s DataSource.
	 * @return PointRenderer.
	 */
	public PointRenderer getPointRenderer(DataSource s) {
		return pointRenderers.get(s);
	}

	/**
	 * Sets the <code>PointRenderer</code> for a certain <code>DataSource</code>
	 * to the specified instance.
	 * @param s DataSource.
	 * @param pointRenderer PointRenderer to be set.
	 */
	public void setPointRenderer(DataSource s, PointRenderer pointRenderer) {
		this.pointRenderers.put(s, pointRenderer);
	}

	/**
	 * Returns the <code>LineRenderer2D</code> for the specified <code>DataSource</code>.
	 * @param s <code>DataSource</code>.
	 * @return <code>LineRenderer2D</code>.
	 */
	public LineRenderer getLineRenderer(DataSource s) {
		return lineRenderers.get(s);
	}

	/**
	 * Sets the <code>LineRenderer2D</code> for a certain <code>DataSource</code>
	 * to the specified value.
	 * @param s <code>DataSource</code>.
	 * @param lineRenderer <code>LineRenderer</code> to be set.
	 */
	public void setLineRenderer(DataSource s, LineRenderer lineRenderer) {
		lineRenderers.put(s, lineRenderer);
	}

	/**
	 * Returns the <code>AreaRenderer2D</code> for the specified
	 * <code>DataSource</code>.
	 * @param s <code>DataSource</code>.
	 * @return <code>AreaRenderer2D</code>.
	 */
	public AreaRenderer getAreaRenderer(DataSource s) {
		return areaRenderers.get(s);
	}

	/**
	 * Sets the <code>AreaRenderer2D</code> for a certain <code>DataSource</code>
	 * to the specified value.
	 * @param s <code>DataSource</code>.
	 * @param areaRenderer <code>AreaRenderer2D</code> to be set.
	 */
	public void setAreaRenderer(DataSource s, AreaRenderer areaRenderer) {
		areaRenderers.put(s, areaRenderer);
	}

}

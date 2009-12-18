package openjchart.plots;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import openjchart.Drawable;
import openjchart.Legend;
import openjchart.PlotArea2D;
import openjchart.data.DataListener;
import openjchart.data.DataSource;
import openjchart.data.DummyData;
import openjchart.data.Row;
import openjchart.data.statistics.Statistics;
import openjchart.plots.axes.Axis;
import openjchart.plots.axes.AxisRenderer2D;
import openjchart.plots.axes.LinearRenderer2D;
import openjchart.plots.lines.LineRenderer2D;
import openjchart.plots.shapes.DefaultShapeRenderer;
import openjchart.plots.shapes.ShapeRenderer;
import openjchart.util.GraphicsUtils;
import openjchart.util.SettingChangeEvent;


public class XYPlot extends Plot implements DataListener  {
	public static final String KEY_GRID_X = "xyplot.grid.x";
	public static final String KEY_GRID_Y = "xyplot.grid.y";
	public static final String KEY_GRID_COLOR = "xyplot.grid.color";

	public static final String KEY_RENDERER_AXIS_X = "xyplot.renderer.axisx";
	public static final String KEY_RENDERER_AXIS_Y = "xyplot.renderer.axisy";

	private double minX;
	private double maxX;
	private double minY;
	private double maxY;
	private Axis axisX;
	private Axis axisY;
	private Drawable axisXComp;
	private Drawable axisYComp;

	private final Map<DataSource, ShapeRenderer> shapeRenderers;
	private final Map<DataSource, LineRenderer2D> lineRenderers;

	protected class XYPlotArea2D extends PlotArea2D {
		@Override
		public void draw(Graphics2D g2d) {
			drawBackground(g2d);
			drawGrid(g2d);
			drawBorder(g2d);
			drawPlot(g2d);
			drawAxes(g2d);
			drawLegend(g2d);
		}

		protected void drawGrid(Graphics2D g2d) {
			boolean isGridX = XYPlot.this.getSetting(KEY_GRID_X);
			boolean isGridY = XYPlot.this.getSetting(KEY_GRID_Y);
			if (!isGridX && !isGridY) {
				return;
			}

			AffineTransform txOrig = g2d.getTransform();
			g2d.translate(getX(), getY());
			AffineTransform txOffset = g2d.getTransform();
			Paint paint = XYPlot.this.getSetting(KEY_GRID_COLOR);
			Rectangle2D bounds = getBounds();

			// Draw gridX
			if (isGridX) {
				AxisRenderer2D axisXRenderer = XYPlot.this.getSetting(KEY_RENDERER_AXIS_X);
				Shape shapeX = axisXRenderer.getSetting(AxisRenderer2D.KEY_SHAPE);
				Rectangle2D shapeBoundsX = shapeX.getBounds2D();
				List<DataPoint2D> ticksX = axisXRenderer.getTicks(axisX);
				Line2D gridLineVert = new Line2D.Double(
					-shapeBoundsX.getMinX(), -shapeBoundsX.getMinY(),
					-shapeBoundsX.getMinX(), bounds.getHeight() - shapeBoundsX.getMinY()
				);
				for (DataPoint2D tick : ticksX) {
					Point2D tickPoint = tick.getPoint();
					if (tickPoint == null) {
						continue;
					}
					g2d.translate(tickPoint.getX(), tickPoint.getY());
					GraphicsUtils.drawPaintedShape(g2d, gridLineVert, paint, null, null);
					g2d.setTransform(txOffset);
				}
			}

			// Draw gridY
			if (isGridY) {
				AxisRenderer2D axisYRenderer = XYPlot.this.getSetting(KEY_RENDERER_AXIS_Y);
				Shape shapeY = axisYRenderer.getSetting(AxisRenderer2D.KEY_SHAPE);
				Rectangle2D shapeBoundsY = shapeY.getBounds2D();
				List<DataPoint2D> ticksY = axisYRenderer.getTicks(axisY);
				Line2D gridLineHoriz = new Line2D.Double(
					-shapeBoundsY.getMinX(), -shapeBoundsY.getMinY(),
					bounds.getWidth() - shapeBoundsY.getMinX(), -shapeBoundsY.getMinY()
				);
				for (DataPoint2D tick : ticksY) {
					Point2D tickPoint = tick.getPoint();
					g2d.translate(tickPoint.getX(), tickPoint.getY());
					GraphicsUtils.drawPaintedShape(g2d, gridLineHoriz, paint, null, null);
					g2d.setTransform(txOffset);
				}
			}

			g2d.setTransform(txOrig);
		}

		protected void drawPlot(Graphics2D g2d) {
			AffineTransform txOrig = g2d.getTransform();
			g2d.translate(getX(), getY());
			AffineTransform txOffset = g2d.getTransform();

			// Paint shapes and lines
			Drawable line;
			for (DataSource s : data) {
				ShapeRenderer shapeRenderer = getShapeRenderer(s);
				LineRenderer2D lineRenderer = getLineRenderer(s);

				DataPoint2D pPrev = null;
				for (int i = 0; i < s.getRowCount(); i++) {
					Row row = new Row(s, i);
					Number valueX = row.get(0);
					Number valueY = row.get(1);
					AxisRenderer2D axisXRenderer = XYPlot.this.getSetting(KEY_RENDERER_AXIS_X);
					AxisRenderer2D axisYRenderer = XYPlot.this.getSetting(KEY_RENDERER_AXIS_Y);
					Point2D axisPosX = axisXRenderer.worldToViewPos(axisX, valueX, true);
					Point2D axisPosY = axisYRenderer.worldToViewPos(axisY, valueY, true);
					if (axisPosX==null || axisPosY==null) {
						continue;
					}
					Point2D pos = new Point2D.Double(axisPosX.getX(), axisPosY.getY());

					Shape shapePath = (shapeRenderer != null) ? shapeRenderer.getShapePath(row) : null;
					DataPoint2D p = new DataPoint2D(pos, null, shapePath, null);
					if (i > 0 && lineRenderer != null && pos != null) {
						line = lineRenderer.getLine(pPrev, p);
						line.draw(g2d);
					}
					pPrev = p;

					if (shapeRenderer != null) {
						g2d.translate(pos.getX(), pos.getY());
						Drawable shape = shapeRenderer.getShape(row);
						shape.draw(g2d);
						g2d.setTransform(txOffset);
					}
				}
			}
			g2d.setTransform(txOrig);
		}
	}

	public class XYLegend extends Legend {
		protected final DataSource DUMMY_DATA = new DummyData(1, 1, 1.0);

		@Override
		protected void drawSymbol(Graphics2D g2d, Drawable symbol, DataSource data) {
			ShapeRenderer shapeRenderer = shapeRenderers.get(data);
			LineRenderer2D lineRenderer = lineRenderers.get(data);

			Row row = new Row(DUMMY_DATA, 0);
			Rectangle2D bounds = symbol.getBounds();

			DataPoint2D p1 = new DataPoint2D(
				new Point2D.Double(bounds.getMinX(), bounds.getCenterY()), null,
				null, null
			);
			DataPoint2D p2 = new DataPoint2D(
				new Point2D.Double(bounds.getCenterX(), bounds.getCenterY()), null,
				(shapeRenderer != null) ? shapeRenderer.getShapePath(row) : null, null
			);
			DataPoint2D p3 = new DataPoint2D(
				new Point2D.Double(bounds.getMaxX(), bounds.getCenterY()), null,
				null, null
			);

			if (lineRenderer != null) {
				lineRenderer.getLine(p1, p2).draw(g2d);
				lineRenderer.getLine(p2, p3).draw(g2d);
			}
			if (shapeRenderer != null) {
				Point2D pos = p2.getPoint();
				AffineTransform txOrig = g2d.getTransform();
				g2d.translate(pos.getX(), pos.getY());
				shapeRenderer.getShape(row).draw(g2d);
				g2d.setTransform(txOrig);
			}
		}
		
	}

	public XYPlot(DataSource... data) {
		super(data);

		shapeRenderers = new HashMap<DataSource, ShapeRenderer>();
		lineRenderers = new LinkedHashMap<DataSource, LineRenderer2D>(data.length);

		setSettingDefault(KEY_GRID_X, true);
		setSettingDefault(KEY_GRID_Y, true);
		setSettingDefault(KEY_GRID_COLOR, new Color(0.0f, 0.0f, 0.0f, 0.2f));

		setPlotArea(new XYPlotArea2D());
		setLegend(new XYLegend());

		ShapeRenderer shapeRendererDefault = new DefaultShapeRenderer();
		for (DataSource source : data) {
			getLegend().add(source);
			setShapeRenderer(source, shapeRendererDefault);
			source.addDataListener(this);
			dataChanged(source);
		}

		// Create axes
		axisX = new Axis(minX, maxX);
		axisY = new Axis(minY, maxY);

		setAxis(Axis.X, axisX, axisXComp);
		setAxis(Axis.Y, axisY, axisYComp);

		setSettingDefault(KEY_RENDERER_AXIS_X, new LinearRenderer2D());
		setSettingDefault(KEY_RENDERER_AXIS_Y, new LinearRenderer2D());
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
		AxisRenderer2D axisXRenderer = getSetting(KEY_RENDERER_AXIS_X);
		AxisRenderer2D axisYRenderer = getSetting(KEY_RENDERER_AXIS_Y);
		Dimension2D axisXSize = null;
		Dimension2D axisYSize = null;

		// Set the new shapes first to allow for correct positioning
		if (axisXComp != null && axisXRenderer != null) {
			axisXSize = axisXComp.getPreferredSize();
			axisXRenderer.setSetting(AxisRenderer2D.KEY_SHAPE, new Line2D.Double(
				0.0, 0.0,
				plotBounds.getWidth(), 0.0
			));
		}
		if (axisYComp != null && axisYRenderer != null) {
			axisYSize = axisYComp.getPreferredSize();
			axisYRenderer.setSetting(AxisRenderer2D.KEY_SHAPE, new Line2D.Double(
				axisYSize.getWidth(), plotBounds.getHeight(),
				axisYSize.getWidth(), 0.0
			));
		}			

		// Set bounds with new axis shapes
		if (axisXComp != null && axisXRenderer != null) {
			double axisXIntersection = axisXRenderer.getSetting(AxisRenderer2D.KEY_INTERSECTION);
			Point2D axisXPos = axisYRenderer.worldToViewPos(axisY, axisXIntersection, false);
			axisXComp.setBounds(
				plotBounds.getMinX(), axisXPos.getY() + plotBounds.getMinY(),
				plotBounds.getWidth(), axisXSize.getHeight()
			);
		}

		if (axisYComp != null && axisYRenderer != null) {
			double axisYIntersection = axisYRenderer.getSetting(AxisRenderer2D.KEY_INTERSECTION);
			Point2D axisYPos = axisXRenderer.worldToViewPos(axisX, axisYIntersection, false);
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

	@Override
	public void settingChanged(SettingChangeEvent event) {
		super.settingChanged(event);

		String key = event.getKey();
		if (KEY_RENDERER_AXIS_X.equals(key)) {
			AxisRenderer2D axisXRenderer = (AxisRenderer2D) event.getValNew();
			axisXComp = axisXRenderer.getRendererComponent(axisX);
			setAxis(Axis.X, axisX, axisXComp);
		}
		else if (KEY_RENDERER_AXIS_Y.equals(key)) {
			AxisRenderer2D axisYRenderer = (AxisRenderer2D) event.getValNew();
			axisYRenderer.setSetting(AxisRenderer2D.KEY_SHAPE_NORMAL_ORIENTATION_CLOCKWISE, true);
			axisYComp = axisYRenderer.getRendererComponent(axisY);
			setAxis(Axis.Y, axisY, axisYComp);
		}
	}

	public ShapeRenderer getShapeRenderer(DataSource s) {
		return shapeRenderers.get(s);
	}

	public void setShapeRenderer(DataSource s, ShapeRenderer shapeRenderer) {
		this.shapeRenderers.put(s, shapeRenderer);
	}

	public LineRenderer2D getLineRenderer(DataSource s) {
		return lineRenderers.get(s);
	}

	public void setLineRenderer(DataSource s, LineRenderer2D lineRenderer) {
		lineRenderers.put(s, lineRenderer);
	}

}

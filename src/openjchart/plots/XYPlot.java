package openjchart.plots;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import openjchart.AbstractDrawable;
import openjchart.Drawable;
import openjchart.data.DataSource;
import openjchart.data.statistics.Statistics;
import openjchart.plots.axes.Axis;
import openjchart.plots.axes.AxisRenderer2D;
import openjchart.plots.axes.LinearRenderer2D;
import openjchart.plots.axes.Tick2D;
import openjchart.plots.lines.LineRenderer2D;
import openjchart.plots.shapes.DefaultShapeRenderer;
import openjchart.plots.shapes.ShapeRenderer;
import openjchart.util.SettingChangeEvent;

public class XYPlot extends Plot {
	public static final String KEY_GRID_X = "xyplot.grid.x";
	public static final String KEY_GRID_Y = "xyplot.grid.y";
	public static final String KEY_GRID_COLOR = "xyplot.grid.color";

	public static final String KEY_RENDERER_AXIS_X = "xyplot.renderer.axisx";
	public static final String KEY_RENDERER_AXIS_Y = "xyplot.renderer.axisy";

	private final Map<DataSource, ShapeRenderer> shapeRenderers;
	private final Map<DataSource, LineRenderer2D> lineRenderers;
	private final List<DataSource> data;
	private double minX;
	private double maxX;
	private double minY;
	private double maxY;
	private Axis axisX;
	private Axis axisY;
	private Drawable axisXComp;
	private Drawable axisYComp;
	private final PlotArea2D plotArea;

	private class PlotArea2D extends AbstractDrawable {
		@Override
		public void draw(Graphics2D g2d) {
			Color bg = getSetting(KEY_PLOTAREA_BACKGROUND);
			if (bg != null) {
				Color colorOld = g2d.getColor();
				g2d.setColor(bg);
				g2d.fill(getBounds());
				g2d.setColor(colorOld);
			}

			drawGrid(g2d);

			Stroke borderStroke = getSetting(KEY_PLOTAREA_BORDER);
			if (borderStroke != null) {
				Stroke strokeOld = g2d.getStroke();
				g2d.setStroke(borderStroke);
				g2d.draw(getBounds());
				g2d.setStroke(strokeOld);
			}

			drawAxes(g2d);
			drawPlot(g2d);
		}

		protected void drawGrid(Graphics2D g2d) {
			boolean isGridX = getSetting(KEY_GRID_X);
			boolean isGridY = getSetting(KEY_GRID_Y);
			if (!isGridX && !isGridY) {
				return;
			}

			AffineTransform txOrig = g2d.getTransform();
			g2d.translate(getX(), getY());
			AffineTransform txOffset = g2d.getTransform();
			Color colorDefault = g2d.getColor();

			g2d.setColor(XYPlot.this.<Color>getSetting(KEY_GRID_COLOR));
			Rectangle2D bounds = getBounds();

			// Draw gridX
			if (isGridX) {
				AxisRenderer2D axisXRenderer = getSetting(KEY_RENDERER_AXIS_X);
				Shape shapeX = axisXRenderer.getSetting(AxisRenderer2D.KEY_SHAPE);
				Rectangle2D shapeBoundsX = shapeX.getBounds2D();
				List<Tick2D> ticksX = axisXRenderer.getTicks(axisX);
				Line2D gridLineVert = new Line2D.Double(
					-shapeBoundsX.getMinX(), -shapeBoundsX.getMinY(),
					-shapeBoundsX.getMinX(), bounds.getHeight() - shapeBoundsX.getMinY()
				);
				for (Tick2D tick : ticksX) {
					Point2D tickPoint = tick.getPosition();
					if (tickPoint == null) {
						continue;
					}
					g2d.translate(tickPoint.getX(), tickPoint.getY());
					g2d.draw(gridLineVert);
					g2d.setTransform(txOffset);
				}
			}

			// Draw gridY
			if (isGridY) {
				AxisRenderer2D axisYRenderer = getSetting(KEY_RENDERER_AXIS_Y);
				Shape shapeY = axisYRenderer.getSetting(AxisRenderer2D.KEY_SHAPE);
				Rectangle2D shapeBoundsY = shapeY.getBounds2D();
				List<Tick2D> ticksY = axisYRenderer.getTicks(axisY);
				Line2D gridLineHoriz = new Line2D.Double(
					-shapeBoundsY.getMinX(), -shapeBoundsY.getMinY(),
					bounds.getWidth() - shapeBoundsY.getMinX(), -shapeBoundsY.getMinY()
				);
				for (Tick2D tick : ticksY) {
					Point2D tickPoint = tick.getPosition();
					g2d.translate(tickPoint.getX(), tickPoint.getY());
					g2d.draw(gridLineHoriz);
					g2d.setTransform(txOffset);
				}
			}

			g2d.setColor(colorDefault);
			g2d.setTransform(txOrig);
		}

		protected void drawPlot(Graphics2D g2d) {
			AffineTransform txOrig = g2d.getTransform();
			g2d.translate(getX(), getY());
			AffineTransform txOffset = g2d.getTransform();
			Color colorDefault = g2d.getColor();

			// Paint shapes and lines
			Drawable line;
			for (DataSource s : data) {
				ShapeRenderer shapeRenderer = getShapeRenderer(s);
				LineRenderer2D lineRenderer = getLineRenderer(s);

				Point2D posPrev = null;
				Shape shapePathOld = null;
				for (int i = 0; i < s.getRowCount(); i++) {
					Number valueX = s.get(0, i);
					Number valueY = s.get(1, i);
					AxisRenderer2D axisXRenderer = getSetting(KEY_RENDERER_AXIS_X);
					AxisRenderer2D axisYRenderer = getSetting(KEY_RENDERER_AXIS_Y);
					Point2D axisPosX = axisXRenderer.worldToViewPos(axisX, valueX);
					Point2D axisPosY = axisYRenderer.worldToViewPos(axisY, valueY);
					if (axisPosX==null || axisPosY==null) {
						continue;
					}
					Point2D pos = new Point2D.Double(axisPosX.getX(), axisPosY.getY());

					Shape shapePath = shapeRenderer.getShapePath(s, i);
					if (i > 0 && lineRenderer != null && pos != null && posPrev != null) {
						DataPoint2D p1 = new DataPoint2D(posPrev, shapePathOld);
						DataPoint2D p2 = new DataPoint2D(pos, shapePath);
						line = lineRenderer.getLine(p1, p2);
						line.draw(g2d);
					}
					posPrev = pos;
					shapePathOld = shapePath;

					g2d.translate(pos.getX(), pos.getY());
					Drawable shape = shapeRenderer.getShape(s, i);
					shape.draw(g2d);
					g2d.setTransform(txOffset);
				}
			}
			g2d.setColor(colorDefault);
			g2d.setTransform(txOrig);
		}
	}

	public XYPlot(DataSource... data) {
		setSettingDefault(KEY_GRID_X, true);
		setSettingDefault(KEY_GRID_Y, true);
		setSettingDefault(KEY_GRID_COLOR, Color.LIGHT_GRAY);
		plotArea = new PlotArea2D();
		add(plotArea, PlotLayout.CENTER);

		this.shapeRenderers = new HashMap<DataSource, ShapeRenderer>();
		this.lineRenderers = new LinkedHashMap<DataSource, LineRenderer2D>(data.length);
		this.data = new LinkedList<DataSource>();

		ShapeRenderer shapeRendererDefault = new DefaultShapeRenderer();
		for (DataSource source : data) {
			this.data.add(source);
			setShapeRenderer(source, shapeRendererDefault);
			dataChanged(source);
			source.addDataListener(this);
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
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);

		// Calculate axis bounds
		Rectangle2D plotBounds = plotArea.getBounds();

		Dimension2D axisXSize = axisXComp.getPreferredSize();
		AxisRenderer2D axisXRenderer = getSetting(KEY_RENDERER_AXIS_X);
		axisXRenderer.setSetting(AxisRenderer2D.KEY_SHAPE, new Line2D.Double(
			0.0, 0.0,
			plotBounds.getWidth(), 0.0
		));
		axisXComp.setBounds(
			plotBounds.getMinX(), plotBounds.getMaxY(),
			plotBounds.getWidth(), axisXSize.getHeight()
		);

		Dimension2D axisYSize = axisYComp.getPreferredSize();
		AxisRenderer2D axisYRenderer = getSetting(KEY_RENDERER_AXIS_Y);
		axisYRenderer.setSetting(AxisRenderer2D.KEY_SHAPE, new Line2D.Double(
			axisYSize.getWidth(), plotBounds.getHeight(),
			axisYSize.getWidth(), 0.0
		));
		axisYComp.setBounds(
			plotBounds.getMinX() - axisYSize.getWidth(),
			plotBounds.getMinY(),
			axisYSize.getWidth(), plotBounds.getHeight()
		);
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

	@Override
	public void dataChanged(DataSource data) {
		super.dataChanged(data);

		minX =  Double.MAX_VALUE;
		maxX = -Double.MAX_VALUE;
		minY =  Double.MAX_VALUE;
		maxY = -Double.MAX_VALUE;
		for (DataSource s : this.data) {
			Statistics stats = s.getStatistics();
			// Set the minimal and maximal value of the axes
			int colX = 0;
			minX = Math.min(minX, stats.get(Statistics.MIN, colX));
			maxX = Math.max(maxX, stats.get(Statistics.MAX, colX));
			int colY = 1;
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
}

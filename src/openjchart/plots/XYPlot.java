package openjchart.plots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import openjchart.AbstractDrawable;
import openjchart.Drawable;
import openjchart.data.DataSource;
import openjchart.data.statistics.Statistics;
import openjchart.plots.axes.AbstractAxisRenderer2D;
import openjchart.plots.axes.Axis;
import openjchart.plots.axes.AxisRenderer2D;
import openjchart.plots.axes.LinearRenderer2D;
import openjchart.plots.lines.DefaultLineRenderer2D;
import openjchart.plots.lines.LineRenderer2D;
import openjchart.plots.shapes.DefaultShapeRenderer;
import openjchart.plots.shapes.ShapeRenderer;

public class XYPlot extends Plot {
	public static final String KEY_GRID_X = "xyplot.grid.x";
	public static final String KEY_GRID_Y = "xyplot.grid.y";
	public static final String KEY_GRID_COLOR = "xyplot.grid.color";

	public static final String KEY_RENDERER_AXIS_X = "xyplot.renderer.axisx";
	public static final String KEY_RENDERER_AXIS_Y = "xyplot.renderer.axisy";

	private final Map<DataSource, ShapeRenderer> shapeRenderers;
	private final Map<DataSource, LineRenderer2D> data;
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
		private double w;
		private double h;
		private double xMin;
		private double xMax;
		private double yMin;
		private double yMax;

		@Override
		public void draw(Graphics2D g2d) {
			drawGrid(g2d);
			drawPlot(g2d);
		}

		@Override
		public void setBounds(double x, double y, double width, double height) {
			super.setBounds(x, y, width, height);

			w = width - 1;
			h = height - 1;
			xMin = x;
			xMax = xMin + w;
			yMin = y;
			yMax = yMin + h;
		}

		protected void drawGrid(Graphics2D g2d) {
			boolean isGridX = getSetting(KEY_GRID_X);
			boolean isGridY = getSetting(KEY_GRID_Y);
			if (!isGridX && !isGridY) {
				return;
			}

			AffineTransform txOld = g2d.getTransform();
			Color colorDefault = g2d.getColor();

			g2d.setColor(XYPlot.this.<Color>getSetting(KEY_GRID_COLOR));

			// Draw gridX
			if (isGridX) {
				AbstractAxisRenderer2D axisXRenderer = getSetting(KEY_RENDERER_AXIS_X);
				double minTickX = axisXRenderer.getMinTick(axisX);
				double maxTickX = axisXRenderer.getMaxTick(axisX);
				Line2D gridLineVert = new Line2D.Double(0, yMin, 0, yMax);
				double tickSpacingX = axisXRenderer.getSetting(AxisRenderer2D.KEY_TICK_SPACING);
				for (double i = minTickX; i <= maxTickX; i += tickSpacingX) {
					double viewX = axisXRenderer.worldToViewPos(axisX, i).getX();
					double translateX = xMin + viewX;
					g2d.translate(translateX, 0);
					g2d.draw(gridLineVert);
					g2d.setTransform(txOld);
				}
			}

			// Draw gridY
			if (isGridY) {
				AbstractAxisRenderer2D axisYRenderer = getSetting(KEY_RENDERER_AXIS_Y);
				double minTickY = axisYRenderer.getMinTick(axisY);
				double maxTickY = axisYRenderer.getMaxTick(axisY);
				Line2D gridLineHoriz = new Line2D.Double(xMin, 0, xMax, 0);
				double tickSpacingY = axisYRenderer.getSetting(AxisRenderer2D.KEY_TICK_SPACING);
				for (double i = minTickY; i <= maxTickY; i += tickSpacingY) {
					double viewY = axisYRenderer.worldToViewPos(axisY, i).getY();
					double translateY = yMax - viewY + 1.0;
					g2d.translate(0, translateY);
					g2d.draw(gridLineHoriz);
					g2d.setTransform(txOld);
				}
			}

			g2d.setColor(colorDefault);
		}

		protected void drawPlot(Graphics2D g2d) {
			AffineTransform txOld = g2d.getTransform();
			Color colorDefault = g2d.getColor();

			// Paint shapes and lines
			Drawable line;
			for (Map.Entry<DataSource, LineRenderer2D> entry : data.entrySet()) {
				DataSource data = entry.getKey();
				LineRenderer2D lineRenderer = entry.getValue();
				double[] lineStart = new double[2];

				ShapeRenderer shapeRenderer = shapeRenderers.get(data);
				for (int i = 0; i < data.getRowCount(); i++) {
					Number valueX = data.get(0, i);
					Number valueY = data.get(1, i);
					AbstractAxisRenderer2D axisXRenderer = getSetting(KEY_RENDERER_AXIS_X);
					AbstractAxisRenderer2D axisYRenderer = getSetting(KEY_RENDERER_AXIS_Y);
					double translateX = axisXRenderer.worldToViewPos(axisX, valueX).getX() + xMin;
					double translateY = axisYRenderer.worldToViewPos(axisY, valueY).getY() + yMin;

					if (i != 0 && lineRenderer != null) {
						line = lineRenderer.getLine(lineStart[0], lineStart[1], translateX, translateY);
						line.draw(g2d);
					}
					lineStart[0] = translateX;
					lineStart[1] = translateY;

					g2d.translate(translateX, translateY);
					Drawable shape = shapeRenderer.getShape(data, i);
					shape.draw(g2d);
					g2d.setTransform(txOld);
				}
			}
			g2d.setColor(colorDefault);
		}
	}

	public XYPlot(DataSource... data) {
		setSettingDefault(KEY_GRID_X, true);
		setSettingDefault(KEY_GRID_Y, true);
		setSettingDefault(KEY_GRID_COLOR, Color.LIGHT_GRAY);
		this.plotArea = new PlotArea2D();

		ShapeRenderer shapeRendererDefault = new DefaultShapeRenderer();
		LineRenderer2D lineRendererDefault = new DefaultLineRenderer2D();
		this.shapeRenderers = new HashMap<DataSource, ShapeRenderer>();
		this.data = new LinkedHashMap<DataSource, LineRenderer2D>(data.length);
		for (DataSource source : data) {
			this.data.put(source, lineRendererDefault);
			this.shapeRenderers.put(source, shapeRendererDefault);
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
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		plotArea.drawGrid(g2d);
		drawAxes(g2d);
		plotArea.drawPlot(g2d);
		drawTitle(g2d);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);

		Insets insets = getInsets();

		// Calculate title and axis bounds
		Label title = getTitle();
		double titleY = insets.top;
		double titleHeight = title != null ? title.getPreferredSize().getHeight() : 0.0;

		double compXHeight = axisXComp.getPreferredSize().getHeight();
		double compYWidth = axisYComp.getPreferredSize().getWidth();
		double compXWidth = getWidth() - compYWidth - insets.left - insets.right;
		double compYHeight = getHeight() - compXHeight  - titleY - titleHeight - insets.bottom;

		double compXposX = compYWidth + insets.left;
		double compXposY = getHeight() - compXHeight  - insets.bottom;
		axisXComp.setBounds(compXposX, compXposY, compXWidth, compXHeight);
		AbstractAxisRenderer2D axisXRenderer = getSetting(KEY_RENDERER_AXIS_X);
		axisXRenderer.setSetting(AxisRenderer2D.KEY_SHAPE, new Line2D.Double(0.0, 0.0, compXWidth, 0.0));

		double titleX = compXposX;
		double titleWidth = compXWidth;
		if (title != null) {
			title.setBounds(titleX, titleY, titleWidth, titleHeight);
		}

		double compYposX = insets.left;
		double compYposY = titleY + titleHeight;
		axisYComp.setBounds(compYposX, compYposY, compYWidth, compYHeight);
		AbstractAxisRenderer2D axisYRenderer = getSetting(KEY_RENDERER_AXIS_Y);
		axisYRenderer.setSetting(AxisRenderer2D.KEY_SHAPE, new Line2D.Double(compYWidth, compYHeight, compYWidth, 0.0));

		// Calculate dimensions of plot area
		double plotAreaX = compYposX + compYWidth;
		double plotAreaY = titleY + titleHeight;
		double plotAreaWidth = width - plotAreaX - insets.right;
		double plotAreaHeight = height - plotAreaY - (height - compXposY);
		plotArea.setBounds(plotAreaX, plotAreaY, plotAreaWidth, plotAreaHeight);
	}

	@Override
	public <T> void setSetting(String key, T value) {
		super.setSetting(key, value);

		if (KEY_RENDERER_AXIS_X.equals(key)) {
			AbstractAxisRenderer2D axisXRenderer = (AbstractAxisRenderer2D) value;
			axisXComp = axisXRenderer.getRendererComponent(axisX);
			setAxis(Axis.X, axisX, axisXComp);
		}
		else if (KEY_RENDERER_AXIS_Y.equals(key)) {
			AbstractAxisRenderer2D axisYRenderer = (AbstractAxisRenderer2D) value;
			axisYRenderer.setSetting(AxisRenderer2D.KEY_SHAPE_NORMAL_ORIENTATION_CLOCKWISE, true);
			axisYComp = axisYRenderer.getRendererComponent(axisY);
			setAxis(Axis.Y, axisY, axisYComp);
		}
	};

	@Override
	public <T> void setSettingDefault(String key, T value) {
		super.setSettingDefault(key, value);

		if (KEY_RENDERER_AXIS_X.equals(key)) {
			AbstractAxisRenderer2D axisXRenderer = (AbstractAxisRenderer2D) value;
			axisXComp = axisXRenderer.getRendererComponent(axisX);
			setAxis(Axis.X, axisX, axisXComp);
		}
		else if (KEY_RENDERER_AXIS_Y.equals(key)) {
			AbstractAxisRenderer2D axisYRenderer = (AbstractAxisRenderer2D) value;
			axisYRenderer.setSetting(AxisRenderer2D.KEY_SHAPE_NORMAL_ORIENTATION_CLOCKWISE, true);
			axisYComp = axisYRenderer.getRendererComponent(axisY);
			setAxis(Axis.Y, axisY, axisYComp);
		}
	};

	public ShapeRenderer getShapeRenderer(DataSource source) {
		return shapeRenderers.get(source);
	}

	public void setShapeRenderer(DataSource source, ShapeRenderer shapeRenderer) {
		this.shapeRenderers.put(source, shapeRenderer);
	}

	@Override
	public void dataChanged(DataSource data) {
		super.dataChanged(data);

		minX = Double.MAX_VALUE;
		maxX = -Double.MAX_VALUE;
		minY = Double.MAX_VALUE;
		maxY = -Double.MAX_VALUE;
		for (DataSource s : this.data.keySet()) {
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

	public LineRenderer2D getLineRenderer(DataSource s) {
		return data.get(s);
	}

	public void setLineRenderer(DataSource s, LineRenderer2D lineRenderer) {
		data.put(s, lineRenderer);
	}
}

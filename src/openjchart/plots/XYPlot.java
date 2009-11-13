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

import openjchart.Drawable;
import openjchart.data.DataSource;
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

	private AbstractAxisRenderer2D axisXRenderer;
	private AbstractAxisRenderer2D axisYRenderer;

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

	public XYPlot(DataSource... data) {
		setSettingDefault(KEY_GRID_X, true);
		setSettingDefault(KEY_GRID_Y, true);
		setSettingDefault(KEY_GRID_COLOR, Color.LIGHT_GRAY);

		this.shapeRenderers = new HashMap<DataSource, ShapeRenderer>();
		ShapeRenderer shapeRendererDefault = new DefaultShapeRenderer();
		this.data = new LinkedHashMap<DataSource, LineRenderer2D>(data.length);
		LineRenderer2D lineRendererDefault = new DefaultLineRenderer2D();
		for (DataSource source : data) {
			this.data.put(source, lineRendererDefault);
			this.shapeRenderers.put(source, shapeRendererDefault);
			dataChanged(source);
			source.addDataListener(this);
		}

		// Create axes
		axisX = new Axis(minX, maxX);
		axisY = new Axis(minY, maxY);

		setAxisXRenderer(new LinearRenderer2D());
		setAxisYRenderer(new LinearRenderer2D());

		setAxis(Axis.X, axisX, axisXComp);
		setAxis(Axis.Y, axisY, axisYComp);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		drawGrid(g2d);
		drawAxes(g2d);
		drawPlot(g2d);
		drawTitle(g2d);
	}

	protected void drawGrid(Graphics2D g2d) {
		boolean isGridX = getSetting(KEY_GRID_X);
		boolean isGridY = getSetting(KEY_GRID_Y);
		if (!isGridX && !isGridY) {
			return;
		}

		AffineTransform txOld = g2d.getTransform();
		Color colorDefault = g2d.getColor();

		Insets insets = getInsets();
		Label title = getTitle();
		double titleOffset = title != null ? title.getY() + title.getHeight() : insets.top;
		double axisXOffset = axisXComp.getHeight();
		double axisYOffset = axisYComp.getWidth();
		// TODO: Use Drawable for plot area instead of calculating each values separately
		double w = getWidth() - 1 - axisYOffset - insets.left - insets.right;
		double h = getHeight() - 1 - axisXOffset - titleOffset - insets.bottom;
		double plotXMin = axisYOffset + insets.left;
		double plotXMax = plotXMin + w;
		double plotYMin = titleOffset;
		double plotYMax = plotYMin + h;

		g2d.setColor(this.<Color>getSetting(KEY_GRID_COLOR));

		// Draw gridX
		if (isGridX) {
			double minTickX = axisXRenderer.getMinTick(axisX);
			double maxTickX = axisXRenderer.getMaxTick(axisX);
			Line2D gridLineVert = new Line2D.Double(0, plotYMin, 0, plotYMax);
			double tickSpacingX = axisXRenderer.getSetting(AxisRenderer2D.KEY_TICK_SPACING);
			for (double i = minTickX; i <= maxTickX; i += tickSpacingX) {
				double viewX = axisXRenderer.worldToViewPos(axisX, i).getX();
				double translateX = plotXMin + viewX;
				g2d.translate(translateX, 0);
				g2d.draw(gridLineVert);
				g2d.setTransform(txOld);
			}
		}

		// Draw gridY
		if (isGridY) {
			double minTickY = axisYRenderer.getMinTick(axisY);
			double maxTickY = axisYRenderer.getMaxTick(axisY);
			Line2D gridLineHoriz = new Line2D.Double(plotXMin, 0, plotXMax, 0);
			double tickSpacingY = axisYRenderer.getSetting(AxisRenderer2D.KEY_TICK_SPACING);
			for (double i = minTickY; i <= maxTickY; i += tickSpacingY) {
				double viewY = axisYRenderer.worldToViewPos(axisY, i).getY();
				double translateY = plotYMax - viewY + 1.0;
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

		Insets insets = getInsets();
		Label title = getTitle();
		double titleOffset = title != null ? title.getY() + title.getHeight() : insets.top;
		double axisXOffset = axisXComp.getHeight();
		double axisYOffset = axisYComp.getWidth();
		// TODO: Use Drawable for plot area instead of calculating each values separately
		double w = getWidth() - 1 - axisYOffset - insets.left - insets.right;
		double h = getHeight() - 1 - axisXOffset - titleOffset - insets.bottom;
		double plotXMin = axisYOffset + insets.left;
		double plotXMax = plotXMin + w;
		double plotYMin = titleOffset;
		double plotYMax = plotYMin + h;

		// Paint shapes and lines
		Drawable line;
		for (Map.Entry<DataSource, LineRenderer2D> entry : data.entrySet()) {
			DataSource data = entry.getKey();
			LineRenderer2D lineRenderer = entry.getValue();
			double[] lineStart = new double[2];

			for (int i = 0; i < data.getRowCount(); i++) {
				Number valueX = data.get(0, i);
				Number valueY = data.get(1, i);
				double translateX = axisXRenderer.worldToViewPos(axisX, valueX).getX() + plotXMin;
				double translateY = axisYRenderer.worldToViewPos(axisY, valueY).getY() + plotYMin;

				if (i != 0 && lineRenderer != null) {
					line = lineRenderer.getLine(lineStart[0], lineStart[1], translateX, translateY);
					line.draw(g2d);
				}
				lineStart[0] = translateX;
				lineStart[1] = translateY;

				g2d.translate(translateX, translateY);
				Drawable shape = shapeRenderers.get(data).getShape(data, i);
				shape.draw(g2d);
				g2d.setTransform(txOld);
			}
		}
		g2d.setColor(colorDefault);
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

		double posX = compYWidth + insets.left;
		double posY = getHeight() - compXHeight  - insets.bottom;
		axisXComp.setBounds(posX, posY, compXWidth, compXHeight);
		axisXRenderer.setSetting(AxisRenderer2D.KEY_SHAPE, new Line2D.Double(0.0, 0.0, compXWidth, 0.0));

		double titleX = posX;
		double titleWidth = compXWidth;
		if (title != null) {
			title.setBounds(titleX, titleY, titleWidth, titleHeight);
		}

		posX = insets.left;
		posY = titleY + titleHeight;
		axisYComp.setBounds(posX, posY, compYWidth, compYHeight);
		axisYRenderer.setSetting(AxisRenderer2D.KEY_SHAPE, new Line2D.Double(compYWidth, compYHeight, compYWidth, 0.0));

		// TODO: Calculate dimensions of plot area
	}

	public AbstractAxisRenderer2D getAxisXRenderer() {
		return axisXRenderer;
	}

	public void setAxisXRenderer(AbstractAxisRenderer2D axisXRenderer) {
		this.axisXRenderer = axisXRenderer;
		axisXComp = axisXRenderer.getRendererComponent(axisX);
		setAxis(Axis.X, axisX, axisXComp);
	}

	public AbstractAxisRenderer2D getAxisYRenderer() {
		return axisYRenderer;
	}

	public void setAxisYRenderer(AbstractAxisRenderer2D axisYRenderer) {
		this.axisYRenderer = axisYRenderer;
		axisYRenderer.setSetting(AxisRenderer2D.KEY_SHAPE_NORMAL_ORIENTATION_CLOCKWISE, true);
		axisYComp = axisYRenderer.getRendererComponent(axisY);
		setAxis(Axis.Y, axisY, axisYComp);
	}

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
			// Set the minimal and maximal value of the axes
			int colX = 0;
			minX = Math.min(minX, s.getMin(colX).doubleValue());
			maxX = Math.max(maxX, s.getMax(colX).doubleValue());
			int colY = 1;
			minY = Math.min(minY, s.getMin(colY).doubleValue());
			maxY = Math.max(maxY, s.getMax(colY).doubleValue());
		}
	}

	public LineRenderer2D getLineRenderer(DataSource s) {
		return data.get(s);
	}

	public void setLineRenderer(DataSource s, LineRenderer2D lineRenderer) {
		data.put(s, lineRenderer);
	}
}

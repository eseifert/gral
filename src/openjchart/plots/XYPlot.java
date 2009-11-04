package openjchart.plots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.Map;

import openjchart.Drawable;
import openjchart.data.DataSeries;
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
	public static final String KEY_GRID = "xyplot.grid";
	public static final String KEY_GRID_COLOR = "xyplot.grid.color";

	private DataSource data;

	private AbstractAxisRenderer2D axisXRenderer;
	private AbstractAxisRenderer2D axisYRenderer;

	private ShapeRenderer shapeRenderer;

	private final Map<DataSeries, LineRenderer2D> series;
	private double minX;
	private double maxX;
	private double minY;
	private double maxY;
	private Axis axisX;
	private Axis axisY;
	private Drawable axisXComp;
	private Drawable axisYComp;

	public XYPlot(DataSource data, DataSeries... series) {
		setSettingDefault(KEY_GRID, true);
		setSettingDefault(KEY_GRID_COLOR, Color.LIGHT_GRAY);

		this.data = data;
		this.series = new HashMap<DataSeries, LineRenderer2D>(series.length);
		LineRenderer2D lineRendererDefault = new DefaultLineRenderer2D();
		for (DataSeries s : series) {
			this.series.put(s, lineRendererDefault);
		}

		dataChanged(this.data);
		this.data.addDataListener(this);

		// Create axes
		axisX = new Axis(minX, maxX);
		axisY = new Axis(minY, maxY);

		setAxisXRenderer(new LinearRenderer2D());
		setAxisYRenderer(new LinearRenderer2D());
		shapeRenderer = new DefaultShapeRenderer();

		setAxis(Axis.X, axisX, axisXComp);
		setAxis(Axis.Y, axisY, axisYComp);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		AffineTransform txOld = g2d.getTransform();
		Color colorDefault = g2d.getColor();

		// Take the Component's insets into consideration
		Insets insets = getInsets();

		Label title = getTitle();
		double titleOffset = title != null ? title.getY() + title.getHeight() : insets.top;
		double axisXOffset = axisXComp.getHeight();
		double axisYOffset = axisYComp.getWidth();
		double w = getWidth() - 1 - axisYOffset - insets.left - insets.right;
		double h = getHeight() - 1 - axisXOffset - titleOffset - insets.bottom;
		double plotXMin = axisYOffset + insets.left;
		double plotXMax = plotXMin + w;
		double plotYMin = titleOffset;
		double plotYMax = plotYMin + h;
		if (getSetting(KEY_GRID)) {
			// Draw gridX
			g2d.setColor(this.<Color>getSetting(KEY_GRID_COLOR));
			double minTickX = axisXRenderer.getMinTick(axisX);
			double maxTickX = axisXRenderer.getMaxTick(axisX);
			double gridOffsetY =
				axisXRenderer.<Double>getSetting(AxisRenderer2D.KEY_TICK_ALIGNMENT) *
				axisXRenderer.<Double>getSetting(AxisRenderer2D.KEY_TICK_LENGTH);
			Line2D gridLineVert = new Line2D.Double(0, plotYMin, 0, plotYMax-gridOffsetY);
			double tickSpacingX = axisXRenderer.getSetting(AxisRenderer2D.KEY_TICK_SPACING);
			for (double i = minTickX; i <= maxTickX; i += tickSpacingX) {
				double viewX = axisXRenderer.worldToView(axisX, i);
				// Do not draw a grid line on the axis
				if (viewX == 0.0) {
					continue;
				}
				double translateX = plotXMin + viewX;
				g2d.translate(translateX, 0);
				g2d.draw(gridLineVert);
				g2d.setTransform(txOld);
			}

			// Draw gridY
			double minTickY = axisYRenderer.getMinTick(axisY);
			double maxTickY = axisYRenderer.getMaxTick(axisY);
			double gridOffsetX =
				axisYRenderer.<Double>getSetting(AxisRenderer2D.KEY_TICK_ALIGNMENT) *
				axisYRenderer.<Double>getSetting(AxisRenderer2D.KEY_TICK_LENGTH);
			Line2D gridLineHoriz = new Line2D.Double(plotXMin+gridOffsetX, 0, plotXMax, 0);
			double tickSpacingY = axisYRenderer.getSetting(AxisRenderer2D.KEY_TICK_SPACING);
			for (double i = minTickY; i <= maxTickY; i += tickSpacingY) {
				double viewY = axisYRenderer.worldToView(axisY, i);
				// Do not draw a grid line on the axis
				if (viewY == 0.0) {
					continue;
				}
				double translateY = plotYMax - viewY + 1.0;
				g2d.translate(0, translateY);
				g2d.draw(gridLineHoriz);
				g2d.setTransform(txOld);
			}
		}

		// Paint shapes and lines
		Drawable line;
		for (Map.Entry<DataSeries, LineRenderer2D> entry : series.entrySet()) {
			DataSeries s = entry.getKey();
			LineRenderer2D lineRenderer = entry.getValue();
			double[] lineStart = new double[2];
			// Retrieve the columns mapped to X and Y axes
			int colX = s.get(DataSeries.X);
			int colY = s.get(DataSeries.Y);

			for (int i = 0; i < data.getRowCount(); i++) {
				double valueX = data.get(colX, i).doubleValue();
				double valueY = data.get(colY, i).doubleValue();
				double translateX = axisXRenderer.worldToView(axisX, valueX) + plotXMin;
				double translateY = plotYMax - axisYRenderer.worldToView(axisY, valueY) + 1.0;

				if (i != 0 && lineRenderer != null) {
					line = lineRenderer.getLine(lineStart[0], lineStart[1], translateX, translateY);
					line.draw(g2d);
				}
				lineStart[0] = translateX;
				lineStart[1] = translateY;

				g2d.translate(translateX, translateY);
				Drawable shape = shapeRenderer.getShape(data, s, i);
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

	public ShapeRenderer getShapeRenderer() {
		return shapeRenderer;
	}

	public void setShapeRenderer(ShapeRenderer shapeRenderer) {
		this.shapeRenderer = shapeRenderer;
	}

	@Override
	public void dataChanged(DataSource data) {
		super.dataChanged(data);

		minX = Double.MAX_VALUE;
		maxX = -Double.MAX_VALUE;
		minY = Double.MAX_VALUE;
		maxY = -Double.MAX_VALUE;
		for (DataSeries s : series.keySet()) {
			// Set the minimal and maximal value of the axes
			int colX = s.get(DataSeries.X);
			minX = Math.min(minX, data.getMin(colX).doubleValue());
			maxX = Math.max(maxX, data.getMax(colX).doubleValue());
			int colY = s.get(DataSeries.Y);
			minY = Math.min(minY, data.getMin(colY).doubleValue());
			maxY = Math.max(maxY, data.getMax(colY).doubleValue());
		}
	}

	public LineRenderer2D getLineRenderer(DataSeries s) {
		return series.get(s);
	}

	public void setLineRenderer(DataSeries s, LineRenderer2D lineRenderer) {
		series.put(s, lineRenderer);
	}
}

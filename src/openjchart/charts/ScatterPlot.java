package openjchart.charts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import openjchart.Drawable;
import openjchart.charts.axes.AbstractAxisRenderer2D;
import openjchart.charts.axes.Axis;
import openjchart.charts.axes.AxisRenderer2D;
import openjchart.charts.axes.LinearRenderer2D;
import openjchart.data.DataSeries;
import openjchart.data.DataTable;

public class ScatterPlot extends Chart {
	public static final String KEY_GRID = "scatterplot.grid";
	public static final String KEY_GRID_COLOR = "scatterplot.grid.color";

	private DataTable data;

	private AbstractAxisRenderer2D axisXRenderer;
	private AbstractAxisRenderer2D axisYRenderer;

	private ShapeRenderer shapeRenderer;

	private List<DataSeries> series;
	private double minX;
	private double maxX;
	private double minY;
	private double maxY;
	private Axis axisX;
	private Axis axisY;
	private Drawable axisXComp;
	private Drawable axisYComp;

	public ScatterPlot(DataTable data, DataSeries... series) {
		setSettingDefault(KEY_GRID, true);
		setSettingDefault(KEY_GRID_COLOR, Color.LIGHT_GRAY);

		this.data = data;
		this.series = new ArrayList<DataSeries>(series.length);
		for (DataSeries s : series) {
			this.series.add(s);
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

		double titleOffset = getTitle().getY() + getTitle().getHeight();
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

		// Paint shapes
		for (DataSeries s : series) {
			// Retrieve the columns mapped to X and Y axes
			int colX = s.get(DataSeries.X);
			int colY = s.get(DataSeries.Y);

			for (int i = 0; i < data.getRowCount(); i++) {
				double valueX = data.get(colX, i).doubleValue();
				double valueY = data.get(colY, i).doubleValue();
				double translateX = axisXRenderer.worldToView(axisX, valueX) + plotXMin;
				double translateY = plotYMax - axisYRenderer.worldToView(axisY, valueY) + 1.0;
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
		double titleY = insets.top;
		double titleHeight = 50.0;

		double compXHeight = axisXComp.getPreferredSize().getHeight();
		double compYWidth = axisYComp.getPreferredSize().getWidth();
		double compXWidth = getWidth() - compYWidth - insets.left - insets.right;
		double compYHeight = getHeight() - compXHeight  - titleY - titleHeight - insets.bottom;

		double posX = compYWidth + insets.left;
		double posY = getHeight() - compXHeight  - insets.bottom;
		axisXComp.setBounds(posX, posY, compXWidth, compXHeight);
		axisXRenderer.setSetting(AxisRenderer2D.KEY_SHAPE, new Line2D.Double(0.0, 0.0, compXWidth, 0.0));

		double titleX = posX;
		double titleWidth = width - insets.left - insets.right;
		getTitle().setBounds(titleX, titleY, titleWidth, titleHeight);

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
	public void dataChanged(DataTable data) {
		super.dataChanged(data);

		minX = Double.MAX_VALUE;
		maxX = -Double.MAX_VALUE;
		minY = Double.MAX_VALUE;
		maxY = -Double.MAX_VALUE;
		for (DataSeries s : series) {
			// Set the minimal and maximal value of the axes
			int colX = s.get(DataSeries.X);
			minX = Math.min(minX, data.getMin(colX).doubleValue());
			maxX = Math.max(maxX, data.getMax(colX).doubleValue());
			int colY = s.get(DataSeries.Y);
			minY = Math.min(minY, data.getMin(colY).doubleValue());
			maxY = Math.max(maxY, data.getMax(colY).doubleValue());
		}
	}
}

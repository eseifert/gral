package openjchart.charts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.border.Border;

import openjchart.Drawable;
import openjchart.charts.axes.AbstractAxisRenderer2D;
import openjchart.charts.axes.Axis;
import openjchart.charts.axes.LinearRenderer2D;
import openjchart.charts.axes.AxisRenderer2D.Orientation;
import openjchart.data.DataSeries;
import openjchart.data.DataTable;

public class ScatterPlot extends Chart {
	private DataTable data;

	private AbstractAxisRenderer2D axisXRenderer;
	private AbstractAxisRenderer2D axisYRenderer;

	private ShapeRenderer shapeRenderer;

	private boolean gridEnabled;

	private List<DataSeries> series;
	private Number minX;
	private Number maxX;
	private Number minY;
	private Number maxY;
	private Axis axisX;
	private Axis axisY;
	private Drawable axisXComp;
	private Drawable axisYComp;

	public ScatterPlot(DataTable data, DataSeries... series) {
		this.data = data;
		axisXRenderer = new LinearRenderer2D();
		axisYRenderer = new LinearRenderer2D();
		shapeRenderer = new DefaultShapeRenderer();

		minX = Double.MAX_VALUE;
		maxX = -Double.MAX_VALUE;
		minY = Double.MAX_VALUE;
		maxY = -Double.MAX_VALUE;

		this.series = new ArrayList<DataSeries>(series.length);
		for (DataSeries s : series) {
			this.series.add(s);

			// Set the minimal and maximal value of the axes
			int colX = s.get(DataSeries.X);
			minX = Math.min(minX.doubleValue(), data.getMin(colX).doubleValue());
			maxX = Math.max(maxX.doubleValue(), data.getMax(colX).doubleValue());
			int colY = s.get(DataSeries.Y);
			minY = Math.min(minY.doubleValue(), data.getMin(colY).doubleValue());
			maxY = Math.max(maxY.doubleValue(), data.getMax(colY).doubleValue());
		}

		gridEnabled = true;

		// Create axes
		axisX = new Axis(minX, maxX);
		axisY = new Axis(minY, maxY);
		axisXComp = axisXRenderer.getRendererComponent(axisX, Orientation.HORIZONTAL);
		axisYComp = axisYRenderer.getRendererComponent(axisY, Orientation.VERTICAL);

		addAxis(axisX, axisXComp);
		addAxis(axisY, axisYComp);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		AffineTransform txOld = g2d.getTransform();
		Color colorDefault = g2d.getColor();

		// Take the Component's insets into consideration
		Insets insets = getInsets();

		double axisXOffset = axisXComp.getHeight()/2.0;
		double axisYOffset = axisYComp.getWidth()/2.0;
		double w = getWidth() - 1 - axisYOffset - insets.left - insets.right;
		double h = getHeight() - 1 - axisXOffset - insets.top - insets.bottom;
		double plotXMin = axisYOffset + insets.left;
		double plotXMax = plotXMin + w;
		double plotYMin = insets.top;
		double plotYMax = plotYMin + h;
		if (gridEnabled) {
			int tickOffset = axisXRenderer.getTickLength()/2;
			// Draw gridX
			g2d.setColor(Color.LIGHT_GRAY);
			double minTick = axisXRenderer.getMinTick(minX.doubleValue());
			double maxTick = axisXRenderer.getMaxTick(maxX.doubleValue());
			Line2D gridLineVert = new Line2D.Double(0, plotYMin, 0, plotYMax-tickOffset);
			for (double i = minTick; i < maxTick; i += axisXRenderer.getTickSpacing()) {
				double translateX = w * axisXRenderer.getPos(axisX, i) + plotXMin;
				// Do not draw a grid line on the axis
				if (translateX == plotXMin) {
					continue;
				}
				g2d.translate(translateX, 0);
				g2d.draw(gridLineVert);
				g2d.setTransform(txOld);
			}

			// Draw gridY
			minTick = axisYRenderer.getMinTick(minY.doubleValue());
			maxTick = axisYRenderer.getMaxTick(maxY.doubleValue());
			Line2D gridLineHoriz = new Line2D.Double(plotXMin+tickOffset, 0, plotXMax, 0);
			for (double i = minTick; i <= maxTick; i += axisYRenderer.getTickSpacing()) {
				double translateY = plotYMax - h*axisYRenderer.getPos(axisY, i);
				// Do not draw a grid line on the axis
				if (translateY == plotYMin) {
					continue;
				}
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
				double translateX = w * axisXRenderer.getPos(axisX, valueX) + plotXMin;
				double translateY = plotYMax - h*axisYRenderer.getPos(axisY, valueY);
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

		double xHeight = 50;
		double yWidth = 100;
		double xWidth = getWidth() - yWidth/2 - insets.left - insets.right;
		double yHeight = getHeight() - xHeight/2  - insets.top - insets.bottom;

		double posX = yWidth/2 + insets.left;
		double posY = getHeight() - xHeight  - insets.bottom;
		axisXComp.setBounds(new Rectangle2D.Double(posX, posY, xWidth, xHeight));

		posX = insets.left;
		posY = insets.top;
		axisYComp.setBounds(new Rectangle2D.Double(posX, posY, yWidth, yHeight));
	}

	@Override
	public Insets getInsets() {
		Border border = getBorder();
		if (border != null) {
			return border.getBorderInsets(this);
		}

		return new Insets(0, 0, 0, 0);
	}

	public boolean isGridEnabled() {
		return gridEnabled;
	}

	public void setGridEnabled(boolean gridEnabled) {
		this.gridEnabled = gridEnabled;
	}

	public AbstractAxisRenderer2D getAxisXRenderer() {
		return axisXRenderer;
	}

	public void setAxisXRenderer(AbstractAxisRenderer2D axisXRenderer) {
		this.axisXRenderer = axisXRenderer;
	}

	public AbstractAxisRenderer2D getAxisYRenderer() {
		return axisYRenderer;
	}

	public void setAxisYRenderer(AbstractAxisRenderer2D axisYRenderer) {
		this.axisYRenderer = axisYRenderer;
	}

	public ShapeRenderer getShapeRenderer() {
		return shapeRenderer;
	}

	public void setShapeRenderer(ShapeRenderer shapeRenderer) {
		this.shapeRenderer = shapeRenderer;
	}
}

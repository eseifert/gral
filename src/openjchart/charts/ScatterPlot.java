package openjchart.charts;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.border.Border;

import openjchart.Drawable;
import openjchart.charts.axes.AbstractAxisRenderer2D;
import openjchart.charts.axes.Axis;
import openjchart.charts.axes.LinearRenderer2D;
import openjchart.charts.axes.AxisRenderer2D.Orientation;
import openjchart.data.DataMapper;
import openjchart.data.DataTable;

public class ScatterPlot extends Chart {
	private DataTable data;

	private AbstractAxisRenderer2D axisXRenderer;
	private AbstractAxisRenderer2D axisYRenderer;

	private Shape shape;
	private Color shapeColor;
	private boolean gridEnabled;

	private int colX;
	private int colY;
	private Number minX;
	private Number maxX;
	private Number minY;
	private Number maxY;
	private Axis axisX;
	private Axis axisY;
	private Drawable axisXComp;
	private Drawable axisYComp;

	public ScatterPlot(DataTable data, DataMapper mapper) {
		this.data = data;
		axisXRenderer = new LinearRenderer2D();
		axisYRenderer = new LinearRenderer2D();

		shape = new Rectangle2D.Float(-4f, -4f, 8f, 8f);
		shapeColor = Color.BLACK;
		gridEnabled = true;

		// Retrieve the columns mapped to X and Y axes
		colX = mapper.get(DataMapper.X);
		colY = mapper.get(DataMapper.Y);

		// Set the minimal and maximal value of the axes
		minX = data.getMin(colX);
		maxX = data.getMax(colX);
		minY = data.getMin(colY);
		maxY = data.getMax(colY);

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
				if (translateX == plotYMin) {
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
				if (translateY == plotXMin) {
					continue;
				}
				g2d.translate(0, translateY);
				g2d.draw(gridLineHoriz);
				g2d.setTransform(txOld);
			}
		}

		// Paint shapes
		g2d.setColor(shapeColor);
		for (int i = 0; i < data.getRowCount(); i++) {
			double valueX = data.get(colX, i).doubleValue();
			double valueY = data.get(colY, i).doubleValue();
			double translateX = w * axisXRenderer.getPos(axisX, valueX) + plotXMin;
			double translateY = plotYMax - h*axisYRenderer.getPos(axisY, valueY);
			g2d.translate(translateX, translateY);
			g2d.fill(shape);
			g2d.setTransform(txOld);
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

	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public Color getShapeColor() {
		return shapeColor;
	}

	public void setShapeColor(Color shapeColor) {
		this.shapeColor = shapeColor;
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
}

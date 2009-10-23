package openjchart.charts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import openjchart.Drawable;
import openjchart.charts.axes.Axis;
import openjchart.charts.axes.LinearRenderer2D;
import openjchart.charts.axes.AxisRenderer2D.Orientation;
import openjchart.data.DataMapper;
import openjchart.data.DataTable;

public class ScatterPlot implements Chart {
	private final LinearRenderer2D axisRenderer;

	private Shape shape;
	private Color shapeColor;
	private boolean gridEnabled;

	public ScatterPlot() {
		axisRenderer = new LinearRenderer2D();

		shape = new Rectangle2D.Float(-4f, -4f, 8f, 8f);
		shapeColor = Color.BLACK;
		gridEnabled = true;
	}

	@Override
	public JComponent getChartRenderer(final DataTable data, final DataMapper mapper) {
		// Retrieve the columns mapped to X and Y axes
		final int colX = mapper.get(DataMapper.X);
		final int colY = mapper.get(DataMapper.Y);

		// Set the minimal and maximal value of the axes
		final Number minX = data.getMin(colX);
		final Number maxX = data.getMax(colX);
		final Number minY = data.getMin(colY);
		final Number maxY = data.getMax(colY);

		// Create axes
		final Axis axisX = new Axis(minX, maxX);
		final Axis axisY = new Axis(minY, maxY);
		final Drawable axisXComp = axisRenderer.getRendererComponent(axisX, Orientation.HORIZONTAL);
		final Drawable axisYComp = axisRenderer.getRendererComponent(axisY, Orientation.VERTICAL);

		final PlotArea plotArea = new PlotArea() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				Graphics2D g2d = (Graphics2D) g;
				AffineTransform txOld = g2d.getTransform();
				Color colorDefault = g2d.getColor();

				// TODO Take the Component's insets into consideration
				/*Border border = getBorder();
				double scaleInsetsX = 0.0;
				double scaleInsetsY = 0.0;
				double transInsetsX = 0.0;
				double transInsetsY = 0.0;
				if (border != null) {
					Insets insets = border.getBorderInsets(this);
					scaleInsetsX = 1 - (insets.left + insets.right)/getWidth();
					scaleInsetsY = 1 - (insets.bottom + insets.top)/getHeight();
					transInsetsX = (insets.left + insets.right)/2;
					transInsetsY = (insets.bottom + insets.top)/2;
					g2d.scale(scaleInsetsX, scaleInsetsY);
					g2d.translate(transInsetsX, -transInsetsY);
				}*/

				double axisOffsetX = axisXComp.getHeight()/2.0;
				double axisOffsetY = axisYComp.getWidth()/2.0;
				double w = getWidth() - 1 - axisOffsetY;
				double h = getHeight() - 1 - axisOffsetX;
				double baseLineX = getHeight() - 1 - axisOffsetX;
				double baseLineY = axisOffsetY;
				if (gridEnabled) {
					// Draw gridX
					g2d.setColor(Color.LIGHT_GRAY);
					double minTick = axisRenderer.getMinTick(minX.doubleValue());
					double maxTick = axisRenderer.getMaxTick(maxX.doubleValue());
					Line2D gridLineVert = new Line2D.Double(0, 0, 0, h);
					for (double i = minTick; i < maxTick; i += axisRenderer.getTickSpacing()) {
						double translateX = w * axisX.getPos(i) + baseLineY;
						if (translateX == baseLineY) {
							continue;
						}
						g2d.translate(translateX, 0);
						g2d.draw(gridLineVert);
						g2d.setTransform(txOld);
					}

					// Draw gridY
					minTick = axisRenderer.getMinTick(minY.doubleValue());
					maxTick = axisRenderer.getMaxTick(maxY.doubleValue());
					Line2D gridLineHoriz = new Line2D.Double(0, 0, w, 0);
					for (double i = minTick; i <= maxTick; i += axisRenderer.getTickSpacing()) {
						double translateY = -h * axisY.getPos(i) + baseLineX;
						if (translateY == baseLineX) {
							continue;
						}
						g2d.translate(baseLineY, translateY);
						g2d.draw(gridLineHoriz);
						g2d.setTransform(txOld);
					}
				}

				// Paint shapes
				g2d.setColor(shapeColor);
				for (int i = 0; i < data.getRowCount(); i++) {
					double valueX = data.get(colX, i).doubleValue();
					double valueY = data.get(colY, i).doubleValue();
					double translateX = w * axisX.getPos(valueX) + baseLineY;
					double translateY = -h * axisY.getPos(valueY) + baseLineX;
					g2d.translate(translateX, translateY);
					g2d.fill(shape);
					g2d.setTransform(txOld);
				}
				g2d.setColor(colorDefault);
			}

			@Override
			public void setBounds(int x, int y, int width, int height) {
				super.setBounds(x, y, width, height);

				double xHeight = 50;
				double yWidth = 100;
				double xWidth = getWidth() - yWidth/2;
				double yHeight = getHeight() - xHeight/2;

				double xPos = yWidth/2;
				double yPos = getHeight() - xHeight;
				axisXComp.setBounds(new Rectangle2D.Double(xPos, yPos, xWidth, xHeight));

				xPos = 0.0;
				yPos = 0.0;
				axisYComp.setBounds(new Rectangle2D.Double(xPos, yPos, yWidth, yHeight));
			}
		};

		plotArea.addAxis(axisX, axisXComp);
		plotArea.addAxis(axisY, axisYComp);

		return plotArea;
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
}

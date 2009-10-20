package openjchart.charts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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

	private Shape valueShape;
	private Color shapeColor;

	public ScatterPlot() {
		axisRenderer = new LinearRenderer2D();

		valueShape = new Rectangle2D.Float(-4f, -4f, 8f, 8f);
		shapeColor = Color.BLACK;
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

				double axisOffsetX = axisXComp.getHeight()/2;
				double axisOffsetY = axisYComp.getWidth()/2;
				double baseLineX = getHeight() - axisOffsetX;
				double baseLineY = axisOffsetY;
				double w = getWidth() - 1 - axisOffsetY;
				double h = getHeight() - 1 - axisOffsetX;
				// Draw gridX
				g2d.setColor(Color.LIGHT_GRAY);
				double minTick = axisRenderer.getMinTick(minX.doubleValue());
				double maxTick = axisRenderer.getMaxTick(maxX.doubleValue());
				Line2D gridLineVert = new Line2D.Double(0, 0, 0, h);
				for (double i = minTick; i < maxTick; i += axisRenderer.getTickSpacing()) {
					double translateX = w * axisX.getPos(i) + baseLineY;
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
					g2d.translate(baseLineY, translateY);
					g2d.draw(gridLineHoriz);
					g2d.setTransform(txOld);
				}

				// Paint shapes
				g2d.setColor(shapeColor);
				for (int i = 0; i < data.getRowCount(); i++) {
					double valueX = data.get(colX, i).doubleValue();
					double valueY = data.get(colY, i).doubleValue();
					double translateX = w * axisX.getPos(valueX) + baseLineY;
					double translateY = -h * axisY.getPos(valueY) + baseLineX;
					g2d.translate(translateX, translateY);
					g2d.fill(valueShape);
					g2d.setTransform(txOld);
				}
				g2d.setColor(colorDefault);
			}
		};

		// Do axis layout
		plotArea.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);

				double xHeight = 50;
				double yWidth = 100;
				double xWidth = plotArea.getWidth() - yWidth/2;
				double yHeight = plotArea.getHeight() - xHeight/2;

				double x = yWidth/2;
				double y = plotArea.getHeight() - xHeight;
				axisXComp.setBounds(new Rectangle2D.Double(x, y, xWidth, xHeight));

				x = 0.0;
				y = 0.0;
				axisYComp.setBounds(new Rectangle2D.Double(x, y, yWidth, yHeight));
			}
		});

		plotArea.addAxis(axisX, axisXComp);
		plotArea.addAxis(axisY, axisYComp);

		return plotArea;
	}
}

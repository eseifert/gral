package openjchart.plots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import openjchart.AbstractDrawable;
import openjchart.Drawable;
import openjchart.data.DataSource;
import openjchart.plots.axes.Axis;
import openjchart.plots.shapes.AbstractShapeRenderer;

public class BarPlot extends XYPlot {
	
	protected class BarRenderer extends AbstractShapeRenderer {

		@Override
		public Drawable getShape(final DataSource data, final int row) {
			return new AbstractDrawable() {
				@Override
				public void draw(Graphics2D g2d) {
					Color colorOld = g2d.getColor();

					g2d.setColor(BarRenderer.this.<Color>getSetting(KEY_COLOR));
					
					//double valueX = data.get(0, row).doubleValue();
					double valueY = data.get(1, row).doubleValue();

					//Axis axisX = getAxis(Axis.X);
					Axis axisY = getAxis(Axis.Y);
					
					double barXMax = getAxisYRenderer().worldToViewPos(axisY, axisY.getMin()).getY();
					double barXMin = getAxisYRenderer().worldToViewPos(axisY, valueY).getY();
					double barWidth = 50.0;  // TODO: Use separate column for bar width
					double barHeight = Math.abs(barXMax - barXMin);

					Shape shape = new Rectangle2D.Double(-barWidth/2.0, 0.0, barWidth, barHeight);
					g2d.fill(shape);
					
					g2d.setColor(colorOld);
				}
			};
		}
		
	}

	public BarPlot(DataSource... data) {
		super(data);

		setSettingDefault(KEY_GRID_X, false);

		for (DataSource s : data) {
			setLineRenderer(s, null);
		}
		setShapeRenderer(new BarRenderer());
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
}

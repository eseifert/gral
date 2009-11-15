package openjchart.plots;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import openjchart.AbstractDrawable;
import openjchart.Drawable;
import openjchart.data.DataSource;
import openjchart.plots.axes.Axis;
import openjchart.plots.axes.AxisRenderer2D;
import openjchart.plots.shapes.AbstractShapeRenderer;
import openjchart.plots.shapes.ShapeRenderer;

public class BarPlot extends XYPlot {
	public static final String KEY_BAR_WIDTH = "barplot.barWidth";

	protected class BarRenderer extends AbstractShapeRenderer {
		@Override
		public Drawable getShape(final DataSource data, final int row) {
			return new AbstractDrawable() {
				@Override
				public void draw(Graphics2D g2d) {
					Number valueX = data.get(0, row);
					Number valueY = data.get(1, row);
					Axis axisX = getAxis(Axis.X);
					Axis axisY = getAxis(Axis.Y);

					AxisRenderer2D axisXRenderer = BarPlot.this.getSetting(KEY_RENDERER_AXIS_X);
					AxisRenderer2D axisYRenderer = BarPlot.this.getSetting(KEY_RENDERER_AXIS_Y);
					double barWidthRel = BarPlot.this.getSetting(KEY_BAR_WIDTH);
					double barAlign = 0.5;
					double barX = axisXRenderer.worldToViewPos(axisX, valueX).getX();
					double barXMin = axisXRenderer.worldToViewPos(axisX, valueX.doubleValue() - barWidthRel*barAlign).getX();
					double barXMax = axisXRenderer.worldToViewPos(axisX, valueX.doubleValue() + barWidthRel*barAlign).getX();
					double barYMin = axisYRenderer.worldToViewPos(axisY, valueY).getY();
					double barYMax = axisYRenderer.worldToViewPos(axisY, axisY.getMin()).getY();

					double barWidth = Math.abs(barXMax - barXMin);
					double barHeight = Math.abs(barYMax - barYMin);

					Shape shape = new Rectangle2D.Double(barXMin - barX, 0.0, barWidth, barHeight);

					Color colorOld = g2d.getColor();
					g2d.setColor(BarRenderer.this.<Color>getSetting(KEY_COLOR));
					g2d.fill(shape);
					g2d.setColor(colorOld);
				}
			};
		}

	}

	public BarPlot(DataSource... data) {
		super(data);

		setSettingDefault(KEY_GRID_X, false);
		setSettingDefault(KEY_BAR_WIDTH, 1.0);

		ShapeRenderer shapeRendererDefault = new BarRenderer();
		for (DataSource s : data) {
			setLineRenderer(s, null);
			setShapeRenderer(s, shapeRendererDefault);
		}
	}
}

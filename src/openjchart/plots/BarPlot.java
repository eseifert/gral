/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.plots;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import openjchart.AbstractDrawable;
import openjchart.Drawable;
import openjchart.data.DataSource;
import openjchart.data.Row;
import openjchart.plots.axes.Axis;
import openjchart.plots.axes.AxisRenderer2D;
import openjchart.plots.shapes.AbstractShapeRenderer;
import openjchart.plots.shapes.ShapeRenderer;
import openjchart.util.GraphicsUtils;
import openjchart.util.MathUtils;

public class BarPlot extends XYPlot {
	public static final String KEY_BAR_WIDTH = "barplot.barWidth";

	protected class BarRenderer extends AbstractShapeRenderer {
		@Override
		public Drawable getShape(final Row row) {
			//final Drawable plotArea = BarPlot.this.plotArea;
			return new AbstractDrawable() {
				@Override
				public void draw(Graphics2D g2d) {
					// TODO: Translate?
					Shape shape = getShapePath(row);
					Paint paint = getSetting(KEY_COLOR);
					Rectangle2D paintBoundaries = null;
					/*
					// TODO: Optionally fill all bars with a single paint:
					AffineTransform txOld = g2d.getTransform();
					Rectangle2D shapeBounds = shape.getBounds2D();
					Rectangle2D paintBoundaries = plotArea.getBounds();
					paintBoundaries = new Rectangle2D.Double(
						shapeBounds.getX(), paintBoundaries.getY() - txOld.getTranslateY(),
						shapeBounds.getWidth(), paintBoundaries.getHeight()
					);
					*/
					GraphicsUtils.fillPaintedShape(g2d, shape, paint, paintBoundaries);
				}
			};
		}

		@Override
		public Shape getShapePath(Row row) {
			double valueX = row.get(0).doubleValue();
			double valueY = row.get(1).doubleValue();
			AxisRenderer2D axisXRenderer = BarPlot.this.getSetting(KEY_AXIS_X_RENDERER);
			AxisRenderer2D axisYRenderer = BarPlot.this.getSetting(KEY_AXIS_Y_RENDERER);
			Axis axisX = getAxis(Axis.X);
			Axis axisY = getAxis(Axis.Y);
			double axisYMin = axisY.getMin().doubleValue();
			double axisYMax = axisY.getMax().doubleValue();
			double axisYOrigin = MathUtils.limit(0.0, axisYMin, axisYMax);

			if ((axisYOrigin <= axisYMin && valueY <= axisYMin) || (axisYOrigin >= axisYMax && valueY >= axisYMax)) {
				return new GeneralPath();
			}

			double barWidthRel = BarPlot.this.getSetting(KEY_BAR_WIDTH);
			double barAlign = 0.5;

			double barXMin = axisXRenderer.getPosition(axisX, valueX - barWidthRel*barAlign, true, false).getX();
			double barXMax = axisXRenderer.getPosition(axisX, valueX + barWidthRel*barAlign, true, false).getX();

			double barYVal = axisYRenderer.getPosition(axisY, valueY, true, false).getY();
			double barYOrigin = axisYRenderer.getPosition(axisY, axisYOrigin, true, false).getY();
			double barYMin = Math.min(barYVal, barYOrigin);
			double barYMax = Math.max(barYVal, barYOrigin);

			double barWidth = Math.abs(barXMax - barXMin);
			double barHeight = Math.abs(barYMax - barYMin);

			double barX = axisXRenderer.getPosition(axisX, valueX, true, false).getX();
			double barY = (barYMax == barYOrigin) ? 0.0 : -barHeight;

			Shape shape = new Rectangle2D.Double(barXMin - barX, barY, barWidth, barHeight);
			return shape;
		}
	}

	public BarPlot(DataSource... data) {
		super(data);

		getPlotArea().setSettingDefault(XYPlotArea2D.KEY_GRID_X, false);
		setSettingDefault(KEY_BAR_WIDTH, 1.0);

		ShapeRenderer shapeRendererDefault = new BarRenderer();
		for (DataSource s : data) {
			setLineRenderer(s, null);
			setShapeRenderer(s, shapeRendererDefault);
		}
	}
}

/**
 * GRAL: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.plots;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import de.erichseifert.gral.AbstractDrawable;
import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer2D;
import de.erichseifert.gral.plots.points.AbstractPointRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.Settings.Key;


/**
 * Class that displays data in a bar plot.
 */
public class BarPlot extends XYPlot {
	/** Key for specifying the relative width of the bars. */
	public static final Key BAR_WIDTH = new Key("barplot.barWidth");

	/**
	 * Class that renders a bar in a bar plot.
	 */
	protected static class BarRenderer extends AbstractPointRenderer {
		private BarPlot plot;

		public BarRenderer(BarPlot plot) {
			this.plot = plot;
		}

		@Override
		public Drawable getPoint(final Axis axisY, final AxisRenderer2D axisYRenderer, final Row row) {
			//final Drawable plotArea = BarPlot.this.plotArea;
			return new AbstractDrawable() {
				@Override
				public void draw(Graphics2D g2d) {
					// TODO: Translate?
					Shape point = getPointPath(row);
					Paint paint = getSetting(COLOR);
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
					GraphicsUtils.fillPaintedShape(g2d, point, paint, paintBoundaries);

					if (BarRenderer.this.<Boolean>getSetting(VALUE_DISPLAYED)) {
						drawValue(g2d, point, row.get(1).doubleValue());
					}
				}
			};
		}

		@Override
		public Shape getPointPath(Row row) {
			double valueX = row.get(0).doubleValue();
			double valueY = row.get(1).doubleValue();
			AxisRenderer2D axisXRenderer = plot.getSetting(BarPlot.AXIS_X_RENDERER);
			AxisRenderer2D axisYRenderer = plot.getSetting(BarPlot.AXIS_Y_RENDERER);
			Axis axisX = plot.getAxis(Axis.X);
			Axis axisY = plot.getAxis(Axis.Y);
			double axisYMin = axisY.getMin().doubleValue();
			double axisYMax = axisY.getMax().doubleValue();
			double axisYOrigin = MathUtils.limit(0.0, axisYMin, axisYMax);

			if ((axisYOrigin <= axisYMin && valueY <= axisYMin) || (axisYOrigin >= axisYMax && valueY >= axisYMax)) {
				return new GeneralPath();
			}

			double barWidthRel = plot.<Double>getSetting(BarPlot.BAR_WIDTH);
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

	/**
	 * Creates a new <code>BarPlot</code> object with the specified <code>DataSource</code>s.
	 * @param data Data to be displayed.
	 */
	public BarPlot(DataSource... data) {
		super(data);

		getPlotArea().setSettingDefault(XYPlotArea2D.GRID_MAJOR_X, false);
		setSettingDefault(BAR_WIDTH, 1.0);

		PointRenderer shapeRendererDefault = new BarRenderer(this);
		for (DataSource s : data) {
			setLineRenderer(s, null);
			setPointRenderer(s, shapeRendererDefault);
		}
	}

}

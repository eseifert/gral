/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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
import java.awt.geom.Rectangle2D;

import de.erichseifert.gral.AbstractDrawable;
import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.DrawingContext;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.points.AbstractPointRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.PointND;


/**
 * <p>Class that displays data in a bar plot.</p>
 * <p>To create a new <code>BarPlot</code> simply create a new instance
 * using one or more data sources. Example:</p>
 * <pre>
 * DataTable data = new DataTable(Integer.class, Double.class);
 * data.add(2010, -5.00);
 * data.add(2011,  3.25);
 * data.add(2012, -0.50);
 * data.add(2012,  4.00);
 *
 * BarPlot plot = new BarPlot(data);
 * </pre>
 */
public class BarPlot extends XYPlot {
	/** Key for specifying a {@link java.lang.Number} value for the relative
	width of the bars. */
	public static final Key BAR_WIDTH = new Key("barplot.barWidth"); //$NON-NLS-1$

	/**
	 * Class that renders a bar in a bar plot.
	 */
	protected static class BarRenderer extends AbstractPointRenderer {
		/** Bar plot this renderer is associated to. */
		private final BarPlot plot;

		/**
		 * Constructor that creates a new instance and initializes it with a
		 * plot as data provider.
		 * @param plot Data provider.
		 */
		public BarRenderer(BarPlot plot) {
			this.plot = plot;
		}

		@Override
		public Drawable getPoint(final Axis axisY,
				final AxisRenderer axisYRenderer, final Row row) {
			//final Drawable plotArea = BarPlot.this.plotArea;
			return new AbstractDrawable() {
				@Override
				public void draw(DrawingContext context) {
					// TODO Translate?
					Shape point = getPointPath(row);
					Paint paint = BarRenderer.this.getSetting(COLOR);
					Rectangle2D paintBoundaries = null;
					Graphics2D graphics = context.getGraphics();
					/*
					// TODO Optionally fill all bars with a single paint:
					AffineTransform txOld = graphics.getTransform();
					Rectangle2D shapeBounds = shape.getBounds2D();
					Rectangle2D paintBoundaries = plotArea.getBounds();
					paintBoundaries = new Rectangle2D.Double(
						shapeBounds.getX(), paintBoundaries.getY() - txOld.getTranslateY(),
						shapeBounds.getWidth(), paintBoundaries.getHeight()
					);
					*/
					GraphicsUtils.fillPaintedShape(
							graphics, point, paint, paintBoundaries);

					if (BarRenderer.this.<Boolean>getSetting(VALUE_DISPLAYED)) {
						drawValue(context, point, row.get(1).doubleValue());
					}
				}
			};
		}

		@Override
		public Shape getPointPath(Row row) {
			double valueX = row.get(0).doubleValue();
			double valueY = row.get(1).doubleValue();
			Axis axisX = plot.getAxis(AXIS_X);
			Axis axisY = plot.getAxis(AXIS_Y);
			AxisRenderer axisXRenderer = plot.getAxisRenderer(AXIS_X);
			AxisRenderer axisYRenderer = plot.getAxisRenderer(AXIS_Y);
			double axisYOrigin = 0.0;

			/*
			double axisYMin = axisY.getMin().doubleValue();
			double axisYMax = axisY.getMax().doubleValue();
			double axisYOrigin = MathUtils.limit(0.0, axisYMin, axisYMax);

			if ((axisYOrigin <= axisYMin && valueY <= axisYMin) ||
			    (axisYOrigin >= axisYMax && valueY >= axisYMax)) {
				return new Path2D.Double();
			}
			//*/

			double barWidthRel =
				plot.<Number>getSetting(BarPlot.BAR_WIDTH).doubleValue();
			double barAlign = 0.5;

			double barXMin = axisXRenderer
				.getPosition(axisX, valueX - barWidthRel*barAlign, true, false)
				.get(PointND.X);
			double barXMax = axisXRenderer
				.getPosition(axisX, valueX + barWidthRel*barAlign, true, false)
				.get(PointND.X);

			double barYVal = axisYRenderer.getPosition(
					axisY, valueY, true, false).get(PointND.Y);
			double barYOrigin = axisYRenderer.getPosition(
					axisY, axisYOrigin, true, false).get(PointND.Y);
			double barYMin = Math.min(barYVal, barYOrigin);
			double barYMax = Math.max(barYVal, barYOrigin);

			double barWidth = Math.abs(barXMax - barXMin);
			double barHeight = Math.abs(barYMax - barYMin);

			double barX = axisXRenderer.getPosition(
					axisX, valueX, true, false).get(PointND.X);
			double barY = (barYMax == barYOrigin) ? 0.0 : -barHeight;

			Shape shape = new Rectangle2D.Double(
					barXMin - barX, barY, barWidth, barHeight);
			return shape;
		}
	}

	/**
	 * Creates a new instance and initializes it with the specified
	 * data sources.
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

/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2012 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael[at]erichseifert.de>
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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.colors.ColorMapper;
import de.erichseifert.gral.plots.points.DefaultPointRenderer2D;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.plots.settings.Key;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Location;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.PointND;


/**
 * <p>Class that displays data in a bar plot.</p>
 * <p>To create a new {@code BarPlot} simply create a new instance
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
	/** Key for specifying a {@link Number} value for the  width of the bars in
	axis coordinates. */
	public static final Key BAR_WIDTH =
		new Key("barplot.bar.width"); //$NON-NLS-1$
	/** Key for specifying a {@link Number} value for the minimum height of the
	bars in view units (e.g. pixels on screen). */
	public static final Key BAR_HEIGHT_MIN =
		new Key("barplot.bar.heightMin"); //$NON-NLS-1$
	/** Key for specifying a {@link Boolean} value which defines whether
	painting should happen over all bars at once, otherwise each bar will be
	filled independently. */
	public static final Key PAINT_ALL_BARS =
		new Key("barplot.bar.paintAll"); //$NON-NLS-1$

	/**
	 * Class that renders a bar in a bar plot.
	 */
	public static class BarRenderer extends DefaultPointRenderer2D {
		/** Key for specifying a {@link java.awt.Stroke} instance used to paint
		the outline of the point shape. */
		public static final Key STROKE = new Key("barplot.bar.stroke"); //$NON-NLS-1$
		/** Key for specifying a {@link java.awt.Paint} instance used to fill
		the point shape. */
		public static final Key STROKE_COLOR = new Key("barplot.bar.stroke.color"); //$NON-NLS-1$

		/** Bar plot this renderer is associated to. */
		private final BarPlot plot;

		/**
		 * Constructor that creates a new instance and initializes it with a
		 * plot as data provider.
		 * @param plot Data provider.
		 */
		public BarRenderer(BarPlot plot) {
			this.plot = plot;
			setSettingDefault(VALUE_LOCATION, Location.NORTH);
			setSettingDefault(STROKE, null);
			setSettingDefault(STROKE_COLOR, Color.BLACK);
		}

		/**
		 * Returns the graphical representation to be drawn for the specified data
		 * value.
		 * @param axis that is used to project the point.
		 * @param axisRenderer Renderer for the axis.
		 * @param row Data row containing the point.
		 * @param col Index of the column that will be projected on the axis.
		 * @return Component that can be used to draw the point
		 */
		@Override
		public Drawable getPoint(final Axis axis,
				final AxisRenderer axisRenderer, final Row row, final int col) {
			return new AbstractDrawable() {
				public void draw(DrawingContext context) {
					PointRenderer renderer = BarRenderer.this;
					Shape point = getPointPath(row);
					Rectangle2D paintBoundaries = null;
					Graphics2D graphics = context.getGraphics();

					ColorMapper colors = renderer.<ColorMapper>getSetting(COLOR);
					Paint paint = colors.get(row.getIndex());

					Boolean paintAllBars = plot.getSetting(PAINT_ALL_BARS);
					if (paintAllBars != null && paintAllBars.booleanValue()) {
						AffineTransform txOld = graphics.getTransform();
						Rectangle2D shapeBounds = point.getBounds2D();
						paintBoundaries = plot.getPlotArea().getBounds();
						paintBoundaries = new Rectangle2D.Double(
							shapeBounds.getX(), paintBoundaries.getY() - txOld.getTranslateY(),
							shapeBounds.getWidth(), paintBoundaries.getHeight()
						);
					}

					GraphicsUtils.fillPaintedShape(
						graphics, point, paint, paintBoundaries);

					Stroke stroke = renderer.<Stroke>getSetting(STROKE);
					Paint strokePaint = renderer.<Paint>getSetting(STROKE_COLOR);
					if (stroke != null && strokePaint != null) {
						GraphicsUtils.drawPaintedShape(
							graphics, point, strokePaint, null, stroke);
					}

					if (renderer.<Boolean>getSetting(VALUE_DISPLAYED)) {
						int colValue = renderer.<Integer>getSetting(VALUE_COLUMN);
						drawValueLabel(context, point, row, colValue);
					}
				}
			};
		}

		/**
		 * Returns a {@code Shape} instance that can be used
		 * for further calculations.
		 * @param row Data row containing the point.
		 * @return Outline that describes the point's shape.
		 */
		@Override
		public Shape getPointPath(Row row) {
			int colX = 0;
			int colY = 1;

			if (!row.isColumnNumeric(colX) || !row.isColumnNumeric(colY)) {
				return null;
			}

			double valueX = ((Number) row.get(colX)).doubleValue();
			double valueY = ((Number) row.get(colY)).doubleValue();
			Axis axisX = plot.getAxis(AXIS_X);
			Axis axisY = plot.getAxis(AXIS_Y);
			AxisRenderer axisXRenderer = plot.getAxisRenderer(AXIS_X);
			AxisRenderer axisYRenderer = plot.getAxisRenderer(AXIS_Y);
			double axisYOrigin = 0.0;

			double barWidthRel = 1.0;
			Number barWidthRelObj = plot.<Number>getSetting(BarPlot.BAR_WIDTH);
			if (barWidthRelObj != null) {
				barWidthRel = barWidthRelObj.doubleValue();
			}
			double barAlign = 0.5;

			// Sanity checks
			if (barWidthRel<0.0) {
				barWidthRel=0.0;
			}

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

			// position of the bar's left edge in screen coordinates
			double barX = axisXRenderer.getPosition(
				axisX, valueX, true, false).get(PointND.X);
			// position of the bar's upper edge in screen coordinates
			// (the origin of the screen y axis is at the top)
			boolean barAboveAxis = barYMax == barYOrigin;
			double barY = barAboveAxis ? 0.0 : -barHeight;

			Number barHeightMinObj = plot.<Number>getSetting(BAR_HEIGHT_MIN);
			if (barHeightMinObj != null) {
				double barHeightMin = barHeightMinObj.doubleValue();
				if (MathUtils.isCalculatable(barHeightMin) && barHeightMin > 0.0 &&
						barHeight < barHeightMin) {
					if (barAboveAxis) {
						barY += -barHeightMin + barHeight;
					}
					barHeight = barHeightMin;
				}
			}

			Shape shape = getBarShape(
				barXMin - barX, barY, barWidth, barHeight);
			return shape;
		}

		/**
		 * Returns the shape for a bar. The default shape is defined in the
		 * settings, but more complex shapes may be implemented by overriding
		 * this method.
		 * @param x Distance from the left in view units (e.g. pixels).
		 * @param y Distance from the top in view units (e.g. pixels).
		 * @param width Width of the shape in view units (e.g. pixels).
		 * @param height Height of the shape in view units (e.g. pixels).
		 * @return A geometric shape for displaying a bar in bar plot.
		 */
		protected Shape getBarShape(double x, double y, double width, double height) {
			Shape shape = getSetting(SHAPE);
			Rectangle2D shapeBounds = shape.getBounds2D();

			AffineTransform tx = new AffineTransform();
			tx.translate(x, y);
			tx.scale(width/shapeBounds.getWidth(), height/shapeBounds.getHeight());
			tx.translate(-shapeBounds.getMinX(), -shapeBounds.getMinY());

			Shape shapeTransformed = tx.createTransformedShape(shape);
			return shapeTransformed;
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
		setSettingDefault(BAR_HEIGHT_MIN, 0.0);
		setSettingDefault(PAINT_ALL_BARS, false);

		PointRenderer pointRenderer = new BarRenderer(this);
		for (DataSource s : data) {
			setLineRenderer(s, null);
			setPointRenderer(s, pointRenderer);
		}
	}

	@Override
	protected void autoScaleAxes() {
		List<DataSource> data = getData();
		if (data.isEmpty()) {
			return;
		}

		super.autoScaleAxes();

		Axis axisX = getAxis(AXIS_X);
		if (axisX != null) {
			double xMin = getAxisMin(AXIS_X);
			double xMax = getAxisMax(AXIS_X);
			double xMargin = (xMax - xMin)/data.get(0).getRowCount()/2.0;
			axisX.setRange(xMin - xMargin, xMax + xMargin);
		}
	}
}

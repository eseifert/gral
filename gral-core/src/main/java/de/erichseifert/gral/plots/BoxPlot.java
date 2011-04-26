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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import de.erichseifert.gral.AbstractDrawable;
import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.DrawingContext;
import de.erichseifert.gral.data.Column;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.points.AbstractPointRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.PointND;


/**
 * <p>Class that displays data as a box-and-whisker plot showing summaries of
 * important statistical values.</p>
 * <p>To create a new <code>BoxPlot</code> simply create a new instance using
 * a data source. Example:</p>
 * <pre>
 * DataTable data = new DataTable(Double.class, Double.class);
 * data.add(10.98, -12.34);
 * data.add( 7.65,  45.67);
 * data.add(43.21,  89.01);
 *
 * BoxPlot plot = new BoxPlot(data);
 * </pre>
 */
public class BoxPlot extends XYPlot {
	/** Key for specifying a {@link java.lang.Number} value for the relative
	width of the boxes. */
	public static final Key BOX_WIDTH = new Key("boxplot.box.width"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	paint the background of the boxes. */
	public static final Key BOX_BACKGROUND =
		new Key("boxplot.box.background"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	paint the border of the boxes. */
	public static final Key BOX_COLOR =
		new Key("boxplot.box.background"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Stroke} instance to be used to
	paint the border of the boxes. */
	public static final Key BOX_BORDER =
		new Key("boxplot.box.border"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	paint the lines of the whiskers. */
	public static final Key WHISKER_COLOR =
		new Key("boxplot.whisker.color"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Stroke} instance to be used to
	paint the lines of the whiskers. */
	public static final Key WHISKER_STROKE =
		new Key("boxplot.whisker.stroke"); //$NON-NLS-1$

	/**
	 * Class that renders a box and its whiskers in a box-and-whisker plot.
	 */
	protected static class BoxWhiskerRenderer extends AbstractPointRenderer {
		/** Bar plot this renderer is associated to. */
		private final BoxPlot plot;

		/**
		 * Constructor that creates a new instance and initializes it with a
		 * plot as data provider.
		 * @param plot Data provider.
		 */
		public BoxWhiskerRenderer(BoxPlot plot) {
			this.plot = plot;
		}

		@Override
		public Drawable getPoint(final Axis axisY,
				final AxisRenderer axisYRenderer, final Row row) {
			//final Drawable plotArea = BarPlot.this.plotArea;
			return new AbstractDrawable() {
				@Override
				public void draw(DrawingContext context) {
					Graphics2D graphics = context.getGraphics();

					double valueX       = row.get(0).doubleValue();
					double valueYMin    = row.get(2).doubleValue();
					double valueYQ1     = row.get(3).doubleValue();
					double valueYMedian = row.get(1).doubleValue();
					double valueYQ3     = row.get(4).doubleValue();
					double valueYMax    = row.get(5).doubleValue();

					Axis axisX = plot.getAxis(AXIS_X);
					Axis axisY = plot.getAxis(AXIS_Y);
					AxisRenderer axisXRenderer = plot.getAxisRenderer(AXIS_X);
					AxisRenderer axisYRenderer = plot.getAxisRenderer(AXIS_Y);

					double boxWidthRel =
						plot.<Number>getSetting(BoxPlot.BOX_WIDTH).doubleValue();
					double boxAlign = 0.5;

					double boxXMin = axisXRenderer
						.getPosition(axisX, valueX - boxWidthRel*boxAlign, true, false)
						.get(PointND.X);
					double boxXMax = axisXRenderer
						.getPosition(axisX, valueX + boxWidthRel*boxAlign, true, false)
						.get(PointND.X);

					double boxYMin = axisYRenderer.getPosition(
							axisY, valueYMin, true, false).get(PointND.Y);
					double boxYQ1 = axisYRenderer.getPosition(
							axisY, valueYQ1, true, false).get(PointND.Y);
					double boxY = axisYRenderer.getPosition(
							axisY, valueYMedian, true, false).get(PointND.Y);
					double boxYQ3 = axisYRenderer.getPosition(
							axisY, valueYQ3, true, false).get(PointND.Y);
					double boxYMax = axisYRenderer.getPosition(
							axisY, valueYMax, true, false).get(PointND.Y);

					double boxWidth = Math.abs(boxXMax - boxXMin);
					double boxHeight = Math.abs(boxYQ3 - boxYQ1);

					double boxX = axisXRenderer.getPosition(
							axisX, valueX, true, false).get(PointND.X);

					// Draw inter-quartile-range box
					Paint paintBox = plot.getSetting(BOX_BACKGROUND);
					Paint paintStrokeBox = plot.getSetting(BOX_COLOR);
					Stroke strokeBox = plot.getSetting(BOX_BORDER);
					Rectangle2D box = new Rectangle2D.Double(
						boxXMin - boxX, boxYQ3 - boxY, boxWidth, boxHeight);
					GraphicsUtils.fillPaintedShape(graphics, box, paintBox, box.getBounds2D());
					GraphicsUtils.drawPaintedShape(graphics, box, paintStrokeBox,
							box.getBounds2D(), strokeBox);

					// Bars and whiskers
					Paint paintWhisker = plot.getSetting(WHISKER_COLOR);
					Stroke strokeWhisker = plot.getSetting(WHISKER_STROKE);

					Line2D whiskerMax = new Line2D.Double(
						0.0, boxYQ3 - boxY,
						0.0, boxYMax - boxY
					);
					Line2D whiskerMin = new Line2D.Double(
						0.0, boxYQ1 - boxY,
						0.0, boxYMin - boxY
					);
					Line2D barMax = new Line2D.Double(
						boxXMin - boxX, boxYMax - boxY,
						boxXMax - boxX, boxYMax - boxY
					);
					Line2D barMin = new Line2D.Double(
						boxXMin - boxX, boxYMin - boxY,
						boxXMax - boxX, boxYMin - boxY
					);
					Line2D barMedian = new Line2D.Double(
						boxXMin - boxX, 0.0,
						boxXMax - boxX, 0.0
					);

					Paint paintOld = graphics.getPaint();
					Stroke strokeOld = graphics.getStroke();
					graphics.setPaint(paintWhisker);
					graphics.setStroke(strokeWhisker);
					graphics.draw(whiskerMax);
					graphics.draw(whiskerMin);
					graphics.draw(barMax);
					graphics.draw(barMin);
					graphics.draw(barMedian);
					graphics.setStroke(strokeOld);
					graphics.setPaint(paintOld);
				}
			};
		}

		@Override
		public Shape getPointPath(Row row) {
			return null;
		}
	}

	/**
	 * Initializes a new box-and-whisker plot with the specified data source.
	 * @param data Data to be displayed.
	 */
	public BoxPlot(DataSource data) {
		setSettingDefault(BOX_WIDTH, 0.75);
		setSettingDefault(BOX_BACKGROUND, Color.WHITE);
		setSettingDefault(BOX_COLOR, Color.BLACK);
		setSettingDefault(BOX_BORDER, new BasicStroke(1f));
		setSettingDefault(WHISKER_COLOR, Color.BLACK);
		setSettingDefault(WHISKER_STROKE, new BasicStroke(1f));

		getPlotArea().setSettingDefault(XYPlotArea2D.GRID_MAJOR_X, false);
		getAxisRenderer(AXIS_X).setSetting(AxisRenderer.TICKS, false);
		getAxisRenderer(AXIS_Y).setSetting(AxisRenderer.INTERSECTION, -Double.MAX_VALUE);

		// Generate data source with statistical values for each column
		DataTable stats = new DataTable(Integer.class, Double.class,
				Double.class, Double.class, Double.class, Double.class);
		for (int c = 0; c < data.getColumnCount(); c++) {
			Column col = data.getColumn(c);
			stats.add(
				c,
				col.getStatistics(Statistics.MEDIAN),
				col.getStatistics(Statistics.MIN),
				col.getStatistics(Statistics.QUARTILE_1),
				col.getStatistics(Statistics.QUARTILE_3),
				col.getStatistics(Statistics.MAX)
			);
		}

		// Set generated data series
		add(stats);
		getAxis(AXIS_X).setRange(-0.5, data.getColumnCount() - 0.5);
		getAxis(AXIS_Y).setRange(
			1.2*stats.getColumn(2).getStatistics(Statistics.MIN),
			1.2*stats.getColumn(5).getStatistics(Statistics.MAX)
		);

		// Adjust rendering
		PointRenderer pointRenderer = new BoxWhiskerRenderer(this);
		setLineRenderer(stats, null);
		setPointRenderer(stats, pointRenderer);
	}

}

/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <mseifert[at]error-reports.org>
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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import de.erichseifert.gral.data.Column;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.colors.ColorMapper;
import de.erichseifert.gral.plots.colors.ContinuousColorMapper;
import de.erichseifert.gral.plots.colors.SingleColor;
import de.erichseifert.gral.plots.legends.AbstractLegend;
import de.erichseifert.gral.plots.legends.ValueLegend;
import de.erichseifert.gral.plots.points.AbstractPointRenderer;
import de.erichseifert.gral.plots.points.PointData;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.PointND;
import de.erichseifert.gral.util.SerializationUtils;


/**
 * <p>Class that displays data as a box-and-whisker plot showing summaries of
 * important statistical values. The data source must provide six columns to
 * the {@code BoxPlot}:</p>
 * <ul>
 *   <li>Box position (for multiple boxes)</li>
 *   <li>Position of the center bar (e.g. median)</li>
 *   <li>Length of the lower whisker and position of the bottom bar
 *   (e.g. minimum)</li>
 *   <li>Position of the bottom edge of the box (e.g. first quartile)</li>
 *   <li>Position of the top edge of the box (e.g. third quartile)</li>
 *   <li>Length of the upper whisker and position of the top bar
 *   (e.g. maximum)</li>
 * </ul>
 * <p>The utility method {@link #createBoxData(DataSource)} can be used to
 * obtain common statistics for these properties from the each column of an
 * existing data source.</p>
 *
 * <p>To create a new {@code BoxPlot} simply create a new instance using
 * a data source. Example:</p>
 * <pre>
 * DataTable data = new DataTable(Double.class, Double.class);
 * data.add(10.98, -12.34);
 * data.add( 7.65,  45.67);
 * data.add(43.21,  89.01);
 * DataSource boxData = BoxPlot.createBoxData(data);
 * BoxPlot plot = new BoxPlot(boxData);
 * </pre>
 */
public class BoxPlot extends XYPlot {
	/** Version id for serialization. */
	private static final long serialVersionUID = -3069831535208696337L;

	/**
	 * Class that renders a box and its whiskers in a box-and-whisker plot.
	 */
	public static class BoxWhiskerRenderer extends AbstractPointRenderer {
		/** Version id for serialization. */
		private static final long serialVersionUID = 2944482729753981341L;

		/** Index of the column for the horizontal position of a box. */
		private int positionColumn;
		/** Index of the column for the vertical center bar. */
		private int centerBarColumn;
		/** Index of the column for the lower vertical bar. */
		private int bottomBarColumn;
		/** Index of the column for the lower end of the box. */
		private int boxBottomColumn;
		/** Index of the column for the upper end of the box. */
		private int boxTopColumn;
		/** Index of the column for the upper vertical bar. */
		private int topBarColumn;

		/** Relative width of each box. 1.0 means boxes touch each other. */
		private double boxWidth;
		/** Color mapping to fill the background of the boxes. */
		private ColorMapper boxBackground;
		/** Paint to fill the border of the boxes. */
		private Paint boxBorderColor;
		/** Stroke to draw the border of the boxes. */
		private transient Stroke boxBorderStroke;

		/** Paint to fill the border of the whiskers. */
		private Paint whiskerColor;
		/** Stroke to draw the border of the whiskers. */
		private transient Stroke whiskerStroke;

		/** Relative width of the vertical bars. */
		private double barWidth;
		/** Paint to fill the center bar. */
		private Paint centerBarColor;
		/** Stroke to draw the center bar. */
		private transient Stroke centerBarStroke;

		/**
		 * Constructor that creates a new instance and initializes it with a
		 * plot as data provider.
		 */
		public BoxWhiskerRenderer() {
			positionColumn = 0;
			centerBarColumn = 1;
			bottomBarColumn = 2;
			boxBottomColumn = 3;
			boxTopColumn = 4;
			topBarColumn = 5;
			boxWidth = 0.75;
			boxBackground = new SingleColor(Color.WHITE);
			boxBorderColor = Color.BLACK;
			boxBorderStroke = new BasicStroke(1f);
			whiskerColor = Color.BLACK;
			whiskerStroke = new BasicStroke(1f);
			barWidth = 0.75;
			centerBarColor = Color.BLACK;
			centerBarStroke = new BasicStroke(
				2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
		}

		/**
		 * Custom deserialization method.
		 * @param in Input stream.
		 * @throws ClassNotFoundException if a serialized class doesn't exist anymore.
		 * @throws IOException if there is an error while reading data from the
		 *         input stream.
		 */
		private void readObject(ObjectInputStream in)
				throws ClassNotFoundException, IOException {
			// Default deserialization
			in.defaultReadObject();
			// Custom deserialization
			boxBorderStroke = (Stroke) SerializationUtils.unwrap(
					(Serializable) in.readObject());
			whiskerStroke = (Stroke) SerializationUtils.unwrap(
					(Serializable) in.readObject());
			centerBarStroke = (Stroke) SerializationUtils.unwrap(
					(Serializable) in.readObject());
		}

		/**
		 * Custom serialization method.
		 * @param out Output stream.
		 * @throws ClassNotFoundException if a serialized class doesn't exist anymore.
		 * @throws IOException if there is an error while writing data to the
		 *         output stream.
		 */
		private void writeObject(ObjectOutputStream out)
				throws ClassNotFoundException, IOException {
			// Default serialization
			out.defaultWriteObject();
			// Custom serialization
			out.writeObject(SerializationUtils.wrap(boxBorderStroke));
			out.writeObject(SerializationUtils.wrap(whiskerStroke));
			out.writeObject(SerializationUtils.wrap(centerBarStroke));
		}

		/**
		 * Returns the index of the column which is used for the horizontal
		 * position of a box.
		 * @return Index of the column that is used for the horizontal position
		 * of a box.
		 */
		public int getPositionColumn() {
			return positionColumn;
		}

		/**
		 * Sets the index of the column which will be used for the horizontal
		 * position of a box.
		 * @param columnIndex Index of the column that is used for the
		 * horizontal position of a box.
		 */
		public void setPositionColumn(int columnIndex) {
			this.positionColumn = columnIndex;
		}

		/**
		 * Returns the index of the column which is used for the center bar.
		 * @return Index of the column which is used for the center bar.
		 */
		public int getCenterBarColumn() {
			return centerBarColumn;
		}

		/**
		 * Sets the index of the column which will be used for the center bar.
		 * @param columnIndex Index of the column which will be used for
		 * the center bar.
		 */
		public void setCenterBarColumn(int columnIndex) {
			this.centerBarColumn = columnIndex;
		}

		/**
		 * Returns the index of the column which is used for the bottom bar.
		 * @return Index of the column which is used for the bottom bar.
		 */
		public int getBottomBarColumn() {
			return bottomBarColumn;
		}

		/**
		 * Sets the index of the column which will be used for the bottom bar.
		 * @param columnIndex Index of the column which will be used for
		 * the bottom bar.
		 */
		public void setBottomBarColumn(int columnIndex) {
			this.bottomBarColumn = columnIndex;
		}

		/**
		 * Returns the index of the column which is used for the bottom edge of
		 * the box.
		 * @return Index of the column which is used for the bottom edge of the
		 * box.
		 */
		public int getBoxBottomColumn() {
			return boxBottomColumn;
		}

		/**
		 * Sets the index of the column which will be used for the bottom edge
		 * of the box.
		 * @param columnIndex Index of the column which will be used for
		 * the bottom edge of the box.
		 */
		public void setColumnBoxBottom(int columnIndex) {
			this.boxBottomColumn = columnIndex;
		}

		/**
		 * Returns the index of the column which is used for the top edge of
		 * the box.
		 * @return Index of the column which is used for the top edge of the
		 * box.
		 */
		public int getBoxTopColumn() {
			return boxTopColumn;
		}

		/**
		 * Sets the index of the column which will be used for the top edge of
		 * the box.
		 * @param columnIndex Index of the column which will be used for the
		 * top edge of the box.
		 */
		public void setBoxTopColumn(int columnIndex) {
			this.boxTopColumn = columnIndex;
		}

		/**
		 * Returns the index of the column which is used for the top bar.
		 * @return Index of the column which is used for the top bar.
		 */
		public int getTopBarColumn() {
			return topBarColumn;
		}

		/**
		 * Sets the index of the column which will be used for the top bar.
		 * @param columnIndex Index of the column which will be used for the
		 * top bar.
		 */
		public void setTopBarColumn(int columnIndex) {
			this.topBarColumn = columnIndex;
		}

		/**
		 * Returns the relative width of the box.
		 * @return Relative width of the box.
		 */
		public double getBoxWidth() {
			return boxWidth;
		}

		/**
		 * Sets the relative width of the box.
		 * @param boxWidth Relative width of the box.
		 */
		public void setBoxWidth(double boxWidth) {
			this.boxWidth = boxWidth;
		}

		/**
		 * Returns the mapping which is used to fill the background of a box.
		 * @return {@code ColorMapper} instance which is used to fill the
		 * background of a box.
		 */
		public ColorMapper getBoxBackground() {
			return boxBackground;
		}

		/**
		 * Sets the mapping which will be used to fill the background of a box.
		 * @param color {@code ColorMapper} instance which will be used to fill
		 * the background of a box.
		 */
		public void setBoxBackground(ColorMapper color) {
			this.boxBackground = color;
		}

		/**
		 * Sets the paint which will be used to fill the background of a box.
		 * @param color {@code Paint} instance which will be used to fill the
		 * background of a box.
		 */
		public void setBoxBackground(Paint color) {
			setBoxBackground(new SingleColor(color));
		}

		/**
		 * Returns the paint which is used to fill the border of a box and the
		 * lines of bars.
		 * @return Paint which is used to fill the border of a box and the
		 * lines of bars.
		 */
		public Paint getBoxBorderColor() {
			return boxBorderColor;
		}

		/**
		 * Sets the paint which will be used to fill the border of a box and
		 * the lines of bars.
		 * @param color Paint which will be used to fill the border of a box
		 * and the lines of bars.
		 */
		public void setBoxBorderColor(Paint color) {
			this.boxBorderColor = color;
		}

		/**
		 * Returns the stroke which is used to paint the border of a box and
		 * the lines of the bars.
		 * @return {@code Stroke} instance which is used to paint the border of
		 * a box and the lines of the bars.
		 */
		public Stroke getBoxBorderStroke() {
			return boxBorderStroke;
		}

		/**
		 * Sets the stroke which will be used to paint the border of a box and
		 * the lines of the bars.
		 * @param stroke {@code Stroke} instance which will be used to paint
		 * the border of a box and the lines of the bars.
		 */
		public void setBoxBorderStroke(Stroke stroke) {
			this.boxBorderStroke = stroke;
		}

		/**
		 * Returns the paint which is used to fill the lines of the whiskers.
		 * @return Paint which is used to fill the lines of the whiskers.
		 */
		public Paint getWhiskerColor() {
			return whiskerColor;
		}

		/**
		 * Sets the paint which will be used to fill the lines of the whiskers.
		 * @param color Paint which will be used to fill the lines of the
		 * whiskers.
		 */
		public void setWhiskerColor(Paint color) {
			this.whiskerColor = color;
		}

		/**
		 * Returns the stroke which is used to paint the lines of the whiskers.
		 * @return {@code Stroke} instance which is used to paint the lines of
		 * the whiskers.
		 */
		public Stroke getWhiskerStroke() {
			return whiskerStroke;
		}

		/**
		 * Sets the stroke which will be used to paint the lines of the
		 * whiskers.
		 * @param stroke {@code Stroke} instance which will be used to paint
		 * the lines of the whiskers.
		 */
		public void setWhiskerStroke(Stroke stroke) {
			this.whiskerStroke = stroke;
		}

		/**
		 * Returns the relative width of the bottom and top bars.
		 * @return Relative width of the bottom and top bars.
		 */
		public double getBarWidth() {
			return barWidth;
		}

		/**
		 * Sets the relative width of the bottom and top bars.
		 * @param width Relative width of the bottom and top bars.
		 */
		public void setBarWidth(double width) {
			this.barWidth = width;
		}

		/**
		 * Returns the paint which is used to fill the lines of the center bar.
		 * @return Paint which is used to fill the lines of the center bar.
		 */
		public Paint getCenterBarColor() {
			return centerBarColor;
		}

		/**
		 * Sets the paint which will be used to fill the lines of the center
		 * bar.
		 * @param color Paint which will be used to fill the lines of the
		 * center bar.
		 */
		public void setCenterBarColor(Paint color) {
			this.centerBarColor = color;
		}

		/**
		 * Returns the stroke which is used to paint the lines of the center
		 * bar.
		 * @return {@code Stroke} instance which is used to paint the lines of
		 * the center bar.
		 */
		public Stroke getCenterBarStroke() {
			return centerBarStroke;
		}

		/**
		 * Sets the stroke which will be used to paint the lines of the
		 * center bar.
		 * @param stroke {@code Stroke} instance which will be used to paint
		 * the lines of the center bar.
		 */
		public void setCenterBarStroke(Stroke stroke) {
			this.centerBarStroke = stroke;
		}

		@Override
		public Drawable getPoint(final PointData data, final Shape shape) {
			return new AbstractDrawable() {
				/** Version id for serialization. */
				private static final long serialVersionUID = 2765031432328349977L;

				public void draw(DrawingContext context) {
					Axis axisX = data.axes.get(0);
					Axis axisY = data.axes.get(1);
					AxisRenderer axisXRenderer = data.axisRenderers.get(0);
					AxisRenderer axisYRenderer = data.axisRenderers.get(1);
					Row row = data.row;

					// Get the values from data columns
					BoxWhiskerRenderer renderer =  BoxWhiskerRenderer.this;
					int colPos = renderer.getPositionColumn();
					int colBarCenter = renderer.getCenterBarColumn();
					int colBarBottom = renderer.getBottomBarColumn();
					int colBoxBottom = renderer.getBoxBottomColumn();
					int colBoxTop = renderer.getBoxTopColumn();
					int colBarTop = renderer.getTopBarColumn();

					if (!row.isColumnNumeric(colPos) ||
							!row.isColumnNumeric(colBarCenter) ||
							!row.isColumnNumeric(colBarBottom) ||
							!row.isColumnNumeric(colBoxBottom) ||
							!row.isColumnNumeric(colBoxTop) ||
							!row.isColumnNumeric(colBarTop)) {
						return;
					}

					double valueX = ((Number) row.get(colPos)).doubleValue();
					double valueYBarBottom = ((Number) row.get(colBarBottom)).doubleValue();
					double valueYBoxBottom = ((Number) row.get(colBoxBottom)).doubleValue();
					double valueYBarCenter = ((Number) row.get(colBarCenter)).doubleValue();
					double valueYBoxTop = ((Number) row.get(colBoxTop)).doubleValue();
					double valueYBarTop = ((Number) row.get(colBarTop)).doubleValue();

					// Calculate positions in screen units
					double boxWidthRel = getBoxWidth();
					double boxAlign = 0.5;
					// Box X
					double boxXMin = axisXRenderer
						.getPosition(axisX, valueX - boxWidthRel*boxAlign, true, false)
						.get(PointND.X);
					double boxX = axisXRenderer.getPosition(
						axisX, valueX, true, false).get(PointND.X);
					double boxXMax = axisXRenderer
						.getPosition(axisX, valueX + boxWidthRel*boxAlign, true, false)
						.get(PointND.X);
					// Box Y
					double barYbottom = axisYRenderer.getPosition(
						axisY, valueYBarBottom, true, false).get(PointND.Y);
					double boxYBottom = axisYRenderer.getPosition(
						axisY, valueYBoxBottom, true, false).get(PointND.Y);
					double barYCenter = axisYRenderer.getPosition(
						axisY, valueYBarCenter, true, false).get(PointND.Y);
					double boxYTop = axisYRenderer.getPosition(
						axisY, valueYBoxTop, true, false).get(PointND.Y);
					double barYTop = axisYRenderer.getPosition(
						axisY, valueYBarTop, true, false).get(PointND.Y);
					double boxWidth = Math.abs(boxXMax - boxXMin);
					// Bars
					double barWidthRel = getBarWidth();
					double barXMin = boxXMin + (1.0 - barWidthRel)*boxWidth/2.0;
					double barXMax = boxXMax - (1.0 - barWidthRel)*boxWidth/2.0;

					// Create shapes
					// The origin of all shapes is (boxX, boxY)
					Rectangle2D boxBounds = new Rectangle2D.Double(
						boxXMin - boxX, boxYTop - barYCenter,
						boxWidth, Math.abs(boxYTop - boxYBottom));
					Rectangle2D shapeBounds = shape.getBounds2D();
					AffineTransform tx = new AffineTransform();
					tx.translate(boxBounds.getX(), boxBounds.getY());
					tx.scale(boxBounds.getWidth()/shapeBounds.getWidth(),
						boxBounds.getHeight()/shapeBounds.getHeight());
					tx.translate(-shapeBounds.getMinX(), -shapeBounds.getMinY());
					Shape box = tx.createTransformedShape(shape);

					Line2D whiskerMax = new Line2D.Double(
						0.0, boxYTop - barYCenter,
						0.0, barYTop - barYCenter
					);
					Line2D whiskerMin = new Line2D.Double(
						0.0, boxYBottom - barYCenter,
						0.0, barYbottom - barYCenter
					);
					Line2D barMax = new Line2D.Double(
						barXMin - boxX, barYTop - barYCenter,
						barXMax - boxX, barYTop - barYCenter
					);
					Line2D barMin = new Line2D.Double(
						barXMin - boxX, barYbottom - barYCenter,
						barXMax - boxX, barYbottom - barYCenter
					);
					Line2D barCenter = new Line2D.Double(
						boxXMin - boxX, 0.0,
						boxXMax - boxX, 0.0
					);

					// Paint shapes
					Graphics2D graphics = context.getGraphics();
					ColorMapper paintBoxMapper = getBoxBackground();
					Paint paintBox;
					if (paintBoxMapper instanceof ContinuousColorMapper) {
						paintBox = ((ContinuousColorMapper) paintBoxMapper)
							.get(valueX);
					} else {
						int index = data.index;
						paintBox = paintBoxMapper.get(index);
					}
					Paint paintStrokeBox = getBoxBorderColor();
					Stroke strokeBox = getBoxBorderStroke();
					Paint paintWhisker = getWhiskerColor();
					Stroke strokeWhisker = getWhiskerStroke();
					Paint paintBarCenter = getCenterBarColor();
					Stroke strokeBarCenter = getCenterBarStroke();
					// Fill box
					GraphicsUtils.fillPaintedShape(
						graphics, box, paintBox, box.getBounds2D());
					// Save current graphics state
					Paint paintOld = graphics.getPaint();
					Stroke strokeOld = graphics.getStroke();
					// Draw whiskers
					graphics.setPaint(paintWhisker);
					graphics.setStroke(strokeWhisker);
					graphics.draw(whiskerMax);
					graphics.draw(whiskerMin);
					// Draw box and bars
					graphics.setPaint(paintStrokeBox);
					graphics.setStroke(strokeBox);
					graphics.draw(box);
					graphics.draw(barMax);
					graphics.draw(barMin);
					graphics.setPaint(paintBarCenter);
					graphics.setStroke(strokeBarCenter);
					graphics.draw(barCenter);
					// Restore previous graphics state
					graphics.setStroke(strokeOld);
					graphics.setPaint(paintOld);
				}
			};
		}

		/**
		 * Returns a {@code Shape} instance that can be used for further
		 * calculations.
		 * @param data Information on axes, renderers, and values.
		 * @return Outline that describes the point's shape.
		 */
		public Shape getPointShape(PointData data) {
			return getShape();
		}

		/**
		 * Returns a graphical representation of the value label to be drawn for
		 * the specified data value.
		 * @param data Information on axes, renderers, and values.
		 * @param shape Outline that describes the bounds for the value label.
		 * @return Component that can be used to draw the value label.
		 */
		public Drawable getValue(final PointData data, final Shape shape) {
			return new AbstractDrawable() {
				/** Version id for serialization. */
				private static final long serialVersionUID1 = 6788431763837737592L;

				public void draw(DrawingContext context) {
					// TODO Implement rendering of value label
				}
			};
		}
	}

	/**
	 * A legend implementation for box-and-whisker plots that displays all
	 * values of the data source as items.
	 */
	public static class BoxPlotLegend extends ValueLegend {
		/** Version id for serialization. */
		private static final long serialVersionUID = 1517792984459627757L;

		/** Associated plot. */
		private final BoxPlot plot;

		/**
		 * Initializes a new instance with the specified plot.
		 * @param plot Associated plot.
		 */
		public BoxPlotLegend(BoxPlot plot) {
			this.plot = plot;
		}

		@Override
		protected Drawable getSymbol(final Row row) {
			return new LegendSymbol(row, (BoxWhiskerRenderer) plot.getPointRenderers(row.getSource()).get(0),
					plot.getFont(), plot.getLegend().getSymbolSize());
		}
	}

	private static class LegendSymbol extends AbstractLegend.AbstractSymbol {
		private final Row row;
		private final BoxWhiskerRenderer boxWhiskerRenderer;

		public LegendSymbol(Row row, BoxWhiskerRenderer boxWhiskerRenderer, Font font, Dimension2D symbolSize) {
			super(font, symbolSize);
			this.row = row;
			this.boxWhiskerRenderer = boxWhiskerRenderer;
		}

		@Override
		public void draw(DrawingContext context) {
			Shape shape = new Rectangle2D.Double(0.0, 0.0, getBounds().getWidth(), getBounds().getHeight());

			Graphics2D graphics = context.getGraphics();
			AffineTransform txOrig = graphics.getTransform();
			graphics.translate(getX(), getY());
			GraphicsUtils.fillPaintedShape(context.getGraphics(), shape,
					boxWhiskerRenderer.getBoxBackground().get(row.getIndex()), null);
			GraphicsUtils.drawPaintedShape(context.getGraphics(), shape, boxWhiskerRenderer.getBoxBorderColor(),
					null, boxWhiskerRenderer.getBoxBorderStroke());
			graphics.setTransform(txOrig);
		}
	}

	/**
	 * Initializes a new box-and-whisker plot with the specified data source.
	 * @param data Data to be displayed.
	 */
	public BoxPlot(DataSource data) {
		setLegend(new BoxPlotLegend(this));

		((XYPlotArea2D) getPlotArea()).setMajorGridX(false);
		getAxisRenderer(AXIS_X).setTickSpacing(1.0);
		getAxisRenderer(AXIS_X).setMinorTicksVisible(false);
		getAxisRenderer(AXIS_X).setIntersection(-Double.MAX_VALUE);
		getAxisRenderer(AXIS_Y).setIntersection(-Double.MAX_VALUE);

		add(data);
		autoscaleAxes();
	}

	/**
	 * Extracts statistics from the columns of an data source that are commonly
	 * used for box-and-whisker plots. The result is a new data source
	 * containing <i>column index</i>, <i>median</i>, <i>mininum</i>, <i>first
	 * quartile</i>, <i>third quartile</i>, and <i>maximum</i> for each column.
	 * @param data Original data source
	 * @return New data source with (columnIndex, median, min, quartile1,
	 *         quartile3, max)
	 */
	@SuppressWarnings("unchecked")
	public static DataSource createBoxData(DataSource data) {
		if (data == null) {
			throw new NullPointerException(
				"Cannot extract statistics from null data source.");
		}

		DataTable stats = new DataTable(Integer.class, Double.class,
			Double.class, Double.class, Double.class, Double.class);

		// Generate statistical values for each column
		for (int c = 0; c < data.getColumnCount(); c++) {
			Column col = data.getColumn(c);
			if (!col.isNumeric()) {
				continue;
			}
			stats.add(
				c + 1,
				col.getStatistics(Statistics.MEDIAN),
				col.getStatistics(Statistics.MIN),
				col.getStatistics(Statistics.QUARTILE_1),
				col.getStatistics(Statistics.QUARTILE_3),
				col.getStatistics(Statistics.MAX)
			);
		}
		return stats;
	}

	@Override
	public void add(int index, DataSource source, boolean visible) {
		if (getData().size() > 0) {
			throw new IllegalArgumentException(
				"This plot type only supports a single data source."); //$NON-NLS-1$
		}
		// By the looks of it, some objects depend on a BoxWhiskerRenderer being present when super.add is called
		// However, super.add overwrites renderers, so we have to create the BoxWhiskerRenderer twice.
		BoxWhiskerRenderer renderer = new BoxWhiskerRenderer();
		setPointRenderers(source, renderer);
		super.add(index, source, visible);
		// FIXME: Overwrites possible present point and line renderers
		setLineRenderers(source, null);
		setPointRenderers(source, renderer);
	}

	@Override
	public void autoscaleAxis(String axisName) {
		if (!AXIS_X.equals(axisName) && !AXIS_Y.equals(axisName)) {
			super.autoscaleAxis(axisName);
		}
		Axis axis = getAxis(axisName);
		if (axis == null || !axis.isAutoscaled()) {
			return;
		}

		List<DataSource> sources = getData();
		if (sources.isEmpty()) {
			return;
		}

		boolean isXAxis = AXIS_X.equals(axisName);

		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (DataSource data : sources) {
			BoxWhiskerRenderer pointRenderer = null;
			for (PointRenderer p : getPointRenderers(data)) {
				if (p instanceof BoxWhiskerRenderer) {
					pointRenderer = (BoxWhiskerRenderer) p;
					break;
				}
			}

			if (pointRenderer == null) {
				continue;
			}

			int minColumnIndex, maxColumnIndex;
			if (isXAxis) {
				minColumnIndex = pointRenderer.getPositionColumn();
				maxColumnIndex = pointRenderer.getPositionColumn();
			} else {
				minColumnIndex = pointRenderer.getBottomBarColumn();
				maxColumnIndex = pointRenderer.getTopBarColumn();
			}

			min = Math.min(min, data.getColumn(minColumnIndex)
					.getStatistics(Statistics.MIN));
			max = Math.max(max, data.getColumn(maxColumnIndex)
					.getStatistics(Statistics.MAX));
		}
		double spacing = (isXAxis) ? 0.5 : 0.05*(max - min);
		axis.setRange(min - spacing, max + spacing);
	}
}

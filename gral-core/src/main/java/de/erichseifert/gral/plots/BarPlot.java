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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.graphics.Location;
import de.erichseifert.gral.plots.areas.AreaRenderer;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.colors.ColorMapper;
import de.erichseifert.gral.plots.legends.AbstractLegend;
import de.erichseifert.gral.plots.legends.Legend;
import de.erichseifert.gral.plots.legends.ValueLegend;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.plots.points.DefaultPointRenderer2D;
import de.erichseifert.gral.plots.points.PointData;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.PointND;
import de.erichseifert.gral.util.SerializationUtils;


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
	/** Version id for serialization. */
	private static final long serialVersionUID = 3177733647455649147L;

	/** Relative width of the bars. 1.0 means the bars touch each other
	 * without gap. */
	private double barWidth;
	/** Minimal height of the bars in pixels. */
	private double barHeightMin;
	/** Decides whether the bars should be filled as a whole, or each bar on
	 * its own. This can e.g. be important for gradients. */
	private boolean paintAllBars;

	/**
	 * Class that renders a bar in a bar plot.
	 */
	public static class BarRenderer extends DefaultPointRenderer2D {
		/** Version id for serialization. */
		private static final long serialVersionUID = 2183638342305398522L;

		/** Plot that contains settings and renderers. */
		private final BarPlot plot;

		/** Stroke to draw the border of the bar. */
		// Custom serialization will be done with a wrapper object
		private transient Stroke borderStroke;
		/** Color to fill the border of the bar. */
		private Paint borderColor;

		/**
		 * Constructor that creates a new instance and initializes it with a
		 * plot as data provider.
		 * @param plot The associated plot.
		 */
		public BarRenderer(BarPlot plot) {
			this.plot = plot;
			setValueLocation(Location.NORTH);
			borderStroke = null;
			borderColor = Color.BLACK;
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
			borderStroke = (Stroke) SerializationUtils.unwrap(
					(Serializable) in.readObject());
		}

		/**
		 * Custom serialization method.
		 * @param out Output stream.
		 * @throws ClassNotFoundException if a serialized class doesn't exist.
		 * @throws IOException if there is an error while writing data to the
		 *         output stream.
		 */
		private void writeObject(ObjectOutputStream out)
				throws ClassNotFoundException, IOException {
			// Default serialization
			out.defaultWriteObject();
			// Custom serialization
			out.writeObject(SerializationUtils.wrap(borderStroke));
		}

		/**
		 * Returns the stroke used to paint the outline of the point shape.
		 * @return Stroke used to paint the outline of the point shape.
		 */
		public Stroke getBorderStroke() {
			return borderStroke;
		}

		/**
		 * Sets the stroke used to paint the outline of the point shape.
		 * @param stroke Stroke used to paint the outline of the point shape.
		 */
		public void setBorderStroke(Stroke stroke) {
			this.borderStroke = stroke;
		}

		/**
		 * Returns the paint which is used to fill the point shape.
		 * @return Paint which is used to fill the point shape.
		 */
		public Paint getBorderColor() {
			return borderColor;
		}

		/**
		 * Sets the paint which will be used to fill the point shape.
		 * @param color Paint which will be used to fill the point shape.
		 */
		public void setBorderColor(Paint color) {
			this.borderColor = color;
		}

		@Override
		public Drawable getPoint(final PointData data, final Shape shape) {
			return new AbstractDrawable() {
				/** Version id for serialization. */
				private static final long serialVersionUID = -3145112034673683520L;

				public void draw(DrawingContext context) {
					BarRenderer renderer = BarRenderer.this;

					Rectangle2D paintBoundaries = null;
					Graphics2D graphics = context.getGraphics();

					ColorMapper colors = renderer.getColor();
					Paint paint = colors.get(data.index);

					if (plot.isPaintAllBars()) {
						AffineTransform txOld = graphics.getTransform();
						Rectangle2D shapeBounds = shape.getBounds2D();
						paintBoundaries = new Rectangle2D.Double();//plot.getPlotArea().getBounds();
						paintBoundaries = new Rectangle2D.Double(
							shapeBounds.getX(), paintBoundaries.getY() - txOld.getTranslateY(),
							shapeBounds.getWidth(), paintBoundaries.getHeight()
						);
					}

					GraphicsUtils.fillPaintedShape(
						graphics, shape, paint, paintBoundaries);

					Stroke stroke = renderer.getBorderStroke();
					Paint strokePaint = renderer.getBorderColor();
					if (stroke != null && strokePaint != null) {
						GraphicsUtils.drawPaintedShape(
							graphics, shape, strokePaint, null, stroke);
					}
				}
			};
		}

		/**
		 * Returns a {@code Shape} instance that can be used for further
		 * calculations.
		 * @param data Information on axes, renderers, and values.
		 * @return Outline that describes the point's shape.
		 */
		@Override
		public Shape getPointShape(PointData data) {
			int colX = 0;
			int colY = 1;

			Axis axisX = data.axes.get(0);
			Axis axisY = data.axes.get(1);
			AxisRenderer axisXRenderer = data.axisRenderers.get(0);
			AxisRenderer axisYRenderer = data.axisRenderers.get(1);
			Row row = data.row;

			if (!row.isColumnNumeric(colX) || !row.isColumnNumeric(colY)) {
				return null;
			}

			double valueX = ((Number) row.get(colX)).doubleValue();
			double valueY = ((Number) row.get(colY)).doubleValue();
			double axisYOrigin = 0.0;

			double barWidthRel = plot.getBarWidth();
			barWidthRel = Math.max(barWidthRel, 0.0);
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

			// position of the bar's left edge in screen coordinates
			double barX = axisXRenderer.getPosition(
				axisX, valueX, true, false).get(PointND.X);
			// position of the bar's upper edge in screen coordinates
			// (the origin of the screen y axis is at the top)
			boolean barAboveAxis = barYMax == barYOrigin;
			double barY = barAboveAxis ? 0.0 : -barHeight;

			double barHeightMin = plot.getBarHeightMin();
			if (MathUtils.isCalculatable(barHeightMin) && barHeightMin > 0.0 &&
					barHeight < barHeightMin) {
				if (barAboveAxis) {
					barY += -barHeightMin + barHeight;
				}
				barHeight = barHeightMin;
			}

			return getBarShape(
				barXMin - barX, barY, barWidth, barHeight);
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
			Shape shape = getShape();
			Rectangle2D shapeBounds = shape.getBounds2D();

			AffineTransform tx = new AffineTransform();
			tx.translate(x, y);
			tx.scale(width/shapeBounds.getWidth(), height/shapeBounds.getHeight());
			tx.translate(-shapeBounds.getMinX(), -shapeBounds.getMinY());

			return tx.createTransformedShape(shape);
		}

		/**
		 * Returns a graphical representation of the value label to be drawn for
		 * the specified data value.
		 * @param data Information on axes, renderers, and values.
		 * @param shape Outline that describes the bounds for the value label.
		 * @return Component that can be used to draw the value label.
		 */
		@Override
		public Drawable getValue(final PointData data, final Shape shape) {
			return new AbstractDrawable() {
				/** Version id for serialization. */
				private static final long serialVersionUID1 = -1133369168849171793L;

				public void draw(DrawingContext context) {
					PointRenderer renderer = BarRenderer.this;
					Row row = data.row;

					if (renderer.isValueVisible()) {
						int colValue = renderer.getValueColumn();
						drawValueLabel(context, shape, row, data.index, colValue);
					}
				}
			};
		}
	}

	/**
	 * A legend implementation for bar plots that displays all values of the
	 * data source as items.
	 */
	public static class BarPlotLegend extends ValueLegend {
		/** Version id for serialization. */
		private static final long serialVersionUID = 4752278896167602641L;

		/** Plot that contains settings and renderers. */
		private final BarPlot plot;

		/**
		 * Constructor that initializes the instance with a plot acting as a
		 * provider for settings and renderers.
		 * @param plot Plot.
		 */
		public BarPlotLegend(BarPlot plot) {
			this.plot = plot;
		}

		@Override
		protected Drawable getSymbol(final Row row) {
			List<PointRenderer> pointRenderers = plot.getPointRenderers(row.getSource());
			BarRenderer barRenderer = (BarRenderer) pointRenderers.get(0);
			return new BarPlot.LegendSymbol(row, barRenderer,
					plot.getFont(), plot.getLegend().getSymbolSize());
		}
	}

	private static class LegendSymbol extends AbstractLegend.AbstractSymbol {
		private final Row row;
		private final BarRenderer barRenderer;

		public LegendSymbol(Row row, BarRenderer barRenderer, Font font, Dimension2D symbolSize) {
			super(font, symbolSize);
			this.row = row;
			this.barRenderer = barRenderer;
		}

		@Override
		public void draw(DrawingContext context) {
			double width = getPreferredSize().getWidth();
			double height = getPreferredSize().getHeight();
			Shape shape = barRenderer.getBarShape(0.0, 0.0, width, height);

			Graphics2D graphics = context.getGraphics();
			AffineTransform txOrig = graphics.getTransform();
			graphics.translate(getX(), getY());
			GraphicsUtils.fillPaintedShape(
					context.getGraphics(), shape, barRenderer.getColor().get(0), null);
			GraphicsUtils.drawPaintedShape(
					context.getGraphics(), shape, barRenderer.getBorderColor(), null, barRenderer.getBorderStroke());
			graphics.setTransform(txOrig);
		}
	}

	/**
	 * Creates a new instance and initializes it with the specified
	 * data sources.
	 * @param data Data to be displayed.
	 */
	public BarPlot(DataSource... data) {
		super(data);

		((XYPlotArea2D) getPlotArea()).setMajorGridX(false);
		barWidth = 1.0;
		barHeightMin = 0.0;
		paintAllBars = false;

		Legend legend = new BarPlotLegend(this);
		setLegend(legend);

		autoscaleAxes();
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

		int rowCount = 0;
		for (DataSource data : sources) {
			rowCount = Math.max(rowCount, data.getRowCount());
		}
		if (rowCount == 0) {
			return;
		}

		double min = getAxisMin(axisName);
		double max = getAxisMax(axisName);
		double spacing = 0.0;
		if (AXIS_X.equals(axisName)) {
			// Add margin
			double barWidth = getBarWidth();
			double margin = barWidth*(max - min)/rowCount;
			spacing = margin/2.0;
		} else {
			// Make sure 0 is always visible for y axis
			min = Math.min(min, 0.0);
			max = Math.max(max, 0.0);
		}
		axis.setRange(min - spacing, max + spacing);
	}

	@Override
	public void add(int index, DataSource source, boolean visible) {
		super.add(index, source, visible);

		// Assign default renderers
		PointRenderer pointRendererDefault = new BarRenderer(this);
		LineRenderer lineRendererDefault = null;
		AreaRenderer areaRendererDefault = null;
		// FIXME: Overwrites possible present point and line renderers
		setPointRenderers(source, pointRendererDefault);
		setLineRenderers(source, lineRendererDefault);
		setAreaRenderers(source, areaRendererDefault);
	}

	/**
	 * Returns the width of the bars in axis coordinates.
	 * @return Width of the bars in axis coordinates.
	 */
	public double getBarWidth() {
		return barWidth;
	}

	/**
	 * Sets the width of the bars in axis coordinates.
	 * @param barWidth Width of the bars in axis coordinates.
	 */
	public void setBarWidth(double barWidth) {
		this.barWidth = barWidth;
	}

	/**
	 * Returns the minimum height of the bars in view units
	 * (e.g. pixels on screen).
	 * @return Minimum height of the bars in view units.
	 */
	public double getBarHeightMin() {
		return barHeightMin;
	}

	/**
	 * Sets the minimum height of the bars in view units
	 * (e.g. pixels on screen).
	 * @param barHeightMin Minimum height of the bars in view units.
	 */
	public void setBarHeightMin(double barHeightMin) {
		this.barHeightMin = barHeightMin;
	}

	/**
	 * Returns whether all bars are filled as a whole, or if each bar is filled
	 * independently.
	 * @return {@code true} if all bars are filled as a whole, or
	 * 		   {@code false} if each bar is filled independently.
	 */
	public boolean isPaintAllBars() {
		return paintAllBars;
	}

	/**
	 * Sets whether all bars will be filled as a whole, or if each bar will be
	 * filled independently.
	 * @param paintAllBars {@code true} to fill all bars as a whole, or
	 * 		   {@code false} to fill each bar independently.
	 */
	public void setPaintAllBars(boolean paintAllBars) {
		this.paintAllBars = paintAllBars;
	}
}

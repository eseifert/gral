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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.erichseifert.gral.data.Column;
import de.erichseifert.gral.data.DataChangeEvent;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.navigation.AbstractNavigator;
import de.erichseifert.gral.navigation.Navigable;
import de.erichseifert.gral.navigation.Navigator;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.axes.LinearRenderer2D;
import de.erichseifert.gral.plots.colors.ColorMapper;
import de.erichseifert.gral.plots.colors.ContinuousColorMapper;
import de.erichseifert.gral.plots.colors.QuasiRandomColors;
import de.erichseifert.gral.plots.points.AbstractPointRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.util.GeometryUtils;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.Location;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.PointND;
import de.erichseifert.gral.util.SettingChangeEvent;


/**
 * <p>Class that displays data as segments of a pie plot. Empty segments are
 * displayed for negative values.</p>
 * <p>To create a new {@code PiePlot} simply create a new instance using
 * a data source. Example:</p>
 * <pre>
 * DataTable data = new DataTable(Integer.class, Double.class);
 * data.add(-23.50);
 * data.add(100.00);
 * data.add( 60.25);
 *
 * PiePlot plot = new PiePlot(data);
 * </pre>
 */
public class PiePlot extends AbstractPlot implements Navigable {
	/** Key for specifying the tangential axis of a pie plot. */
	public static String AXIS_TANGENTIAL = "tangential"; //$NON-NLS-1$

	/** Key for specifying {@link java.awt.Point2D} instance defining the
	center of the pie. The coordinates must be relative to the plot area
	dimensions, i.e. 0.0 means left/top, 0.5 means the center, and 1.0 means
	right/bottom. */
	public static final Key CENTER =
		new Key("pieplot.center"); //$NON-NLS-1$
	/** Key for specifying a {@link Number} value for the radius of the pie
	relative to the plot area size. */
	public static final Key RADIUS =
		new Key("pieplot.radius"); //$NON-NLS-1$
	/** Key for specifying a {@link Number} value for the starting angle of the
	first segment in degrees. The angle is applied counterclockwise. */
	public static final Key START =
		new Key("pieplot.start"); //$NON-NLS-1$
	/** Key for specifying a {@link Boolean} value which decides whether the
	segments should be ordered clockwise ({@code true}) or counterclockwise
	({@code false}). */
	public static final Key CLOCKWISE =
		new Key("pieplot.clockwise"); //$NON-NLS-1$

	/** Mapping from data source to point renderer. */
	private final Map<DataSource, PointRenderer> pointRenderers;
	/** Slice objects with start and end position for each visible data source. */
	private final Map<DataSource, List<Slice>> slices;
	/** Cache for the {@code Navigator} implementation. */
	private PiePlotNavigator navigator;

	/**
	 * Navigator implementation for pie plots. Zooming changes the
	 * {@code RADIUS} setting and panning the {@code CENTER} setting.
	 */
	public static class PiePlotNavigator extends AbstractNavigator {
		/** Pie plot that will be navigated. */
		private final PiePlot plot;
		/** Location of center in default state. */
		private PointND<? extends Number> centerOriginal;
		/** Zoom level in default state. */
		private double zoomOriginal;
		/** Current zoom level. */
		private double zoom;

		/**
		 * Initializes a new instance with a pie plot to be navigated.
		 * @param plot Pie plot.
		 */
		public PiePlotNavigator(PiePlot plot) {
			this.plot = plot;
			this.zoom = 1.0;
			setDefaultState();
		}

		/**
		 * Returns the current zoom level of the associated object.
		 * @return Current zoom level.
		 */
		public double getZoom() {
			return zoom;
		}

		/**
		 * Sets the zoom level of the associated object to the specified value.
		 * @param zoomNew New zoom level.
		 */
		public void setZoom(double zoomNew) {
			if (!isZoomable() || (zoomNew <= 0.0) ||
					!MathUtils.isCalculatable(zoomNew)) {
				return;
			}
			double zoomOld = getZoom();
			zoomNew = MathUtils.limit(zoomNew, getZoomMin(), getZoomMax());
			if (zoomOld == zoomNew) {
				return;
			}
			zoom = zoomNew;
			plot.setSetting(PiePlot.RADIUS, zoomOriginal/getZoom());
		}

		/**
		 * Returns the current center point. The returned point contains value in
		 * world units.
		 * @return Center point in world units.
		 */
		public PointND<? extends Number> getCenter() {
			Point2D center = plot.<Point2D>getSetting(PiePlot.CENTER);
			return new PointND<Double>(center.getX(), center.getY());
		}

		/**
		 * Sets a new center point. The values of the point are in world units.
		 * @param center New center point in world units.
		 */
		public void setCenter(PointND<? extends Number> center) {
			if (center == null || !isPannable()) {
				return;
			}
			Point2D center2d = center.getPoint2D();
			plot.setSetting(PiePlot.CENTER, center2d);
		}

		/**
		 * Moves the center by the relative values of the specified point.
		 * The values of the point are in screen units.
		 * @param deltas Relative values to use for panning.
		 */
		@SuppressWarnings("unchecked")
		public void pan(PointND<? extends Number> deltas) {
			PlotArea plotArea = plot.getPlotArea();
			PointND<Double> center = (PointND<Double>) getCenter();
			double x = center.get(0).doubleValue();
			x += deltas.get(0).doubleValue()/plotArea.getWidth();
			double y = center.get(1).doubleValue();
			y += deltas.get(1).doubleValue()/plotArea.getHeight();
			center.set(0, x);
			center.set(1, y);
			setCenter(center);
		}

		/**
		 * Sets the object's position and zoom level to the default state.
		 */
		public void reset() {
			setCenter(centerOriginal);
			setZoom(1.0);
		}

		/**
		 * Sets the current state as the default state of the object.
		 * Resetting the navigator will then return to the default state.
		 */
		public void setDefaultState() {
			centerOriginal = getCenter();
			zoomOriginal = plot.<Number>getSetting(PiePlot.RADIUS).doubleValue();
		}
	}

	/**
	 * Class that represents the drawing area of a {@code PiePlot}.
	 */
	public static class PiePlotArea2D extends PlotArea {
		/** Pie plot that this renderer is associated to. */
		private final PiePlot plot;

		/**
		 * Constructor that creates a new instance and initializes it with a
		 * plot acting as data provider.
		 * @param plot Data provider.
		 */
		public PiePlotArea2D(PiePlot plot) {
			this.plot = plot;
		}

		/**
		 * Draws the {@code Drawable} with the specified drawing context.
		 * @param context Environment used for drawing
		 */
		public void draw(DrawingContext context) {
			drawBackground(context);
			drawBorder(context);
			drawPlot(context);
		}

		@Override
		protected void drawPlot(DrawingContext context) {
			Graphics2D graphics = context.getGraphics();
			AffineTransform txOrig = graphics.getTransform();
			graphics.translate(getX(), getY());

			// TODO Use real font size instead of fixed value
			final double fontSize = 10.0;

			Shape clipBoundsOld = graphics.getClip();
			Insets2D clipOffset = getSetting(CLIPPING);
			if (clipOffset != null) {
				// Perform clipping
				Shape clipBounds = new Rectangle2D.Double(
					clipOffset.getLeft()*fontSize,
					clipOffset.getTop()*fontSize,
					getWidth() - clipOffset.getHorizontal()*fontSize,
					getHeight() - clipOffset.getVertical()*fontSize
				);
				// Take care of old clipping region. This is used when getting
				// scrolled in a JScrollPane for example.
				if (clipBoundsOld != null) {
					Area clipBoundsNew = new Area(clipBoundsOld);
					clipBoundsNew.intersect(new Area(clipBounds));
					clipBounds = clipBoundsNew;
				}
				graphics.setClip(clipBounds);
			}

			// Get width and height of the plot area for relative sizes
			Rectangle2D bounds = getBounds();

			// Move to center, so origin for point renderers will be (0, 0)
			Point2D center = plot.<Point2D>getSetting(PiePlot.CENTER);
			graphics.translate(
				center.getX()*bounds.getWidth(),
				center.getY()*bounds.getHeight()
			);

			// Paint points and lines
			for (DataSource s : plot.getVisibleData()) {
				// Skip empty data source
				if (s.getColumnCount() == 0) {
					continue;
				}

				int colIndex = 0;
				if (colIndex < 0 || colIndex >= s.getColumnCount() ||
						!s.isColumnNumeric(colIndex)) {
					continue;
				}

				PointRenderer pointRenderer = plot.getPointRenderer(s);

				String[] axisNames = plot.getMapping(s);
				Axis axis = plot.getAxis(axisNames[0]);
				if (!axis.isValid()) {
					continue;
				}
				AxisRenderer axisRenderer = plot.getAxisRenderer(axisNames[0]);

				for (int rowIndex = 0; rowIndex < s.getRowCount(); rowIndex++) {
					Row row = s.getRow(rowIndex);
					Drawable point = pointRenderer.getPoint(
						axis, axisRenderer, row, colIndex);
					point.setBounds(bounds);
					point.draw(context);
				}
			}

			if (clipOffset != null) {
				// Reset clipping
				graphics.setClip(clipBoundsOld);
			}

			graphics.setTransform(txOrig);
		}
	}

	/**
	 * Data class for storing slice information in world units.
	 */
	protected static final class Slice {
		/** Value where the slice starts. */
		public final double start;
		/** Value where the slice ends. */
		public final double end;

		/**
		 * Initializes a new slice with start and end value.
		 * @param start Value where the slice starts.
		 * @param end Value where the slice ends.
		 */
		public Slice(double start, double end) {
			this.start = start;
			this.end = end;
		}
	}

	/**
	 * A point renderer for a single slice in a pie plot.
	 */
	public static class PieSliceRenderer extends AbstractPointRenderer {
		/** Key for specifying a {@link Number} value for the outer radius of
		the pie relative to the radius set in the plot. */
		public static final Key RADIUS_OUTER =
			new Key("pieplot.radius.inner"); //$NON-NLS-1$
		/** Key for specifying a {@link Number} value for the inner radius of
		the pie relative to the radius set in the plot. */
		public static final Key RADIUS_INNER =
			new Key("pieplot.radius.inner"); //$NON-NLS-1$
		/** Key for specifying an instance of
		{@link de.erichseifert.gral.plots.colors.ColorMapper} used for coloring
		the segments. */
		public static final Key COLORS =
			new Key("pieplot.colorlist"); //$NON-NLS-1$
		/** Key for specifying a {@link Number} value for the width of gaps
		between the segments. */
		public static final Key GAP =
			new Key("pieplot.gap"); //$NON-NLS-1$

		/** Pie plot this renderer is attached to. */
		private final PiePlot plot;

		/**
		 * Initializes a new instance with a pie plot object.
		 * @param plot Pie plot.
		 */
		public PieSliceRenderer(PiePlot plot) {
			this.plot =  plot;

			setSettingDefault(VALUE_COLUMN, 0);
			setSettingDefault(ERROR_COLUMN_TOP, 1);
			setSettingDefault(ERROR_COLUMN_BOTTOM, 2);

			setSettingDefault(RADIUS_OUTER, 1.0);
			setSettingDefault(RADIUS_INNER, 0.0);
			setSettingDefault(COLORS, new QuasiRandomColors());
			setSettingDefault(GAP, 0.0);
		}

		/**
		 * Returns the graphical representation to be drawn for the specified data
		 * value.
		 * @param axis that is used to project the point.
		 * @param axisRenderer Renderer for the axis.
		 * @param row Data row containing the point.
		 * @param col Index of the column that will be projected on the axis.
		 * @return Component that can be used to draw the point.
		 */
		public Drawable getPoint(final Axis axis, final AxisRenderer axisRenderer,
				final Row row, final int col) {
			return new AbstractDrawable() {
				public void draw(DrawingContext context) {
					PointRenderer renderer = PieSliceRenderer.this;

					int col = 0; //this.<Number>getSetting(PiePlot.COLUMN).intValue();
					Number valueObj = (Number) row.get(col);
					if (!MathUtils.isCalculatable(valueObj) ||
							valueObj.doubleValue() <= 0.0) {
						return;
					}

					Font font = renderer.<Font>getSetting(
						PieSliceRenderer.VALUE_FONT);
					double fontSize = font.getSize2D();

					double plotAreaSize = Math.min(getWidth(), getHeight())/2.0;
					double radiusRel = plot.<Number>getSetting(
						PiePlot.RADIUS).doubleValue();
					double radiusRelOuter = renderer.<Number>getSetting(
						PieSliceRenderer.RADIUS_OUTER).doubleValue();
					double radius = plotAreaSize*radiusRel;
					double radiusOuter = radius*radiusRelOuter;

					// Construct slice
					double sum = plot.getSum(row.getSource());
					if (sum == 0.0) {
						return;
					}

					Slice slice = plot.getSlice(
						row.getSource(), row.getIndex());
					if (slice == null) {
						return;
					}

					double sliceStartRel = slice.start/sum;
					double sliceEndRel = slice.end/sum;

					double start = plot.<Number>getSetting(
						PiePlot.START).doubleValue();

					boolean clockwise = plot.<Boolean>getSetting(
						PiePlot.CLOCKWISE);
					double sliceSpan = (sliceEndRel - sliceStartRel)*360.0;
					double sliceStart;
					if (clockwise) {
						sliceStart = start - sliceEndRel*360.0;
					} else {
						sliceStart = start + sliceStartRel*360.0;
					}
					start = MathUtils.normalizeDegrees(start);

					Arc2D pieSlice = new Arc2D.Double(
						-radiusOuter, -radiusOuter,
						2.0*radiusOuter, 2.0*radiusOuter,
						sliceStart, sliceSpan,
						Arc2D.PIE
					);
					Area doughnutSlice = new Area(pieSlice);

					double gap = renderer.<Number>getSetting(
						PieSliceRenderer.GAP).doubleValue();
					if (gap > 0.0) {
						Stroke sliceStroke =
							new BasicStroke((float) (gap*fontSize));
						Area sliceContour =
							new Area(sliceStroke.createStrokedShape(pieSlice));
						doughnutSlice.subtract(sliceContour);
					}

					double radiusRelInner = renderer.<Number>getSetting(
						PieSliceRenderer.RADIUS_INNER).doubleValue();
					if (radiusRelInner > 0.0 && radiusRelInner < radiusRelOuter) {
						double radiusInner = radius*radiusRelInner;
						Ellipse2D inner = new Ellipse2D.Double(
							-radiusInner, -radiusInner,
							2.0*radiusInner, 2.0*radiusInner
						);
						Area hole = new Area(inner);
						doughnutSlice.subtract(hole);
					}

					// Paint slice
					ColorMapper colorMapper = renderer.<ColorMapper>getSetting(
						PieSliceRenderer.COLORS);
					Paint paint;
					if (colorMapper instanceof ContinuousColorMapper) {
						double coloringRel = 0.0;
						int rows = row.getSource().getRowCount();
						if (rows > 1) {
							double posRel = row.getIndex() / (double)(rows - 1);
							double posRelInv = 1.0 - posRel;
							coloringRel =
								posRelInv*sliceStartRel + posRel*sliceEndRel;
						}
						paint = ((ContinuousColorMapper) colorMapper).get(coloringRel);
					} else {
						paint = colorMapper.get(row.getIndex());
					}
					GraphicsUtils.fillPaintedShape(
						context.getGraphics(), doughnutSlice, paint, null);

					if (renderer.<Boolean>getSetting(VALUE_DISPLAYED)) {
						int colValue = renderer.<Integer>getSetting(VALUE_COLUMN);
						drawValueLabel(context, slice, radius, row, colValue);
					}
				}
			};
		}

		/**
		 * Returns a {@code Shape} instance that can be used for further
		 * calculations.
		 * @param row Data row containing the point.
		 * @return Outline that describes the point's shape.
		 */
		public Shape getPointPath(Row row) {
			throw new UnsupportedOperationException(
				"Not available for pie plots."); //$NON-NLS-1$
		}

		/**
		 * Draws the specified value label for the specified shape.
		 * @param context Environment used for drawing.
		 * @param slice Pie slice to draw.
		 * @param radius Radius of pie slice in view units (e.g. pixels).
		 * @param row Data row containing the point.
		 * @param col Index of the column that will be projected on the axis.
		 */
		protected void drawValueLabel(DrawingContext context, Slice slice,
				double radius, Row row, int col) {
			Comparable<?> value = row.get(col);

			// Formatting
			Format format = getSetting(VALUE_FORMAT);
			if ((format == null) && (value instanceof Number)) {
				format = NumberFormat.getInstance();
			}

			// Text to display
			String text = (format != null) ? format.format(value) : value.toString();

			// Visual settings
			Color color = getSetting(VALUE_COLOR);
			Font font = getSetting(VALUE_FONT);
			double fontSize = font.getSize2D();

			// Layout settings
			Location location = getSetting(VALUE_LOCATION);
			double alignX = this.<Number>getSetting(VALUE_ALIGNMENT_X).doubleValue();
			double alignY = this.<Number>getSetting(VALUE_ALIGNMENT_Y).doubleValue();
			Number rotation = this.<Number>getSetting(VALUE_ROTATION);
			Number distanceObj = getSetting(VALUE_DISTANCE);
			double distance = 0.0;
			if (MathUtils.isCalculatable(distanceObj)) {
				distance = distanceObj.doubleValue()*fontSize;
			}

			// Vertical layout
			double radiusRelOuter = this.<Number>getSetting(
				RADIUS_OUTER).doubleValue();
			double radiusRelInner = this.<Number>getSetting(
					RADIUS_INNER).doubleValue();
			double radiusOuter = radius*radiusRelOuter;
			double radiusInner = radius*radiusRelInner;
			double distanceV = distance;
			double labelPosV;
			if (location == Location.NORTH) {
				labelPosV = radiusOuter + distanceV;
			} else if (location == Location.SOUTH) {
				labelPosV = Math.max(radiusInner - distanceV, 0);
			} else {
				double sliceHeight = radiusOuter - radiusInner;
				if (2.0*distance >= sliceHeight) {
					alignY = 0.5;
					distanceV = 0.0;
				}
				labelPosV = radiusInner + distanceV +
					alignY*(sliceHeight - 2.0*distanceV);
			}

			// Horizontal layout
			double sum = plot.getSum(row.getSource());
			if (sum == 0.0) {
				return;
			}
			double sliceStartRel = slice.start/sum;
			double sliceEndRel = slice.end/sum;
			double circumference = 2.0*labelPosV*Math.PI;
			double distanceRelH = distance/circumference;
			double sliceWidthRel = sliceEndRel - sliceStartRel;
			if (2.0*distanceRelH >= sliceWidthRel) {
				alignX = 0.5;
				distanceRelH = 0.0;
			}
			double labelPosRelH = sliceStartRel + distanceRelH +
				alignX*(sliceWidthRel - 2.0*distanceRelH);
			double angleStart = Math.toRadians(-plot.<Number>getSetting(
				PiePlot.START).doubleValue());
			double direction = 1.0;
			if (!plot.<Boolean>getSetting(PiePlot.CLOCKWISE)) {
				direction = -1.0;
			}
			double angle = angleStart + direction*labelPosRelH*2.0*Math.PI;
			double dirX = Math.cos(angle);
			double dirY = Math.sin(angle);

			// Create a label with the settings
			Label label = new Label(text);
			label.setSetting(Label.ALIGNMENT_X, 1.0 - 0.5*dirX - 0.5);
			label.setSetting(Label.ALIGNMENT_Y, 0.5*dirY + 0.5);
			label.setSetting(Label.ROTATION, rotation);
			label.setSetting(Label.COLOR, color);
			label.setSetting(Label.FONT, font);

			// Calculate label position
			Dimension2D sizeLabel = label.getPreferredSize();
			double anchorX = 0.5;
			double anchorY = 0.5;
			if (location == Location.NORTH || location == Location.SOUTH) {
				anchorX = dirX*sizeLabel.getWidth()/2.0;
				anchorY = dirY*sizeLabel.getHeight()/2.0;
				if (location == Location.SOUTH) {
					anchorX = -anchorX;
					anchorY = -anchorY;
				}
			}

			// Resize label component
			double x = labelPosV*dirX + anchorX - sizeLabel.getWidth()/2.0;
			double y = labelPosV*dirY + anchorY - sizeLabel.getHeight()/2.0;
			double w = sizeLabel.getWidth();
			double h = sizeLabel.getHeight();
			label.setBounds(x, y, w, h);

			label.draw(context);
		}
	}

	/**
	 * Initializes a new pie plot with the specified data source.
	 * @param data Data to be displayed.
	 */
	public PiePlot(DataSource data) {
		super();

		setSettingDefault(CENTER, new Point2D.Double(0.5, 0.5));
		setSettingDefault(RADIUS, 1.0);
		setSettingDefault(START, 0.0);
		setSettingDefault(CLOCKWISE, true);

		pointRenderers = new HashMap<DataSource, PointRenderer>();
		slices = new HashMap<DataSource, List<Slice>>();

		setPlotArea(new PiePlotArea2D(this));
		// TODO Implement legend for pie plot
		// setLegend(new PiePlotLegend(this));

		add(data);

		createDefaultAxes();
		createDefaultAxisRenderers();

		dataUpdated(data);
		data.addDataListener(this);
	}

	@Override
	protected void createDefaultAxes() {
		// Create x axis and y axis by default
		Axis axisPie = new Axis();
		setAxis(AXIS_TANGENTIAL, axisPie);
	}

	@Override
	protected void autoScaleAxes() {
		List<DataSource> sources = getVisibleData();
		if (sources.isEmpty()) {
			return;
		}

		DataSource data = sources.get(0);
		if (data.getRowCount() == 0) {
			return;
		}

		double sum = getSum(data);
		if (sum == 0.0) {
			return;
		}

		for (String axisName : getAxesNames()) {
			Axis axis = getAxis(axisName);
			if (axis == null || !axis.isAutoscaled()) {
				continue;
			}
			axis.setRange(0.0, sum);
		}
	}

	@Override
	protected void createDefaultAxisRenderers() {
		// Create a linear renderer for the pie slices by default
		AxisRenderer renderer = new LinearRenderer2D();
		// Create a circle with radius 1.0 as shape for the axis
		Shape shape = new Ellipse2D.Double(-1.0, -1.0, 2.0, 2.0);
		renderer.setSetting(AxisRenderer.SHAPE, shape);
		// Don't show axis
		renderer.setSetting(AxisRenderer.SHAPE_VISIBLE, false);

		setAxisRenderer(AXIS_TANGENTIAL, renderer);
	}

	@Override
	public void add(int index, DataSource source, boolean visible) {
		if (getData().size() != 0) {
			throw new IllegalArgumentException(
				"This plot type only supports a single data source."); //$NON-NLS-1$
		}
		super.add(index, source, visible);
		PointRenderer pointRendererDefault = new PieSliceRenderer(this);
		setPointRenderer(source, pointRendererDefault);
		setMapping(source, AXIS_TANGENTIAL);
	}

	/**
	 * Returns the {@code PointRenderer} for the specified data source.
	 * @param s Data source.
	 * @return PointRenderer.
	 */
	public PointRenderer getPointRenderer(DataSource s) {
		return pointRenderers.get(s);
	}

	/**
	 * Sets the {@code PointRenderer} for a certain data source to the
	 * specified value.
	 * @param s Data source.
	 * @param pointRenderer PointRenderer to be set.
	 */
	public void setPointRenderer(DataSource s, PointRenderer pointRenderer) {
		this.pointRenderers.put(s, pointRenderer);
	}

	/**
	 * Returns a navigator instance that can control the current object.
	 * @return A navigator instance.
	 */
	public Navigator getNavigator() {
		if (navigator == null) {
			navigator = new PiePlotNavigator(this);
		}
		return navigator;
	}

	/**
	 * Returns the sum of all absolute values from the specified data source up
	 * to the row with the specified index. This is used to determine the
	 * position of pie slices.
	 * @param source Data source.
	 * @param index Index of the row.
	 * @return Sum of all absolute values from the specified data source up
	 *         to the row with the specified index
	 */
	protected Slice getSlice(DataSource source, int index) {
		if (index < 0) {
			return null;
		}
		List<Slice> dataSlices;
		synchronized (slices) {
			if (!slices.containsKey(source)) {
				createSlices(source);
			}
			dataSlices = slices.get(source);
		}
		if (dataSlices == null || index >= dataSlices.size()) {
			return null;
		}
		return dataSlices.get(index);
	}

	/**
	 * Returns the sum of all absolute values in the data column of a specified
	 * data source.
	 * @param source Data source.
	 * @return Sum of all absolute values for the specified data source.
	 */
	protected double getSum(DataSource source) {
		double sum = 0.0;
		synchronized (source) {
			Slice lastSlice = getSlice(source, source.getRowCount() - 1);
			if (lastSlice != null) {
				sum = lastSlice.end;
			}
		}
		return sum;
	}

	/**
	 * Creates the slice objects with start and end information for a specified
	 * data source.
	 * @param source Data source.
	 */
	private void createSlices(DataSource source) {
		if (!isVisible(source)) {
			return;
		}
		final int colIndex = 0;
		Column col = source.getColumn(colIndex);
        List<Slice> dataSlices = new ArrayList<Slice>(col.size());
        slices.put(source, dataSlices);

        double start = 0.0;
        for (Comparable<?> cell : col) {
            Number numericCell = (Number) cell;
            double value = 0.0;
            if (MathUtils.isCalculatable(numericCell)) {
                value = numericCell.doubleValue();
            }
            // abs() is required because negative values cause
            // "empty" slices
            double span = Math.abs(value);
            Slice slice = new Slice(start, start + span);
            dataSlices.add(slice);
            start += span;
        }
	}

	/**
	 * Rebuilds cached information for a specified data source.
	 * @param source Data source.
	 */
	protected void revalidate(DataSource source) {
		slices.remove(source);
		autoScaleAxes();
	}

	@Override
	public void settingChanged(SettingChangeEvent event) {
		super.settingChanged(event);
		Key key = event.getKey();

		AxisRenderer axisRenderer = getAxisRenderer(PiePlot.AXIS_TANGENTIAL);
		if ((START.equals(key) || CLOCKWISE.equals(key)) && axisRenderer != null) {
			Shape shape = axisRenderer.<Shape>getSetting(AxisRenderer.SHAPE);

			if (shape != null) {
				if (START.equals(key) && event.getValOld() != null) {
					double startOld = ((Number) event.getValOld()).doubleValue();
					double startNew = ((Number) event.getValNew()).doubleValue();
					double delta = Math.toRadians(startOld - startNew);
					AffineTransform tx = AffineTransform.getRotateInstance(delta);
					shape = tx.createTransformedShape(shape);
					axisRenderer.setSetting(AxisRenderer.SHAPE, shape);
				} else if (CLOCKWISE.equals(key)) {
					shape = GeometryUtils.reverse(shape);
					axisRenderer.setSetting(AxisRenderer.SHAPE, shape);
				}
			}
		}
	}

	@Override
	public void dataAdded(DataSource source, DataChangeEvent... events) {
		super.dataAdded(source, events);
		revalidate(source);
	}

	@Override
	public void dataUpdated(DataSource source, DataChangeEvent... events) {
		super.dataUpdated(source, events);
		revalidate(source);
	}

	@Override
	public void dataRemoved(DataSource source, DataChangeEvent... events) {
		super.dataRemoved(source, events);
		revalidate(source);
	}
}

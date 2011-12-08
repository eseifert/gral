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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.erichseifert.gral.AbstractDrawable;
import de.erichseifert.gral.AbstractNavigator;
import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.DrawingContext;
import de.erichseifert.gral.Navigable;
import de.erichseifert.gral.Navigator;
import de.erichseifert.gral.PlotArea;
import de.erichseifert.gral.data.Column;
import de.erichseifert.gral.data.DataListener;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.Row;
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
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.PointND;


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
public class PiePlot extends Plot implements DataListener, Navigable {
	/** Key for specifying the x-axis of an xy-plot. */
	public static String AXIS_PIE = "pie"; //$NON-NLS-1$

	/** Key for specifying {@link java.awt.Point2D} instance defining the
	center of the pie. The coordinates must be relative to the plot area
	dimensions, i.e. 0.0 means left/top, 0.5 means the center, and 1.0 means
	right/bottom. */
	public static final Key CENTER =
		new Key("pieplot.center"); //$NON-NLS-1$
	/** Key for specifying a {@link java.lang.Number} value for the radius of
	the pie relative to the plot area size. */
	public static final Key RADIUS =
		new Key("pieplot.radius"); //$NON-NLS-1$
	/** Key for specifying a {@link java.lang.Number} value for the
	starting angle of the first segment in degrees. The angle is applied
	counterclockwise. */
	public static final Key START =
		new Key("pieplot.start"); //$NON-NLS-1$
	/** Key for specifying a {@link java.lang.Boolean} value which decides
	whether the segments should be ordered clockwise ({@code true}) or
	counterclockwise ({@code false}). */
	public static final Key CLOCKWISE =
		new Key("pieplot.clockwise"); //$NON-NLS-1$

	/** Mapping from data source to point renderer. */
	private final Map<DataSource, PointRenderer> pointRenderers;
	/** Accumulated absolute values of the data source used to calculate the
	slice positions. */
	private final Map<DataSource, List<Double>> slicePositions;
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
		protected synchronized void drawPlot(DrawingContext context) {
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
	 * A point renderer for a single slice in a pie plot.
	 */
	public static class PieSliceRenderer extends AbstractPointRenderer {
		/** Key for specifying a {@link java.lang.Number} value for the outer
		radius of the pie relative to the radius set in the plot. */
		public static final Key RADIUS_OUTER =
			new Key("pieplot.radius.inner"); //$NON-NLS-1$
		/** Key for specifying a {@link java.lang.Number} value for the inner
		radius of the pie relative to the radius set in the plot. */
		public static final Key RADIUS_INNER =
			new Key("pieplot.radius.inner"); //$NON-NLS-1$
		/** Key for specifying an instance of
		{@link de.erichseifert.gral.plots.colors.ColorMapper} used for coloring
		the segments. */
		public static final Key COLORS =
			new Key("pieplot.colorlist"); //$NON-NLS-1$
		/** Key for specifying a {@link java.lang.Number} value for the width of
		gaps between the segments. */
		public static final Key GAP =
			new Key("pieplot.gap"); //$NON-NLS-1$
		private final PiePlot plot;

		/**
		 * Initializes a new instance with a pie plot object.
		 * @param plot Pie plot.
		 */
		public PieSliceRenderer(PiePlot plot) {
			this.plot =  plot;

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

					double plotAreaSize = Math.min(getWidth(), getHeight());
					double sizeRel = plot.<Number>getSetting(
						PiePlot.RADIUS).doubleValue();
					double sizeRelOuter = renderer.<Number>getSetting(
							PieSliceRenderer.RADIUS_OUTER).doubleValue();
					double size = plotAreaSize*sizeRel;
					double sizeOuter = size*sizeRelOuter;

					// Construct slice
					double valueStart = 0.0;
					if (row.getIndex() > 0) {
						valueStart = plot.getSlicePosition(
							row.getSource(), row.getIndex() - 1);
					}
					double valueEnd = plot.getSlicePosition(
						row.getSource(), row.getIndex());
					double valueMax = axis.getMax().doubleValue();

					double sliceStartRel = valueStart/valueMax;
					double sliceEndRel = valueEnd/valueMax;

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
						-sizeOuter/2d, -sizeOuter/2.0,
						sizeOuter, sizeOuter,
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

					double sizeRelInner = renderer.<Number>getSetting(
						PieSliceRenderer.RADIUS_INNER).doubleValue();
					if (sizeRelInner > 0.0 && sizeRelInner < sizeRelOuter) {
						double sizeInner = size*sizeRelInner;
						Ellipse2D inner = new Ellipse2D.Double(
							-sizeInner/2d, -sizeInner/2d, sizeInner, sizeInner);
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
			throw new UnsupportedOperationException("Not available for pie plots.");
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
		slicePositions = new HashMap<DataSource, List<Double>>();

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
	public void refresh() {
		// Remove obsolete calculations
		List<DataSource> sources = getVisibleData();
		for (DataSource data : slicePositions.keySet()) {
			if (!sources.contains(data)) {
				slicePositions.remove(data);
			}
		}

		// Maintain a list of slice positions in world/data units for each
		// data source
		int colIndex = 0;
		for (DataSource data : sources) {
			Column col = data.getColumn(colIndex);
			List<Double> positions = slicePositions.get(data);
			if (positions != null) {
				positions.clear();
			} else {
				positions = new ArrayList<Double>(col.size());
				slicePositions.put(data, positions);
			}
			double sum = 0.0;
			for (Comparable<?> cell : col) {
				Number numericCell = (Number) cell;
				double value = 0.0;
				if (MathUtils.isCalculatable(numericCell)) {
					value = numericCell.doubleValue();
				}
				// abs is required because negative values cause "empty" slices
				sum += Math.abs(value);
				positions.add(sum);
			}
			slicePositions.put(data, positions);
		}

		// The maximum values could have been changed, so adjust all axes
		autoScaleAxes();
	}

	@Override
	protected void createDefaultAxes() {
		// Create x axis and y axis by default
		Axis axisPie = new Axis();
		setAxis(AXIS_PIE, axisPie);
	}

	@Override
	protected void autoScaleAxes() {
		List<DataSource> sources = getVisibleData();
		if (sources.isEmpty()) {
			return;
		}

		DataSource data = sources.get(0);
		List<Double> positions = slicePositions.get(data);
		if (positions == null || positions.isEmpty()) {
			return;
		}

		double sum = positions.get(positions.size() - 1);
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
		Shape shape = new Ellipse2D.Double(-100.0, -100.0, 200.0, 200.0);
		renderer.setSetting(AxisRenderer.SHAPE, shape);
		// Don't show axis
		renderer.setSetting(AxisRenderer.SHAPE_VISIBLE, false);

		setAxisRenderer(AXIS_PIE, renderer);
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
		setMapping(source, AXIS_PIE);
	}

	@Override
	public boolean remove(DataSource source) {
		boolean removed = super.remove(source);
		slicePositions.remove(source);
		return removed;
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
	protected Double getSlicePosition(DataSource source, int index) {
		List<Double> positions = slicePositions.get(source);
		if (positions == null) {
			return null;
		}
		return positions.get(index);
	}

	@Override
	public <T> void setSetting(Key key, T value) {
		T valueOld = this.<T>getSetting(key);
		super.setSetting(key, value);
		if (value == null || value.equals(valueOld)) {
			return;
		}
		if (START.equals(key) || CLOCKWISE.equals(key)) {
			AxisRenderer axisRenderer = getAxisRenderer(PiePlot.AXIS_PIE);
			Shape shape = axisRenderer.<Shape>getSetting(AxisRenderer.SHAPE);

			if (shape != null) {
				if (START.equals(key)) {
					double startOld = ((Number) valueOld).doubleValue();
					double startNew = ((Number) value).doubleValue();
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
	};
}

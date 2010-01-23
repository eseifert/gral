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

package openjchart.plots.axes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.Format;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import openjchart.AbstractDrawable;
import openjchart.Drawable;
import openjchart.plots.Label;
import openjchart.plots.axes.Tick2D.TickType;
import openjchart.util.GeometryUtils;
import openjchart.util.GraphicsUtils;
import openjchart.util.MathUtils;
import openjchart.util.SettingChangeEvent;
import openjchart.util.Settings;
import openjchart.util.SettingsListener;

/**
 * Abstract class that provides function for rendering 2-dimensional axes.
 * Functionality includes:
 * <ul>
 * <li>Calculating tick positions of an axis</li>
 * <li>Calculating tick normals</li>
 * <li>Administration of settings</li>
 * </ul>
 */
public abstract class AbstractAxisRenderer2D implements AxisRenderer2D, SettingsListener {
	private final Settings settings;

	private Line2D[] shapeLines;
	private Point2D[] shapeLineNormals;
	private double[] shapeSegmentLengths;
	private double[] shapeLengths;

	/**
	 * Creates a new AbstractAxisRenderer2D object with default settings.
	 */
	public AbstractAxisRenderer2D() {
		settings = new Settings(this);

		setSettingDefault(KEY_INTERSECTION, 0.0);

		setSettingDefault(KEY_SHAPE_DIRECTION_SWAPPED, false);
		setSettingDefault(KEY_SHAPE, new Line2D.Double(0.0, 0.0, 1.0, 0.0));
		setSettingDefault(KEY_SHAPE_NORMAL_ORIENTATION_CLOCKWISE, false);
		setSettingDefault(KEY_SHAPE_STROKE, new BasicStroke());
		setSettingDefault(KEY_SHAPE_COLOR, Color.BLACK);

		setSettingDefault(KEY_TICKS, true);
		setSettingDefault(KEY_TICKS_SPACING, 1.0);
		setSettingDefault(KEY_TICKS_LENGTH, 1.0);
		setSettingDefault(KEY_TICKS_STROKE, new BasicStroke());
		setSettingDefault(KEY_TICKS_ALIGNMENT, 0.5);
		setSettingDefault(KEY_TICKS_COLOR, Color.BLACK);

		setSettingDefault(KEY_TICK_LABELS_FORMAT, NumberFormat.getInstance());
		setSettingDefault(KEY_TICK_LABELS_DISTANCE, 1.0);
		setSettingDefault(KEY_TICK_LABELS_OUTSIDE, true);
		setSettingDefault(KEY_TICK_LABELS_ROTATION, 0.0);

		setSettingDefault(KEY_TICKS_CUSTOM, null);

		setSettingDefault(KEY_TICKS_MINOR, true);
		setSettingDefault(KEY_TICKS_MINOR_COUNT, 1);
		setSettingDefault(KEY_TICKS_MINOR_LENGTH, 0.5);
		setSettingDefault(KEY_TICKS_MINOR_STROKE, new BasicStroke());
		setSettingDefault(KEY_TICKS_MINOR_ALIGNMENT, 0.5);
		setSettingDefault(KEY_TICKS_MINOR_COLOR, Color.BLACK);

		setSettingDefault(KEY_LABEL, null);
		setSettingDefault(KEY_LABEL_DISTANCE, 1.0);
		setSettingDefault(KEY_LABEL_ROTATION, 0.0);
		setSettingDefault(KEY_LABEL_COLOR, Color.BLACK);
	}

	@Override
	public Drawable getRendererComponent(final Axis axis) {
		final Drawable component = new AbstractDrawable() {
			@Override
			public void draw(Graphics2D g2d) {
				if (shapeLines == null || shapeLines.length == 0) {
					return;
				}

				// Remember old state of Graphics2D instance
				AffineTransform txOrig = g2d.getTransform();
				g2d.translate(getX(), getY());
				Stroke strokeOld = g2d.getStroke();
				Paint paintOld = g2d.getPaint();

				// Draw axis shape
				Paint axisPaint = AbstractAxisRenderer2D.this.<Paint>getSetting(KEY_SHAPE_COLOR);
				Stroke axisStroke = AbstractAxisRenderer2D.this.<Stroke>getSetting(KEY_SHAPE_STROKE);
				Shape shape = AbstractAxisRenderer2D.this.<Shape>getSetting(KEY_SHAPE);
				GraphicsUtils.drawPaintedShape(g2d, shape, axisPaint, null, axisStroke);

				final double fontHeight = 10.0;

				// Draw ticks
				boolean drawTicksMajor = getSetting(KEY_TICKS);
				boolean drawTicksMinor = getSetting(KEY_TICKS_MINOR);
				if (drawTicksMajor || drawTicksMinor) {
					List<Tick2D> ticks = getTicks(axis);  // Calculate tick positions (in pixel coordinates)

					boolean isTickLabelOutside = AbstractAxisRenderer2D.this.<Boolean>getSetting(KEY_TICK_LABELS_OUTSIDE);
					double tickLabelRotation = AbstractAxisRenderer2D.this.<Double>getSetting(KEY_TICK_LABELS_ROTATION);
					double tickLabelDist = AbstractAxisRenderer2D.this.<Double>getSetting(KEY_TICK_LABELS_DISTANCE)*fontHeight;
					Line2D tickShape = new Line2D.Double();

					for (Tick2D tick : ticks) {
						Point2D tickPoint = tick.getPosition();
						Point2D tickNormal = tick.getNormal();

						// Draw tick
						if (tickPoint == null || tickNormal == null) {
							continue;
						}

						double tickLength = AbstractAxisRenderer2D.this.<Double>getSetting(KEY_TICKS_LENGTH)*fontHeight;
						double tickAlignment = AbstractAxisRenderer2D.this.<Double>getSetting(KEY_TICKS_ALIGNMENT);
						Paint tickPaint = AbstractAxisRenderer2D.this.<Paint>getSetting(KEY_TICKS_COLOR);
						Stroke tickStroke = AbstractAxisRenderer2D.this.<Stroke>getSetting(KEY_TICKS_STROKE);
						if (TickType.MINOR.equals(tick.getType())) {
							tickLength = AbstractAxisRenderer2D.this.<Double>getSetting(KEY_TICKS_MINOR_LENGTH)*fontHeight;
							tickAlignment = AbstractAxisRenderer2D.this.<Double>getSetting(KEY_TICKS_MINOR_ALIGNMENT);
							tickPaint = AbstractAxisRenderer2D.this.<Paint>getSetting(KEY_TICKS_MINOR_COLOR);
							tickStroke = AbstractAxisRenderer2D.this.<Stroke>getSetting(KEY_TICKS_MINOR_STROKE);
						}

						double tickLengthInner = tickLength*(tickAlignment);
						double tickLengthOuter = tickLength*(1.0 - tickAlignment);
						tickShape.setLine(
							tickPoint.getX() - tickNormal.getX()*tickLengthInner, tickPoint.getY() - tickNormal.getY()*tickLengthInner,
							tickPoint.getX() + tickNormal.getX()*tickLengthOuter, tickPoint.getY() + tickNormal.getY()*tickLengthOuter
						);
						GraphicsUtils.drawPaintedShape(g2d, tickShape, tickPaint, null, tickStroke);

						// Draw label
						if (TickType.MAJOR.equals(tick.getType()) || TickType.CUSTOM.equals(tick.getType())) {
							String tickLabelText = tick.getLabel();
							if (tickLabelText != null && !tickLabelText.trim().isEmpty()) {
								Label tickLabel = new Label(tickLabelText);
								// TODO: Allow separate colors for ticks and tick labels?
								tickLabel.setSetting(Label.KEY_COLOR, tickPaint);
								double labelDist = tickLengthOuter + tickLabelDist;
								layoutLabel(tickLabel, tickPoint, tickNormal, labelDist, isTickLabelOutside, tickLabelRotation);
								tickLabel.draw(g2d);
							}
						}
					}
				}

				// Draw axis label
				String labelText = AbstractAxisRenderer2D.this.<String>getSetting(KEY_LABEL);
				if (labelText != null && !labelText.trim().isEmpty()) {
					Label axisLabel = new Label(labelText);
					axisLabel.setSetting(Label.KEY_COLOR, AbstractAxisRenderer2D.this.<Paint>getSetting(KEY_LABEL_COLOR));

					// FIXME: use tick label height instead of constant value
					double tickLength = AbstractAxisRenderer2D.this.<Double>getSetting(KEY_TICKS_LENGTH)*fontHeight;
					double tickAlignment = AbstractAxisRenderer2D.this.<Double>getSetting(KEY_TICKS_ALIGNMENT);
					double tickLengthOuter = tickLength*(1.0 - tickAlignment);
					double tickLabelDist = AbstractAxisRenderer2D.this.<Double>getSetting(KEY_TICK_LABELS_DISTANCE)*fontHeight;

					double labelDistance = AbstractAxisRenderer2D.this.<Double>getSetting(KEY_LABEL_DISTANCE)*fontHeight;
					double labelDist = tickLengthOuter + tickLabelDist + fontHeight + labelDistance;
					double labelRotation = AbstractAxisRenderer2D.this.<Double>getSetting(KEY_LABEL_ROTATION);
					double axisLabelPos = (axis.getMin().doubleValue() + axis.getMax().doubleValue()) * 0.5;
					boolean isTickLabelOutside = AbstractAxisRenderer2D.this.<Boolean>getSetting(KEY_TICK_LABELS_OUTSIDE);

					Point2D labelPos = getPosition(axis, axisLabelPos, false, true);
					Point2D labelNormal = getNormal(axis, axisLabelPos, false, true);
					layoutLabel(axisLabel, labelPos, labelNormal, labelDist, isTickLabelOutside, labelRotation);

					axisLabel.draw(g2d);
				}

				g2d.setPaint(paintOld);
				g2d.setStroke(strokeOld);
				g2d.setTransform(txOrig);
			}

			private void layoutLabel(Label label, Point2D labelPos, Point2D labelNormal, double labelDist, boolean isLabelOutside, double rotation) {
				Rectangle2D labelSize = label.getTextRectangle();
				Shape marginShape = new Rectangle2D.Double(
					0, 0,
					labelSize.getWidth() + 2.0*labelDist, labelSize.getHeight() + 2.0*labelDist
				);
				Rectangle2D marginBounds = marginShape.getBounds2D();
				label.setSetting(Label.KEY_ROTATION, rotation);
				if ((rotation%360.0) != 0.0) {
					marginShape = AffineTransform.getRotateInstance(
						-rotation/180.0*Math.PI,
						marginBounds.getCenterX(),
						marginBounds.getCenterY()
					).createTransformedShape(marginShape);
				}
				marginBounds = marginShape.getBounds2D();

				double intersRayLength = marginBounds.getHeight()*marginBounds.getHeight() + marginBounds.getWidth()*marginBounds.getWidth();
				List<Point2D> descriptionBoundsIntersections = GeometryUtils.intersection(
					marginBounds,
					new Line2D.Double(
						marginBounds.getCenterX(),
						marginBounds.getCenterY(),
						marginBounds.getCenterX() + (isLabelOutside?-1.0:1.0)*labelNormal.getX()*intersRayLength,
						marginBounds.getCenterY() + (isLabelOutside?-1.0:1.0)*labelNormal.getY()*intersRayLength
					)
				);
				if (!descriptionBoundsIntersections.isEmpty()) {
					Point2D inters = descriptionBoundsIntersections.get(0);
					double intersX = inters.getX() - marginBounds.getCenterX();
					double intersY = inters.getY() - marginBounds.getCenterY();
					double posX = labelPos.getX() - intersX - labelSize.getWidth()/2.0;
					double posY = labelPos.getY() - intersY - labelSize.getHeight()/2.0;

					label.setBounds(posX, posY, labelSize.getWidth(), labelSize.getHeight());
				}
			}

			@Override
			public Dimension2D getPreferredSize() {
				// FIXME: use real font height instead of fixed value
				final double fontHeight = 10.0;
				double tickLength = getSetting(KEY_TICKS_LENGTH);
				tickLength *= fontHeight;
				double tickAlignment = getSetting(KEY_TICKS_ALIGNMENT);
				double tickLengthOuter = tickLength*(1.0 - tickAlignment);
				double labelDistance = getSetting(KEY_TICK_LABELS_DISTANCE);
				double labelDist = labelDistance*fontHeight + tickLengthOuter;
				double minSize = fontHeight + labelDist + tickLengthOuter;
				return new openjchart.util.Dimension2D.Double(minSize, minSize);
			}
		};

		return component;
	}

	@Override
	public List<Tick2D> getTicks(Axis axis) {
		double tickSpacing = getSetting(KEY_TICKS_SPACING);
		int ticksMinorCount = getSetting(KEY_TICKS_MINOR_COUNT);
		double tickSpacingMinor = (ticksMinorCount > 0) ? tickSpacing/(ticksMinorCount + 1) : tickSpacing;

		double min = axis.getMin().doubleValue();
		double max = axis.getMax().doubleValue();
		double minTick = MathUtils.ceil(min, tickSpacing);
		double maxTick = MathUtils.floor(max, tickSpacing);

		List<Tick2D> ticks = new LinkedList<Tick2D>();
		Set<Double> tickPositions = new HashSet<Double>();
		Set<Double> tickPositionsCustom = getTickPositionsCustom();

		// Add major ticks
		for (double tickPositionWorld = minTick; tickPositionWorld <= maxTick; tickPositionWorld += tickSpacingMinor) {
			TickType tickType = MathUtils.almostEqual(tickPositionWorld % tickSpacing, 0.0, 1e-14) ? TickType.MAJOR : TickType.MINOR;
			Tick2D major = getTick(tickType, axis, tickPositionWorld);
			if (major.getPosition() != null
					&& !tickPositionsCustom.contains(tickPositionWorld)
					&& !tickPositions.contains(tickPositionWorld)) {
				ticks.add(major);
				tickPositions.add(tickPositionWorld);
			}
		}
		// Add custom ticks
		Map<Double, String> labelsCustom = getSetting(KEY_TICKS_CUSTOM);
		if (labelsCustom != null) {
			for (Map.Entry<Double, String> entry : labelsCustom.entrySet()) {
				Tick2D tick = getTick(TickType.CUSTOM, axis, entry.getKey());
				ticks.add(tick);
			}
		}

		return ticks;
	}

	protected Set<Double> getTickPositionsCustom() {
		Map<Double, String> labelsCustom = getSetting(KEY_TICKS_CUSTOM);
		if (labelsCustom != null) {
			return labelsCustom.keySet();
		}
		return new HashSet<Double>();
	}

	/**
	 * Returns the point of the tick (in pixel coordinates) on the
	 * specified axis with the specified value.
	 * @param axis Axis containing the tick.
	 * @param tickPositionWorld Displayed value on the axis.
	 * @return DataPoint2D of the desired tick.
	 */
	protected Tick2D getTick(TickType type, Axis axis, double tickPositionWorld) {
		// Calculate position of tick on axis shape
		Point2D tickPoint = getPosition(axis, tickPositionWorld, false, false);

		// Calculate tick normal
		Point2D tickNormal = getNormal(axis, tickPositionWorld, false, false);

		// Retrieve tick label
		String tickLabel;
		Map<Double, String> labelsCustom = getSetting(KEY_TICKS_CUSTOM);
		if (labelsCustom != null && labelsCustom.containsKey(tickPositionWorld)) {
			tickLabel = labelsCustom.get(tickPositionWorld);
		} else {
			Format labelFormat = getSetting(KEY_TICK_LABELS_FORMAT);
			tickLabel = labelFormat.format(tickPositionWorld);
		}

		Tick2D tick = new Tick2D(type, tickPoint, tickNormal, null, null, tickLabel);
		return tick;
	}

	@Override
	public Point2D getNormal(Axis axis, Number value, boolean extrapolate, boolean forceLinear) {
		double valueView;
		if (forceLinear) {
			valueView = (value.doubleValue() - axis.getMin().doubleValue())/axis.getRange()*getShapeLength();
		} else {
			valueView = worldToView(axis, value, extrapolate);
		}

		int segmentIndex = MathUtils.binarySearchFloor(shapeLengths, valueView);
		if (segmentIndex < 0 || segmentIndex >= shapeLines.length) {
			return null;
		}

		segmentIndex = MathUtils.limit(segmentIndex, 0, shapeLineNormals.length - 1);
		double normalOrientation = AbstractAxisRenderer2D.this.<Boolean>getSetting(KEY_SHAPE_NORMAL_ORIENTATION_CLOCKWISE) ? 1.0 : -1.0;
		Point2D tickNormal = new Point2D.Double(
			normalOrientation * shapeLineNormals[segmentIndex].getX(),
			normalOrientation * shapeLineNormals[segmentIndex].getY()
		);

		return tickNormal;
	}

	/**
	 * Returns the length of the shape path which is used to render axes.
	 * @return Shape length.
	 */
	protected double getShapeLength() {
		if (shapeLengths == null || shapeLengths.length == 0) {
			return 0.0;
		}
		return shapeLengths[shapeLengths.length - 1];
	}

	@Override
	public Point2D getPosition(Axis axis, Number value, boolean extrapolate, boolean forceLinear) {
		if (shapeLines == null || shapeLines.length == 0) {
			return null;
		}

		double valueView;
		if (forceLinear) {
			valueView = (value.doubleValue() - axis.getMin().doubleValue())/axis.getRange()*getShapeLength();
		} else {
			valueView = worldToView(axis, value, extrapolate);
		}

		if (Double.isNaN(valueView) || Double.isInfinite(valueView)) {
			return null;
		}

		if (valueView <= 0.0 || valueView >= getShapeLength()) {
			if (extrapolate) {
				// do linear extrapolation if point lies outside of shape
				int segmentIndex = (valueView <= 0.0) ? 0 : shapeLines.length - 1;
				Line2D segment = shapeLines[segmentIndex];
				double segmentLen = shapeSegmentLengths[segmentIndex];
				double shapeLen = shapeLengths[segmentIndex];
				return new Point2D.Double(
					segment.getX1() + (segment.getX2() - segment.getX1())/segmentLen * (valueView - shapeLen),
					segment.getY1() + (segment.getY2() - segment.getY1())/segmentLen * (valueView - shapeLen)
				);
			} else {
				if (valueView <= 0.0) {
					return shapeLines[0].getP1();
				} else {
					return shapeLines[shapeLines.length - 1].getP2();
				}
			}
		}

		// Determine to which segment the value belongs using a binary search
		int i = MathUtils.binarySearchFloor(shapeLengths, valueView);

		if (i < 0 || i >= shapeLines.length) {
			return null;
		}
		Line2D line = shapeLines[i];

		double posRel = (valueView - shapeLengths[i]) / shapeSegmentLengths[i];
		Point2D pos = new Point2D.Double(
			line.getX1() + (line.getX2() - line.getX1())*posRel,
			line.getY1() + (line.getY2() - line.getY1())*posRel
		);
		return pos;
	}

	/**
	 * Calculates important aspects of the specified shape.
	 * @param shape Shape to be evaluated.
	 */
	protected void evaluateShape(Shape shape) {
		boolean directionSwapped =  getSetting(KEY_SHAPE_DIRECTION_SWAPPED);
		shapeLines = GeometryUtils.shapeToLines(shape, directionSwapped);
		shapeSegmentLengths = new double[shapeLines.length];
		shapeLengths = new double[shapeLines.length + 1];  // First length is always 0.0, last length is the total length
		shapeLineNormals = new Point2D[shapeLines.length];

		if (shapeLines.length == 0) {
			return;
		}

		for (int i = 0; i < shapeLines.length; i++) {
			Line2D line = shapeLines[i];

			// Calculate length of axis shape at each shape segment
			double segmentLength = line.getP1().distance(line.getP2());
			shapeSegmentLengths[i] = segmentLength;
			shapeLengths[i + 1] = shapeLengths[i] + segmentLength;

			// Calculate normalized vector perpendicular to current axis shape segment
			shapeLineNormals[i] = new Point2D.Double(
				 (line.getY2() - line.getY1()) / segmentLength,
				-(line.getX2() - line.getX1()) / segmentLength
			);
		}
	}

	@Override
	public <T> T getSetting(String key) {
		return settings.<T>get(key);
	}

	@Override
	public <T> void setSetting(String key, T value) {
		settings.<T>set(key, value);
	}

	@Override
	public <T> void removeSetting(String key) {
		settings.remove(key);
	}

	@Override
	public <T> void setSettingDefault(String key, T value) {
		settings.<T>setDefault(key, value);
	}

	@Override
	public <T> void removeSettingDefault(String key) {
		settings.removeDefault(key);
	}

	@Override
	public void settingChanged(SettingChangeEvent event) {
		String key = event.getKey();
		if (KEY_SHAPE.equals(key)) {
			evaluateShape((Shape) event.getValNew());
		}
	}

}
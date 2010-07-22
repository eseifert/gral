/*
 * GRAL: GRAphing Library for Java(R)
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

package de.erichseifert.gral.plots.axes;

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

import de.erichseifert.gral.AbstractDrawable;
import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.DrawingContext;
import de.erichseifert.gral.plots.Label;
import de.erichseifert.gral.plots.axes.Tick.TickType;
import de.erichseifert.gral.util.GeometryUtils;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.PointND;
import de.erichseifert.gral.util.SettingChangeEvent;
import de.erichseifert.gral.util.Settings;
import de.erichseifert.gral.util.SettingsListener;
import de.erichseifert.gral.util.Settings.Key;


/**
 * <p>Abstract class that provides function for rendering axes in
 * two-dimensional space.</p>
 * <p>Functionality includes:</p>
 * <ul>
 *   <li>Calculating tick positions of an axis</li>
 *   <li>Calculating tick normals</li>
 *   <li>Administration of settings</li>
 * </ul>
 */
public abstract class AbstractAxisRenderer2D implements AxisRenderer, SettingsListener {
	private final Settings settings;

	private Line2D[] shapeLines;
	private Point2D[] shapeLineNormals;
	private double[] shapeSegmentLengths;
	private double[] shapeLengths;

	/**
	 * Initializes a new <code>AbstractAxisRenderer2D</code> instances with
	 * default settings.
	 */
	public AbstractAxisRenderer2D() {
		settings = new Settings(this);

		setSettingDefault(INTERSECTION, 0.0);

		setSettingDefault(SHAPE_DIRECTION_SWAPPED, false);  // Must be set before SHAPE
		setSettingDefault(SHAPE, new Line2D.Double(0.0, 0.0, 1.0, 0.0));
		setSettingDefault(SHAPE_VISIBLE, true);
		setSettingDefault(SHAPE_NORMAL_ORIENTATION_CLOCKWISE, false);
		setSettingDefault(SHAPE_STROKE, new BasicStroke());
		setSettingDefault(SHAPE_COLOR, Color.BLACK);
		setSettingDefault(SHAPE_DIRECTION_SWAPPED, false);

		setSettingDefault(TICKS, true);
		setSettingDefault(TICKS_SPACING, 1.0);
		setSettingDefault(TICKS_LENGTH, 1.0);
		setSettingDefault(TICKS_STROKE, new BasicStroke());
		setSettingDefault(TICKS_ALIGNMENT, 0.5);
		setSettingDefault(TICKS_COLOR, Color.BLACK);

		setSettingDefault(TICK_LABELS, true);
		setSettingDefault(TICK_LABELS_FORMAT, NumberFormat.getInstance());
		setSettingDefault(TICK_LABELS_DISTANCE, 1.0);
		setSettingDefault(TICK_LABELS_OUTSIDE, true);
		setSettingDefault(TICK_LABELS_ROTATION, 0.0);

		setSettingDefault(TICKS_CUSTOM, null);

		setSettingDefault(TICKS_MINOR, true);
		setSettingDefault(TICKS_MINOR_COUNT, 1);
		setSettingDefault(TICKS_MINOR_LENGTH, 0.5);
		setSettingDefault(TICKS_MINOR_STROKE, new BasicStroke());
		setSettingDefault(TICKS_MINOR_ALIGNMENT, 0.5);
		setSettingDefault(TICKS_MINOR_COLOR, Color.BLACK);

		setSettingDefault(LABEL, null);
		setSettingDefault(LABEL_DISTANCE, 1.0);
		setSettingDefault(LABEL_ROTATION, 0.0);
		setSettingDefault(LABEL_COLOR, Color.BLACK);
	}

	@Override
	public Drawable getRendererComponent(final Axis axis) {
		final Drawable component = new AbstractDrawable() {
			@Override
			public void draw(DrawingContext context) {
				if (shapeLines == null || shapeLines.length == 0) {
					return;
				}

				Graphics2D graphics = context.getGraphics();

				// Remember old state of Graphics2D instance
				AffineTransform txOrig = graphics.getTransform();
				graphics.translate(getX(), getY());
				Stroke strokeOld = graphics.getStroke();
				Paint paintOld = graphics.getPaint();

				// Draw axis shape
				Paint axisPaint = AbstractAxisRenderer2D.this.getSetting(SHAPE_COLOR);
				Stroke axisStroke = AbstractAxisRenderer2D.this.getSetting(SHAPE_STROKE);
				boolean isShapeVisible =
					AbstractAxisRenderer2D.this.<Boolean>getSetting(SHAPE_VISIBLE);
				if (isShapeVisible) {
					Shape shape = AbstractAxisRenderer2D.this.getSetting(SHAPE);
					GraphicsUtils.drawPaintedShape(graphics, shape, axisPaint, null, axisStroke);
				}

				// TODO: Use real font size
				final double fontHeight = 10.0;

				// Draw ticks
				boolean drawTicksMajor =
					AbstractAxisRenderer2D.this.<Boolean>getSetting(TICKS);
				boolean drawTicksMinor =
					AbstractAxisRenderer2D.this.<Boolean>getSetting(TICKS_MINOR);
				if (drawTicksMajor || drawTicksMinor) {
					List<Tick> ticks = getTicks(axis);  // Calculate tick positions (in pixel coordinates)

					boolean isTickLabelVisible =
						AbstractAxisRenderer2D.this.<Boolean>getSetting(TICK_LABELS);
					boolean isTickLabelOutside =
						AbstractAxisRenderer2D.this.<Boolean>getSetting(TICK_LABELS_OUTSIDE);
					double tickLabelRotation =
						AbstractAxisRenderer2D.this.<Number>getSetting(TICK_LABELS_ROTATION)
						.doubleValue();
					double tickLabelDist =
						AbstractAxisRenderer2D.this.<Number>getSetting(TICK_LABELS_DISTANCE)
						.doubleValue()*fontHeight;
					Line2D tickShape = new Line2D.Double();

					for (Tick tick : ticks) {
						// Draw tick
						if (tick.getPosition() == null || tick.getNormal() == null) {
							continue;
						}
						Point2D tickPoint = tick.getPosition().getPoint2D();
						Point2D tickNormal = tick.getNormal().getPoint2D();

						double tickLength;
						double tickAlignment;
						Paint tickPaint;
						Stroke tickStroke;
						if (TickType.MINOR.equals(tick.getType())) {
							tickLength =
								AbstractAxisRenderer2D.this.<Number>getSetting(TICKS_MINOR_LENGTH)
								.doubleValue()*fontHeight;
							tickAlignment =
								AbstractAxisRenderer2D.this.<Number>getSetting(TICKS_MINOR_ALIGNMENT)
								.doubleValue();
							tickPaint =
								AbstractAxisRenderer2D.this.<Paint>getSetting(TICKS_MINOR_COLOR);
							tickStroke =
								AbstractAxisRenderer2D.this.<Stroke>getSetting(TICKS_MINOR_STROKE);
						} else {
							tickLength =
								AbstractAxisRenderer2D.this.<Number>getSetting(TICKS_LENGTH)
								.doubleValue()*fontHeight;
							tickAlignment =
								AbstractAxisRenderer2D.this.<Number>getSetting(TICKS_ALIGNMENT)
								.doubleValue();
							tickPaint =
								AbstractAxisRenderer2D.this.<Paint>getSetting(TICKS_COLOR);
							tickStroke =
								AbstractAxisRenderer2D.this.<Stroke>getSetting(TICKS_STROKE);
						}

						double tickLengthInner = tickLength*tickAlignment;
						double tickLengthOuter = tickLength*(1.0 - tickAlignment);
						tickShape.setLine(
							tickPoint.getX() - tickNormal.getX()*tickLengthInner,
							tickPoint.getY() - tickNormal.getY()*tickLengthInner,
							tickPoint.getX() + tickNormal.getX()*tickLengthOuter,
							tickPoint.getY() + tickNormal.getY()*tickLengthOuter
						);
						GraphicsUtils.drawPaintedShape(graphics, tickShape, tickPaint, null, tickStroke);

						// Draw label
						if (isTickLabelVisible && (TickType.MAJOR.equals(tick.getType()) ||
								TickType.CUSTOM.equals(tick.getType()))) {
							String tickLabelText = tick.getLabel();
							if (tickLabelText != null && !tickLabelText.trim().isEmpty()) {
								Label tickLabel = new Label(tickLabelText);
								// TODO: Allow separate colors for ticks and tick labels?
								tickLabel.setSetting(Label.COLOR, tickPaint);
								double labelDist = tickLengthOuter + tickLabelDist;
								layoutLabel(tickLabel, tickPoint, tickNormal,
										labelDist, isTickLabelOutside, tickLabelRotation);
								tickLabel.draw(context);
							}
						}
					}
				}

				// Draw axis label
				String labelText = AbstractAxisRenderer2D.this.<String>getSetting(LABEL);
				if (labelText != null && !labelText.trim().isEmpty()) {
					Label axisLabel = new Label(labelText);
					axisLabel.setSetting(Label.COLOR,
							AbstractAxisRenderer2D.this.<Paint>getSetting(LABEL_COLOR));

					// FIXME: use tick label height instead of constant value
					double tickLength =
						AbstractAxisRenderer2D.this.<Number>getSetting(TICKS_LENGTH)
						.doubleValue()*fontHeight;
					double tickAlignment =
						AbstractAxisRenderer2D.this.<Number>getSetting(TICKS_ALIGNMENT)
						.doubleValue();
					double tickLengthOuter = tickLength*(1.0 - tickAlignment);
					double tickLabelDist =
						AbstractAxisRenderer2D.this.<Number>getSetting(TICK_LABELS_DISTANCE)
						.doubleValue()*fontHeight;

					double labelDistance =
						AbstractAxisRenderer2D.this.<Number>getSetting(LABEL_DISTANCE)
						.doubleValue()*fontHeight;
					double labelDist =
						tickLengthOuter + tickLabelDist + fontHeight + labelDistance;
					double labelRotation =
						AbstractAxisRenderer2D.this.<Number>getSetting(LABEL_ROTATION)
						.doubleValue();
					double axisLabelPos =
						(axis.getMin().doubleValue() + axis.getMax().doubleValue()) * 0.5;
					boolean isTickLabelOutside =
						AbstractAxisRenderer2D.this.<Boolean>getSetting(TICK_LABELS_OUTSIDE);

					PointND<Double> labelPos = getPosition(axis, axisLabelPos, false, true);
					PointND<Double> labelNormal = getNormal(axis, axisLabelPos, false, true);

					if (labelPos != null && labelNormal != null) {
						layoutLabel(axisLabel, labelPos.getPoint2D(), labelNormal.getPoint2D(), labelDist,
								isTickLabelOutside, labelRotation);
						axisLabel.draw(context);
					}
				}

				graphics.setPaint(paintOld);
				graphics.setStroke(strokeOld);
				graphics.setTransform(txOrig);
			}

			private void layoutLabel(Label label, Point2D labelPos, Point2D labelNormal,
					double labelDist, boolean isLabelOutside, double rotation) {
				Rectangle2D labelSize = label.getTextRectangle();
				Shape marginShape = new Rectangle2D.Double(
					0, 0,
					labelSize.getWidth() + 2.0*labelDist, labelSize.getHeight() + 2.0*labelDist
				);
				Rectangle2D marginBounds = marginShape.getBounds2D();
				label.setSetting(Label.ROTATION, rotation);
				if ((rotation%360.0) != 0.0) {
					marginShape = AffineTransform.getRotateInstance(
						-rotation/180.0*Math.PI,
						marginBounds.getCenterX(),
						marginBounds.getCenterY()
					).createTransformedShape(marginShape);
				}
				marginBounds = marginShape.getBounds2D();

				double intersRayLength = marginBounds.getHeight()*marginBounds.getHeight() +
					marginBounds.getWidth()*marginBounds.getWidth();
				double intersRayDir = (isLabelOutside?-1.0:1.0)*intersRayLength;
				List<Point2D> descriptionBoundsIntersections = GeometryUtils.intersection(
					marginBounds,
					new Line2D.Double(
						marginBounds.getCenterX(),
						marginBounds.getCenterY(),
						marginBounds.getCenterX() + intersRayDir*labelNormal.getX(),
						marginBounds.getCenterY() + intersRayDir*labelNormal.getY()
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
				double tickLength =
					AbstractAxisRenderer2D.this.<Number>getSetting(TICKS_LENGTH)
					.doubleValue()*fontHeight;
				double tickAlignment =
					AbstractAxisRenderer2D.this.<Number>getSetting(TICKS_ALIGNMENT)
					.doubleValue();
				double tickLengthOuter =
					tickLength*(1.0 - tickAlignment);
				double labelDistance =
					AbstractAxisRenderer2D.this.<Number>getSetting(TICK_LABELS_DISTANCE)
					.doubleValue();
				double labelDist =
					labelDistance*fontHeight + tickLengthOuter;
				double minSize =
					fontHeight + labelDist + tickLengthOuter;
				return new de.erichseifert.gral.util.Dimension2D.Double(minSize, minSize);
			}
		};

		return component;
	}

	@Override
	public List<Tick> getTicks(Axis axis) {
		double tickSpacing = this.<Number>getSetting(TICKS_SPACING).doubleValue();
		int ticksMinorCount = this.<Integer>getSetting(TICKS_MINOR_COUNT);
		double tickSpacingMinor = (ticksMinorCount > 0)
				? tickSpacing/(ticksMinorCount + 1)
				: tickSpacing;

		double min = axis.getMin().doubleValue();
		double max = axis.getMax().doubleValue();
		double minTickMajor = MathUtils.ceil(min, tickSpacing);
		double minTickMinor = MathUtils.ceil(min, tickSpacingMinor);

		int ticksTotal = (int)Math.floor((max - min)/tickSpacingMinor);
		int initialTicksMinor = (int)Math.floor((minTickMajor - min)/tickSpacingMinor);

		List<Tick> ticks = new LinkedList<Tick>();
		Set<Number> tickPositions = new HashSet<Number>();
		Set<Number> tickPositionsCustom = getTickPositionsCustom();

		// Add major and minor ticks
		for (int i = 0; i < ticksTotal; i++) {  // Use integer to avoid rounding errors
			double tickPositionWorld = minTickMinor + i*tickSpacingMinor;
			boolean isMajor = (tickPositions.size() - initialTicksMinor) %
					(ticksMinorCount + 1) == 0;
			TickType tickType = isMajor ? TickType.MAJOR : TickType.MINOR;
			Tick tick = getTick(tickType, axis, tickPositionWorld);
			if (tick.getPosition() != null
					&& !tickPositionsCustom.contains(tickPositionWorld)
					&& !tickPositions.contains(tickPositionWorld)) {
				ticks.add(tick);
				tickPositions.add(tickPositionWorld);
			}
		}
		// Add custom ticks
		Map<Number, String> labelsCustom = getSetting(TICKS_CUSTOM);
		if (labelsCustom != null) {
			for (Number tickPositionWorldObj : labelsCustom.keySet()) {
				double tickPositionWorld = tickPositionWorldObj.doubleValue();
				if (tickPositionWorld >= min && tickPositionWorld <= max) {
					Tick tick = getTick(TickType.CUSTOM, axis, tickPositionWorld);
					ticks.add(tick);
				}
			}
		}

		return ticks;
	}

	/**
	 * Returns a set of all user-defined tick mark positions.
	 * @return Set of all user-defined tick mark positions.
	 */
	protected Set<Number> getTickPositionsCustom() {
		Map<Number, String> labelsCustom = getSetting(TICKS_CUSTOM);
		if (labelsCustom != null) {
			return labelsCustom.keySet();
		}
		return new HashSet<Number>();
	}

	/**
	 * Returns the point of the tick mark (in pixel coordinates) on the
	 * specified axis with the specified value.
	 * @param type Type of tick mark.
	 * @param axis Axis containing the tick mark.
	 * @param tickPositionWorld Displayed value on the axis.
	 * @return Object describing the desired tick mark.
	 */
	protected Tick getTick(TickType type, Axis axis, double tickPositionWorld) {
		// Calculate position of tick on axis shape
		PointND<Double> tickPoint = getPosition(axis, tickPositionWorld, false, false);

		// Calculate tick normal
		PointND<Double> tickNormal = getNormal(axis, tickPositionWorld, false, false);

		// Retrieve tick label
		String tickLabel;
		Map<Number, String> labelsCustom = getSetting(TICKS_CUSTOM);
		if (labelsCustom != null && labelsCustom.containsKey(tickPositionWorld)) {
			tickLabel = labelsCustom.get(tickPositionWorld);
		} else {
			Format labelFormat = getSetting(TICK_LABELS_FORMAT);
			tickLabel = labelFormat.format(tickPositionWorld);
		}

		Tick tick = new Tick(type, tickPoint, tickNormal, null, null, tickLabel);
		return tick;
	}

	@Override
	public PointND<Double> getNormal(Axis axis, Number value, boolean extrapolate, boolean forceLinear) {
		double valueView;
		if (forceLinear) {
			valueView = (value.doubleValue() - axis.getMin().doubleValue()) /
				axis.getRange()*getShapeLength();
		} else {
			valueView = worldToView(axis, value, extrapolate);
		}

		int segmentIndex = MathUtils.binarySearchFloor(shapeLengths, valueView);
		if (segmentIndex < 0 || segmentIndex >= shapeLines.length) {
			return null;
		}

		segmentIndex = MathUtils.limit(segmentIndex, 0, shapeLineNormals.length - 1);
		double normalOrientation =
			AbstractAxisRenderer2D.this.<Boolean>getSetting(SHAPE_NORMAL_ORIENTATION_CLOCKWISE)
			? 1.0 : -1.0;
		PointND<Double> tickNormal = new PointND<Double>(
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
	public PointND<Double> getPosition(Axis axis, Number value,
			boolean extrapolate, boolean forceLinear) {
		if (shapeLines == null || shapeLines.length == 0) {
			return null;
		}

		double valueView;
		if (forceLinear) {
			valueView = (value.doubleValue() - axis.getMin().doubleValue()) /
				axis.getRange()*getShapeLength();
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
				double relLen = (valueView - shapeLen)/segmentLen;
				return new PointND<Double>(
					segment.getX1() + (segment.getX2() - segment.getX1())*relLen,
					segment.getY1() + (segment.getY2() - segment.getY1())*relLen
				);
			} else {
				if (valueView <= 0.0) {
					Point2D p2d = shapeLines[0].getP1();
					return new PointND<Double>(p2d.getX(), p2d.getY());
				} else {
					Point2D p2d = shapeLines[shapeLines.length - 1].getP2();
					return new PointND<Double>(p2d.getX(), p2d.getY());
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
		PointND<Double> pos = new PointND<Double>(
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
		boolean directionSwapped =  this.<Boolean>getSetting(SHAPE_DIRECTION_SWAPPED);
		shapeLines = GeometryUtils.shapeToLines(shape, directionSwapped);
		shapeSegmentLengths = new double[shapeLines.length];
		// First length is always 0.0, last length is the total length
		shapeLengths = new double[shapeLines.length + 1];
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
	public <T> T getSetting(Key key) {
		return settings.<T>get(key);
	}

	@Override
	public <T> void setSetting(Key key, T value) {
		settings.<T>set(key, value);
	}

	@Override
	public <T> void removeSetting(Key key) {
		settings.remove(key);
	}

	@Override
	public <T> void setSettingDefault(Key key, T value) {
		settings.<T>setDefault(key, value);
	}

	@Override
	public <T> void removeSettingDefault(Key key) {
		settings.removeDefault(key);
	}

	@Override
	public void settingChanged(SettingChangeEvent event) {
		Key key = event.getKey();
		if (SHAPE.equals(key)) {
			evaluateShape((Shape) event.getValNew());
		}
	}

}

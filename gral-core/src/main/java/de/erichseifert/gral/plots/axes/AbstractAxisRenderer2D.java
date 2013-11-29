/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.gral.plots.axes;

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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.Format;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.plots.Label;
import de.erichseifert.gral.plots.axes.Tick.TickType;
import de.erichseifert.gral.plots.settings.BasicSettingsStorage;
import de.erichseifert.gral.plots.settings.SettingChangeEvent;
import de.erichseifert.gral.plots.settings.SettingsListener;
import de.erichseifert.gral.util.GeometryUtils;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.PointND;
import de.erichseifert.gral.util.SerializationUtils;


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
public abstract class AbstractAxisRenderer2D extends BasicSettingsStorage
		implements AxisRenderer, SettingsListener {
	/** Version id for serialization. */
	private static final long serialVersionUID = 5623525683845512624L;
	/** Line segments approximating the shape of the axis. */
	private Line2D[] shapeLines;
	/** Normals of the line segments approximating the axis. */
	private Point2D[] shapeLineNormals;
	/** Lengths of the line segments approximating the axis. */
	private double[] shapeSegmentLengths;
	/** Length of the the axis up to a certain approximating line segment. */
	private double[] shapeLengths;

	/** Intersection point of the axis. */
	private Number intersection;
	/** Shape used for drawing. */
	private Shape shape;
	/** Decides whether the shape is drawn. */
	private boolean shapeVisible;
	/** Decides whether the shape normals are orientated clockwise. */
	private boolean shapeNormalOrientationClockwise;
	/** Paint used to draw axis shape, ticks, and labels. */
	private Paint shapeColor;
	/** Stroke used for drawing the axis shape. */
	// Property will be serialized using a wrapper
	private transient Stroke shapeStroke;
	/** Decides whether the axis direction will be changed. */
	private boolean shapeDirectionSwapped;
	/** Decides whether major ticks are drawn. */
	private boolean ticksDrawn;
	/** Distance on axis in which major ticks are drawn. */
	private Number tickSpacing;
	/** Decides whether automatic tick spacing is enabled. */
	private boolean ticksAutoSpaced;
	/** Tick length relative to the font */
	private Number tickLength;
	/** Stroke which is used to draw all major ticks. */
	// Property will be serialized using a wrapper
	private transient Stroke tickStroke;
	/** Alignment of major ticks relative to the axis. */
	private Number tickAlignment;
	/** Font used to display the text of major ticks. */
	private Font tickFont;
	/** Paint used to draw the shapes of major ticks. */
	private Paint tickColor;
	/** Decides whether tick labels will be shown. */
	private boolean tickLabelsEnabled;
	/** Format which converts the tick values to labels. */
	private Format tickLabelFormat;
	/** Distance between labels and ticks relative to the font height. */
	private Number tickLabelDistance;

	/**
	 * Initializes a new instance with default settings.
	 */
	public AbstractAxisRenderer2D() {
		addSettingsListener(this);

		intersection = 0.0;
		// The direction must defined as swapped before the shape is evaluated.
		shapeDirectionSwapped = false;
		shape = new Line2D.Double(0.0, 0.0, 1.0, 0.0);
		evaluateShape(shape);

		shapeVisible = true;
		shapeNormalOrientationClockwise = false;
		shapeStroke = new BasicStroke();
		shapeColor = Color.BLACK;

		ticksDrawn = true;
		tickSpacing = 0.0;
		ticksAutoSpaced = false;
		tickLength = 1.0;
		tickStroke = new BasicStroke();
		tickAlignment = 0.5;
		tickFont = Font.decode(null);
		tickColor = Color.BLACK;

		tickLabelsEnabled = true;
		tickLabelFormat = NumberFormat.getInstance();
		tickLabelDistance = 1.0;
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
		setSettingDefault(LABEL_FONT, Font.decode(null));
		setSettingDefault(LABEL_COLOR, Color.BLACK);
	}

	/**
	 * Returns a component that displays the specified axis.
	 * @param axis axis to be displayed
	 * @return component displaying the axis
	 * @see Axis
	 */
	public Drawable getRendererComponent(final Axis axis) {
		final Drawable component = new AbstractDrawable() {
			/** Version id for serialization. */
			private static final long serialVersionUID = 3605211198378801694L;

			/**
			 * Draws the {@code Drawable} with the specified drawing context.
			 * @param context Environment used for drawing
			 */
			public void draw(DrawingContext context) {
				if (shapeLines == null || shapeLines.length == 0) {
					return;
				}

				AbstractAxisRenderer2D renderer = AbstractAxisRenderer2D.this;
				Graphics2D graphics = context.getGraphics();

				// Remember old state of Graphics2D instance
				AffineTransform txOrig = graphics.getTransform();
				graphics.translate(getX(), getY());
				Stroke strokeOld = graphics.getStroke();
				Paint paintOld = graphics.getPaint();

				// Draw axis shape
				Paint axisPaint = renderer.getShapeColor();
				Stroke axisStroke = renderer.getShapeStroke();
				boolean isShapeVisible = renderer.isShapeVisible();
				if (isShapeVisible) {
					Shape shape = renderer.getShape();
					GraphicsUtils.drawPaintedShape(
							graphics, shape, axisPaint, null, axisStroke);
				}

				double fontSize =
					renderer.getTickFont().getSize2D();

				// Draw ticks
				boolean drawTicksMajor = renderer.isTicksDrawn();
				boolean drawTicksMinor =
					renderer.<Boolean>getSetting(TICKS_MINOR);
				if (drawTicksMajor || (drawTicksMajor && drawTicksMinor)) {
					// Calculate tick positions (in pixel coordinates)
					List<Tick> ticks = getTicks(axis);

					boolean isTickLabelVisible =
						renderer.isTickLabelsEnabled();
					boolean isTickLabelOutside =
						renderer.<Boolean>getSetting(TICK_LABELS_OUTSIDE);
					double tickLabelRotation =
						renderer.<Number>getSetting(TICK_LABELS_ROTATION)
						.doubleValue();
					double tickLabelDist =
						renderer.getTickLabelDistance()
						.doubleValue()*fontSize;
					Line2D tickShape = new Line2D.Double();

					for (Tick tick : ticks) {
						// Draw tick
						if ((tick.position == null)
								|| (tick.normal == null)) {
							continue;
						}
						Point2D tickPoint = tick.position.getPoint2D();
						Point2D tickNormal = tick.normal.getPoint2D();

						double tickLength;
						double tickAlignment;
						Paint tickPaint;
						Stroke tickStroke;
						if (TickType.MINOR.equals(tick.type)) {
							tickLength =
								renderer.<Number>getSetting(TICKS_MINOR_LENGTH)
								.doubleValue()*fontSize;
							tickAlignment =
								renderer.<Number>getSetting(TICKS_MINOR_ALIGNMENT)
								.doubleValue();
							tickPaint =
								renderer.<Paint>getSetting(TICKS_MINOR_COLOR);
							tickStroke =
								renderer.<Stroke>getSetting(TICKS_MINOR_STROKE);
						} else {
							tickLength = getTickLengthAbsolute();
							tickAlignment =
								renderer.getTickAlignment()
								.doubleValue();
							tickPaint =
								renderer.getTickColor();
							tickStroke = renderer.getTickStroke();
						}

						double tickLengthInner = tickLength*tickAlignment;
						double tickLengthOuter = tickLength*(1.0 - tickAlignment);

						if ((drawTicksMajor && (tick.type == TickType.MAJOR) ||
								tick.type == TickType.CUSTOM) || (drawTicksMinor &&
								tick.type == TickType.MINOR)) {
							tickShape.setLine(
								tickPoint.getX() - tickNormal.getX()*tickLengthInner,
								tickPoint.getY() - tickNormal.getY()*tickLengthInner,
								tickPoint.getX() + tickNormal.getX()*tickLengthOuter,
								tickPoint.getY() + tickNormal.getY()*tickLengthOuter
							);
							GraphicsUtils.drawPaintedShape(
									graphics, tickShape, tickPaint, null, tickStroke);
						}

						// Draw label
						if (isTickLabelVisible && (tick.type == TickType.MAJOR ||
								tick.type == TickType.CUSTOM)) {
							String tickLabelText = tick.label;
							if (tickLabelText != null && !tickLabelText.trim().isEmpty()) {
								Label tickLabel = new Label(tickLabelText);
								tickLabel.setSetting(Label.FONT,
										renderer.getTickFont());
								// TODO Allow separate colors for ticks and tick labels?
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
				String labelText = renderer.<String>getSetting(LABEL);
				if (labelText != null && !labelText.trim().isEmpty()) {
					Label axisLabel = new Label(labelText);
					axisLabel.setSetting(Label.FONT,
							renderer.<Font>getSetting(LABEL_FONT));
					axisLabel.setSetting(Label.COLOR,
							renderer.<Paint>getSetting(LABEL_COLOR));

					double tickLength = getTickLengthAbsolute();
					double tickAlignment = renderer.getTickAlignment()
						.doubleValue();
					double tickLengthOuter = tickLength*(1.0 - tickAlignment);
					double tickLabelDist = renderer.getTickLabelDistance()
						.doubleValue()*fontSize;

					double labelDistance = renderer.<Number>getSetting(LABEL_DISTANCE)
						.doubleValue()*fontSize;
					double labelDist =
						tickLengthOuter + tickLabelDist + fontSize + labelDistance;
					double labelRotation = renderer.<Number>getSetting(LABEL_ROTATION)
						.doubleValue();
					double axisLabelPos =
						(axis.getMin().doubleValue() + axis.getMax().doubleValue()) * 0.5;
					boolean isTickLabelOutside = renderer.<Boolean>getSetting(TICK_LABELS_OUTSIDE);

					PointND<Double> labelPos = getPosition(axis, axisLabelPos, false, true);
					PointND<Double> labelNormal = getNormal(axis, axisLabelPos, false, true);

					if (labelPos != null && labelNormal != null) {
						layoutLabel(axisLabel, labelPos.getPoint2D(),
								labelNormal.getPoint2D(), labelDist,
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
						Math.toRadians(-rotation),
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
				AbstractAxisRenderer2D renderer = AbstractAxisRenderer2D.this;
				double fontSize = renderer.getTickFont().getSize2D();
				double tickLength = getTickLengthAbsolute();
				double tickAlignment = renderer.getTickAlignment()
					.doubleValue();
				double tickLengthOuter = tickLength*(1.0 - tickAlignment);
				double labelDistance = renderer.getTickLabelDistance()
					.doubleValue();
				double labelDist = labelDistance*fontSize + tickLengthOuter;
				double minSize = fontSize + labelDist + tickLengthOuter;
				return new de.erichseifert.gral.util.Dimension2D.Double(minSize, minSize);
			}
		};

		return component;
	}

	/**
	 * Returns a list of all tick element on the axis.
	 * @param axis Axis
	 * @return A list of {@code Tick} instances
	 */
	public List<Tick> getTicks(Axis axis) {
		List<Tick> ticks = new LinkedList<Tick>();

		if (!axis.isValid()) {
			return ticks;
		}

		double min = axis.getMin().doubleValue();
		double max = axis.getMax().doubleValue();

		Set<Double> tickPositions = new HashSet<Double>();

		createTicksCustom(ticks, axis, min, max, tickPositions);

		boolean isAutoSpacing = isTicksAutoSpaced();
		// If the spacing is invalid, use auto spacing
		if (!isAutoSpacing) {
			Number tickSpacing = getTickSpacing();
			if (tickSpacing == null) {
				isAutoSpacing = true;
			} else {
				double tickSpacingValue = tickSpacing.doubleValue();
				if (tickSpacingValue <= 0.0 || !MathUtils.isCalculatable(tickSpacingValue)) {
					isAutoSpacing = true;
				}
			}
		}

		createTicks(ticks, axis, min, max, tickPositions, isAutoSpacing);

		return ticks;
	}

	/**
	 * Returns the absolute length of a major tick in pixels.
	 * @return Tick length in pixels.
	 */
	protected double getTickLengthAbsolute() {
		double fontSize = getTickFont().getSize2D();
		return getTickLength().doubleValue()*fontSize;
	}

	/**
	 * Adds minor and major ticks to a list of ticks.
	 * @param ticks List of ticks
	 * @param axis Axis
	 * @param min Minimum value of axis
	 * @param max Maximum value of axis
	 * @param tickPositions Set of tick positions
	 * @param isAutoSpacing Use automatic scaling
	 */
	protected abstract void createTicks(List<Tick> ticks, Axis axis,
			double min, double max, Set<Double> tickPositions,
			boolean isAutoSpacing);

	/**
	 * Adds custom ticks to a list of ticks.
	 * @param ticks List of ticks
	 * @param axis Axis
	 * @param min Minimum value of axis
	 * @param max Maximum value of axis
	 * @param tickPositions Set of tick positions
	 */
	protected void createTicksCustom(List<Tick> ticks, Axis axis,
			double min, double max, Set<Double> tickPositions) {
		Map<? extends Number, String> labelsCustom = getSetting(TICKS_CUSTOM);
		if (labelsCustom != null) {
			for (Number tickPositionWorldObj : labelsCustom.keySet()) {
				double tickPositionWorld = tickPositionWorldObj.doubleValue();
				if (tickPositionWorld < min || tickPositionWorld > max) {
					continue;
				}
				Tick tick = getTick(
					TickType.CUSTOM, axis, tickPositionWorld);
				ticks.add(tick);
				tickPositions.add(tickPositionWorld);
			}
		}
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
			Format labelFormat = getTickLabelFormat();
			if (labelFormat != null) {
				tickLabel = labelFormat.format(tickPositionWorld);
			} else {
				tickLabel = String.valueOf(tickPositionWorld);
			}
		}

		Tick tick = new Tick(type, tickPoint, tickNormal, null, null, tickLabel);
		return tick;
	}

	/**
	 * Returns the normal vector at the position of the specified value.
	 * The vector is normalized.
	 * @param axis Axis
	 * @param value World coordinate value to convert
	 * @param extrapolate Option to activate extrapolation value that are not
	 *        on the axis
	 * @param forceLinear Force linear interpolation.
	 * @return N-dimensional normal vector at the position
	 */
	public PointND<Double> getNormal(Axis axis, Number value,
			boolean extrapolate, boolean forceLinear) {
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

		segmentIndex = MathUtils.limit(
				segmentIndex, 0, shapeLineNormals.length - 1);
		Boolean normalOrientationClockwise = AbstractAxisRenderer2D.this
			.isShapeNormalOrientationClockwise();
		double normalOrientation = (normalOrientationClockwise != null &&
				normalOrientationClockwise.booleanValue()) ? 1.0 : -1.0;
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

	/**
	 * Returns the position of the specified value on the axis.
	 * The value is returned in view coordinates.
	 * @param axis Axis
	 * @param value World coordinate value to convert
	 * @param extrapolate Option to activate extrapolation value that are not
	 *        on the axis
	 * @param forceLinear Force linear interpolation.
	 * @return N-dimensional point of the value
	 */
	public PointND<Double> getPosition(Axis axis, Number value,
			boolean extrapolate, boolean forceLinear) {
		if (shapeLines == null || shapeLines.length == 0 || value == null) {
			return null;
		}

		double valueView;
		if (forceLinear) {
			valueView = (value.doubleValue() - axis.getMin().doubleValue()) /
				axis.getRange()*getShapeLength();
		} else {
			valueView = worldToView(axis, value, extrapolate);
		}

		if (Double.isNaN(valueView)) {
			return null;
		}

		// TODO Check if this is a valid way to allow infinite values
		if (valueView == Double.NEGATIVE_INFINITY) {
			valueView = 0.0;
		} else if (valueView == Double.POSITIVE_INFINITY) {
			valueView = getShapeLength();
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
	protected final void evaluateShape(Shape shape) {
		boolean directionSwapped = isShapeDirectionSwapped();
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

			// Calculate a normalized vector perpendicular to the current
			// axis shape segment
			shapeLineNormals[i] = new Point2D.Double(
				 (line.getY2() - line.getY1()) / segmentLength,
				-(line.getX2() - line.getX1()) / segmentLength
			);
		}
	}

	/**
	 * Invoked if a setting has changed.
	 * @param event Event containing information about the changed setting.
	 */
	public void settingChanged(SettingChangeEvent event) {
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
		// Normal deserialization
		in.defaultReadObject();
		shapeStroke = (Stroke) SerializationUtils.unwrap((Serializable) in.readObject());
		tickStroke = (Stroke) SerializationUtils.unwrap((Serializable) in.readObject());

		// Restore listeners
		addSettingsListener(this);
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeObject(SerializationUtils.wrap(shapeStroke));
		out.writeObject(SerializationUtils.wrap(tickStroke));
	}

	@Override
	public Number getIntersection() {
		return intersection;
	}

	@Override
	public void setIntersection(Number intersection) {
		this.intersection = intersection;
	}

	@Override
	public Shape getShape() {
		return shape;
	}

	@Override
	public void setShape(Shape shape) {
		this.shape = shape;
		evaluateShape(shape);
	}

	@Override
	public boolean isShapeVisible() {
		return shapeVisible;
	}

	@Override
	public void setShapeVisible(boolean shapeVisible) {
		this.shapeVisible = shapeVisible;
	}

	@Override
	public boolean isShapeNormalOrientationClockwise() {
		return shapeNormalOrientationClockwise;
	}

	@Override
	public void setShapeNormalOrientationClockwise(boolean clockwise) {
		this.shapeNormalOrientationClockwise = clockwise;
	}

	@Override
	public Paint getShapeColor() {
		return shapeColor;
	}

	@Override
	public void setShapeColor(Paint shapeColor) {
		this.shapeColor = shapeColor;
	}

	@Override
	public Stroke getShapeStroke() {
		return shapeStroke;
	}

	@Override
	public void setShapeStroke(Stroke shapeStroke) {
		this.shapeStroke = shapeStroke;
	}

	@Override
	public boolean isShapeDirectionSwapped() {
		return shapeDirectionSwapped;
	}

	@Override
	public void setShapeDirectionSwapped(boolean shapeDirectionSwapped) {
		this.shapeDirectionSwapped = shapeDirectionSwapped;
	}

	@Override
	public boolean isTicksDrawn() {
		return ticksDrawn;
	}

	@Override
	public void setTicksDrawn(boolean ticksDrawn) {
		this.ticksDrawn = ticksDrawn;
	}

	@Override
	public Number getTickSpacing() {
		return tickSpacing;
	}

	@Override
	public void setTickSpacing(Number tickSpacing) {
		this.tickSpacing = tickSpacing;
	}

	@Override
	public boolean isTicksAutoSpaced() {
		return ticksAutoSpaced;
	}

	@Override
	public void setTicksAutoSpaced(boolean ticksAutoSpaced) {
		this.ticksAutoSpaced = ticksAutoSpaced;
	}

	@Override
	public Number getTickLength() {
		return tickLength;
	}

	@Override
	public void setTickLength(Number tickLength) {
		this.tickLength = tickLength;
	}

	@Override
	public Stroke getTickStroke() {
		return tickStroke;
	}

	@Override
	public void setTickStroke(Stroke tickStroke) {
		this.tickStroke = tickStroke;
	}

	@Override
	public Number getTickAlignment() {
		return tickAlignment;
	}

	@Override
	public void setTickAlignment(Number tickAlignment) {
		this.tickAlignment = tickAlignment;
	}

	@Override
	public Font getTickFont() {
		return tickFont;
	}

	@Override
	public void setTickFont(Font tickFont) {
		this.tickFont = tickFont;
	}

	@Override
	public Paint getTickColor() {
		return tickColor;
	}

	@Override
	public void setTickColor(Paint tickColor) {
		this.tickColor = tickColor;
	}

	@Override
	public boolean isTickLabelsEnabled() {
		return tickLabelsEnabled;
	}

	@Override
	public void setTickLabelsEnabled(boolean tickLabelsEnabled) {
		this.tickLabelsEnabled = tickLabelsEnabled;
	}

	@Override
	public Format getTickLabelFormat() {
		return tickLabelFormat;
	}

	@Override
	public void setTickLabelFormat(Format tickLabelFormat) {
		this.tickLabelFormat = tickLabelFormat;
	}

	@Override
	public Number getTickLabelDistance() {
		return tickLabelDistance;
	}

	@Override
	public void setTickLabelDistance(Number tickLabelDistance) {
		this.tickLabelDistance = tickLabelDistance;
	}
}

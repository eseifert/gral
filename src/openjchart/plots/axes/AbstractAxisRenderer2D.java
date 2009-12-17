package openjchart.plots.axes;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import openjchart.AbstractDrawable;
import openjchart.Drawable;
import openjchart.plots.DataPoint2D;
import openjchart.plots.Label;
import openjchart.util.GeometryUtils;
import openjchart.util.MathUtils;
import openjchart.util.SettingChangeEvent;
import openjchart.util.Settings;
import openjchart.util.SettingsListener;

public abstract class AbstractAxisRenderer2D implements AxisRenderer2D, SettingsListener {
	private final Settings settings;

	private Line2D[] shapeLines;
	private Point2D[] shapeLineNormals;
	private double[] shapeSegmentLengths;
	private double[] shapeLengths;

	private double tickLengthInner;
	private double tickLengthOuter;
	private double tickLabelDist;

	public AbstractAxisRenderer2D() {
		settings = new Settings(this);

		setSettingDefault(KEY_INTERSECTION, 0.0);

		setSettingDefault(KEY_SHAPE_DIRECTION_SWAPPED, false);
		setSettingDefault(KEY_SHAPE, new Line2D.Double(0.0, 0.0, 1.0, 0.0));
		setSettingDefault(KEY_SHAPE_NORMAL_ORIENTATION_CLOCKWISE, false);
		setSettingDefault(KEY_SHAPE_STROKE, new BasicStroke());

		setSettingDefault(KEY_TICK_SPACING, 1.0);
		setSettingDefault(KEY_TICK_LENGTH, 10.0);
		setSettingDefault(KEY_TICK_STROKE, new BasicStroke());
		setSettingDefault(KEY_TICK_ALIGNMENT, 0.5);

		setSettingDefault(KEY_TICK_LABEL_FORMAT, NumberFormat.getInstance());
		setSettingDefault(KEY_TICK_LABEL_DISTANCE, 1.0);
		setSettingDefault(KEY_TICK_LABEL_OUTSIDE, true);

		setSettingDefault(KEY_LABEL, null);
		setSettingDefault(KEY_LABEL_DISTANCE, 10.0);
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

				// Calculate tick positions (in pixel coordinates)
				List<DataPoint2D> ticks = getTicks(axis);

				// Draw axis shape
				g2d.setStroke(AbstractAxisRenderer2D.this.<Stroke>getSetting(KEY_SHAPE_STROKE));
				g2d.draw(AbstractAxisRenderer2D.this.<Shape>getSetting(KEY_SHAPE));
				g2d.setStroke(strokeOld);

				// Draw ticks
				double tickLength = getSetting(KEY_TICK_LENGTH);
				double tickAlignment = getSetting(KEY_TICK_ALIGNMENT);
				tickLengthInner = tickLength*(tickAlignment);
				tickLengthOuter = tickLength*(1.0 - tickAlignment);
				tickLabelDist = AbstractAxisRenderer2D.this.<Double>getSetting(KEY_TICK_LABEL_DISTANCE)*tickLength;

				boolean isTickLabelOutside = getSetting(KEY_TICK_LABEL_OUTSIDE);
				Rectangle2D tickLabelBounds = new Rectangle2D.Double();
				Line2D tickShape = new Line2D.Double();

				g2d.setStroke(AbstractAxisRenderer2D.this.<Stroke>getSetting(KEY_TICK_STROKE));
				for (DataPoint2D tick : ticks) {
					Point2D tickPoint = tick.getPosition();
					Point2D tickNormal = tick.getNormal();
					String tickLabelText = tick.getLabel();

					// Draw tick
					if (tickPoint == null) {
						continue;
					}
					tickShape.setLine(
						tickPoint.getX() - tickNormal.getX()*tickLengthInner, tickPoint.getY() - tickNormal.getY()*tickLengthInner,
						tickPoint.getX() + tickNormal.getX()*tickLengthOuter, tickPoint.getY() + tickNormal.getY()*tickLengthOuter
					);
					g2d.draw(tickShape);

					// Draw label
					Label tickLabel = new Label(tickLabelText);

					// Bounding box for label
					Dimension2D tickLabelSize = tickLabel.getPreferredSize();
					// Add padding to bounding box
					tickLabelBounds.setFrame(
						0.0, 0.0,
						tickLabelSize.getWidth() + 2.0*tickLabelDist, tickLabelSize.getHeight() + 2.0*tickLabelDist
					);
					double intersectionRayLength = tickLabelBounds.getHeight()*tickLabelBounds.getHeight() + tickLabelBounds.getWidth()*tickLabelBounds.getWidth();
					List<Point2D> labelBoundsIntersections = GeometryUtils.intersection(
							tickLabelBounds,
							new Line2D.Double(
								tickLabelBounds.getCenterX(),
								tickLabelBounds.getCenterY(),
								tickLabelBounds.getCenterX() + (isTickLabelOutside?-1.0:1.0)*tickNormal.getX()*intersectionRayLength,
								tickLabelBounds.getCenterY() + (isTickLabelOutside?-1.0:1.0)*tickNormal.getY()*intersectionRayLength
							)
					);
					if (labelBoundsIntersections.isEmpty()) {
						continue;
					}
					double intersX = labelBoundsIntersections.get(0).getX() - tickLabelBounds.getCenterX();
					double intersY = labelBoundsIntersections.get(0).getY() - tickLabelBounds.getCenterY();
					double tickLabelPosX = (isTickLabelOutside ? tickShape.getX2() : tickShape.getX1()) - intersX - tickLabelSize.getWidth()/2.0;
					double tickLabelPosY = (isTickLabelOutside ? tickShape.getY2() : tickShape.getY1()) - intersY - tickLabelSize.getHeight()/2.0;
					tickLabel.setBounds(tickLabelPosX, tickLabelPosY, tickLabelSize.getWidth(), tickLabelSize.getHeight());
					tickLabel.draw(g2d);
				}

				// Draw axis label
				String labelText = getSetting(KEY_LABEL);
				if (labelText != null && !labelText.trim().isEmpty()) {
					Label axisLabel = new Label(labelText);
					// FIXME: use tick label height instead of constant value
					double fontHeight = 10.0;
					double labelDistance = getSetting(KEY_LABEL_DISTANCE);
					double labelDist = tickLengthOuter + tickLabelDist + fontHeight + labelDistance;
					double axisCenter = (axis.getMin().doubleValue() + axis.getMax().doubleValue()) / 2.0;
					Point2D axisLabelPos = worldToViewPos(axis, axisCenter, false);
					Point2D axisLabelNormal = getNormal(axis, axisCenter, false);
					Dimension2D descriptionSize = axisLabel.getPreferredSize();
					Rectangle2D descriptionBounds = new Rectangle2D.Double(
							0, 0,
							descriptionSize.getWidth() + 2.0*labelDist, descriptionSize.getHeight() + 2.0*labelDist
					);
					double intersectionRayLength = descriptionBounds.getHeight()*descriptionBounds.getHeight() + descriptionBounds.getWidth()*descriptionBounds.getWidth();
					List<Point2D> descriptionBoundsIntersections = GeometryUtils.intersection(
							descriptionBounds,
							new Line2D.Double(
								descriptionBounds.getCenterX(),
								descriptionBounds.getCenterY(),
								descriptionBounds.getCenterX() + (isTickLabelOutside?-1.0:1.0)*axisLabelNormal.getX()*intersectionRayLength,
								descriptionBounds.getCenterY() + (isTickLabelOutside?-1.0:1.0)*axisLabelNormal.getY()*intersectionRayLength
							)
					);
					if (!descriptionBoundsIntersections.isEmpty()) {
						double intersX = descriptionBoundsIntersections.get(0).getX() - descriptionBounds.getCenterX();
						double intersY = descriptionBoundsIntersections.get(0).getY() - descriptionBounds.getCenterY();
						double descriptionPosX = axisLabelPos.getX() - intersX - descriptionSize.getWidth()/2.0;
						double descriptionPosY = axisLabelPos.getY() - intersY - descriptionSize.getHeight()/2.0;
						axisLabel.setBounds(descriptionPosX, descriptionPosY, descriptionSize.getWidth(), descriptionSize.getHeight());
						axisLabel.draw(g2d);
					}
				}

				g2d.setStroke(strokeOld);
				g2d.setTransform(txOrig);
			}

			@Override
			public Dimension2D getPreferredSize() {
				// FIXME: use real font height instead of fixed value
				double fontHeight = 10.0;
				double tickLength = getSetting(KEY_TICK_LENGTH);
				double tickAlignment = getSetting(KEY_TICK_ALIGNMENT);
				double tickLengthOuter = tickLength*(1.0 - tickAlignment);
				double labelDistance = getSetting(KEY_TICK_LABEL_DISTANCE);
				double labelDist = labelDistance*tickLength + tickLengthOuter;
				double minSize = fontHeight + labelDist + tickLengthOuter;
				return new openjchart.util.Dimension2D.Double(minSize, minSize);
			}
		};

		return component;
	}

	@Override
	public List<DataPoint2D> getTicks(Axis axis) {
		double tickSpacing = getSetting(KEY_TICK_SPACING);
		double min = axis.getMin().doubleValue();
		double max = axis.getMax().doubleValue();
		double minTick = Math.ceil(min/tickSpacing) * tickSpacing;
		double maxTick = Math.floor(max/tickSpacing) * tickSpacing;

		List<Double> tickPositionsWorld = new ArrayList<Double>();
		for (double tickPositionWorld=minTick; tickPositionWorld<=maxTick; tickPositionWorld++) {
			tickPositionsWorld.add(tickPositionWorld);
		}

		List<DataPoint2D> tickPositions = new LinkedList<DataPoint2D>();
		for (double tickPositionWorld : tickPositionsWorld) {
			// Calculate position of tick on axis shape
			Point2D tickPoint = worldToViewPos(axis, tickPositionWorld, false);

			if (tickPoint == null) {
				continue;
			}

			// Calculate tick normal
			Point2D tickNormal = getNormal(axis, tickPositionWorld, false);

			Format labelFormat = getSetting(KEY_TICK_LABEL_FORMAT);
			String tickLabel = labelFormat.format(tickPositionWorld);

			tickPositions.add(new DataPoint2D(tickPoint, tickNormal, null, tickLabel));
		}

		return tickPositions;
	}

	private Point2D getNormal(Axis axis, Number value, boolean extrapolate) {
		double tickPositionView = worldToView(axis, value, extrapolate);
		int segmentIndex = MathUtils.binarySearchFloor(shapeLengths, tickPositionView);

		if (segmentIndex < 0) {
			throw new IndexOutOfBoundsException("Could not find shape segment for value "+value+".");
		}

		segmentIndex = MathUtils.limit(segmentIndex, 0, shapeLineNormals.length - 1);
		double normalOrientation = AbstractAxisRenderer2D.this.<Boolean>getSetting(KEY_SHAPE_NORMAL_ORIENTATION_CLOCKWISE) ? 1.0 : -1.0;
		Point2D tickNormal = new Point2D.Double(
			normalOrientation * shapeLineNormals[segmentIndex].getX(),
			normalOrientation * shapeLineNormals[segmentIndex].getY()
		);

		return tickNormal;
	}

	protected double getShapeLength() {
		if (shapeLengths == null || shapeLengths.length == 0) {
			return 0.0;
		}
		return shapeLengths[shapeLengths.length - 1];
	}

	@Override
	public Point2D worldToViewPos(Axis axis, Number value, boolean extrapolate) {
		if (shapeLines == null || shapeLines.length == 0) {
			return null;
		}

		double length = worldToView(axis, value, extrapolate);

		if (Double.isNaN(length) || Double.isInfinite(length)) {
			return null;
		}

		if (length <= 0.0 || length >= getShapeLength()) {
			if (extrapolate) {
				// do linear extrapolation if point lies outside of shape
				int segmentIndex = (length <= 0.0) ? 0 : shapeLines.length - 1;
				Line2D segment = shapeLines[segmentIndex];
				double segmentLen = shapeSegmentLengths[segmentIndex];
				double shapeLen = shapeLengths[segmentIndex];
				return new Point2D.Double(
					segment.getX1() + (segment.getX2() - segment.getX1())/segmentLen * (length - shapeLen),
					segment.getY1() + (segment.getY2() - segment.getY1())/segmentLen * (length - shapeLen)
				);
			} else {
				if (length <= 0.0) {
					return shapeLines[0].getP1();
				} else {
					return shapeLines[shapeLines.length - 1].getP2();
				}
			}
		}

		// Determine to which segment the value belongs using a binary search
		int i = MathUtils.binarySearchFloor(shapeLengths, length);

		if (i < 0 || i >= shapeLines.length) {
			return null;
		}
		Line2D line = shapeLines[i];

		double posRel = (length - shapeLengths[i]) / shapeSegmentLengths[i];
		Point2D pos = new Point2D.Double(
			line.getX1() + (line.getX2() - line.getX1())*posRel,
			line.getY1() + (line.getY2() - line.getY1())*posRel
		);
		return pos;
	}

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

	public <T> T getSetting(String key) {
		return settings.<T>get(key);
	}

	public <T> void setSetting(String key, T value) {
		settings.<T>set(key, value);
	}

	@Override
	public <T> void removeSetting(String key) {
		settings.remove(key);
	}

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
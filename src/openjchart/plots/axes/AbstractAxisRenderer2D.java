package openjchart.plots.axes;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
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
import openjchart.plots.Label;
import openjchart.util.Dimension2D;
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
	private double labelDist;

	public AbstractAxisRenderer2D() {
		settings = new Settings();
		settings.addSettingsListener(this);

		setSettingDefault(KEY_INTERSECTION, 0.0);

		setSettingDefault(KEY_SHAPE_DIRECTION_SWAPPED, false);
		setSettingDefault(KEY_SHAPE, new Line2D.Double(0.0, 0.0, 1.0, 0.0));
		setSettingDefault(KEY_SHAPE_NORMAL_ORIENTATION_CLOCKWISE, false);
		setSettingDefault(KEY_SHAPE_STROKE, new BasicStroke());

		setSettingDefault(KEY_TICK_SPACING, 1.0);
		setSettingDefault(KEY_TICK_LENGTH, 10.0);
		setSettingDefault(KEY_TICK_STROKE, new BasicStroke());
		setSettingDefault(KEY_TICK_ALIGNMENT, 0.5);

		setSettingDefault(KEY_LABEL_FORMAT, NumberFormat.getInstance());
		setSettingDefault(KEY_LABEL_DISTANCE, 1.0);
		setSettingDefault(KEY_LABEL_OUTSIDE, true);
	}

	@Override
	public Drawable getRendererComponent(final Axis axis) {
		final Drawable component = new AbstractDrawable() {
			@Override
			public void draw(Graphics2D g2d) {
				if (shapeLines==null || shapeLines.length==0) {
					return;
				}
				AffineTransform txOld = g2d.getTransform();
				Stroke strokeOld = g2d.getStroke();

				// Calculate tick positions (in pixel coordinates)
				List<Tick2D> ticks = getTicks(axis);

				// Draw axis shape
				g2d.setStroke(AbstractAxisRenderer2D.this.<Stroke>getSetting(KEY_SHAPE_STROKE));
				g2d.draw(AbstractAxisRenderer2D.this.<Shape>getSetting(KEY_SHAPE));
				g2d.setStroke(strokeOld);

				// Draw ticks
				double tickLength = getSetting(KEY_TICK_LENGTH);
				double tickAlignment = getSetting(KEY_TICK_ALIGNMENT);
				tickLengthInner = tickLength*(tickAlignment);
				tickLengthOuter = tickLength*(1.0 - tickAlignment);
				labelDist = AbstractAxisRenderer2D.this.<Double>getSetting(KEY_LABEL_DISTANCE)*tickLength;

				Rectangle2D labelBoundsPadded = new Rectangle2D.Double();
				Line2D tickShape = new Line2D.Double();

				g2d.setStroke(AbstractAxisRenderer2D.this.<Stroke>getSetting(KEY_TICK_STROKE));
				for (Tick2D tick : ticks) {
					Point2D tickPoint = tick.getPosition();
					Point2D tickNormal = tick.getNormal();
					String tickLabel = tick.getLabel();

					// Draw tick
					if (tickPoint == null) continue;
					g2d.translate(tickPoint.getX(), tickPoint.getY());
					tickShape.setLine(
						-tickNormal.getX()*tickLengthInner, -tickNormal.getY()*tickLengthInner,
						 tickNormal.getX()*tickLengthOuter,  tickNormal.getY()*tickLengthOuter
					);
					g2d.draw(tickShape);

					// Draw label
					Label label = new Label(tickLabel);

					// Bounding box for label
					Rectangle2D labelBounds = label.getBounds();
					// Add padding to bounding box
					labelBoundsPadded.setFrame(
						0.0,
						0.0,
						labelBounds.getWidth()  + 2.0*labelDist,
						labelBounds.getHeight() + 2.0*labelDist
					);
					boolean isLabelOutside = getSetting(KEY_LABEL_OUTSIDE);
					List<Point2D> labelBoundsIntersections = GeometryUtils.intersection(
							labelBoundsPadded,
							new Line2D.Double(
								labelBoundsPadded.getCenterX(),
								labelBoundsPadded.getCenterY(),
								labelBoundsPadded.getCenterX() + (isLabelOutside?-1.0:1.0)*tickNormal.getX()*labelBoundsPadded.getWidth(),
								labelBoundsPadded.getCenterY() + (isLabelOutside?-1.0:1.0)*tickNormal.getY()*labelBoundsPadded.getWidth()
							)
					);
					double intersX = labelBoundsIntersections.get(0).getX() - labelBoundsPadded.getCenterX();
					double intersY = labelBoundsIntersections.get(0).getY() - labelBoundsPadded.getCenterY();
					double labelPosX = -intersX - 0.50*labelBounds.getWidth()  + (isLabelOutside?tickShape.getX2():tickShape.getX1());
					double labelPosY = -intersY + 0.35*labelBounds.getHeight() + (isLabelOutside?tickShape.getY2():tickShape.getY1());  // FIXME: 0.35?
					g2d.translate(labelPosX, labelPosY);
					label.draw(g2d);

					g2d.setTransform(txOld);
				}
				g2d.setStroke(strokeOld);
			}

			@Override
			public Dimension2D getPreferredSize() {
				double fontHeight = 10.0;
				double tickLength = getSetting(KEY_TICK_LENGTH);
				double tickAlignment = getSetting(KEY_TICK_ALIGNMENT);
				double tickLengthOuter = tickLength*(1.0 - tickAlignment);
				double labelDistance = getSetting(KEY_LABEL_DISTANCE);
				double labelDist = labelDistance*tickLength + tickLengthOuter;
				double minSize = fontHeight + labelDist + tickLengthOuter;
				return new Dimension2D.Double(minSize, minSize);
			}
		};

		return component;
	}

	@Override
	public List<Tick2D> getTicks(Axis axis) {
		double tickSpacing = getSetting(KEY_TICK_SPACING);
		double min = axis.getMin().doubleValue();
		double max = axis.getMax().doubleValue();
		double minTick = Math.ceil(min/tickSpacing) * tickSpacing;
		double maxTick = Math.floor(max/tickSpacing) * tickSpacing;

		List<Double> tickPositionsWorld = new ArrayList<Double>();
		for (double tickPositionWorld=minTick; tickPositionWorld<=maxTick; tickPositionWorld++) {
			tickPositionsWorld.add(tickPositionWorld);
		}

		List<Tick2D> tickPositions = new LinkedList<Tick2D>();
		double normalOrientation = AbstractAxisRenderer2D.this.<Boolean>getSetting(KEY_SHAPE_NORMAL_ORIENTATION_CLOCKWISE) ? 1.0 : -1.0;
		for (double tickPositionWorld : tickPositionsWorld) {
			double tickPositionView = worldToView(axis, tickPositionWorld);
			int segmentIndex = MathUtils.binarySearchFloor(shapeLengths, tickPositionView);

			if (segmentIndex < 0) {
				continue;
			}

			// Calculate position of tick on axis shape
			Point2D tickPoint = worldToViewPos(axis, tickPositionWorld);
			
			if (tickPoint == null) continue;

			segmentIndex = MathUtils.limit(segmentIndex, 0, shapeLineNormals.length - 1);
			Point2D tickNormal = new Point2D.Double(
				normalOrientation * shapeLineNormals[segmentIndex].getX(),
				normalOrientation * shapeLineNormals[segmentIndex].getY()
			);

			Format labelFormat = getSetting(KEY_LABEL_FORMAT);
			String tickLabel = labelFormat.format(tickPositionWorld);

			tickPositions.add(new Tick2D(tickPoint, tickNormal, tickLabel));
		}

		return tickPositions;
	}

	protected double getShapeLength() {
		if (shapeLengths == null || shapeLengths.length == 0) {
			return 0.0;
		}
		return shapeLengths[shapeLengths.length - 1];
	}

	@Override
	public Point2D worldToViewPos(Axis axis, Number value) {
		if (shapeLines == null || shapeLines.length == 0) {
			return null;
		}

		double length = worldToView(axis, value);

		if (Double.isNaN(length) || Double.isInfinite(length)) {
			return null;
		}

		// do linear extrapolation if point lies outside of shape
		if (length <= 0.0 || length >= getShapeLength()) {
			int segmentIndex = (length <= 0.0) ? 0 : shapeLines.length - 1;
			Line2D segment = shapeLines[segmentIndex];
			double segmentLen = shapeSegmentLengths[segmentIndex];
			double shapeLen = shapeLengths[segmentIndex];
			return new Point2D.Double(
				segment.getX1() + (segment.getX2() - segment.getX1())/segmentLen * (length - shapeLen),
				segment.getY1() + (segment.getY2() - segment.getY1())/segmentLen * (length - shapeLen)
			);
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
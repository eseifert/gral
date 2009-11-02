package openjchart.plots.axes;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;

import openjchart.AbstractDrawable;
import openjchart.Drawable;
import openjchart.util.Dimension2D;
import openjchart.util.GeometryUtils;
import openjchart.util.Settings;

public abstract class AbstractAxisRenderer2D implements AxisRenderer2D {
	private final Settings settings;

	protected List<Line2D> shapeLines;
	protected double[] shapeSegmentLengths;
	protected double[] shapeLengths;
	protected double shapeLength;

	private double tickLengthInner;
	private double tickLengthOuter;
	private double labelDist;

	public AbstractAxisRenderer2D() {
		settings = new Settings();

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
				if (shapeLines==null || shapeLines.isEmpty()) {
					return;
				}
				AffineTransform txOld = g2d.getTransform();
				FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
				Stroke strokeOld = g2d.getStroke();

				// Calculate tick positions (in pixel coordinates)
				double minTick = getMinTick(axis);
				double tickSpacing = getSetting(KEY_TICK_SPACING);
				double tickLength = getSetting(KEY_TICK_LENGTH);
				double tickAlignment = getSetting(KEY_TICK_ALIGNMENT);
				double[] tickPositionsWorld = new double[(int) (axis.getRange()/tickSpacing) + 2];
				double[] tickPositionsView = new double[(int) (axis.getRange()/tickSpacing) + 2];
				for (int i=0; i<tickPositionsWorld.length; i++) {
					tickPositionsWorld[i] = minTick + i*tickSpacing;
					tickPositionsView[i] = worldToView(axis, tickPositionsWorld[i]);
				}

				// Draw axis shape
				g2d.setStroke(AbstractAxisRenderer2D.this.<Stroke>getSetting(KEY_SHAPE_STROKE));
				g2d.draw(AbstractAxisRenderer2D.this.<Shape>getSetting(KEY_SHAPE));
				g2d.setStroke(strokeOld);

				// Draw ticks
				// FIXME: Rausziehen
				tickLengthInner = tickLength*(tickAlignment);
				tickLengthOuter = tickLength*(1.0 - tickAlignment);
				labelDist = AbstractAxisRenderer2D.this.<Double>getSetting(KEY_LABEL_DISTANCE)*tickLength;

				Rectangle2D labelBoundsPadded = new Rectangle2D.Double();
				Line2D tickShape = new Line2D.Double();

				int currentTick = 0;
				Iterator<Line2D> linesIterator = shapeLines.iterator();
				for (int i=0; i<shapeLines.size(); i++) {
					Line2D line = linesIterator.next();
					double segmentLength = shapeSegmentLengths[i];
					double shapeLengthCur = shapeLengths[i];

					// Calculate normalized vector perpendicular to current axis shape segment
					// for ticks and labels
					boolean isNormalOrientationClockwise = AbstractAxisRenderer2D.this.<Boolean>getSetting(KEY_SHAPE_NORMAL_ORIENTATION_CLOCKWISE);
					double tickNormalX = (isNormalOrientationClockwise?1:-1)*(line.getY2() - line.getY1()) / segmentLength;
					double tickNormalY = (isNormalOrientationClockwise?-1:1)*(line.getX2() - line.getX1()) / segmentLength;

					// If the next ticks lie on our current axis shape segment
					g2d.setStroke(AbstractAxisRenderer2D.this.<Stroke>getSetting(KEY_TICK_STROKE));
					while (currentTick<tickPositionsView.length && tickPositionsView[currentTick] <= shapeLengthCur + segmentLength) {
						// Interpolate segment ends to get tick position
						double tickPosRel = (tickPositionsView[currentTick] - shapeLengthCur) / segmentLength;
						double tickPosX = line.getX1() + (line.getX2() - line.getX1())*tickPosRel;
						double tickPosY = line.getY1() + (line.getY2() - line.getY1())*tickPosRel;

						// Draw tick and label
						g2d.translate(tickPosX, tickPosY);
						tickShape.setLine(
								-tickNormalX*tickLengthInner, -tickNormalY*tickLengthInner,
								tickNormalX*tickLengthOuter,  tickNormalY*tickLengthOuter
						);
						g2d.draw(tickShape);

						Format labelFormat = getSetting(KEY_LABEL_FORMAT);
						String label = labelFormat.format(tickPositionsWorld[currentTick]);

						// Bounding box for label
						Rectangle2D labelBounds = metrics.getStringBounds(label, g2d);
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
										labelBoundsPadded.getCenterX() + (isLabelOutside?-tickNormalX:tickNormalX)*labelBoundsPadded.getWidth(),
										labelBoundsPadded.getCenterY() + (isLabelOutside?-tickNormalY:tickNormalY)*labelBoundsPadded.getWidth()
								)
						);
						double intersX = labelBoundsIntersections.get(0).getX() - labelBoundsPadded.getCenterX();
						double intersY = labelBoundsIntersections.get(0).getY() - labelBoundsPadded.getCenterY();
						double labelPosX = -intersX - 0.50*labelBounds.getWidth()  + (isLabelOutside?tickShape.getX2():tickShape.getX1());
						double labelPosY = -intersY + 0.35*labelBounds.getHeight() + (isLabelOutside?tickShape.getY2():tickShape.getY1());  // FIXME: 0.35?
						g2d.drawString(label, (float)labelPosX, (float)labelPosY);

						g2d.setTransform(txOld);

						currentTick++;
					}
					g2d.setStroke(strokeOld);
				}
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

	public double getMinTick(Axis axis) {
		double tickSpacing = getSetting(KEY_TICK_SPACING);
		return Math.ceil(axis.getMin().doubleValue()/tickSpacing) * tickSpacing;
	}

	public double getMaxTick(Axis axis) {
		double tickSpacing = getSetting(KEY_TICK_SPACING);
		return Math.floor(axis.getMax().doubleValue()/tickSpacing) * tickSpacing;
	}

	protected void evaluateShape(Shape shape) {
		shapeLines = GeometryUtils.shapeToLines(shape);

		// Calculate length of axis shape at each shape segment
		shapeSegmentLengths = new double[shapeLines.size()];
		shapeLengths = new double[shapeLines.size()];
		Iterator<Line2D> linesIterator = shapeLines.iterator();
		shapeLength = 0.0;
		for (int i=0; i<shapeLines.size(); i++) {
			Line2D line = linesIterator.next();
			double segmentLength = line.getP1().distance(line.getP2());
			shapeSegmentLengths[i] = segmentLength;
			shapeLengths[i] = shapeLength;
			shapeLength += segmentLength;
		}
	}

	public <T> T getSetting(String key) {
		return settings.<T>get(key);
	}

	public <T> void setSetting(String key, T value) {
		settings.<T>set(key, value);
		if (KEY_SHAPE.equals(key)) {
			evaluateShape((Shape)value);
		}
	}

	public <T> void setSettingDefault(String key, T value) {
		settings.<T>setDefault(key, value);
		if (KEY_SHAPE.equals(key)) {
			evaluateShape((Shape)value);
		}
	}
}
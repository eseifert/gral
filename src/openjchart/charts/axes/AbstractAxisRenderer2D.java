package openjchart.charts.axes;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
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

public abstract class AbstractAxisRenderer2D implements AxisRenderer2D {
	private Shape shape;
	private boolean normalOrientationClockwise;
	private Color color;

	private double tickSpacing;
	private double tickLength;
	private double tickAlignment;

	private Format labelFormat;
	private double labelDistance;
	private boolean labelOutside;

	protected List<Line2D> shapeLines;
	protected double[] shapeSegmentLengths;
	protected double[] shapeLengths;
	protected double shapeLength;

	/**
	 * Default constructor that initializes the renderer
	 * with the following default values:
	 * <ul>
	 *   <li>the axis, the ticks and the labels are colored black</li>
	 *   <li>ticks appear every <code>1</code> numbers</li>
	 *   <li>ticks have a length of <code>10px</code></li>
	 *   <li>ticks are centered</li>
	 *   <li>labels are formatted as numbers</li>
	 *   <li>the distance of labels to their ticks is the same as the tick length</li>
	 * </ul>
	 */
	public AbstractAxisRenderer2D() {
		this(
			new Line2D.Double(0.0, 0.0, 1.0, 0.0),
			true,
			Color.BLACK,
			1.0,
			10.0,
			0.5,
			NumberFormat.getInstance(),
			1.0
		);
	}

	/**
	 * Constructor that initializes the renderer
	 * @param shape shape of the axis
	 * @param normalOrientationClockwise determines whether normal vector is calculated using clockwise rotation
	 * @param color of axis, ticks and labels
	 * @param tickSpacing Interval for ticks
	 * @param tickLength Length of ticks
	 * @param tickAlignment alignment of ticks
	 * @param labelFormat Format of labels</li>
	 * @param labelDistance the distance of labels to their ticks
	 */
	public AbstractAxisRenderer2D(Shape shape, boolean normalOrientationClockwise, Color color,
			double tickSpacing, double tickLength, double tickAlignment, Format labelFormat, double labelDistance) {
		setShape(shape);
		this.normalOrientationClockwise = normalOrientationClockwise;
		this.color = Color.BLACK;

		this.tickSpacing = tickSpacing;
		this.tickLength = tickLength;
		this.tickAlignment = tickAlignment;

		this.labelFormat = labelFormat;
		this.labelDistance = labelDistance;
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

				// Calculate tick positions (in pixel coordinates)
				double minTick = getMinTick(axis);
				double[] tickPositionsWorld = new double[(int) (axis.getRange()/getTickSpacing()) + 2];
				double[] tickPositionsView = new double[(int) (axis.getRange()/getTickSpacing()) + 2];
				for (int i=0; i<tickPositionsWorld.length; i++) {
					tickPositionsWorld[i] = minTick + i*getTickSpacing();
					tickPositionsView[i] = worldToView(axis, tickPositionsWorld[i]);
				}

				// Draw axis shape and ticks
				g2d.draw(getShape());

				// FIXME: Rausziehen
				double tickLengthInner = getTickLength()*(getTickAlignment());
				double tickLengthOuter = getTickLength()*(1.0 - getTickAlignment());
				double labelDist = getLabelDistance()*getTickLength();

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
					double tickNormalX = (isNormalOrientationClockwise()?1:-1)*(line.getY2() - line.getY1()) / segmentLength;
					double tickNormalY = (isNormalOrientationClockwise()?-1:1)*(line.getX2() - line.getX1()) / segmentLength;

					// If the next tick(s) lie(s) on our current axis shape segment
					while (tickPositionsView[currentTick] <= shapeLengthCur + segmentLength) {
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

						String label = getLabelFormat().format(tickPositionsWorld[currentTick]);
						// Bounding box for label
						Rectangle2D labelBounds = metrics.getStringBounds(label, g2d);
						// Add padding to bounding box
						labelBoundsPadded.setFrame(
							0.0,
							0.0,
							labelBounds.getWidth()  + 2.0*labelDist,
							labelBounds.getHeight() + 2.0*labelDist
						);
						List<Point2D> labelBoundsIntersections = GeometryUtils.intersection(
							labelBoundsPadded,
							new Line2D.Double(
								labelBoundsPadded.getCenterX(),
								labelBoundsPadded.getCenterY(),
								labelBoundsPadded.getCenterX() + (isLabelOutside()?-tickNormalX:tickNormalX)*labelBoundsPadded.getWidth(),
								labelBoundsPadded.getCenterY() + (isLabelOutside()?-tickNormalY:tickNormalY)*labelBoundsPadded.getWidth()
							)
						);
						double intersX = labelBoundsIntersections.get(0).getX() - labelBoundsPadded.getCenterX();
						double intersY = labelBoundsIntersections.get(0).getY() - labelBoundsPadded.getCenterY();
						double labelPosX = -intersX - 0.50*labelBounds.getWidth()  + (isLabelOutside()?tickShape.getX2():tickShape.getX1());
						double labelPosY = -intersY + 0.35*labelBounds.getHeight() + (isLabelOutside()?tickShape.getY2():tickShape.getY1());  // FIXME: 0.35?
						g2d.drawString(label, (float)labelPosX, (float)labelPosY);

						g2d.setTransform(txOld);

						currentTick++;
					}
				}
			}

			@Override
			public Dimension2D getPreferredSize() {
				double fontHeight = 10.0;
				double tickLengthOuter = getTickLength()*(1.0 - getTickAlignment());
				double labelDist = labelDistance*getTickLength() + tickLengthOuter;
				double minSize = fontHeight + labelDist + tickLengthOuter;
				return new Dimension2D.Double(minSize, minSize);
			}
		};

		return component;
	}

	public double getMinTick(Axis axis) {
		return Math.ceil(axis.getMin().doubleValue()/tickSpacing) * tickSpacing;
	}

	public double getMaxTick(Axis axis) {
		return Math.floor(axis.getMax().doubleValue()/tickSpacing) * tickSpacing;
	}

	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;

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

	public boolean isNormalOrientationClockwise() {
		return normalOrientationClockwise;
	}

	public void setNormalOrientationClockwise(boolean normalOrientationClockwise) {
		this.normalOrientationClockwise = normalOrientationClockwise;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public double getTickSpacing() {
		return tickSpacing;
	}

	public void setTickSpacing(double tickSpacing) {
		this.tickSpacing = tickSpacing;
	}

	public double getTickLength() {
		return tickLength;
	}

	public void setTickLength(double tickLength) {
		this.tickLength = tickLength;
	}

	public double getTickAlignment() {
		return tickAlignment;
	}

	public void setTickAlignment(double tickAlignment) {
		this.tickAlignment = tickAlignment;
	}

	public Format getLabelFormat() {
		return labelFormat;
	}

	public void setLabelFormat(Format labelFormat) {
		this.labelFormat = labelFormat;
	}

	public double getLabelDistance() {
		return labelDistance;
	}

	public void setLabelDistance(double labelDistance) {
		this.labelDistance = labelDistance;
	}

	public boolean isLabelOutside() {
		return labelOutside;
	}

	public void setLabelOutside(boolean labelOutside) {
		this.labelOutside = labelOutside;
	}

}
package openjchart.plots;

import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 * Class for storing points of a plot.
 * @author Erich Seifert
 */
public class DataPoint2D {
	private final Point2D point;
	private final Point2D normal;
	private final Shape shape;
	private final String label;

	public DataPoint2D(Point2D point, Point2D normal, Shape shape, String label) {
		this.point = point;
		this.normal = normal;
		this.shape = shape;
		this.label = label;
	}

	public Point2D getPoint() {
		return point;
	}

	public Point2D getNormal() {
		return normal;
	}

	public Shape getShape() {
		return shape;
	}

	public String getLabel() {
		return label;
	}

}

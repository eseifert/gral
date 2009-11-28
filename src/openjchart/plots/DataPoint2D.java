package openjchart.plots;

import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 * Class for storing points of a plot.
 * @author Erich Seifert
 */
public class DataPoint2D {
	private final Point2D position;
	private final Point2D normal;
	private final Shape shape;
	private final String label;

	public DataPoint2D(Point2D position, Point2D normal, Shape shape, String label) {
		this.position = position;
		this.normal = normal;
		this.shape = shape;
		this.label = label;
	}

	public Point2D getPosition() {
		return position;
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

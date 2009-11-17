package openjchart.plots;

import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 * Class for storing points of a plot.
 * @author Erich Seifert
 */
public class DataPoint2D {
	private final Point2D position;
	private final Shape shape;

	public DataPoint2D(Point2D position, Shape shape) {
		this.position = position;
		this.shape = shape;
	}

	public Point2D getPosition() {
		return position;
	}

	public double getX() {
		return position.getX();
	}

	public double getY() {
		return position.getY();
	}

	public Shape getShape() {
		return shape;
	}

}

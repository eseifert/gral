package openjchart.plots.axes;

import java.awt.geom.Point2D;

public class Tick2D {
	private final Point2D position;
	private final Point2D normal;
	private final String label;

	public Tick2D(Point2D position, Point2D normal, String label) {
		this.position = position;
		this.normal = normal;
		this.label = label;
	}

	public Point2D getPosition() {
		return position;
	}

	public Point2D getNormal() {
		return normal;
	}

	public String getLabel() {
		return label;
	}
}

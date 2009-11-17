package openjchart.util;
import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;


public abstract class GeometryUtils {
	public static final double EPSILON = 1e-3;
	public static final double EPSILON_SQ = EPSILON*EPSILON;

	public static Line2D[] shapeToLines(Shape path, boolean swapped) {
		Deque<Line2D> lines = new ArrayDeque<Line2D>();
		PathIterator i = new FlatteningPathIterator(path.getPathIterator(null), 0.5);

		double[] coords = new double[6];
		double[] coordsPrev = new double[6];
		while (!i.isDone()) {
			int segment = i.currentSegment(coords);

			if (segment==PathIterator.SEG_LINETO || segment==PathIterator.SEG_CLOSE) {
				Line2D line;
				if (!swapped) {
					line = new Line2D.Double(coordsPrev[0], coordsPrev[1], coords[0], coords[1]);
					lines.addLast(line);
				} else {
					line = new Line2D.Double(coords[0], coords[1], coordsPrev[0], coordsPrev[1]);
					lines.addFirst(line);
				}
			}
			if (segment==PathIterator.SEG_CLOSE && !lines.isEmpty()) {
				Point2D firstPoint = lines.getFirst().getP1();
				Point2D lastPoint = lines.getLast().getP2();
				if (!firstPoint.equals(lastPoint)) {
					Line2D line;
					if (!swapped) {
						line = new Line2D.Double(coords[0], coords[1], firstPoint.getX(), firstPoint.getY());
						lines.addLast(line);
					} else {
						line = new Line2D.Double(firstPoint.getX(), firstPoint.getY(), coords[0], coords[1]);
						lines.addFirst(line);
					}
				}
			}

			System.arraycopy(coords, 0, coordsPrev, 0, 6);
			i.next();
		}
		Line2D[] linesArray = new Line2D[lines.size()];
		lines.toArray(linesArray);
		return linesArray;
	}

	/**
     * Returns all intersection points of two shapes.
     * @param s1 First shape
     * @param s2 Second shape
     * @return Intersection points, or empty array if
     * no intersections were found
     */
    public static List<Point2D> intersection(final Shape s1, final Shape s2) {
    	List<Point2D> intersections = new ArrayList<Point2D>(2);
    	Line2D[] lines1 = shapeToLines(s1, false);
    	Line2D[] lines2 = shapeToLines(s2, false);

    	for (Line2D l1 : lines1) {
			for (Line2D l2 : lines2) {
				Point2D intersection = intersection(l1, l2);
				if (intersection != null) {
					intersections.add(intersection);
				}
			}
		}

    	return intersections;
	}

    /**
     * Returns the intersection point of two lines.
     * @param l1 First line
     * @param l2 Second line
     * @return Intersection point, or <code>null</code> if
     * no intersection was found
     */
    public static Point2D intersection(final Line2D l1, final Line2D l2) {
    	Point2D p0 = l1.getP1();
		Point2D d0 = new Point2D.Double(l1.getX2()-p0.getX(), l1.getY2()-p0.getY());
		Point2D p1 = l2.getP1();
		Point2D d1 = new Point2D.Double(l2.getX2()-p1.getX(), l2.getY2()-p1.getY());

		Point2D e = new Point2D.Double(p1.getX()-p0.getX(), p1.getY()-p0.getY());
		double kross = d0.getX()*d1.getY() - d0.getY()*d1.getX();
		double sqrKross = kross * kross;
		double sqrLen0 = d0.distanceSq(0.0, 0.0);
		double sqrLen1 = d1.distanceSq(0.0, 0.0);

		if (sqrKross > EPSILON_SQ * sqrLen0 * sqrLen1) {
			double s = (e.getX()*d1.getY() - e.getY()*d1.getX()) / kross;
			if (s < 0d || s > 1d) {
				return null;
			}
			double t = (e.getX()*d0.getY() - e.getY()*d0.getX()) / kross;
			if (t < 0d || t > 1d) {
				return null;
			}
			return new Point2D.Double(
				p0.getX() + s * d0.getX(), p0.getY() + s * d0.getY()
			);
		}

		/*
		double sqrLenE = e.lengthSq();
		kross = e.cross(d0);
		sqrKross = kross*kross;
		if (sqrKross > SQR_EPSILON*sqrLen0*sqrLenE) {
			return null;
		}
		*/

		return null;
	}

    /**
     * Expand or shrink a shape in all directions by a defined offset.
     * @param s Shape
     * @param offset Offset
     * @return New shape that was expanded or shrunk by the specified amount
     */
    public static Area grow(final Shape s, final double offset) {
    	Area shape = new Area(s);
    	if (Math.abs(offset) < EPSILON) {
    		return shape;
    	}

    	BasicStroke stroke = new BasicStroke((float)Math.abs(2.0*offset));
    	Area strokeShape = new Area(stroke.createStrokedShape(s));

    	if (offset > 0.0) {
    		shape.add(strokeShape);
    	} else {
    		shape.subtract(strokeShape);
    	}

    	return shape;
    }

}

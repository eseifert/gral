/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <mseifert[at]error-reports.org>
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
package de.erichseifert.gral.util;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Abstract class that represents a collection of utility functions
 * concerning geometry.
 */
public abstract class GeometryUtils {
	/** Precision. */
	public static final double EPSILON = 1e-5;
	/** Precision squared. */
	public static final double EPSILON_SQ = EPSILON*EPSILON;

	/**
	 * Default constructor that prevents creation of class.
	 */
	private GeometryUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the line fragments of the specified Shape.
	 * @param path Shape to be divided.
	 * @param swapped Invert segment direction.
	 * @return Array of lines.
	 */
	public static Line2D[] shapeToLines(Shape path, boolean swapped) {
		Deque<Line2D> lines = new ArrayDeque<>();
		PathIterator i =
			new FlatteningPathIterator(path.getPathIterator(null), 0.5);

		double[] coords = new double[6];
		double[] coordsPrev = new double[6];
		while (!i.isDone()) {
			int segment = i.currentSegment(coords);

			if (segment == PathIterator.SEG_LINETO ||
					segment == PathIterator.SEG_CLOSE) {
				Line2D line;
				if (!swapped) {
					line = new Line2D.Double(
						coordsPrev[0], coordsPrev[1], coords[0], coords[1]);
					lines.addLast(line);
				} else {
					line = new Line2D.Double(
						coords[0], coords[1], coordsPrev[0], coordsPrev[1]);
					lines.addFirst(line);
				}
			}
			if (segment == PathIterator.SEG_CLOSE && !lines.isEmpty()) {
				Point2D firstPoint = lines.getFirst().getP1();
				Point2D lastPoint = lines.getLast().getP2();
				if (!firstPoint.equals(lastPoint)) {
					Line2D line;
					if (!swapped) {
						line = new Line2D.Double(
							coords[0], coords[1], firstPoint.getX(), firstPoint.getY());
						lines.addLast(line);
					} else {
						line = new Line2D.Double(
							firstPoint.getX(), firstPoint.getY(), coords[0], coords[1]);
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
    	List<Point2D> intersections = new ArrayList<>(2);
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
     * @return Intersection point, or {@code null} if
     * no intersection was found
     */
    public static Point2D intersection(final Line2D l1, final Line2D l2) {
    	Point2D p0 = l1.getP1();
		Point2D d0 = new Point2D.Double(l1.getX2() - p0.getX(), l1.getY2() - p0.getY());
		Point2D p1 = l2.getP1();
		Point2D d1 = new Point2D.Double(l2.getX2() - p1.getX(), l2.getY2() - p1.getY());

		Point2D e = new Point2D.Double(p1.getX() - p0.getX(), p1.getY() - p0.getY());
		double kross = d0.getX()*d1.getY() - d0.getY()*d1.getX();
		double sqrKross = kross*kross;
		double sqrLen0 = d0.distanceSq(0.0, 0.0);
		double sqrLen1 = d1.distanceSq(0.0, 0.0);

		if (sqrKross > EPSILON_SQ * sqrLen0 * sqrLen1) {
			double s = (e.getX()*d1.getY() - e.getY()*d1.getX())/kross;
			if (s < 0d || s > 1d) {
				return null;
			}
			double t = (e.getX()*d0.getY() - e.getY()*d0.getX())/kross;
			if (t < 0d || t > 1d) {
				return null;
			}
			return new Point2D.Double(
				p0.getX() + s*d0.getX(), p0.getY() + s*d0.getY()
			);
		}

		/*
		double sqrLenE = e.lengthSq();
		kross = e.cross(d0);
		sqrKross = kross*kross;
		if (sqrKross > EPSILON_SQ*sqrLen0*sqrLenE) {
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
    	return grow(s, offset, BasicStroke.JOIN_MITER, 10f);
    }

    /**
     * Expand or shrink a shape in all directions by a defined offset.
     * @param s Shape
     * @param offset Offset to expand/shrink
     * @param join Method for handling edges (see BasicStroke)
     * @param miterlimit Limit for miter joining method
     * @return New shape that is expanded or shrunk by the specified amount
     */
    public static Area grow(final Shape s, final double offset, int join,
    		float miterlimit) {
    	Area shape = new Area(s);

    	if (MathUtils.almostEqual(offset, 0.0, EPSILON)) {
    		return shape;
    	}

    	Stroke stroke = new BasicStroke((float)Math.abs(2.0*offset),
    			BasicStroke.CAP_SQUARE, join, miterlimit);
    	Area strokeShape = new Area(stroke.createStrokedShape(s));

    	if (offset > 0.0) {
    		shape.add(strokeShape);
    	} else {
    		shape.subtract(strokeShape);
    	}

    	return shape;
    }

    /**
     * Subtract a specified geometric area of data points from another shape to yield gaps.
     * @param shapeArea Shape from which to subtract.
     * @param gap Size of the gap.
     * @param rounded Gap corners will be rounded if {@code true}.
     * @param pointPos Position of the data point
     * @param pointShape Shape of the data point
     * @return Shape with punched holes
     */
    public static Area punch(Area shapeArea, double gap, boolean rounded,
    		Point2D pointPos, Shape pointShape) {
		if (gap <= 1e-10 || pointPos == null || pointShape == null) {
			return shapeArea;
		}

		AffineTransform tx = AffineTransform.getTranslateInstance(
			pointPos.getX(), pointPos.getY());

		int gapJoin = rounded ? BasicStroke.JOIN_ROUND : BasicStroke.JOIN_MITER;
		Area gapArea = GeometryUtils.grow(
			tx.createTransformedShape(pointShape), gap, gapJoin, 10f);

		shapeArea.subtract(gapArea);

		return shapeArea;
    }

    /**
     * Utility data class for the values of the segments in a geometric shape.
     */
    public static final class PathSegment implements Serializable {
    	/** Version id for serialization. */
		private static final long serialVersionUID = 526444553637955799L;

		/** Segment type id as defined in {@link PathIterator}. */
    	public final int type;
    	/** Starting point. */
    	public final Point2D start;
    	/** End point. */
    	public final Point2D end;
    	/** Coordinates necessary to draw the segment. */
    	public final double[] coords;

    	/**
    	 * Initializes a new instance with type, starting and end point, and
    	 * all other coordinates that are necessary to draw the segment.
    	 * @param type Segment type id as defined in {@link PathIterator}.
    	 * @param start Starting point.
    	 * @param end End point.
    	 * @param coords Array of coordinates necessary to draw the segment.
    	 */
    	public PathSegment(int type, Point2D start, Point2D end, double[] coords) {
			this.type = type;
			this.start = start;
			this.end = end;
			this.coords = new double[6];
			System.arraycopy(coords, 0, this.coords, 0, 6);
		}
    }

    /**
     * Returns a list of a shape's segments as they are returned by its path
     * iterator.
     * @param shape Shape to be iterated.
     * @return A list of path segment objects.
     */
    public static List<PathSegment> getSegments(Shape shape) {
    	PathIterator path =  shape.getPathIterator(null);

    	Point2D pointStart = null, pointEnd = null;
		double[] coords = new double[6];
		List<PathSegment> segments = new LinkedList<>();
		while (!path.isDone()) {
			int type = path.currentSegment(coords);

			if (type == PathIterator.SEG_MOVETO || type == PathIterator.SEG_LINETO) {
				pointEnd = new Point2D.Double(coords[0], coords[1]);
			} else if (type == PathIterator.SEG_QUADTO) {
				pointEnd = new Point2D.Double(coords[2], coords[3]);
			} else if (type == PathIterator.SEG_CUBICTO) {
				pointEnd = new Point2D.Double(coords[4], coords[5]);
			}

			PathSegment segment = new PathSegment(type, pointStart, pointEnd, coords);
			segments.add(segment);

			pointStart = pointEnd;
			path.next();
		}

		return segments;
    }

    /**
     * Constructs a geometric shape from a list of path segments.
     * @param segments List of path segments.
     * @param isDouble {@code true} if the shape contents should be stored with
     *        double values, {@code false} if they should be stored as float.
     * @return A geometric shape.
     */
    public static Shape getShape(List<PathSegment> segments, boolean isDouble) {
    	if (isDouble) {
    		return getShapeDouble(segments);
    	} else {
    		return getShapeFloat(segments);
    	}
    }

    /**
     * Constructs a geometric shape with double precision from a list of path
     * segments.
     * @param segments List of path segments.
     * @return A geometric shape.
     */
    private static Shape getShapeDouble(List<PathSegment> segments) {
		Path2D.Double path =
			new Path2D.Double(Path2D.WIND_NON_ZERO, segments.size());
		for (PathSegment segment : segments) {
			double[] coords = segment.coords;
			if (segment.type == PathIterator.SEG_MOVETO) {
				path.moveTo(coords[0], coords[1]);
			} else if (segment.type == PathIterator.SEG_LINETO) {
				path.lineTo(coords[0], coords[1]);
			} else if (segment.type == PathIterator.SEG_QUADTO) {
				path.quadTo(coords[0], coords[1],
					coords[2], coords[3]);
			} else if (segment.type == PathIterator.SEG_CUBICTO) {
				path.curveTo(coords[0], coords[1],
					coords[2], coords[3],
					coords[4], coords[5]);
			} else if (segment.type == PathIterator.SEG_CLOSE) {
				path.closePath();
			}
		}
		return path;
	}

    /**
     * Constructs a geometric shape with single precision from a list of path
     * segments.
     * @param segments List of path segments.
     * @return A geometric shape.
     */
	private static Shape getShapeFloat(List<PathSegment> segments) {
		Path2D.Float path =
			new Path2D.Float(Path2D.WIND_NON_ZERO, segments.size());
		for (PathSegment segment : segments) {
			float[] coords = new float[segment.coords.length];
			for (int i = 0; i < coords.length; i++) {
				coords[i] = (float) segment.coords[i];
			}
			if (segment.type == PathIterator.SEG_MOVETO) {
				path.moveTo(coords[0], coords[1]);
			} else if (segment.type == PathIterator.SEG_LINETO) {
				path.lineTo(coords[0], coords[1]);
			} else if (segment.type == PathIterator.SEG_QUADTO) {
				path.quadTo(coords[0], coords[1],
					coords[2], coords[3]);
			} else if (segment.type == PathIterator.SEG_CUBICTO) {
				path.curveTo(coords[0], coords[1],
					coords[2], coords[3],
					coords[4], coords[5]);
			} else if (segment.type == PathIterator.SEG_CLOSE) {
				path.closePath();
			}
		}
		return path;
	}

	/**
     * Returns a clone of a specified shape which  has a reversed order of the
     * points, lines and curves.
     * @param shape Original shape.
     * @return Shape with reversed direction.
     */
    public static Shape reverse(Shape shape) {
    	List<PathSegment> segments = getSegments(shape);

		boolean closed = false;
		Path2D reversed =
			new Path2D.Double(Path2D.WIND_NON_ZERO, segments.size());
		ListIterator<PathSegment> i = segments.listIterator(segments.size());
		while (i.hasPrevious()) {
			PathSegment segment = i.previous();

			if (segment.type == PathIterator.SEG_CLOSE) {
				closed = true;
				continue;
			}

			if (reversed.getCurrentPoint() == null) {
				reversed.moveTo(
					segment.end.getX(), segment.end.getY());
			}
			if (segment.type == PathIterator.SEG_LINETO) {
				reversed.lineTo(
					segment.start.getX(), segment.start.getY());
			} else if (segment.type == PathIterator.SEG_QUADTO) {
				reversed.quadTo(
					segment.coords[0], segment.coords[1],
					segment.start.getX(), segment.start.getY());
			} else if (segment.type == PathIterator.SEG_CUBICTO) {
				reversed.curveTo(
					segment.coords[2], segment.coords[3],
					segment.coords[0], segment.coords[1],
					segment.start.getX(), segment.start.getY());
			} else if (segment.type == PathIterator.SEG_MOVETO) {
				if (closed) {
					reversed.closePath();
					closed = false;
				}
			}
		}

		return reversed;
    }
}

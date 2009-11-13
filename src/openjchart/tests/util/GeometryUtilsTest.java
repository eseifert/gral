package openjchart.tests.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import openjchart.util.GeometryUtils;

import org.junit.Test;

public class GeometryUtilsTest {
	private static final double DELTA = 1e-5;

	@Test
	public void testIntersectionShapeShape() {
		Line2D l1, l2;
		Point2D expected;

		l1 = new Line2D.Double(0.0, 0.0, 1.0, 1.0);
		l2 = new Line2D.Double(0.0, 1.0, 1.0, 0.0);
		expected = new Point2D.Double(0.5, 0.5);
		assertEquals(expected, GeometryUtils.intersection(l1, l2));

		l1 = new Line2D.Double( 0.0,  0.0, -1.0, -1.0);
		l2 = new Line2D.Double( 0.0, -1.0, -1.0,  0.0);
		expected = new Point2D.Double(-0.5, -0.5);
		assertEquals(expected, GeometryUtils.intersection(l1, l2));

		l1 = new Line2D.Double(0.0, 0.0, 1.0, 1.0);
		l2 = new Line2D.Double(0.0, 1.0, 1.0, 2.0);
		expected = null;
		assertEquals(expected, GeometryUtils.intersection(l1, l2));
	}

	@Test
	public void testGrow() {
		Shape normal = new Rectangle2D.Double(0.0, 0.0, 1.0, 1.0);
		Shape grown = GeometryUtils.grow(normal, 0.5);

		// Test inner region
		for (double coord=-0.25; coord<=1.25; coord+=0.25) {
			assertTrue(grown.contains(coord, coord));
		}

		// Test boundary
		Rectangle2D grownBounds = grown.getBounds2D();
		assertEquals(-0.5, grownBounds.getMinX(), DELTA);
		assertEquals(-0.5, grownBounds.getMinY(), DELTA);
		assertEquals( 1.5, grownBounds.getMaxX(), DELTA);
		assertEquals( 1.5, grownBounds.getMaxY(), DELTA);
	}

}

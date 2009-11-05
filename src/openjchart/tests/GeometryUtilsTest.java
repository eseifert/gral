package openjchart.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import openjchart.util.GeometryUtils;

import org.junit.Test;

public class GeometryUtilsTest {
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
		assertTrue(grown.contains( 0.500,  0.500));
		assertTrue(grown.contains(-0.250, -0.250));
		assertTrue(grown.contains( 1.250,  1.250));
		assertTrue(grown.contains(-0.499, -0.499));  // FIXME: -0.5, -0.5
		assertTrue(grown.contains( 1.499,  1.499));  // FIXME:  1.5,  1.5
	}

}

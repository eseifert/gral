package openjchart.tests;

import static org.junit.Assert.assertEquals;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import openjchart.util.GeometryUtils;

import org.junit.Test;

public class GeometryUtilsTest {
	@Test
	public void testIntersectionLineLine() {
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
}

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.junit.Test;

public class GeometryUtilsTest {
	private static final double DELTA = 1e-14;

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

	@Test
	public void testIntersectionShapeShape() {
		Shape s1 = new Rectangle2D.Double(0.0, 0.0, 1.0, 1.0);
		Shape s2 = new Rectangle2D.Double(0.5, 0.5, 1.0, 1.0);
		Point2D expected1 = new Point2D.Double(1.0, 0.5);
		Point2D expected2 = new Point2D.Double(0.5, 1.0);

		List<Point2D> intersections = GeometryUtils.intersection(s1, s2);
		assertTrue(intersections.contains(expected1));
		assertTrue(intersections.contains(expected2));
	}

	@Test
	public void testGrow() {
		Shape normal = new Rectangle2D.Double(0.0, 0.0, 1.0, 1.0);
		Shape grown = GeometryUtils.grow(normal, 0.5);
		Rectangle2D normalBounds = normal.getBounds2D();
		Rectangle2D grownBounds = grown.getBounds2D();

		// Test growth
		for (double coord=-0.25; coord<=1.25; coord+=0.25) {
			assertTrue(grown.contains(coord, coord));
		}
		assertEquals(-0.5, grownBounds.getMinX(), DELTA);
		assertEquals(-0.5, grownBounds.getMinY(), DELTA);
		assertEquals( 1.5, grownBounds.getMaxX(), DELTA);
		assertEquals( 1.5, grownBounds.getMaxY(), DELTA);

		// Test zero growth
		grown = GeometryUtils.grow(normal, 0.0);
		grownBounds = grown.getBounds2D();
		assertEquals(normalBounds, grownBounds);

		// Test shrinking
		grown = GeometryUtils.grow(normal, -0.25);
		grownBounds = grown.getBounds2D();
		assertEquals(0.25, grownBounds.getMinX(), DELTA);
		assertEquals(0.25, grownBounds.getMinY(), DELTA);
		assertEquals(0.75, grownBounds.getMaxX(), DELTA);
		assertEquals(0.75, grownBounds.getMaxY(), DELTA);
	}

}

/**
 * GRAL: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import org.junit.Test;

import de.erichseifert.gral.util.MathUtils;

public class MathUtilsTest {
	public static final double DELTA = 1e-15;

	@Test
	public void testAlmostEqual() {
		double delta = 1e-5;
		assertTrue(MathUtils.almostEqual(1.0, 1.0, delta));
		assertFalse(MathUtils.almostEqual(1.0, 2.0, delta));
		assertTrue(MathUtils.almostEqual(1.0, 1.0 + 0.5*delta, delta));
		assertFalse(MathUtils.almostEqual(1.0, 1.0 + 2.0*delta, delta));
	}

	@Test
	public void testRound() {
		assertEquals(1.0, MathUtils.round(1.0/1.0, 1e-1), DELTA);
		assertEquals(0.7, MathUtils.round(2.0/3.0, 1e-1), DELTA);
		assertEquals(0.5, MathUtils.round(1.0/2.0, 1e-1), DELTA);
		assertEquals(0.3, MathUtils.round(1.0/3.0, 1e-1), DELTA);
		assertEquals(0.1, MathUtils.round(1.0/9.0, 1e-1), DELTA);
		assertEquals(1.00, MathUtils.round(1.0/1.0, 1e-2), DELTA);
		assertEquals(0.67, MathUtils.round(2.0/3.0, 1e-2), DELTA);
		assertEquals(0.50, MathUtils.round(1.0/2.0, 1e-2), DELTA);
		assertEquals(0.33, MathUtils.round(1.0/3.0, 1e-2), DELTA);
		assertEquals(0.11, MathUtils.round(1.0/9.0, 1e-2), DELTA);
	}

	@Test
	public void testFloor() {
		assertEquals(1.0, MathUtils.floor(1.0/1.0, 1e-1), DELTA);
		assertEquals(0.6, MathUtils.floor(2.0/3.0, 1e-1), DELTA);
		assertEquals(0.5, MathUtils.floor(1.0/2.0, 1e-1), DELTA);
		assertEquals(0.3, MathUtils.floor(1.0/3.0, 1e-1), DELTA);
		assertEquals(0.1, MathUtils.floor(1.0/9.0, 1e-1), DELTA);
		assertEquals(1.00, MathUtils.floor(1.0/1.0, 1e-2), DELTA);
		assertEquals(0.66, MathUtils.floor(2.0/3.0, 1e-2), DELTA);
		assertEquals(0.50, MathUtils.floor(1.0/2.0, 1e-2), DELTA);
		assertEquals(0.33, MathUtils.floor(1.0/3.0, 1e-2), DELTA);
		assertEquals(0.11, MathUtils.floor(1.0/9.0, 1e-2), DELTA);
	}

	@Test
	public void testCeil() {
		assertEquals(1.0, MathUtils.ceil(1.0/1.0, 1e-1), DELTA);
		assertEquals(0.7, MathUtils.ceil(2.0/3.0, 1e-1), DELTA);
		assertEquals(0.5, MathUtils.ceil(1.0/2.0, 1e-1), DELTA);
		assertEquals(0.4, MathUtils.ceil(1.0/3.0, 1e-1), DELTA);
		assertEquals(0.2, MathUtils.ceil(1.0/9.0, 1e-1), DELTA);
		assertEquals(1.00, MathUtils.ceil(1.0/1.0, 1e-2), DELTA);
		assertEquals(0.67, MathUtils.ceil(2.0/3.0, 1e-2), DELTA);
		assertEquals(0.50, MathUtils.ceil(1.0/2.0, 1e-2), DELTA);
		assertEquals(0.34, MathUtils.ceil(1.0/3.0, 1e-2), DELTA);
		assertEquals(0.12, MathUtils.ceil(1.0/9.0, 1e-2), DELTA);
	}

	@Test
	public void testLimit() {
		assertEquals(0.0, MathUtils.limit(-0.5, 0.0, 1.0), DELTA);
		assertEquals(0.0, MathUtils.limit( 0.0, 0.0, 1.0), DELTA);
		assertEquals(0.5, MathUtils.limit( 0.5, 0.0, 1.0), DELTA);
		assertEquals(1.0, MathUtils.limit( 1.0, 0.0, 1.0), DELTA);
		assertEquals(1.0, MathUtils.limit( 1.5, 0.0, 1.0), DELTA);
	}

	@Test
	public void testBinarySearch() {
		double[] a = {0.0, 1.0};

		assertEquals(0, MathUtils.binarySearch(a, 0.0));
		assertEquals(1, MathUtils.binarySearch(a, 0.5));
		assertEquals(1, MathUtils.binarySearch(a, 1.0));

		assertEquals(0, MathUtils.binarySearchFloor(a, 0.0));
		assertEquals(0, MathUtils.binarySearchFloor(a, 0.5));
		assertEquals(1, MathUtils.binarySearchFloor(a, 1.0));

		assertEquals(0, MathUtils.binarySearchCeil(a, 0.0));
		assertEquals(1, MathUtils.binarySearchCeil(a, 0.5));
		assertEquals(1, MathUtils.binarySearchCeil(a, 1.0));
	}

	@Test
	public void testRandomizedSelect() {
		List<Double> a = Arrays.<Double>asList(13.0, 5.0, 8.0, 3.0, 1.0, 2.0, 1.0);

		for (int i = 0; i < a.size(); i++) {
			assertEquals(i, MathUtils.randomizedSelect(a, 0, a.size() - 1, i + 1));
		}

		// Check for integrity after sorting
		assertEquals(2, Collections.frequency(a,  1.0));
		assertEquals(1, Collections.frequency(a,  2.0));
		assertEquals(1, Collections.frequency(a,  3.0));
		assertEquals(1, Collections.frequency(a,  5.0));
		assertEquals(1, Collections.frequency(a,  8.0));
		assertEquals(1, Collections.frequency(a, 13.0));

		// Check behavior for empty lists
		List<Double> b = Arrays.<Double>asList();
		assertEquals(-1, MathUtils.randomizedSelect(b, 0, a.size() - 1, 1));
	}

}

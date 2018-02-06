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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class MathUtilsTest {
	public static final double DELTA = 1e-14;

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
		// Test with precision 0.0
		assertEquals(0.0, MathUtils.round(1.0, 0.0), DELTA);
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
		// Test with precision 0.0
		assertEquals(0.0, MathUtils.floor(1.0, 0.0), DELTA);
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
		// Test with precision 0.0
		assertEquals(0.0, MathUtils.ceil(1.0, 0.0), DELTA);
	}

	@Test
	public void testLimitDouble() {
		assertEquals(0.0, MathUtils.limit(-0.5, 0.0, 1.0), DELTA);
		assertEquals(0.0, MathUtils.limit( 0.0, 0.0, 1.0), DELTA);
		assertEquals(0.5, MathUtils.limit( 0.5, 0.0, 1.0), DELTA);
		assertEquals(1.0, MathUtils.limit( 1.0, 0.0, 1.0), DELTA);
		assertEquals(1.0, MathUtils.limit( 1.5, 0.0, 1.0), DELTA);
	}

	@Test
	public void testLimitFloat() {
		assertEquals(0.0, MathUtils.limit(-0.5f, 0.0f, 1.0f), DELTA);
		assertEquals(0.0, MathUtils.limit( 0.0f, 0.0f, 1.0f), DELTA);
		assertEquals(0.5, MathUtils.limit( 0.5f, 0.0f, 1.0f), DELTA);
		assertEquals(1.0, MathUtils.limit( 1.0f, 0.0f, 1.0f), DELTA);
		assertEquals(1.0, MathUtils.limit( 1.5f, 0.0f, 1.0f), DELTA);
	}

	@Test
	public void testLimitInt() {
		assertEquals( 0, MathUtils.limit(-5, 0, 10));
		assertEquals( 0, MathUtils.limit( 0, 0, 10));
		assertEquals( 5, MathUtils.limit( 5, 0, 10));
		assertEquals(10, MathUtils.limit(10, 0, 10));
		assertEquals(10, MathUtils.limit(15, 0, 10));
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
		List<Double> a = Arrays.asList(13.0, 5.0, 8.0, 3.0, 1.0, 2.0, 1.0);

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
		List<Double> b = Arrays.asList();
		assertEquals(-1, MathUtils.randomizedSelect(b, 0, a.size() - 1, 1));
	}

	@Test
	public void testMagnitude() {
		assertEquals( -0.01, MathUtils.magnitude(10.0,  -0.05), DELTA);
		assertEquals(  0.01, MathUtils.magnitude(10.0,   0.05), DELTA);
		assertEquals(  1.00, MathUtils.magnitude(10.0,   3.14), DELTA);
		assertEquals( 10.00, MathUtils.magnitude(10.0,  54.32), DELTA);
		assertEquals(100.00, MathUtils.magnitude(10.0, 123.45), DELTA);
	}

	@Test
	public void testQuantile() {
		List<Double> values = Arrays.asList(
				11.4, 17.3, 21.3, 25.9, 40.1, 50.5, 60.0, 70.0, 75.0);

		assertEquals(11.40, MathUtils.quantile(values, 0.0), DELTA);
		assertEquals(16.12, MathUtils.quantile(values, 0.1), DELTA);
		assertEquals(19.70, MathUtils.quantile(values, 0.2), DELTA);
		assertEquals(23.14, MathUtils.quantile(values, 0.3), DELTA);
		assertEquals(28.74, MathUtils.quantile(values, 0.4), DELTA);
		assertEquals(40.10, MathUtils.quantile(values, 0.5), DELTA);
		assertEquals(48.42, MathUtils.quantile(values, 0.6), DELTA);
		assertEquals(56.20, MathUtils.quantile(values, 0.7), DELTA);
		assertEquals(64.00, MathUtils.quantile(values, 0.8), DELTA);
		assertEquals(71.00, MathUtils.quantile(values, 0.9), DELTA);
		assertEquals(75.00, MathUtils.quantile(values, 1.0), DELTA);
	}

	@Test
	public void testIsCalculatable() {
		// Number
		assertTrue(MathUtils.isCalculatable(Byte.valueOf((byte) 0)));
		assertTrue(MathUtils.isCalculatable(Integer.valueOf(0)));
		assertTrue(MathUtils.isCalculatable(Long.valueOf(0L)));
		assertTrue(MathUtils.isCalculatable(new Float(0f)));
		assertTrue(MathUtils.isCalculatable(new Double(0.0)));
		assertFalse(MathUtils.isCalculatable(null));
		assertFalse(MathUtils.isCalculatable(new Double(Double.NaN)));
		assertFalse(MathUtils.isCalculatable(new Double(Double.NEGATIVE_INFINITY)));
		assertFalse(MathUtils.isCalculatable(new Double(Double.POSITIVE_INFINITY)));

		// double
		assertTrue(MathUtils.isCalculatable(0.0));
		assertFalse(MathUtils.isCalculatable(Double.NaN));
		assertFalse(MathUtils.isCalculatable(Double.NEGATIVE_INFINITY));
		assertFalse(MathUtils.isCalculatable(Double.POSITIVE_INFINITY));
	}
}

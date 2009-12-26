/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.tests.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import openjchart.util.MathUtils;

import org.junit.Test;

public class MathUtilsTest {
	@Test
	public void testAlmostEqual() {
		double delta = 1e-5;
		assertTrue(MathUtils.almostEqual(1.0, 1.0, delta));
		assertFalse(MathUtils.almostEqual(1.0, 2.0, delta));
		assertTrue(MathUtils.almostEqual(1.0, 1.0 + 0.5*delta, delta));
		assertFalse(MathUtils.almostEqual(1.0, 1.0 + 2.0*delta, delta));
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

		// TODO: More tests
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
	}

}

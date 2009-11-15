package openjchart.tests.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
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
	}

}

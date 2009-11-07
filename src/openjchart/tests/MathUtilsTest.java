package openjchart.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
		double[] a = new double[] {0.0, 1.0};

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
}

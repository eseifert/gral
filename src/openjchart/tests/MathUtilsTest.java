package openjchart.tests;

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
}

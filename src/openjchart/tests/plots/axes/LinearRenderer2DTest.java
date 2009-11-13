package openjchart.tests.plots.axes;

import static org.junit.Assert.assertEquals;
import openjchart.plots.axes.Axis;
import openjchart.plots.axes.LinearRenderer2D;

import org.junit.Before;
import org.junit.Test;

public class LinearRenderer2DTest {
	private static Axis axis;
	private static LinearRenderer2D renderer;

	@Before
	public void setUp() throws Exception {
		axis = new Axis(-5, 5);
		renderer = new LinearRenderer2D();
	}

	@Test
	public void testWorldToView() {
		double delta = 1e-5;
		assertEquals(0.0, renderer.worldToView(axis, -5), delta);
		assertEquals(1.0, renderer.worldToView(axis, 5), delta);
		assertEquals(0.5, renderer.worldToView(axis, 0), delta);
		assertEquals(-0.5, renderer.worldToView(axis, -10), delta);
		assertEquals(1.5, renderer.worldToView(axis, 10), delta);
		assertEquals(0.8, renderer.worldToView(axis, 3), delta);
	}

}

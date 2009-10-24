package openjchart.tests;

import static org.junit.Assert.assertEquals;
import openjchart.charts.axes.Axis;
import openjchart.charts.axes.LinearRenderer2D;

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
	public void testGetPos() {
		double delta = 1e-5;
		assertEquals(0.0, renderer.getPos(axis, -5), delta);
		assertEquals(1.0, renderer.getPos(axis, 5), delta);
		assertEquals(0.5, renderer.getPos(axis, 0), delta);
		assertEquals(-0.5, renderer.getPos(axis, -10), delta);
		assertEquals(1.5, renderer.getPos(axis, 10), delta);
		assertEquals(0.8, renderer.getPos(axis, 3), delta);
	}

}

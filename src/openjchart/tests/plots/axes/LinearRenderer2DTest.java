package openjchart.tests.plots.axes;

import static org.junit.Assert.assertEquals;
import openjchart.plots.axes.Axis;
import openjchart.plots.axes.LinearRenderer2D;

import org.junit.Before;
import org.junit.Test;

public class LinearRenderer2DTest {
	private Axis axis;
	private LinearRenderer2D renderer;

	@Before
	public void setUp() {
		axis = new Axis(-5, 5);
		renderer = new LinearRenderer2D();
	}

	@Test
	public void testWorldToView() {
		double delta = 1e-10;

		assertEquals( 0.0, renderer.worldToView(axis,  -5, false), delta);
		assertEquals( 1.0, renderer.worldToView(axis,   5, false), delta);
		assertEquals( 0.5, renderer.worldToView(axis,   0, false), delta);
		assertEquals( 0.0, renderer.worldToView(axis, -10, false), delta);
		assertEquals( 1.0, renderer.worldToView(axis,  10, false), delta);
		assertEquals( 0.8, renderer.worldToView(axis,   3, false), delta);

		assertEquals( 0.0, renderer.worldToView(axis,  -5, true), delta);
		assertEquals( 1.0, renderer.worldToView(axis,   5, true), delta);
		assertEquals( 0.5, renderer.worldToView(axis,   0, true), delta);
		assertEquals(-0.5, renderer.worldToView(axis, -10, true), delta);
		assertEquals( 1.5, renderer.worldToView(axis,  10, true), delta);
		assertEquals( 0.8, renderer.worldToView(axis,   3, true), delta);
	}

}

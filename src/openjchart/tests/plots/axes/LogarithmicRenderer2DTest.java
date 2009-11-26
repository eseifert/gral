package openjchart.tests.plots.axes;

import static org.junit.Assert.assertEquals;
import openjchart.plots.axes.Axis;
import openjchart.plots.axes.LogarithmicRenderer2D;

import org.junit.Before;
import org.junit.Test;

public class LogarithmicRenderer2DTest {
	private static Axis axis;
	private static LogarithmicRenderer2D renderer;

	@Before
	public void setUp() throws Exception {
		axis = new Axis(0, 10);
		renderer = new LogarithmicRenderer2D();
	}

	@Test
	public void testWorldToView() {
		double delta = 1e-5;
		double logMaxValue = Math.log(10);
		assertEquals(Double.NEGATIVE_INFINITY, renderer.worldToView(axis, 0, true), delta);
		assertEquals(1.0, renderer.worldToView(axis, 10, true), delta);
		assertEquals(Math.log(5) / logMaxValue, renderer.worldToView(axis, 5, true), delta);
		assertEquals(Math.log(1) / logMaxValue, renderer.worldToView(axis, 1, true), delta);
		assertEquals(Math.log(9) / logMaxValue, renderer.worldToView(axis, 9, true), delta);
		assertEquals(Math.log(0.1) / logMaxValue, renderer.worldToView(axis, 0.1, true), delta);
	}

}

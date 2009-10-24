package openjchart.tests;

import static org.junit.Assert.assertEquals;
import openjchart.charts.axes.Axis;
import openjchart.charts.axes.LogarithmicRenderer2D;

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
	public void testGetPos() {
		double delta = 1e-5;
		double logMaxValue = Math.log(10);
		assertEquals(Double.NEGATIVE_INFINITY, renderer.getPos(axis, 0), delta);
		assertEquals(1.0, renderer.getPos(axis, 10), delta);
		assertEquals(Math.log(5) / logMaxValue, renderer.getPos(axis, 5), delta);
		assertEquals(Math.log(1) / logMaxValue, renderer.getPos(axis, 1), delta);
		assertEquals(Math.log(9) / logMaxValue, renderer.getPos(axis, 9), delta);
		assertEquals(Math.log(0.1) / logMaxValue, renderer.getPos(axis, 0.1), delta);
	}

}

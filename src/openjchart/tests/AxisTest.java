package openjchart.tests;

import static org.junit.Assert.assertEquals;
import openjchart.charts.axes.Axis;

import org.junit.Before;
import org.junit.Test;

public class AxisTest {
	private static Axis axis;

	@Before
	public void setUp() throws Exception {
		axis = new Axis(-5, 5);
	}

	@Test
	public void testGetPos() {
		double delta = 1e-5;
		assertEquals( 0.0, axis.getPos( -5), delta);
		assertEquals( 1.0, axis.getPos(  5), delta);
		assertEquals( 0.5, axis.getPos(  0), delta);
		assertEquals( 0.2, axis.getPos( -3), delta);
		assertEquals(-0.5, axis.getPos(-10), delta);
		assertEquals( 1.5, axis.getPos( 10), delta);
	}
}

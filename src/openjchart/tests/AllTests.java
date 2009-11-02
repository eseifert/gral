package openjchart.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	UtilTests.class,
	DataTableTest.class,
	HistogramTest.class,
	AxisTest.class,
	LinearRenderer2DTest.class,
	LogarithmicRenderer2DTest.class
})
public class AllTests {
}

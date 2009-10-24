package openjchart.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	AxisTest.class,
	LinearRenderer2DTest.class,
	LogarithmicRenderer2DTest.class,
	DataTableTest.class
})
public class AllTests {
}

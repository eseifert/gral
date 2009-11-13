package openjchart.tests;

import openjchart.tests.data.DataTests;
import openjchart.tests.plots.PlotsTests;
import openjchart.tests.util.UtilTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	UtilTests.class,
	DataTests.class,
	PlotsTests.class
})
public class AllTests {
}

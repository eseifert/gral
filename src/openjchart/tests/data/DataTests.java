package openjchart.tests.data;

import openjchart.tests.data.statistics.StatisticsTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	DataTableTest.class,
	DataSeriesTest.class,
	StatisticsTests.class
})
public class DataTests {
}

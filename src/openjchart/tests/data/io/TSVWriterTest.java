package openjchart.tests.data.io;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import openjchart.data.DataTable;
import openjchart.data.io.TSVWriter;

import org.junit.BeforeClass;
import org.junit.Test;

public class TSVWriterTest {
	private static DataTable data;

	@BeforeClass
	public static void setUpBeforeClass() {
		data = new DataTable(Double.class, Double.class, Integer.class);
		data.add(0.0, 10.0, 20);
		data.add(1.0, 11.0, 21);
		data.add(2.0, 12.0, 22);
	}

	@Test
	public void testWriter() throws IOException {
		Writer output = new StringWriter();

		TSVWriter tsv = new TSVWriter(output);
		tsv.write(data);

		assertEquals(
			"0.0\t10.0\t20\n" +
			"1.0\t11.0\t21\n" +
			"2.0\t12.0\t22\n",
			output.toString()
		);
	}

}
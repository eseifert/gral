package openjchart.tests.data.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;

import openjchart.data.DataSource;
import openjchart.data.io.TSVReader;

import org.junit.Test;

public class TSVReaderTest {
	@Test
	public void testReader() throws IOException, ParseException {
		Reader input = new StringReader(
			"0\t10.0\t20\n" +
			"1\t11.0\t21\n" +
			"2\t12.0\t22"
		);

		TSVReader tsv = new TSVReader(input, Integer.class, Double.class, Double.class);
		DataSource data = tsv.read();

		assertEquals( 0,   data.get(0, 0));
		assertEquals( 1,   data.get(0, 1));
		assertEquals( 2,   data.get(0, 2));
		assertEquals(10.0, data.get(1, 0));
		assertEquals(11.0, data.get(1, 1));
		assertEquals(12.0, data.get(1, 2));
		assertEquals(20.0, data.get(2, 0));
		assertEquals(21.0, data.get(2, 1));
		assertEquals(22.0, data.get(2, 2));
	}

	@Test
	public void testIllegalNumberFormat() throws IOException {
		Reader input = new StringReader(
			"0.0\t10.0\t20\n" +
			"1\t11.0\t21\n" +
			"2\t12.0\t22"
		);
		TSVReader tsv = new TSVReader(input, Integer.class, Double.class, Double.class);
		try {
			tsv.read();
			fail("Expected ParseException");
		} catch (ParseException e) {
		}
	}

	@Test
	public void testIllegalColumnCount() throws IOException, ParseException {
		Reader input = new StringReader(
			"0\t10.0\t20\n" +
			"1\t11.0\n" +
			"2\t12.0\t22"
		);
		TSVReader tsv = new TSVReader(input, Integer.class, Double.class, Double.class);
		try {
			tsv.read();
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
		}
	}
}
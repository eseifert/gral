/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <mseifert[at]error-reports.org>
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.erichseifert.gral.io.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import org.junit.Test;

import de.erichseifert.gral.data.DataSource;

public class CSVReaderTest {
	@Test
	@SuppressWarnings("unchecked")
	public void testMimeTypes() throws IOException {
		String[] formats = {
			"text/csv",
			"text/tab-separated-values"
		};

		String[] dataStrings = {
			"0,10.0,20\r\n" +
			"1,11.0,21\r\n" +
			"2,12.0,22\r\n",

			"0\t10.0\t20\r\n" +
			"1\t11.0\t21\r\n" +
			"2\t12.0\t22\r\n"
		};

		for (int i = 0; i < formats.length; i++) {
			DataReader reader = DataReaderFactory.getInstance().get(formats[i]);
			ByteArrayInputStream input = new ByteArrayInputStream(dataStrings[i].getBytes());
			DataSource data = reader.read(input, Integer.class, Double.class, Double.class);
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
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSeparator() throws IOException {
		InputStream input = new ByteArrayInputStream((
			"0;10.0;20\r\n" +
			"1;11.0;21\r\n" +
			"2;12.0;22\r\n"
		).getBytes());

		DataReader reader = DataReaderFactory.getInstance().get("text/csv");
		reader.setSetting(CSVReader.SEPARATOR_CHAR, ';');
		DataSource data = reader.read(input, Integer.class, Double.class, Double.class);

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
	@SuppressWarnings("unchecked")
	public void testQuotedNumbers() throws IOException {
		InputStream input = new ByteArrayInputStream((
			"\"0\",\"10.0\",\"20\"\r\n" +
			"\"1\",\"11.0\",\"21\"\r\n" +
			"\"2\",\"12.0\",\"22\"\r\n"
		).getBytes());
		DataReader reader = DataReaderFactory.getInstance().get("text/csv");
		DataSource data = reader.read(input, Integer.class, Double.class, Double.class);

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
	@SuppressWarnings("unchecked")
	public void testQuotedString() throws IOException {
		InputStream input = new ByteArrayInputStream((
			"\"foo\tbar\"\tfoo bar\r\n" +
			"foobar\t\"foo \"\"the\"\" bar\"\r\n"
		).getBytes());
		DataReader reader = DataReaderFactory.getInstance().get("text/tab-separated-values");
		DataSource data = reader.read(input, String.class, String.class);

		assertEquals("foo\tbar", data.get(0, 0));
		assertEquals("foo bar", data.get(1, 0));
		assertEquals("foobar", data.get(0, 1));
		assertEquals("foo \"the\" bar", data.get(1, 1));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testLineSeparators() throws IOException {
		String[] dataStrings = {
			"0,10.0,20\r\n" +
			"1,11.0,21\r\n" +
			"2,12.0,22\r\n",

			"0,10.0,20\r" +
			"1,11.0,21\r" +
			"2,12.0,22\r",

			"0,10.0,20\n" +
			"1,11.0,21\n" +
			"2,12.0,22\n",

			"0,10.0,20\r\n" +
			"1,11.0,21\r\n" +
			"2,12.0,22",

			"0,10.0,20\r" +
			"1,11.0,21\r" +
			"2,12.0,22",

			"0,10.0,20\n" +
			"1,11.0,21\n" +
			"2,12.0,22"
		};

		for (String dataString : dataStrings) {
			DataReader reader = DataReaderFactory.getInstance().get("text/csv");
			ByteArrayInputStream input = new ByteArrayInputStream(dataString.getBytes());
			DataSource data = reader.read(input, Integer.class, Double.class, Double.class);
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
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testIllegalType() throws IOException {
		InputStream input = new ByteArrayInputStream((
			"0.0,10.0,20\r\n" +
			"1,11.0,21\r\n" +
			"2,12.0,22\r\n"
		).getBytes());
		DataReader reader = DataReaderFactory.getInstance().get("text/csv");
		try {
			reader.read(input, Integer.class, Double.class, Double.class);
			fail("Expected IOException");
		} catch (IOException e) {
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testNotEnoughColumns() throws IOException, ParseException {
		InputStream input = new ByteArrayInputStream((
			"0,10.0,20\r\n" +
			"1,11.0\r\n" +
			"2,12.0,22\r\n"
		).getBytes());
		DataReader reader = DataReaderFactory.getInstance().get("text/csv");
		try {
			reader.read(input, Integer.class, Double.class, Double.class);
			fail("Expected IllegalArgumentException because there are not enough columns.");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testTooManyColumns() throws IOException, ParseException {
		InputStream input = new ByteArrayInputStream((
			"0,10.0,20\r\n" +
			"1,11.0,21,42\r\n" +
			"2,12.0,22\r\n"
		).getBytes());
		DataReader reader = DataReaderFactory.getInstance().get("text/csv");
		try {
			reader.read(input, Integer.class, Double.class, Double.class);
			fail("Expected IllegalArgumentException because there are too many columns.");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testEmptyValues() throws IOException, ParseException {
		InputStream input = new ByteArrayInputStream((
			"0,10.0,\r\n" +
			"1,,21\r\n" +
			",,3\r\n"
		).getBytes());
		DataReader reader = DataReaderFactory.getInstance().get("text/csv");
		DataSource data = reader.read(input, Integer.class, Double.class, Double.class);
		assertEquals( 0,   data.get(0, 0));
		assertEquals(10.0, data.get(1, 0));
		assertNull(        data.get(2, 0));
		assertEquals( 1,   data.get(0, 1));
		assertNull(        data.get(1, 1));
		assertEquals(21.0, data.get(2, 1));
		assertNull(        data.get(0, 2));
		assertNull(        data.get(1, 2));
		assertEquals( 3.0, data.get(2, 2));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testNegativeIntegerValues() throws IOException, ParseException {
		InputStream input = new ByteArrayInputStream((
			"-0,-10.0,-20\r\n" +
			"-1,-11.0,-21\r\n" +
			"-2,-12.0,-22\r\n"
		).getBytes());
		DataReader reader = DataReaderFactory.getInstance().get("text/csv");
		DataSource data = reader.read(input, Integer.class, Double.class, Double.class);
		assertEquals(  0,   data.get(0, 0));
		assertEquals( -1,   data.get(0, 1));
		assertEquals( -2,   data.get(0, 2));
		assertEquals(-10.0, data.get(1, 0));
		assertEquals(-11.0, data.get(1, 1));
		assertEquals(-12.0, data.get(1, 2));
		assertEquals(-20.0, data.get(2, 0));
		assertEquals(-21.0, data.get(2, 1));
		assertEquals(-22.0, data.get(2, 2));
	}
}

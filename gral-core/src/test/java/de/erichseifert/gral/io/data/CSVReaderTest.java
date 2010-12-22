/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2010 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import org.junit.Test;

import de.erichseifert.gral.data.DataSource;

public class CSVReaderTest {
	@Test
	public void testReader() throws IOException {
		InputStream input = new ByteArrayInputStream((
			"0;10.0;20\r\n" +
			"1;11.0;21\r\n" +
			"2;12.0;22"
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
	public void testSeparator() throws IOException {
		InputStream input = new ByteArrayInputStream((
			"0\t10.0\t20\r\n" +
			"1\t11.0\t21\r\n" +
			"2\t12.0\t22"
		).getBytes());

		DataReader reader = DataReaderFactory.getInstance().get("text/csv");
		reader.setSetting("separator", "\t");
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
	public void testIllegalNumberFormat() throws IOException {
		InputStream input = new ByteArrayInputStream((
			"0.0;10.0;20\r\n" +
			"1;11.0;21\r\n" +
			"2;12.0;22"
		).getBytes());
		DataReader reader = DataReaderFactory.getInstance().get("text/csv");
		try {
			reader.read(input, Integer.class, Double.class, Double.class);
			fail("Expected IOException");
		} catch (IOException e) {
		}
	}

	@Test
	public void testIllegalColumnCount() throws IOException, ParseException {
		InputStream input = new ByteArrayInputStream((
			"0;10.0;20\r\n" +
			"1;11.0\r\n" +
			"2;12.0;22"
		).getBytes());
		DataReader reader = DataReaderFactory.getInstance().get("text/csv");
		try {
			reader.read(input, Integer.class, Double.class, Double.class);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
		}
	}

}
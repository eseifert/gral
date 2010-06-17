/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.data.DataTable;

public class CSVWriterTest {
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
		OutputStream output = new ByteArrayOutputStream();

		DataWriter writer = DataWriterFactory.getInstance().get("text/csv");
		writer.write(data, output);

		assertEquals(
			"0.0;10.0;20\r\n" +
			"1.0;11.0;21\r\n" +
			"2.0;12.0;22\r\n",
			output.toString()
		);
	}

	@Test
	public void testSeparator() throws IOException {
		OutputStream output = new ByteArrayOutputStream();

		DataWriter writer = DataWriterFactory.getInstance().get("text/csv");
		writer.setSetting("separator", "\t");
		writer.write(data, output);

		assertEquals(
			"0.0\t10.0\t20\r\n" +
			"1.0\t11.0\t21\r\n" +
			"2.0\t12.0\t22\r\n",
			output.toString()
		);
	}

}
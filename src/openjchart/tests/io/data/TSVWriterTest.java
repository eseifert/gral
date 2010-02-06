/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.tests.io.data;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import openjchart.data.DataTable;
import openjchart.io.data.DataWriter;
import openjchart.io.data.DataWriterFactory;

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
		OutputStream output = new ByteArrayOutputStream();

		DataWriter tsv = DataWriterFactory.getInstance().getWriter(DataWriter.TYPE_CSV);
		tsv.write(data, output);

		assertEquals(
			"0.0\t10.0\t20\n" +
			"1.0\t11.0\t21\n" +
			"2.0\t12.0\t22\n",
			output.toString()
		);
	}

}
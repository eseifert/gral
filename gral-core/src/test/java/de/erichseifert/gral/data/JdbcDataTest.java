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
package de.erichseifert.gral.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.DummyJdbc;
import de.erichseifert.gral.TestUtils;

public class JdbcDataTest {
	private Connection connection;
	private DataTable table;

	@Before
	@SuppressWarnings("unchecked")
	public void setUpBeforeClass() {
		table = new DataTable(
			Byte.class, Short.class, Integer.class, Long.class,
			Float.class, Double.class,
			Date.class, Time.class, Timestamp.class,
			String.class
		);
		table.add((byte) 3, (short) 5, 1, 9L, 4.8f, 5.9, new Date(6), new Time(1), new Timestamp(8), "Jan"); // 0
		table.add((byte) 8, (short) 2, 2, 8L, 2.7f, 2.4, new Date(3), new Time(5), new Timestamp(2), "Feb"); // 1
		table.add((byte) 5, (short) 6, 3, 7L, 6.3f, 6.6, new Date(4), new Time(4), new Timestamp(7), "Mar"); // 2
		table.add((byte) 6, (short) 2, 4, 6L, 3.4f, 2.1, new Date(7), new Time(6), new Timestamp(1), "Apr"); // 3
		table.add((byte) 4, (short) 1, 5, 5L, 5.1f, 1.0, new Date(2), new Time(2), new Timestamp(6), "May"); // 4
		table.add((byte) 9, (short) 5, 6, 4L, 1.5f, 5.8, new Date(5), new Time(7), new Timestamp(4), "Jun"); // 5
		table.add((byte) 8, (short) 7, 7, 3L, 7.2f, 7.3, new Date(1), new Time(3), new Timestamp(3), "Jul"); // 6
		table.add((byte) 1, (short) 9, 8, 2L, 8.6f, 9.2, new Date(8), new Time(8), new Timestamp(5), "Aug"); // 7

		connection = new DummyJdbc(table);
	}

	@Test
	public void testCreate() {
		JdbcData data = new JdbcData(connection, "foobar");
		assertEquals(10, data.getColumnCount());
		assertEquals(8, data.getRowCount());
		Class<? extends Comparable<?>>[] typesExpected = table.getColumnTypes();
		Class<? extends Comparable<?>>[] typesActual = data.getColumnTypes();
		assertEquals(data.getColumnCount(), typesActual.length);
		assertArrayEquals(typesExpected, typesActual);
	}

	@Test
	public void testGetIntInt() {
		JdbcData data = new JdbcData(connection, "foobar");
		for (int rowIndex = 0; rowIndex < table.getRowCount(); rowIndex++) {
			for (int colIndex = 0; colIndex < table.getColumnCount(); colIndex++) {
				Comparable<?> expected = table.get(colIndex, rowIndex);
				Comparable<?> actual = data.get(colIndex, rowIndex);
				assertEquals(
					String.format("Wrong value at col=%d, row=%d.", colIndex, rowIndex),
					expected, actual
				);
			}
		}
	}

	@Test
	public void testXyz() {
	}

	@Test(expected=UnsupportedOperationException.class)
	@SuppressWarnings("unused")
	public void testSerialization() throws IOException, ClassNotFoundException {
		DataSource original = new JdbcData(connection, "foobar");
		DataSource deserialized = TestUtils.serializeAndDeserialize(original);
    }
}

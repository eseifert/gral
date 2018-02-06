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

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.data.statistics.Statistics;

public class ColumnTest {
	private static final double DELTA = TestUtils.DELTA;
	private static Column<Integer> col1;
	private static Column<Integer> col2;

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setUpBeforeClass() {
		col1 = new Column<>(Integer.class, 1, 2, 3, 4, 5, 6, 7, 8);
		col2 = new Column<>(Integer.class, 1, 3, 2, 6, 4, 8, 9, 11);
	}

	@Test
	public void testColumnFromIterableContainsValues() {
		Iterable<Integer> data = Arrays.asList(1, 2, 3, 4);

		Column<Integer> column = new Column<>(Integer.class, data);

		assertThat(column, hasItems(1, 2, 3, 4));
	}

	@Test
	public void testSizeReturnsTheNumberOfElements() {
		Column<Integer> column = new Column<>(Integer.class, 1, 2, 3, 4);

		int size = column.size();

		assertEquals(4, size);
	}

	@Test
	public void testGetReturnsValueAtSpecifiedElement() {
		Column<Integer> column = new Column<>(Integer.class, 1, 2, 3, 4);

		int value = column.get(1);

		assertEquals(2, value);
	}

	@Test
	public void testGetWithIndexOutOfColumnSizeReturnsNull() {
		Column<Integer> col = new Column<>(Integer.class, 1, 2);
		int colSize = col.size();

		Integer elementOutOfRange = col.get(colSize);

		assertThat(elementOutOfRange, nullValue());
	}

	@Test
	public void testColumnsWithIdenticalTypesAndValuesAreEqual() {
		Column<Integer> col1 = new Column<>(Integer.class, 1, 2, 3);
		Column<Integer> col2 = new Column<>(Integer.class, 1, 2, 3);

		boolean equal = col1.equals(col2);

		assertTrue(equal);
	}

	@Test
	public void testColumnDoesNotEqualANonColumnObject() {
		Column<Integer> column = new Column<>(Integer.class, 1, 2, 3);
		Object someObject = new Object();

		boolean equal = column.equals(someObject);

		assertFalse(equal);
	}

	@Test
	public void testColumnsWithDifferentTypesAndIdenticalValuesAreNotEqual() {
		Column<Integer> col1 = new Column<>(Integer.class, 1, 2, 3);
		@SuppressWarnings("unchecked")
		Column<?> col2 = new Column(Long.class, 1, 2, 3);

		boolean equal = col1.equals(col2);

		assertFalse(equal);
	}

	@Test
	public void testColumnsWithIdenticalTypesAndDifferentValuesAreNotEqual() {
		Column<Integer> col1 = new Column<>(Integer.class, 1, 2, 3);
		Column<Integer> col2 = new Column<>(Integer.class, 3, 2, 1);

		boolean equal = col1.equals(col2);

		assertFalse(equal);
	}

	@Test
	public void testToStringIsIdenticalForIdenticalColumns() {
		Column col1 = new Column(Integer.class, 1, 2, 3);
		Column col2 = new Column(Integer.class, 1, 2, 3);
		assertEquals(col1.toString(), col2.toString());
	}

	@Test
	public void testToStringIsNotNull() {
		assertNotNull(col1.toString());
	}

	@Test
	public void testToStringIsNotEmpty() {
		assertFalse(col1.toString().isEmpty());
	}

	@Test
	public void testStatistics() {
		assertEquals( 8.0, col2.getStatistics(Statistics.N),   DELTA);
		assertEquals( 1.0, col2.getStatistics(Statistics.MIN), DELTA);
		assertEquals(11.0, col2.getStatistics(Statistics.MAX), DELTA);
		assertEquals(44.0, col2.getStatistics(Statistics.SUM), DELTA);
	}

	@Test
	public void testSerializationPreservesSize() throws IOException, ClassNotFoundException {
		Column<Integer> original = new Column<>(Integer.class, 1, 2, 3);
		Column<Integer> deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(original.size(), deserialized.size());
	}

	@Test
	public void testGetTypeReturnsDataType() {
		Column<Integer> column = col1;

		Class<? extends Comparable<?>> columnType = column.getType();

		assertEquals(Integer.class, columnType);
	}
}

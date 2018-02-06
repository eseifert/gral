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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import org.hamcrest.CoreMatchers;

public class RecordTest {
	@Test
	public void testCreateEmptyRecord() {
		new Record();
	}

	@Test
	public void testCreatableFromComparables() {
		new Record(-3.0, 1, "SomeString", null);
	}

	@Test
	public void testCreatableFromList() {
		List<Comparable<?>> comparables = new ArrayList<>(4);
		comparables.add(-3.0);
		comparables.add(1);
		comparables.add("SomeString");
		comparables.add(null);

		new Record(comparables);
	}

	@Test
	public void testAllowsRetrievingValues() {
		Record record = new Record(-3.0, 1, "SomeString", null);

		assertThat(record.<Double>get(0), is(-3.0));
		assertThat(record.<Integer>get(1), is(1));
		assertThat(record.<String>get(2), is("SomeString"));
		assertThat(record.get(3), nullValue());
	}

	@Test
	public void testIteratorReturnsValues() {
		Record record = new Record(-3.0, 1, "SomeString", null);

		assertThat(record, CoreMatchers.<Comparable<?>>hasItems(-3.0, 1, "SomeString", null));
	}

	@Test
	public void testSizeReturnsNumberOfElements() {
		Record record = new Record(null, null, null);

		assertThat(record.size(), is(3));
	}

	@Test
	public void testIsSerializable() throws IOException {
		Record record = new Record(-3.0, 1, "SomeString", null);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (ObjectOutputStream oos = new ObjectOutputStream(out)) {
			oos.writeObject(record);
		} catch (NotSerializableException e) {
			fail("Unable to serialize " + Record.class.getName());
		}
		assertTrue(out.size() > 0);
	}

	@Test
	public void testRecordsWithDifferentSizeAreUnequal() {
		Record shorterRecord = new Record(0, 1);
		Record longerRecord = new Record(0, 1, 2, 3);

		boolean equal = shorterRecord.equals(longerRecord);

		assertThat(equal, is(false));
	}

	@Test
	public void testRecordsWithIdenticalContentsAreEqual() {
		Record r1 = new Record(-3.0, 1, "SomeString", null);
		Record r2 = new Record(-3.0, 1, "SomeString", null);

		boolean equal = r1.equals(r2);

		assertThat(equal, is(true));
	}

	@Test
	public void testIsNumericReturnsTrueIfValueIsNumber() {
		Record record = new Record(-3.0, 1, "SomeString", null);

		boolean numeric = record.isNumeric(1);

		assertThat(numeric, is(true));
	}

	@Test
	public void testIsNumericReturnsFalseIfValueIsNoNumber() {
		Record record = new Record(-3.0, 1, "SomeString", null);

		boolean numeric = record.isNumeric(2);

		assertThat(numeric, is(false));
	}

	@Test
	public void testIsNumericReturnsFalseIfValueIsNull() {
		Record record = new Record(-3.0, 1, "SomeString", null);

		boolean numeric = record.isNumeric(3);

		assertThat(numeric, is(false));
	}

	@Test
	public void testToStringReturnsReadableTuple() {
		Record record = new Record(-3.0, 1, "SomeString", null);

		String string = record.toString();

		assertThat(string, is("("+"-3.0, "+"1, "+"SomeString, "+"null"+")"));
	}

	@Test
	public void testInsertDoesNotModifyRecord() {
		Record record = new Record(-3.0, 1, "SomeString", null);
		Record identicalRecord = new Record(-3.0, 1, "SomeString", null);
		Comparable<?> someComparable = 5;
		int somePosition = 2;

		record.insert(someComparable, somePosition);

		assertThat(record, is(identicalRecord));
	}

	@Test
	public void testInsertAddsElementAtTheSpecifiedPosition() {
		Record record = new Record(-3.0, 1, "SomeString", null);
		Comparable<?> someComparable = 5;
		int somePosition = 2;

		Record newRecord = record.insert(someComparable, somePosition);

		assertThat(newRecord, hasItems(-3.0, 1, someComparable, "SomeString", null));
	}
}

/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2026 Erich Seifert <dev[at]erichseifert.de>,
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.Test;

import org.hamcrest.Matchers;

import de.erichseifert.gral.TestUtils;

public class RecordTest {
	@Test
	public void testCreateEmptyRecord() {
		var record = new Record();

		assertThat(record.size(), is(0));
	}

	@Test
	public void testCreatableFromComparables() {
		var record = new Record(-3.0, 1, "SomeString", null);

		assertThat(record, Matchers.<Comparable<?>>contains(-3.0, 1, "SomeString", null));
	}

	@Test
	public void testCreatableFromList() {
		List<Comparable<?>> comparables = new ArrayList<>(4);
		comparables.add(-3.0);
		comparables.add(1);
		comparables.add("SomeString");
		comparables.add(null);

		var record = new Record(comparables);

		assertThat(record, Matchers.<Comparable<?>>contains(-3.0, 1, "SomeString", null));
	}

	@Test
	public void testAllowsRetrievingValues() {
		var record = new Record(-3.0, 1, "SomeString", null);

		assertThat(record.<Double>get(0), is(-3.0));
		assertThat(record.<Integer>get(1), is(1));
		assertThat(record.<String>get(2), is("SomeString"));
		assertThat(record.get(3), nullValue());
	}

	@Test
	public void testIteratorReturnsValues() {
		var record = new Record(-3.0, 1, "SomeString", null);

		assertThat(record, Matchers.<Comparable<?>>contains(-3.0, 1, "SomeString", null));
	}

	@Test
	public void testSizeReturnsNumberOfElements() {
		var record = new Record(null, null, null);

		assertThat(record.size(), is(3));
	}

	@Test
	public void testIsSerializable() throws IOException, ClassNotFoundException {
		var record = new Record(-3.0, 1, "SomeString", null);

		Record deserialized = TestUtils.serializeAndDeserialize(record);

		assertThat(deserialized, is(record));
	}

	@Test
	public void testRecordsWithDifferentSizeAreUnequal() {
		var shorterRecord = new Record(0, 1);
		var longerRecord = new Record(0, 1, 2, 3);

		boolean equal = shorterRecord.equals(longerRecord);

		assertThat(equal, is(false));
	}

	@Test
	public void testRecordsWithIdenticalContentsAreEqual() {
		var r1 = new Record(-3.0, 1, "SomeString", null);
		var r2 = new Record(-3.0, 1, "SomeString", null);

		boolean equal = r1.equals(r2);

		assertThat(equal, is(true));
	}

	@Test
	public void testEqualRecordsHaveEqualHashCodes() {
		var r1 = new Record(-3.0, 1, "SomeString", null);
		var r2 = new Record(-3.0, 1, "SomeString", null);

		assertThat(r1, is(r2));
		assertThat(r1.hashCode(), is(r2.hashCode()));
	}

	@Test
	public void testRecordsCanBeUsedInHashBasedCollections() {
		var records = new HashSet<Record>();
		records.add(new Record(-3.0, 1, "SomeString", null));

		assertThat(records.contains(new Record(-3.0, 1, "SomeString", null)), is(true));
		assertThat(records.contains(new Record(-3.0, 1, "OtherString", null)), is(false));
	}

	@Test
	public void testIsNumericReturnsTrueIfValueIsNumber() {
		var record = new Record(-3.0, 1, "SomeString", null);

		boolean numeric = record.isNumeric(1);

		assertThat(numeric, is(true));
	}

	@Test
	public void testIsNumericReturnsFalseIfValueIsNoNumber() {
		var record = new Record(-3.0, 1, "SomeString", null);

		boolean numeric = record.isNumeric(2);

		assertThat(numeric, is(false));
	}

	@Test
	public void testIsNumericReturnsFalseIfValueIsNull() {
		var record = new Record(-3.0, 1, "SomeString", null);

		boolean numeric = record.isNumeric(3);

		assertThat(numeric, is(false));
	}

	@Test
	public void testToStringReturnsReadableTuple() {
		var record = new Record(-3.0, 1, "SomeString", null);

		String string = record.toString();

		assertThat(string, is("("+"-3.0, "+"1, "+"SomeString, "+"null"+")"));
	}

	@Test
	public void testInsertDoesNotModifyRecord() {
		var record = new Record(-3.0, 1, "SomeString", null);
		var identicalRecord = new Record(-3.0, 1, "SomeString", null);
		Comparable<?> someComparable = 5;
		int somePosition = 2;

		record.insert(someComparable, somePosition);

		assertThat(record, is(identicalRecord));
	}

	@Test
	public void testInsertAddsElementAtTheSpecifiedPosition() {
		var record = new Record(-3.0, 1, "SomeString", null);
		Comparable<?> someComparable = 5;
		int somePosition = 2;

		Record newRecord = record.insert(someComparable, somePosition);

		assertThat(newRecord, Matchers.<Comparable<?>>contains(-3.0, 1, someComparable, "SomeString", null));
	}
}

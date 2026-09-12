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

import static java.util.Arrays.copyOf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>An immutable tuple of values, one per column. A {@code DataTable} stores
 * its rows as records, and {@link de.erichseifert.gral.data.comparators.DataComparator}
 * sorts them.</p>
 *
 * <p>Unlike {@link Row}, a record is self-contained: it holds its own copy of
 * the values and has no reference to a data source, which is why it keeps its
 * values when the source changes and can be compared by value. None of its
 * methods modify it; {@link #insert(Comparable, int)} returns a new record.</p>
 *
 * <pre>
 * Record record = new Record(1.0, 2.0, "label");
 * Double x = record.get(0);
 * boolean numeric = record.isNumeric(2);   // false
 * Record wider = record.insert(0.5, 1);    // (1.0, 0.5, 2.0, label)
 * </pre>
 *
 * @see DataSource#getRecord(int)
 */
public class Record implements Iterable<Comparable<?>>, Serializable {
	/** Version id for serialization. */
	private static final long serialVersionUID = -4745244626788459039L;

	/** Values of this record, one per column. */
	private Comparable<?>[] values;

	/**
	 * Initializes a new record with a copy of the specified values.
	 * @param values Values in column order. May contain {@code null} entries.
	 */
	public Record(List<? extends Comparable<?>> values) {
		this.values = values.toArray(new Comparable[0]);
	}

	/**
	 * Initializes a new record with a copy of the specified values.
	 * @param values Values in column order. May contain {@code null} entries.
	 */
	public Record(Comparable<?>... values) {
		this.values = copyOf(values, values.length);
	}

	/**
	 * Returns the value at the specified position. The result is cast to the
	 * type expected at the call site, which is unchecked: asking for the wrong
	 * type throws {@code ClassCastException} at the caller, not here.
	 * @param <T> Expected type of the value.
	 * @param index Position of the value, starting at 0.
	 * @return Value at that position, possibly {@code null}.
	 * @throws ArrayIndexOutOfBoundsException if the position does not exist.
	 */
	public <T extends Comparable<?>> T get(int index) {
		return (T) values[index];
	}

	/**
	 * Returns the number of values in this record.
	 * @return Number of values.
	 */
	public int size() {
		return values.length;
	}

	@Override
	public Iterator<Comparable<?>> iterator() {
		return Arrays.asList(values).iterator();
	}

	/**
	 * Returns whether the value at the specified position is a number. This
	 * inspects the value itself, so a {@code null} in a numeric column is not
	 * numeric.
	 * @param index Position of the value, starting at 0.
	 * @return {@code true} if the value is a {@code Number}, otherwise
	 *         {@code false}.
	 */
	public boolean isNumeric(int index) {
		return values[index] instanceof Number;
	}

	/**
	 * Two records are equal if they have the same size and equal values in the
	 * same order.
	 * @param obj Object to compare to.
	 * @return {@code true} if the object is an equal record, otherwise
	 *         {@code false}.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Record)) {
			return false;
		}
		Record record = (Record) obj;
		return size() == record.size() && Arrays.equals(this.values, record.values);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(values);
	}

	@Override
	public String toString() {
		return Arrays.stream(values)
				.map(String::valueOf)
				.collect(Collectors.joining(", ", "(", ")"));
	}

	/**
	 * Returns a copy of this record with an additional value inserted at the
	 * specified position. This record is left unchanged.
	 * @param value Value to insert. May be {@code null}.
	 * @param position Position the new value will occupy, starting at 0. A
	 *        position equal to {@link #size()} appends the value.
	 * @return A new record, one value longer than this one.
	 * @throws IndexOutOfBoundsException if the position is negative or greater
	 *         than {@link #size()}.
	 */
	public Record insert(Comparable<?> value, int position) {
		var recordCopyAsList = new ArrayList<Comparable<?>>(Arrays.asList(values));
		recordCopyAsList.add(position, value);
		return new Record(recordCopyAsList);
	}
}

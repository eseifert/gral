/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.gral.data;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Locale;

/**
 * Abstract base for reading substructures of a data source, i.e. columns or
 * rows. <code>DataAccessor</code>s are iterable and provide utility methods
 * for statistics and array conversion.
 * @see DataSource
 */
public abstract class DataAccessor implements Iterable<Number> {
	/** Data source that provides the values that should be accessed. */
	private final DataSource source;
	/** Index of current column or row. */
	private final int index;

	/**
	 * Initializes a new instance with the specified data source and an access
	 * index.
	 * @param source Data source.
	 * @param index Column index.
	 */
	public DataAccessor(DataSource source, int index) {
		this.source = source;
		this.index = index;
	}

	/**
	 * Returns the data source containing this column.
	 * @return Data source containing this column.
	 */
	public DataSource getSource() {
		return source;
	}

	/**
	 * Returns the index to access the data source.
	 * @return Data index.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns the value of the data source for the specified index.
	 * @param index Index.
	 * @return Value of the accessed cell.
	 */
	public abstract Number get(int index);

	/**
	 * Returns the number of elements in this column.
	 * @return Number of elements
	 */
	public abstract int size();

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DataAccessor)) {
			return false;
		}
		DataAccessor r = (DataAccessor) obj;
		int size = size();
		if (r.size() != size) {
			return false;
		}
		for (int i = 0; i < size; i++) {
			if (!r.get(i).equals(get(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return source.hashCode() ^ index;
	}

	@Override
	public String toString() {
		return String.format(Locale.US,
				"%s[source=%s,index=%d]", //$NON-NLS-1$
				getClass().getName(), getSource(), getIndex());
	}

	/**
	 * Converts the data column to an array.
	 * @param data Optional array as data sink.
	 *             If array is <code>null</code> a new array will be created.
	 * @return Array with row data;
	 */
	public Number[] toArray(Number[] data) {
		if (data == null) {
			data = new Number[size()];
		}
		if (data.length != size()) {
			throw new IllegalArgumentException(MessageFormat.format(
				"Array of size {0,number,integer} does not match {1,number,integer} elements.", //$NON-NLS-1$
				data.length, size()));
		}
		for (int i = 0; i < data.length; i++) {
			data[i] = get(i);
		}
		return data;
	}

	/**
	 * Returns the specified statistical information for this data.
	 * @param key Requested Statistical information.
	 * @return Calculated value.
	 */
	public abstract double getStatistics(String key);

	@Override
	public Iterator<Number> iterator() {
		return new Iterator<Number>() {
			private int i;

			@Override
			public boolean hasNext() {
				return i < size();
			}

			@Override
			public Number next() {
				Number value = get(i++);
				return value;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}

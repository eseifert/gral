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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import de.erichseifert.gral.data.statistics.Statistics;

/**
 * <p>A one-dimensional sequence of values of a single type, together with that
 * type. A column is a <em>snapshot</em>: it copies the values it is given, so
 * later changes to the data source it came from are not visible through it.
 * That is the difference to {@link Row}, which reads through to its source.</p>
 *
 * <p>Columns are usually obtained from a data source rather than constructed:</p>
 * <pre>
 * DataTable data = new DataTable(Double.class, Double.class);
 * data.add(1.0, 4.0);
 * data.add(2.0, 5.0);
 *
 * Column&lt;?&gt; col = data.getColumn(1);
 * Comparable&lt;?&gt; secondValue = col.get(1);        // 5.0
 * double mean = col.getStatistics(Statistics.MEAN); // 4.5
 * </pre>
 *
 * <p>Building one directly is mainly useful for assembling a table from
 * columns rather than from rows:</p>
 * <pre>
 * Column&lt;Double&gt; x = new Column&lt;&gt;(Double.class, 1.0, 2.0, 3.0);
 * Column&lt;Double&gt; y = new Column&lt;&gt;(Double.class, 4.0, 5.0, 6.0);
 * DataTable table = new DataTable(x, y);
 * </pre>
 *
 * @param <T> Type of the values in this column.
 * @see DataSource#getColumn(int)
 * @see Row
 */
public class Column<T extends Comparable<T>> implements Iterable<T>, Serializable {
	/** Version id for serialization. */
	private static final long serialVersionUID = 7380420622890027262L;

	/** Type of the values stored in this column. */
	private final Class<T> dataType;
	/** Copy of the values of this column, in row order. */
	private final List<T> data;

	/**
	 * Initializes a new column with the specified type and values. The values
	 * are copied, so the array can be modified afterwards without affecting
	 * this column.
	 * @param dataType Type of the values.
	 * @param data Values in row order. May contain {@code null} entries.
	 */
	public Column(Class<T> dataType, T... data) {
		this(dataType, Arrays.asList(data));
	}

	/**
	 * Initializes a new column with the specified type and values. The values
	 * are copied into this column as the iterable is traversed.
	 * @param dataType Type of the values.
	 * @param data Values in row order. May contain {@code null} entries.
	 */
	public Column(Class<T> dataType, Iterable<T> data) {
		this.dataType = dataType;
		this.data = new ArrayList<>();
		for (T item : data) {
			this.data.add(item);
		}
	}

	/**
	 * Returns the value in the specified row. Indexes beyond the end of the
	 * column yield {@code null} rather than an exception, because a column may
	 * be shorter than the table it belongs to.
	 * @param row Row index, starting at 0.
	 * @return Value in that row, or {@code null} if the index is beyond the
	 *         last row or the cell is empty.
	 */
	public T get(int row) {
		return row >= data.size() ? null : data.get(row);
	}

	/**
	 * Returns the number of values in this column.
	 * @return Number of values.
	 */
	public int size() {
		return data.size();
	}

	/**
	 * Returns whether this column only contains numbers. This is decided from
	 * the column type, not from the values, so an empty column of a numeric
	 * type is numeric.
	 * @return {@code true} if this column is numeric, otherwise {@code false}.
	 */
	public boolean isNumeric() {
		return Number.class.isAssignableFrom(getType());
	}

	/**
	 * Returns the type of the values in this column.
	 * @return Type of the values.
	 */
	public Class<? extends Comparable<?>> getType() {
		return dataType;
	}

	/**
	 * Computes a statistical measure over the values of this column. The
	 * measure is named by one of the string constants on {@link Statistics},
	 * for example {@link Statistics#MEAN} or {@link Statistics#MAX}.
	 * Non-numeric and {@code null} values are skipped.
	 * @param key Name of the measure, see {@link Statistics}.
	 * @return Value of the measure.
	 */
	public double getStatistics(String key) {
		return new Statistics(data).get(key);
	}

	@Override
	public int hashCode() {
		return dataType.hashCode() ^ data.hashCode();
	}

	/**
	 * Two columns are equal if they have the same type and contain equal
	 * values in the same order. The data source a column was taken from is not
	 * part of its identity.
	 * @param obj Object to compare to.
	 * @return {@code true} if the object is an equal column, otherwise
	 *         {@code false}.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Column)) {
			return false;
		}
		Column<?> column = (Column<?>) obj;
		return getType().equals(column.getType()) && data.equals(column.data);
	}

	@Override
	public Iterator<T> iterator() {
		return data.iterator();
	}
}

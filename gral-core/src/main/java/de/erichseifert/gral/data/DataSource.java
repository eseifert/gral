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

import de.erichseifert.gral.data.statistics.Statistics;

/**
 * <p>Read-only access to a rectangular grid of values addressed by column and
 * row. This is the model every plot reads from; where the values come from is
 * up to the implementation, which may be an in-memory table
 * ({@link DataTable}), a view of another source ({@link DataSeries},
 * {@link RowSubset}), a filter
 * ({@link de.erichseifert.gral.data.filters.Filter2D}), or a database query
 * ({@link JdbcData}).</p>
 *
 * <p>Every value is a {@code Comparable}, and every column has a declared type.
 * Columns need not be numeric: a column of {@code String} labels is perfectly
 * valid, it just cannot be plotted on an axis. Cells may be {@code null}.</p>
 *
 * <pre>
 * for (int row = 0; row &lt; data.getRowCount(); row++) {
 *     for (int col = 0; col &lt; data.getColumnCount(); col++) {
 *         Comparable&lt;?&gt; value = data.get(col, row);
 *         …
 *     }
 * }
 * </pre>
 *
 * <p>Note the argument order of {@link #get(int, int)}: the column comes
 * first.</p>
 *
 * <p>Iterating a data source directly visits every cell in row-major order,
 * i.e. all columns of the first row, then all columns of the second, and so
 * on &mdash; not one row or one column at a time. Use {@link #getRow(int)} or
 * {@link #getColumn(int)} for those.</p>
 *
 * <p>A data source reports changes to registered
 * {@link DataListener}s. Implementations are expected to fire those
 * notifications whenever their values change, including when an underlying
 * source changes, so that plots can repaint and rescale their axes.</p>
 *
 * @see MutableDataSource
 * @see DataTable
 */
public interface DataSource extends Iterable<Comparable<?>> {
	/**
	 * Returns the column with the specified index. The returned column holds a
	 * copy of the values, so it does not track later changes of this data
	 * source.
	 * @param col index of the column to return
	 * @return the specified column of the data source
	 */
	// It is not possible to use this function with a generic type parameter,
	// due to broken type inference prior to Java 8.
	Column<?> getColumn(int col);

	/**
	 * Returns the declared data types of all columns, in column order. The
	 * returned array is a copy and may be modified freely.
	 * @return The data types of all column in the data source
	 */
	Class<? extends Comparable<?>>[] getColumnTypes();

	/**
	 * Returns the row with the specified index. The returned row is a live
	 * view: reading from it reads through to this data source.
	 * @param row index of the row to return
	 * @return the specified row of the data source
	 */
	Row getRow(int row);

	/**
	 * Returns the value at the specified column and row. Note that the column
	 * index comes first.
	 * @param col index of the column to return
	 * @param row index of the row to return
	 * @return the specified value of the data cell, which may be {@code null}
	 */
	Comparable<?> get(int col, int row);

	/**
	 * Returns statistical measures over all values of this data source. The
	 * measures are looked up by the string constants on {@link Statistics},
	 * for example {@code getStatistics().get(Statistics.MEAN)}. The result is
	 * computed on demand and discarded when the data changes, so it always
	 * reflects the current values.
	 * @return statistical information
	 */
	Statistics getStatistics();

	/**
	 * Computes one statistical measure per column and returns the results as a
	 * data source with a single row and one {@code Double} column per column of
	 * this data source.
	 * @param key Name of the measure, see {@link Statistics}.
	 * @return Single-row data source holding the measure for each column.
	 */
	DataSource getColumnStatistics(String key);

	/**
	 * Computes one statistical measure per row and returns the results as a
	 * data source with a single {@code Double} column and one row per row of
	 * this data source.
	 * @param key Name of the measure, see {@link Statistics}.
	 * @return Single-column data source holding the measure for each row.
	 */
	DataSource getRowStatistics(String key);

	/**
	 * Returns the number of rows of the data source.
	 * @return number of rows in the data source.
	 */
	int getRowCount();

	/**
	 * Returns the name of this data source. Legends display this name for the
	 * series they show.
	 * @return a name string, or {@code null} if none was set
	 */
	String getName();

	/**
	 * Returns the number of columns of the data source.
	 * @return number of columns in the data source.
	 */
	int getColumnCount();

	/**
	 * Returns whether the column at the specified index contains numbers. This
	 * is decided from the declared column type, not from the values. Only
	 * numeric columns can be mapped to an axis.
	 * @param columnIndex Index of the column to test.
	 * @return {@code true} if the column is numeric, otherwise {@code false}.
	 *         An index outside the valid range yields {@code false} rather
	 *         than an exception.
	 */
	boolean isColumnNumeric(int columnIndex);

	/**
	 * Adds the specified {@code DataListener} to this data source. The listener
	 * will be notified whenever values are added, removed or updated. Adding
	 * the same listener twice has no additional effect.
	 * @param dataListener listener to be added.
	 */
	void addDataListener(DataListener dataListener);

	/**
	 * Removes the specified {@code DataListener} from this data source.
	 * Removing a listener that was never added has no effect.
	 * @param dataListener listener to be removed.
	 */
	void removeDataListener(DataListener dataListener);

	/**
	 * Returns the values of the specified row as an immutable record. In
	 * contrast to {@link #getRow(int)} this is a snapshot that does not read
	 * through to this data source.
	 * @param row index of the row to return
	 * @return the values of that row
	 */
	Record getRecord(int row);
}

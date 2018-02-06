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

import de.erichseifert.gral.data.statistics.Statistics;

/**
 * Interface for an immutable access to tabular data.
 *
 * @see MutableDataSource
 */
public interface DataSource extends Iterable<Comparable<?>> {
	/**
	 * Returns the column with the specified index.
	 * @param col index of the column to return
	 * @return the specified column of the data source
	 */
	// It is not possible to use this function with a generic type parameter,
	// due to broken type inference prior to Java 8.
	Column<?> getColumn(int col);

	/**
	 * Returns the data types of all columns.
	 * @return The data types of all column in the data source
	 */
	Class<? extends Comparable<?>>[] getColumnTypes();

	/**
	 * Returns the row with the specified index.
	 * @param row index of the row to return
	 * @return the specified row of the data source
	 */
	Row getRow(int row);

	/**
	 * Returns the value with the specified row and column index.
	 * @param col index of the column to return
	 * @param row index of the row to return
	 * @return the specified value of the data cell
	 */
	Comparable<?> get(int col, int row);

	/**
	 * Retrieves a object instance that contains various statistical
	 * information on the current data source.
	 * @return statistical information
	 */
	Statistics getStatistics();

	DataSource getColumnStatistics(String key);

	DataSource getRowStatistics(String key);

	/**
	 * Returns the number of rows of the data source.
	 * @return number of rows in the data source.
	 */
	int getRowCount();

	/**
	 * Returns the name of this series.
	 * @return a name string
	 */
	String getName();

	/**
	 * Returns the number of columns of the data source.
	 * @return number of columns in the data source.
	 */
	int getColumnCount();

	/**
	 * Returns whether the column at the specified index contains numbers.
	 * @param columnIndex Index of the column to test.
	 * @return {@code true} if the column is numeric, otherwise {@code false}.
	 */
	boolean isColumnNumeric(int columnIndex);

	/**
	 * Adds the specified {@code DataListener} to this data source.
	 * @param dataListener listener to be added.
	 */
	void addDataListener(DataListener dataListener);

	/**
	 * Removes the specified {@code DataListener} from this data source.
	 * @param dataListener listener to be removed.
	 */
	void removeDataListener(DataListener dataListener);

	Record getRecord(int row);
}

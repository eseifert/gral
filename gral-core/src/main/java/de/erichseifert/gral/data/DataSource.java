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

import de.erichseifert.gral.data.statistics.Statistics;

/**
 * Immutable view on a source for tabular data.
 */
public interface DataSource extends Iterable<Number> {
	/**
	 * Returns the column with the specified index.
	 * @param col index of the column to return
	 * @return the specified column of the data source
	 */
	Column getColumn(int col);

	/**
	 * Returns the data types of all columns.
	 * @return The data types of all column in the data source
	 */
	Class<? extends Number>[] getColumnTypes();

	/**
	 * Returns the row with the specified index.
	 * @param row index of the row to return
	 * @return the specified row of the data source
	 */
	Row getRow(int row);

	/**
	 * Returns the row with the specified index.
	 * @param col index of the column to return
	 * @param row index of the row to return
	 * @return the specified value of the data cell
	 */
	Number get(int col, int row);

	/**
	 * Retrieves a object instance that contains various statistical
	 * information on the current data source.
	 * @return statistical information
	 */
	Statistics getStatistics();

	/**
	 * Returns the number of rows of the data source.
	 * @return number of rows in the data source.
	 */
	int getRowCount();

	/**
	 * Returns the number of columns of the data source.
	 * @return number of columns in the data source.
	 */
	int getColumnCount();

	/**
	 * Adds the specified <code>DataListener</code> to this data source.
	 * @param dataListener listener to be added.
	 */
	void addDataListener(DataListener dataListener);

	/**
	 * Removes the specified <code>DataListener</code> from this data source.
	 * @param dataListener listener to be removed.
	 */
	void removeDataListener(DataListener dataListener);
}

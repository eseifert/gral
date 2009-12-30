/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.data;

import openjchart.data.statistics.Statistics;

/**
 * Immutable view on a source for tabular data.
 */
public interface DataSource extends Iterable<Number[]> {

	/**
	 * Returns the row with the specified index.
	 * @param row index of the row to return
	 * @return the specified row of the table
	 */
	Number[] get(int row);

	/**
	 * Returns the row with the specified index.
	 * @param col index of the column to return
	 * @param row index of the row to return
	 * @return the specified value of the table cell
	 */
	Number get(int col, int row);

	/**
	 * Retrieves a object instance that contains various statistical information
	 * on the current data source.
	 * @return statistical information
	 */
	Statistics getStatistics();

	/**
	 * Returns the number of rows of the table.
	 * @return number of rows in the table
	 */
	int getRowCount();

	/**
	 * Returns the number of columns of the table.
	 * @return number of columns in the table
	 */
	int getColumnCount();

	/**
	 * Adds the specified DataListener to this DataSource.
	 * @param dataListener listener to be added
	 */
	public void addDataListener(DataListener dataListener);

	/**
	 * Adds the specified DataListener from this DataSource.
	 * @param dataListener listener to be removed
	 */
	public void removeDataListener(DataListener dataListener);
}

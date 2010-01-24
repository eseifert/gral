/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
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


/**
 * Class for storing a row of a data source.
 */
public class Row {
	private final DataSource source;
	private final int row;

	/**
	 * Creates a new Row object with the specified DataSource and row index.
	 * @param source DataSource.
	 * @param row Row index.
	 */
	public Row(DataSource source, int row) {
		this.source = source;
		this.row = row;
	}

	/**
	 * Returns the DataSource containing this row.
	 * @return DataSource containing this row.
	 */
	public DataSource getSource() {
		return source;
	}

	/**
	 * Returns the index of this row in the DataSource.
	 * @return Row index.
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Returns the value of this row for the specified column.
	 * @param col Column index.
	 * @return Value of the cell.
	 */
	public Number get(int col) {
		if (getSource() == null) {
			return null;
		}
		return getSource().get(col, getRow());
	}

}

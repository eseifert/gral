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

import java.util.Arrays;

/**
 * Class that represents a DataSource containing the same value in each cell.
 */
public class DummyData extends AbstractDataSource {
	private Number value;
	private Number[] dummyRow;
	private int cols;
	private int rows;

	/**
	 * Creates a new DummyData object with the specified number of columns
	 * and rows, which are filled all over with the same specified value.
	 * @param cols Number of columns.
	 * @param rows Number of rows.
	 * @param value Value of the cells.
	 */
	public DummyData(int cols, int rows, Number value) {
		this.cols = cols;
		this.rows = rows;
		this.value = value;
		dummyRow = new Number[this.cols];
		Arrays.fill(dummyRow, this.value);
	}

	@Override
	public Number[] get(int row) {
		return dummyRow;
	}

	@Override
	public Number get(int col, int row) {
		return get(row)[col];
	}

	@Override
	public int getColumnCount() {
		return cols;
	}

	@Override
	public int getRowCount() {
		return rows;
	}

}

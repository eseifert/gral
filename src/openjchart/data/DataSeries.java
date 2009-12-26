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

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a view on a DataSource.
 * @see DataSource
 */
public class DataSeries extends AbstractDataSource implements DataListener {
	private DataSource data;
	private final List<Integer> cols;
	private String name;

	/**
	 * Constructor without name.
	 * The first column will be column 0, the second column 1 and so on,
	 * whereas the value of the specified columns is the column number
	 * in the DataSource.
	 * @param data Data source
	 * @param cols Column numbers
	 */
	public DataSeries(DataSource data, int... cols) {
		this(null, data, cols);
	}

	/**
	 * Constructor.
	 * The first column will be column 0, the second column 1 and so on,
	 * whereas the value of the specified columns is the column number
	 * in the DataSource.
	 * @param name Descriptive name
	 * @param data Data source
	 * @param cols Column numbers
	 */
	public DataSeries(String name, DataSource data, int... cols) {
		this.name = name;
		this.data = data;
		this.data.addDataListener(this);
		this.cols = new ArrayList<Integer>(cols.length);
		for (int col : cols) {
			this.cols.add(col);
		}
	}

	/**
	 * Returns the name of this series.
	 * @return a name string
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this series.
	 * @param name name to be set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Number[] get(int row) {
		Number[] rowOrig = data.get(row);
		Number[] rowFiltered = new Number[cols.size()];
		int filteredIndex = 0;
		for (int index : cols) {
			rowFiltered[filteredIndex++] = rowOrig[index];
		}
		return rowFiltered;
	}

	@Override
	public Number get(int col, int row) {
		Number number = null;
		try {
			int dataCol = cols.get(col);
			number = data.get(dataCol, row);
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
		}
		return number;
	}

	@Override
	public int getColumnCount() {
		return cols.size();
	}

	@Override
	public int getRowCount() {
		return data.getRowCount();
	}

	@Override
	public void dataChanged(DataSource data) {
		notifyDataChanged();
	}

	@Override
	public String toString() {
		return getName();
	}
}

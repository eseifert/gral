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


/**
 * Class that represents a data source containing the same value in each cell.
 * It can be used for test purposes or for efficiently creating constant data.
 */
public class DummyData extends AbstractDataSource {
	/** Value that will be returned for all positions in this data source. */
	private final Number value;
	/** Number of columns. */
	private final int cols;
	/** Number of rows. */
	private final int rows;

	/**
	 * Creates a new instance with the specified number of columns
	 * and rows, which are filled all over with the same specified value.
	 * @param cols Number of columns.
	 * @param rows Number of rows.
	 * @param value Value of the cells.
	 */
	public DummyData(int cols, int rows, Number value) {
		this.cols = cols;
		this.rows = rows;
		this.value = value;
	}

	@Override
	public Number get(int col, int row) {
		return value;
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

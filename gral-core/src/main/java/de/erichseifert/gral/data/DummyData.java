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

import java.util.Arrays;


/**
 * Class that represents a data source containing the same value in each cell.
 * It can be used for test purposes or for efficiently creating constant data.
 */
public class DummyData extends AbstractDataSource {
	/** Version id for serialization. */
	private static final long serialVersionUID = 5780257823757438260L;

	/** Value that will be returned for all positions in this data source. */
	private final Comparable<?> value;
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
	@SuppressWarnings("unchecked")
	public DummyData(int cols, int rows, Comparable<?> value) {
		this.cols = cols;
		this.rows = rows;
		this.value = value;

		Class<? extends Comparable<?>>[] types = new Class[cols];
		Arrays.fill(types, value.getClass());
		setColumnTypes(types);
	}

	/**
	 * Returns the row with the specified index.
	 * @param col index of the column to return
	 * @param row index of the row to return
	 * @return the specified value of the data cell
	 */
	public Comparable<?> get(int col, int row) {
		return value;
	}

	@Override
	public int getColumnCount() {
		return cols;
	}

	/**
	 * Returns the number of rows of the data source.
	 * @return number of rows in the data source.
	 */
	public int getRowCount() {
		return rows;
	}

}

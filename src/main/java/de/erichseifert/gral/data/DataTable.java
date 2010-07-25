/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import de.erichseifert.gral.data.comparators.DataComparator;


/**
 * Creates a DataTable object.
 * DataTable is the basic implementation of DataSource.
 * Implemented functionality includes:
 * <ul>
 * <li>Adding and getting rows rows</li>
 * <li>Getting rows cells</li>
 * <li>Deleting the table</li>
 * <li>Getting row and column count</li>
 * <li>Sorting the table with a specific DataComparator</li>
 * </ul>
 */
public class DataTable extends AbstractDataSource {
	private final ArrayList<Number[]> rows;
	private final Class<? extends Number>[] types;

	/**
	 * Creates a new DataTable object.
	 * @param types type for each column
	 */
	public DataTable(Class<? extends Number>... types) {
		this.types = Arrays.copyOf(types, types.length);
		rows = new ArrayList<Number[]>();
	}

	/**
	 * Adds a row with the specified values to the table.
	 * The values are added in the order they are specified. If the
	 * types of the table column and the value do not match, an exception
	 * is thrown.
	 * @param values values to be added as a row
	 * @throws IllegalArgumentException if the type of the
	 * table column and the type of the value that should be added
	 * do not match
	 */
	public void add(Number... values) {
		if (types.length != values.length) {
			throw new IllegalArgumentException(
					"Wrong number of columns! Expected " + types.length +
					", got " + values.length);
		}
		Number[] row = new Number[types.length];
		for (int i = 0; i < values.length; i++) {
			Object obj = values[i];
			if (!(types[i].isAssignableFrom(obj.getClass()))) {
				throw new IllegalArgumentException(
						"Wrong column type! Expected " + types[i] + ", got " +
						obj.getClass());
			}
			row[i] = values[i];
		}
		rows.add(row);
		notifyDataChanged();
	}

	/**
	 * Removes a specified row from the table.
	 * @param row Index of the row to remove
	 */
	public void remove(int row) {
		rows.remove(row);
		notifyDataChanged();
	}

	/**
	 * Deletes all rows this table contains.
	 */
	public void clear() {
		rows.clear();
		notifyDataChanged();
	}

	@Override
	public Number get(int col, int row) {
		return rows.get(row)[col];
	}

	/**
	 * MAkes sure that the data table is prepared to store the specified number
	 * of rows.
	 * @param rows Number of rows that the data table should be prepared for.
	 */
	public void ensureRows(int rows) {
		this.rows.ensureCapacity(rows);
	}

	/**
	 * Sets the value of a certain cell.
	 * @param col Column of the cell to change.
	 * @param row Row of the cell to change.
	 * @param value New value to be set.
	 * @return Old value that was replaced.
	 */
	public Number set(int col, int row, Number value) {
		Number old = get(col, row);
		if (!old.equals(value)) {
			rows.get(row)[col] = value;
			notifyDataChanged();
		}
		return old;
	}

	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public int getColumnCount() {
		return types.length;
	}

	/**
	 * Returns the data type of the specified column.
	 * @param col Column.
	 * @return Data type.
	 */
	public Class<? extends Number> getColumnClass(int col) {
		return this.types[col];
	}

	/**
	 * Sorts the table with the specified DataComparators.
	 * The values are compared in the way the comparators are specified.
	 * @param comparators comparators used for sorting
	 */
	public void sort(final DataComparator... comparators) {
		Collections.sort(rows, new Comparator<Number[]>() {
			@Override
			public int compare(Number[] o1, Number[] o2) {
				for (DataComparator comp : comparators) {
					int result = comp.compare(o1, o2);
					if (result != 0) {
						return result;
					}
				}
				return 0;
			}
		});
	}
}

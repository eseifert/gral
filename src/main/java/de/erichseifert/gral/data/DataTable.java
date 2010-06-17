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
	private final Class<?>[] types;

	/**
	 * Creates a new DataTable object.
	 * @param types type for each column
	 */
	public DataTable(Class<? extends Number>... types) {
		this.types = new Class[types.length];
		System.arraycopy(types, 0, this.types, 0, types.length);
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

	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public int getColumnCount() {
		return types.length;
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

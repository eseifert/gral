/* GRAL : a free graphing library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
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
 * <li>Adding and getting data rows</li>
 * <li>Getting data cells</li>
 * <li>Deleting the table</li>
 * <li>Getting row and column count</li>
 * <li>Sorting the table with a specific DataComparator</li>
 * </ul>
 */
public class DataTable extends AbstractDataSource {
	private final ArrayList<Number[]> data;
	private Class<?>[] types;

	/**
	 * Creates a new DataTable object.
	 * @param types type for each column
	 */
	public DataTable(Class<? extends Number>... types) {
		this.types = new Class[types.length];
		System.arraycopy(types, 0, this.types, 0, types.length);
		data = new ArrayList<Number[]>();
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
			throw new IllegalArgumentException("Wrong number of columns! Expected "+types.length+", got "+values.length);
		}
		Number[] row = new Number[types.length];
		for (int i = 0; i < values.length; i++) {
			Object obj = values[i];
			if (!(types[i].isAssignableFrom(obj.getClass()))) {
				throw new IllegalArgumentException("Wrong column type! Expected "+types[i]+", got "+obj.getClass());
			}
			row[i] = values[i];
		}
		data.add(row);
		notifyDataChanged();
	}

	/**
	 * Deletes all data this table contains.
	 */
	public void clear() {
		data.clear();
		notifyDataChanged();
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.data.DataSource#get(int)
	 */
	public Number[] get(int row) {
		return data.get(row);
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.data.DataSource#get(int, int)
	 */
	public Number get(int col, int row) {
		return data.get(row)[col];
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.data.DataSource#getRowCount()
	 */
	public int getRowCount() {
		return data.size();
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.data.DataSource#getColumnCount()
	 */
	public int getColumnCount() {
		return types.length;
	}

	/**
	 * Sorts the table with the specified DataComparators.
	 * The values are compared in the way the comparators are specified.
	 * @param comparators comparators used for sorting
	 */
	public void sort(final DataComparator... comparators) {
		Collections.sort(data, new Comparator<Number[]>() {
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

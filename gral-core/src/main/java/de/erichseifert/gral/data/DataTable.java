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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.erichseifert.gral.data.comparators.DataComparator;


/**
 * <p>A writable implementation of a data source. Additionally to the standard
 * functionality of <code>DataSource</code> it allows for adding and deleting
 * rows. Additionally, all data in the table can be deleted.</p>
 * <p>The data in the table can be sorted row-wise with the method
 * <code>sort(DataComparator...)</code>. For example, this way column 1 could
 * be sorted ascending and column 3 descending.</p>
 *
 * @see DataSource
 */
public class DataTable extends AbstractDataSource {
	/** All values stored as rows of column arrays. */
	private final List<Number[]> rows;
	/** Number of rows. */
	private int rowCount;

	/**
	 * Initializes a new instance with the specified number of columns and
	 * column types.
	 * @param types type for each column
	 */
	public DataTable(Class<? extends Number>... types) {
		super(types);
		rows = new ArrayList<Number[]>();
	}

	public DataTable(DataSource source) {
		this(source.getColumnTypes());
		for (int rowIndex = 0; rowIndex < source.getRowCount(); rowIndex++) {
			add(source.getRow(rowIndex));
		}
	}

	/**
	 * Adds a row with the specified <code>Number</code> values to the table.
	 * The values are added in the order they are specified. If the types of
	 * the table columns and the values do not match, an
	 * <code>IllegalArgumentException</code> is thrown.
	 * @param values values to be added as a row
	 */
	public void add(Number... values) {
		add(Arrays.asList(values));
	}

	/**
	 * Adds a row with the specified container's elements to the table.
	 * The values are added in the order they are specified. If the types of
	 * the table columns and the values do not match, an
	 * <code>IllegalArgumentException</code> is thrown.
	 * @param values values to be added as a row
	 */
	public void add(Collection<? extends Number> values) {
		if (values.size() != getColumnCount()) {
			throw new IllegalArgumentException(MessageFormat.format(
					"Wrong number of columns! Expected {0,number,integer}, got {1,number,integer}.", //$NON-NLS-1$
					getColumnCount(), values.size()));
		}
		int i = 0;
		Number[] row = new Number[values.size()];
		DataChangeEvent[] events = new DataChangeEvent[row.length];
		Class<? extends Number>[] types = getColumnTypes();
		for (Number value : values) {
			if ((value != null)
					&& !(types[i].isAssignableFrom(value.getClass()))) {
				throw new IllegalArgumentException(MessageFormat.format(
						"Wrong column type! Expected {0}, got {1}.", //$NON-NLS-1$
						types[i], value.getClass()));
			}
			row[i] = value;
			events[i] = new DataChangeEvent(this, i, rowCount + 1, null, value);
			i++;
		}
		rows.add(row);
		rowCount++;
		notifyDataChanged(events);
	}

	/**
	 * Adds the specified row to the table.
	 * The values are added in the order they are specified. If the types of
	 * the table columns and the values do not match, an
	 * <code>IllegalArgumentException</code> is thrown.
	 * @param row Row to be added
	 */
	public void add(Row row) {
		List<Number> values = new ArrayList<Number>(row.size());
		for (Number value : row) {
			values.add(value);
		}
		add(values);
	}

	/**
	 * Removes a specified row from the table.
	 * @param row Index of the row to remove
	 */
	public void remove(int row) {
		Row r = new Row(this, row);
		DataChangeEvent[] events = new DataChangeEvent[getColumnCount()];
		for (int col = 0; col < events.length; col++) {
			events[col] = new DataChangeEvent(this, col, row, r.get(col), null);
		}
		rows.remove(row);
		rowCount--;
		notifyDataChanged(events);
	}

	/**
	 * Deletes all rows this table contains.
	 */
	public void clear() {
		rows.clear();
		rowCount = 0;
		notifyDataChanged();
	}

	@Override
	public Number get(int col, int row) {
		return rows.get(row)[col];
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
			notifyDataChanged(new DataChangeEvent(this, col, row, old, value));
		}
		return old;
	}

	@Override
	public int getRowCount() {
		return rowCount;
	}

	/**
	 * Sorts the table rows with the specified DataComparators.
	 * The row values are compared in the way the comparators are specified.
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

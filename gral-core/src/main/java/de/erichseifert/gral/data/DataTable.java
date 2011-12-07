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
 * functionality of {@code DataSource} it allows for adding and deleting
 * rows. Additionally, all data in the table can be deleted.</p>
 * <p>The data in the table can be sorted row-wise with the method
 * {@code sort(DataComparator...)}. For example, this way column 1 could
 * be sorted ascending and column 3 descending.</p>
 *
 * @see DataSource
 */
public class DataTable extends AbstractDataSource {
	/** All values stored as rows of column arrays. */
	private final List<Comparable<?>[]> rows;
	/** Number of rows. */
	private int rowCount;

	/**
	 * Initializes a new instance with the specified number of columns and
	 * column types.
	 * @param types Type for each column
	 */
	public DataTable(Class<? extends Comparable<?>>... types) {
		super(types);
		rows = Collections.synchronizedList(new ArrayList<Comparable<?>[]>());
	}

	/**
	 * Initializes a new instance with the specified number of columns and
	 * a single column type.
	 * @param cols Number of columns
	 * @param type Data type for all columns
	 */
	@SuppressWarnings("unchecked")
	public DataTable(int cols, Class<? extends Comparable<?>> type) {
		this();
		Class<? extends Comparable<?>>[] types = new Class[cols];
		Arrays.fill(types, type);
		setColumnTypes(types);
	}

	/**
	 * Initializes a new instance with the column types, and data of another
	 * data source.
	 * @param source Data source to clone.
	 */
	public DataTable(DataSource source) {
		this(source.getColumnTypes());
		for (int rowIndex = 0; rowIndex < source.getRowCount(); rowIndex++) {
			add(source.getRow(rowIndex));
		}
	}

	/**
	 * Adds a row with the specified comparable values to the table.
	 * The values are added in the order they are specified. If the types of
	 * the table columns and the values do not match, an
	 * {@code IllegalArgumentException} is thrown.
	 * @param values values to be added as a row
	 */
	public void add(Comparable<?>... values) {
		add(Arrays.asList(values));
	}

	/**
	 * Adds a row with the specified container's elements to the table.
	 * The values are added in the order they are specified. If the types of
	 * the table columns and the values do not match, an
	 * {@code IllegalArgumentException} is thrown.
	 * @param values values to be added as a row
	 */
	public void add(Collection<? extends Comparable<?>> values) {
		if (values.size() != getColumnCount()) {
			throw new IllegalArgumentException(MessageFormat.format(
				"Wrong number of columns! Expected {0,number,integer}, got {1,number,integer}.", //$NON-NLS-1$
				getColumnCount(), values.size()));
		}
		int i = 0;
		Comparable<?>[] row = new Comparable<?>[values.size()];
		DataChangeEvent[] events = new DataChangeEvent[row.length];
		Class<? extends Comparable<?>>[] types = getColumnTypes();
		for (Comparable<?> value : values) {
			if ((value != null)
					&& !(types[i].isAssignableFrom(value.getClass()))) {
				throw new IllegalArgumentException(MessageFormat.format(
					"Wrong column type! Expected {0}, got {1}.", //$NON-NLS-1$
					types[i], value.getClass()));
			}
			row[i] = value;
			events[i] = new DataChangeEvent(this, i, rowCount, null, value);
			i++;
		}
		rows.add(row);
		rowCount++;
		notifyDataAdded(events);
	}

	/**
	 * Adds the specified row to the table.
	 * The values are added in the order they are specified. If the types of
	 * the table columns and the values do not match, an
	 * {@code IllegalArgumentException} is thrown.
	 * @param row Row to be added
	 */
	public void add(Row row) {
		List<Comparable<?>> values = new ArrayList<Comparable<?>>(row.size());
		for (Comparable<?> value : row) {
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
		notifyDataRemoved(events);
	}

	/**
	 * Deletes all rows this table contains.
	 */
	public void clear() {
		rows.clear();
		rowCount = 0;
		// FIXME Give arguments to the following method invocation
		notifyDataRemoved();
	}

	/**
	 * Returns the row with the specified index.
	 * @param col index of the column to return
	 * @param row index of the row to return
	 * @return the specified value of the data cell
	 */
	public Comparable<?> get(int col, int row) {
		return rows.get(row)[col];
	}

	/**
	 * Sets the value of a cell specified by its column and row indexes.
	 * @param <T> Data type of the cell.
	 * @param col Column of the cell to change.
	 * @param row Row of the cell to change.
	 * @param value New value to be set.
	 * @return Old value that was replaced.
	 */
	@SuppressWarnings("unchecked")
	public <T> Comparable<T> set(int col, int row, Comparable<T> value) {
		Comparable<T> old = (Comparable<T>) get(col, row);
		if (!old.equals(value)) {
			rows.get(row)[col] = value;
			notifyDataUpdated(new DataChangeEvent(this, col, row, old, value));
		}
		return old;
	}

	/**
	 * Returns the number of rows of the data source.
	 * @return number of rows in the data source.
	 */
	public int getRowCount() {
		return rowCount;
	}

	/**
	 * Sorts the table rows with the specified DataComparators.
	 * The row values are compared in the way the comparators are specified.
	 * @param comparators comparators used for sorting
	 */
	public void sort(final DataComparator... comparators) {
		Collections.sort(rows, new Comparator<Comparable<?>[]>() {
			public int compare(Comparable<?>[] o1, Comparable<?>[] o2) {
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

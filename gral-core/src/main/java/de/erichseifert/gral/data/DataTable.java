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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.erichseifert.gral.data.comparators.DataComparator;

/**
 * An in-memory, random access implementation of a mutable data source using
 * arrays to store its values.
 *
 * @see DataSource
 * @see MutableDataSource
 */
public class DataTable extends AbstractDataSource implements MutableDataSource {
	/** Version id for serialization. */
	private static final long serialVersionUID = 535236774042654449L;

	/** All values stored as rows of column arrays. */
	private final List<Record> rows;

	/**
	 * Comparator class for comparing two records using a
	 * specified set of {@code DataComparator}s.
	 */
	private final class RecordComparator implements Comparator<Record> {
		/** Rules to use for sorting. */
		private final DataComparator[] comparators;

		/**
		 * Initializes a new instance with a specified set of
		 * {@code DataComparator}s.
		 * @param comparators Set of {@code DataComparator}s to use as rules.
		 */
		public RecordComparator(DataComparator[] comparators) {
			this.comparators = comparators;
		}

		/**
		 * Compares two records using the rules defined by the
		 * {@code DataComparator}s of this instance.
		 * @param record1 First record to compare.
		 * @param record2 Second record to compare.
	     * @return A negative number if first argument is less than the second,
	     *         zero if first argument is equal to the second,
	     *         or a positive integer as the greater than the second.
		 */
		public int compare(Record record1, Record record2) {
			for (DataComparator comparator : comparators) {
				int result = comparator.compare(record1, record2);
				if (result != 0) {
					return result;
				}
			}
			return 0;
		}
	}

	public DataTable() {
		rows = new ArrayList<>();
	}

	/**
	 * Initializes a new instance with the specified number of columns and
	 * column types.
	 * @param types Type for each column
	 */
	public DataTable(Class<? extends Comparable<?>>... types) {
		super(types);
		rows = new ArrayList<>();
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
			add(source.getRecord(rowIndex));
		}
	}

	public DataTable(Column... columns) {
		super(columns);
		rows = new ArrayList<>();

		int maxRowCount = 0;
		for (Column column : columns) {
			maxRowCount = Math.max(maxRowCount, column.size());
		}

		for (int rowIndex = 0; rowIndex < maxRowCount; rowIndex++) {
			List<Comparable<?>> rowData = new ArrayList<>(1 + columns.length);
			for (Column column : columns) {
				rowData.add(column.get(rowIndex));
			}
			rows.add(new Record(rowData));
		}
	}

	/**
	 * Adds a row with the specified comparable values to the table.
	 * The values are added in the order they are specified. If the types of
	 * the table columns and the values do not match, an
	 * {@code IllegalArgumentException} is thrown.
	 * @param values values to be added as a row
	 * @return Index of the row that has been added.
	 */
	public int add(Comparable<?>... values) {
		return add(Arrays.asList(values));
	}

	/**
	 * Adds a row with the specified container's elements to the table.
	 * The values are added in the order they are specified. If the types of
	 * the table columns and the values do not match, an
	 * {@code IllegalArgumentException} is thrown.
	 * @param values values to be added as a row
	 * @return Index of the row that has been added.
	 */
	public int add(List<? extends Comparable<?>> values) {
		DataChangeEvent[] events;
		if (values.size() != getColumnCount()) {
			throw new IllegalArgumentException(MessageFormat.format(
					"Wrong number of columns! Expected {0,number,integer}, got {1,number,integer}.", //$NON-NLS-1$
					getColumnCount(), values.size()));
		}

		// Check row data types
		Class<? extends Comparable<?>>[] types = getColumnTypes();
		for (int colIndex = 0; colIndex < values.size(); colIndex++) {
			Comparable<?> value = values.get(colIndex);
			if ((value != null)
					&& !(types[colIndex].isAssignableFrom(value.getClass()))) {
				throw new IllegalArgumentException(MessageFormat.format(
						"Wrong column type! Expected {0}, got {1}.", //$NON-NLS-1$
						types[colIndex], value.getClass()));
			}
		}

		// Add data to row
		Record row = new Record(values);
		events = new DataChangeEvent[row.size()];
		for (int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
			Comparable<?> value = values.get(columnIndex);
			events[columnIndex] = new DataChangeEvent(this, columnIndex, rows.size(), null, value);
		}

		int rowIndex;
		synchronized (rows) {
			rows.add(row);
			rowIndex = rows.size();
		}
		notifyDataAdded(events);
		return rowIndex - 1;
	}

	/**
	 * Adds the specified row to the table.
	 * The values are added in the order they are specified. If the types of
	 * the table columns and the values do not match, an
	 * {@code IllegalArgumentException} is thrown.
	 * @param row Row to be added
	 * @return Index of the row that has been added.
	 */
	public int add(Row row) {
		List<Comparable<?>> values;
		synchronized (row) {
			values = new ArrayList<>(row.size());
			for (Comparable<?> value : row) {
				values.add(value);
			}
		}
		return add(values);
	}

	public void add(Record row) {
		if (row.size() != getColumnCount()) {
			throw new IllegalArgumentException("Invalid element count in Record to be added. " +
					"Expected: "+getColumnCount()+", got: "+row.size());
		}
		rows.add(row);
	}

	/**
	 * Removes a specified row from the table.
	 * @param row Index of the row to remove
	 */
	public void remove(int row) {
		DataChangeEvent[] events;
		synchronized (rows) {
			Row r = new Row(this, row);
			events = new DataChangeEvent[getColumnCount()];
			for (int col = 0; col < events.length; col++) {
				events[col] = new DataChangeEvent(this, col, row, r.get(col), null);
			}
			rows.remove(row);
		}
		notifyDataRemoved(events);
	}

	/**
	 * Removes the last row from the table.
	 */
	public void removeLast() {
		DataChangeEvent[] events;
		synchronized (this) {
			int row = getRowCount() - 1;
			Row r = new Row(this, row);
			events = new DataChangeEvent[getColumnCount()];
			for (int col = 0; col < events.length; col++) {
				events[col] = new DataChangeEvent(this, col, row, r.get(col), null);
			}
			rows.remove(row);
		}
		notifyDataRemoved(events);
	}

	/**
	 * Deletes all rows this table contains.
	 */
	public void clear() {
		DataChangeEvent[] events;
		synchronized (this) {
			int cols = getColumnCount();
			int rows = getRowCount();
			events = new DataChangeEvent[cols*rows];
			for (int row = 0; row < rows; row++) {
				for (int col = 0; col < cols; col++) {
					events[col + row*cols] = new DataChangeEvent(
						this, col, row, get(col, row), null);
				}
			}
			this.rows.clear();
		}
		notifyDataRemoved(events);
	}

	/**
	 * Returns the row with the specified index.
	 * @param col index of the column to return
	 * @param row index of the row to return
	 * @return the specified value of the data cell
	 */
	public Comparable<?> get(int col, int row) {
		Record r;
		synchronized (rows) {
			if (row >= rows.size()) {
				return null;
			}
			r = rows.get(row);
		}
		if (r == null) {
			return null;
		}
		return r.get(col);
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
		Comparable<T> old;
		DataChangeEvent event = null;
		synchronized (this) {
			old = (Comparable<T>) get(col, row);
			if (old == null || !old.equals(value)) {
				Record record = rows.get(row);
				ArrayList<Comparable<?>> values = new ArrayList<>(record.size());
				for (Comparable<?> element : record) {
					values.add(element);
				}
				values.set(col, value);
				Record updatedRecord = new Record(values);
				rows.set(row, updatedRecord);
				event = new DataChangeEvent(this, col, row, old, value);
			}
		}
		if (event != null) {
			notifyDataUpdated(event);
		}
		return old;
	}

	/**
	 * Returns the number of rows of the data source.
	 * @return number of rows in the data source.
	 */
	public int getRowCount() {
		return rows.size();
	}

	/**
	 * Sorts the table rows with the specified DataComparators.
	 * The row values are compared in the way the comparators are specified.
	 * @param comparators comparators used for sorting
	 */
	public void sort(final DataComparator... comparators) {
		synchronized (rows) {
			RecordComparator comparator = new RecordComparator(comparators);
			Collections.sort(rows, comparator);
		}
	}

	@Override
	public void setName(String name) {
		super.setName(name);
	}
}

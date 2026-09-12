/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2026 Erich Seifert <dev[at]erichseifert.de>,
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
import java.util.Comparator;
import java.util.List;

import de.erichseifert.gral.data.comparators.DataComparator;

/**
 * <p>The ordinary in-memory data source: a list of {@link Record}s with a fixed
 * set of typed columns. This is where most programs put their numbers.</p>
 *
 * <pre>
 * // One class per column; the number of classes is the number of columns.
 * DataTable data = new DataTable(Double.class, Double.class, String.class);
 * data.add(1.0, 4.0, "first");
 * data.add(2.0, 5.0, "second");
 *
 * data.set(2, 0, "1st");               // column 2 of row 0
 * Comparable&lt;?&gt; value = data.get(1, 1); // 5.0
 * data.remove(0);
 * </pre>
 *
 * <p>Adding a row checks both the number of values and their types against the
 * columns and throws {@code IllegalArgumentException} on a mismatch; a
 * {@code null} value is accepted in any column. Every successful modification
 * notifies the registered {@link DataListener}s, which is what lets a plot
 * follow a table that is still being filled.</p>
 *
 * <p>Rows are stored in insertion order until {@link #sort(DataComparator...)}
 * is called. Access by index is constant time; removing a row from the middle
 * shifts the rows after it, so their indexes change.</p>
 *
 * <p>Individual modifications are synchronized, but a sequence of them is not
 * atomic, and reading while another thread writes is not guarded. Treat the
 * table as single-threaded unless all access is externally coordinated.</p>
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
	 * Initializes a new table without any columns. Columns cannot be added
	 * afterwards, so this is only useful for an empty placeholder.
	 */
	public DataTable() {
		rows = new ArrayList<>();
	}

	/**
	 * Initializes a new, empty table with one column per specified type. The
	 * types are enforced when rows are added.
	 * @param types Type for each column, in column order.
	 */
	public DataTable(Class<? extends Comparable<?>>... types) {
		super(types);
		rows = new ArrayList<>();
	}

	/**
	 * Initializes a new, empty table with the specified number of columns, all
	 * of the same type.
	 * @param cols Number of columns.
	 * @param type Data type for all columns.
	 */
	@SuppressWarnings("unchecked")
	public DataTable(int cols, Class<? extends Comparable<?>> type) {
		this();
		Class<? extends Comparable<?>>[] types = new Class[cols];
		Arrays.fill(types, type);
		setColumnTypes(types);
	}

	/**
	 * Initializes a new table that copies the column types and all values of
	 * another data source. The copy is independent: later changes to the
	 * original are not reflected here. Use {@link DataSeries} or
	 * {@link RowSubset} instead if a live view is wanted.
	 * @param source Data source to clone.
	 */
	public DataTable(DataSource source) {
		this(source.getColumnTypes());
		for (int rowIndex = 0; rowIndex < source.getRowCount(); rowIndex++) {
			add(source.getRecord(rowIndex));
		}
	}

	/**
	 * Initializes a new table from the specified columns, taking both the
	 * column types and the values from them. If the columns differ in length,
	 * the table gets as many rows as the longest one and the shorter columns
	 * are padded with {@code null}.
	 * @param columns Columns that make up the table, in column order.
	 */
	public DataTable(Column... columns) {
		super(columns);
		rows = new ArrayList<>();

		int maxRowCount = Arrays.stream(columns)
				.mapToInt(Column::size)
				.max()
				.orElse(0);

		for (int rowIndex = 0; rowIndex < maxRowCount; rowIndex++) {
			var rowData = new ArrayList<Comparable<?>>(1 + columns.length);
			for (Column column : columns) {
				rowData.add(column.get(rowIndex));
			}
			rows.add(new Record(rowData));
		}
	}

	/**
	 * Adds a row with the specified values at the end of the table. The values
	 * are assigned to the columns in the order given. A {@code null} is
	 * accepted in any column.
	 * @param values values to be added as a row
	 * @return Index of the row that has been added.
	 * @throws IllegalArgumentException if the number of values differs from the
	 *         number of columns, or a value does not fit its column type.
	 */
	public int add(Comparable<?>... values) {
		return add(Arrays.asList(values));
	}

	/**
	 * Adds a row with the elements of the specified list at the end of the
	 * table. The values are assigned to the columns in list order. A
	 * {@code null} is accepted in any column.
	 * @param values values to be added as a row
	 * @return Index of the row that has been added.
	 * @throws IllegalArgumentException if the number of values differs from the
	 *         number of columns, or a value does not fit its column type.
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
		var row = new Record(values);
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
	 * Copies the values of the specified row into a new row at the end of the
	 * table. The row may come from another data source.
	 * @param row Row to be added
	 * @return Index of the row that has been added.
	 * @throws IllegalArgumentException if the number of values differs from the
	 *         number of columns, or a value does not fit its column type.
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

	/**
	 * Adds the specified record as a new row at the end of the table. In
	 * contrast to the other {@code add} methods, only the number of values is
	 * checked, not their types.
	 * @param row Record to be added, with one value per column.
	 * @throws IllegalArgumentException if the record does not have one value
	 *         per column.
	 */
	public void add(Record row) {
		if (row.size() != getColumnCount()) {
			throw new IllegalArgumentException("Invalid element count in Record to be added. " +
					"Expected: "+getColumnCount()+", got: "+row.size());
		}

		var events = new DataChangeEvent[row.size()];
		synchronized (rows) {
			int rowIndex = rows.size();
			for (int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
				events[columnIndex] = new DataChangeEvent(
					this, columnIndex, rowIndex, null, row.get(columnIndex));
			}
			rows.add(row);
		}
		notifyDataAdded(events);
	}

	/**
	 * Removes the row with the specified index. The rows after it move up by
	 * one, so their indexes change.
	 * @param row Index of the row to remove
	 */
	public void remove(int row) {
		DataChangeEvent[] events;
		synchronized (rows) {
			var r = new Row(this, row);
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
			var r = new Row(this, row);
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
	 * Returns the value at the specified column and row. Note that the column
	 * index comes first. A row index beyond the last row yields {@code null}
	 * rather than an exception.
	 * @param col index of the column to return
	 * @param row index of the row to return
	 * @return the specified value of the data cell, which may be {@code null}
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
	 * Sets the value of a cell specified by its column and row indexes. Note
	 * that the column index comes first. Writing the value the cell already
	 * holds changes nothing and fires no notification. The type of the value is
	 * not checked against the column type here.
	 * @param <T> Data type of the cell.
	 * @param col Column of the cell to change.
	 * @param row Row of the cell to change.
	 * @param value New value to be set.
	 * @return Old value that was replaced, possibly {@code null}.
	 */
	@SuppressWarnings("unchecked")
	public <T> Comparable<T> set(int col, int row, Comparable<T> value) {
		Comparable<T> old;
		DataChangeEvent event = null;
		synchronized (this) {
			old = (Comparable<T>) get(col, row);
			if (old == null || !old.equals(value)) {
				Record record = rows.get(row);
				var values = new ArrayList<Comparable<?>>(record.size());
				for (Comparable<?> element : record) {
					values.add(element);
				}
				values.set(col, value);
				var updatedRecord = new Record(values);
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
	 * Sorts the rows of this table in place. The first comparator decides the
	 * order; each following one is only consulted for rows the previous ones
	 * considered equal. Passing no comparator leaves the order unchanged.
	 * Sorting does not fire a change notification, so a plot that is already
	 * displayed will not repaint by itself.
	 * @param comparators comparators used for sorting
	 */
	public void sort(DataComparator... comparators) {
		if (comparators.length == 0) {
			return;
		}
		Comparator<Record> comparator = comparators[0];
		for (int i = 1; i < comparators.length; i++) {
			comparator = comparator.thenComparing(comparators[i]);
		}
		synchronized (rows) {
			rows.sort(comparator);
		}
	}

	@Override
	public void setName(String name) {
		super.setName(name);
	}
}

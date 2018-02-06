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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import de.erichseifert.gral.data.statistics.Statistics;


/**
 * Abstract implementation of the {@code DataSource} interface.
 * This class provides access to statistical information,
 * administration and notification of listeners and supports
 * iteration of data values.
 */
public abstract class AbstractDataSource implements DataSource, Serializable {
	/** Version id for serialization. */
	private static final long serialVersionUID = 9139975565475816812L;

	/** Name of the data source. */
	private String name;
	/** Number of columns. */
	private int columnCount;
	/** Data types that are allowed in the respective columns. */
	private Class<? extends Comparable<?>>[] types;
	/** Set of objects that will be notified of changes to the data values. */
	private transient Set<DataListener> dataListeners;
	/** Statistical description of the data values. */
	private transient Statistics statistics;

	/**
	 * Iterator that returns each row of the DataSource.
	 */
	private class DataSourceIterator implements Iterator<Comparable<?>> {
		/** Index of current column. */
		private int col;
		/** Index of current row. */
		private int row;

		/**
		 * Initializes a new iterator instance that starts at (0, 0).
		 */
		public DataSourceIterator() {
			col = 0;
			row = 0;
		}

	    /**
	     * Returns {@code true} if the iteration has more elements.
	     * (In other words, returns {@code true} if {@code next}
	     * would return an element rather than throwing an exception.)
	     * @return {@code true} if the iterator has more elements.
	     */
		public boolean hasNext() {
			return (col < getColumnCount()) && (row < getRowCount());
		}

	    /**
	     * Returns the next element in the iteration.
	     * @return the next element in the iteration.
	     * @exception NoSuchElementException iteration has no more elements.
	     */
		public Comparable<?> next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			Comparable<?> value = get(col, row);
			if (++col >= getColumnCount()) {
				col = 0;
				++row;
			}
			return value;
		}

	    /**
	     * Method that theoretically removes a cell from a data source.
	     * However, this is not supported.
	     */
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public AbstractDataSource() {
		this(null, new Class[0]);
	}

	/**
	 * Initializes a new instance with the specified name, number of columns, and
	 * column types.
	 * @param name name of the DataSource
	 * @param types type for each column
	 */
	public AbstractDataSource(String name, Class<? extends Comparable<?>>... types) {
		this.name = name;
		setColumnTypes(types);
		dataListeners = new LinkedHashSet<>();
	}

	/**
	 * Initializes a new instance with the specified number of columns and
	 * column types.
	 * @param types type for each column
	 */
	public AbstractDataSource(Class<? extends Comparable<?>>... types) {
		this(null, types);
	}

	public AbstractDataSource(Column... remainingColumns) {
		Class<? extends Comparable<?>>[] columnTypes = new Class[remainingColumns.length];
		for (int columnIndex = 0; columnIndex < remainingColumns.length; columnIndex++) {
			Column column = remainingColumns[columnIndex];
			columnTypes[columnIndex] = column.getType();
		}
		setColumnTypes(columnTypes);

		dataListeners = new LinkedHashSet<>();
	}

	/**
	 * Retrieves a object instance that contains various statistical
	 * information on the current data source.
	 * @return statistical information
	 */
	public Statistics getStatistics() {
		if (statistics == null) {
			statistics = new Statistics(this);
		}
		return statistics;
	}

	public DataSource getColumnStatistics(String key) {
		Class[] columnTypes = new Class[getColumnCount()];
		Arrays.fill(columnTypes, Double.class);
		DataTable statisticsTable = new DataTable(columnTypes);
		List<Double> colStatistics = new ArrayList<>(columnTypes.length);
		for (int colIndex = 0; colIndex < getColumnCount(); colIndex++) {
			Column col = getColumn(colIndex);
			colStatistics.add(col.getStatistics(key));
		}
		if (!colStatistics.isEmpty()) {
			statisticsTable.add(colStatistics);
		}
		return statisticsTable;
	}

	public DataSource getRowStatistics(String key) {
		DataTable statisticsTable = getRowCount() != 0 ? new DataTable(Double.class) : new DataTable();
		for (int rowIndex = 0; rowIndex < getRowCount(); rowIndex++) {
			Record row = getRecord(rowIndex);
			statisticsTable.add(new Statistics(row).get(key));
		}
		return statisticsTable;
	}

	/**
	 * Adds the specified {@code DataListener} to this data source.
	 * @param dataListener listener to be added.
	 */
	public void addDataListener(DataListener dataListener) {
		dataListeners.add(dataListener);
	}

	/**
	 * Removes the specified {@code DataListener} from this data source.
	 * @param dataListener listener to be removed.
	 */
	public void removeDataListener(DataListener dataListener) {
		dataListeners.remove(dataListener);
	}

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
	public Iterator<Comparable<?>> iterator() {
		return new DataSourceIterator();
	}

	/**
	 * Notifies all registered listeners that data values have been added.
	 * @param events Event objects describing all values that have been added.
	 */
	protected void notifyDataAdded(DataChangeEvent... events) {
		List<DataListener> listeners = new LinkedList<>(dataListeners);
		for (DataListener dataListener : listeners) {
			dataListener.dataAdded(this, events);
		}
	}

	/**
	 * Notifies all registered listeners that data values have been removed.
	 * @param events Event objects describing all values that have been removed.
	 */
	protected void notifyDataRemoved(DataChangeEvent... events) {
		List<DataListener> listeners = new LinkedList<>(dataListeners);
		for (DataListener dataListener : listeners) {
			dataListener.dataRemoved(this, events);
		}
	}

	/**
	 * Notifies all registered listeners that data values have changed.
	 * @param events Event objects describing all values that have changed.
	 */
	protected void notifyDataUpdated(DataChangeEvent... events) {
		List<DataListener> listeners = new LinkedList<>(dataListeners);
		for (DataListener dataListener : listeners) {
			dataListener.dataUpdated(this, events);
		}
	}

	/**
	 * Returns the column with the specified index.
	 * @param col index of the column to return
	 * @return the specified column of the data source
	 */
	@Override
	public Column<?> getColumn(int col) {
		Class<? extends Comparable<?>> columnType = getColumnTypes()[col];
		List<Comparable<?>> columnData = new LinkedList<>();
		for (int rowIndex = 0; rowIndex < getRowCount(); rowIndex++) {
			Record record = getRecord(rowIndex);
			columnData.add(record.get(col));
		}
		return new Column(columnType, columnData.toArray(new Comparable[0]));
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Record getRecord(int row) {
		return new Record(getRow(row).toArray(null));
	}

	// Allows DataTable to reuse the name property
	protected void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the number of columns of the data source.
	 * @return number of columns in the data source.
	 */
	public int getColumnCount() {
		return columnCount;
	}


	/**
	 * Returns the data types of all columns.
	 * @return The data types of all column in the data source
	 */
	public Class<? extends Comparable<?>>[] getColumnTypes() {
		return Arrays.copyOf(this.types, this.types.length);
	}

	/**
	 * Returns whether the column at the specified index contains numbers.
	 * @param columnIndex Index of the column to test.
	 * @return {@code true} if the column is numeric, otherwise {@code false}.
	 */
	public boolean isColumnNumeric(int columnIndex) {
		if (columnIndex < 0 || columnIndex >= types.length) {
			return false;
		}
		Class<?> columnType = types[columnIndex];
		return Number.class.isAssignableFrom(columnType);
	}

	/**
	 * Sets the data types of all columns. This also changes the number of
	 * columns.
	 * @param types Data types.
	 */
	protected void setColumnTypes(Class<? extends Comparable<?>>... types) {
		this.types = Arrays.copyOf(types, types.length);
		columnCount = types.length;
	}

	/**
	 * Returns the row with the specified index.
	 * @param row Index of the row to return
	 * @return the Specified row of the data source
	 */
	public Row getRow(int row) {
		return new Row(this, row);
	}

	/**
	 * Custom deserialization method.
	 * @param in Input stream.
	 * @throws ClassNotFoundException if a serialized class doesn't exist anymore.
	 * @throws IOException if there is an error while reading data from the
	 *         input stream.
	 */
	private void readObject(ObjectInputStream in)
			throws ClassNotFoundException, IOException {
		// Normal deserialization
		in.defaultReadObject();

		// Handle transient fields
		dataListeners = new HashSet<>();
		// Statistics can be omitted. It's created using a lazy getter.
	}
}

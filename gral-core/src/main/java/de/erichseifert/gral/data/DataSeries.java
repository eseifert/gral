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
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a view on several columns of a {@code DataSource}.
 * @see DataSource
 */
public class DataSeries extends AbstractDataSource implements DataListener {
	/** Version id for serialization. */
	private static final long serialVersionUID = 5568085894125740972L;

	/** Data source that provides the columns for this data series. */
	private final DataSource data;
	/** Columns that should be mapped to the series. */
	private final List<Integer> cols;

	/**
	 * Constructor without name. The first column will be column
	 * {@code 0}, the second column {@code 1} and so on,
	 * whereas the value of the specified columns is the column number
	 * in the data source.
	 * @param data Data source
	 * @param cols Column numbers
	 */
	public DataSeries(DataSource data, int... cols) {
		this(null, data, cols);
	}

	/**
	 * Constructor that initializes a named data series. The first column will
	 * be column {@code 0}, the second column {@code 1} and so on,
	 * whereas the value of the specified columns is the column number in the
	 * data source.
	 * @param name Descriptive name
	 * @param data Data source
	 * @param cols Column numbers
	 */
	@SuppressWarnings("unchecked")
	public DataSeries(String name, DataSource data, int... cols) {
		super(name);
		this.data = data;
		this.cols = new ArrayList<>();
		this.data.addDataListener(this);

		Class<? extends Comparable<?>>[] typesOrig = data.getColumnTypes();
		Class<? extends Comparable<?>>[] types;

		if (cols.length > 0) {
			types = new Class[cols.length];
			int t = 0;
			for (int colIndex : cols) {
				this.cols.add(colIndex);
				types[t++] = typesOrig[colIndex];
			}
		} else {
			for (int colIndex = 0; colIndex < data.getColumnCount(); colIndex++) {
				this.cols.add(colIndex);
			}
			types = typesOrig;
		}

		setColumnTypes(types);
	}

	/**
	 * Returns the row with the specified index.
	 * @param col index of the column to return
	 * @param row index of the row to return
	 * @return the specified value of the data cell
	 */
	public Comparable<?> get(int col, int row) {
		try {
			int dataCol = cols.get(col);
			return data.get(dataCol, row);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	@Override
	public int getColumnCount() {
		return cols.size();
	}

	/**
	 * Returns the number of rows of the data source.
	 * @return number of rows in the data source.
	 */
	public int getRowCount() {
		return data.getRowCount();
	}

	/**
	 * Method that is invoked when data has been added.
	 * This method is invoked by objects that provide support for
	 * {@code DataListener}s and should not be called manually.
	 * @param source Data source that has been changed.
	 * @param events Optional event object describing the data values that
	 *        have been added.
	 */
	public void dataAdded(DataSource source, DataChangeEvent... events) {
		notifyDataAdded(events);
	}

	/**
	 * Method that is invoked when data has been updated.
	 * This method is invoked by objects that provide support for
	 * {@code DataListener}s and should not be called manually.
	 * @param source Data source that has been changed.
	 * @param events Optional event object describing the data values that
	 *        have been updated.
	 */
	public void dataUpdated(DataSource source, DataChangeEvent... events) {
		notifyDataUpdated(events);
	}

	/**
	 * Method that is invoked when data has been removed.
	 * This method is invoked by objects that provide support for
	 * {@code DataListener}s and should not be called manually.
	 * @param source Data source that has been changed.
	 * @param events Optional event object describing the data values that
	 *        have been removed.
	 */
	public void dataRemoved(DataSource source, DataChangeEvent... events) {
		notifyDataRemoved(events);
	}

	@Override
	public String toString() {
		return getName();
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

		// Restore listeners
		data.addDataListener(this);
	}
}

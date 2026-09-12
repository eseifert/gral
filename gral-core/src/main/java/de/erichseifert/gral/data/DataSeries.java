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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>A view that selects columns of another data source, optionally reordering
 * them, and gives the result a name. This is how a table that holds several
 * variables is split into the series of a plot, and the name is what a legend
 * displays.</p>
 *
 * <pre>
 * // Columns of the table: time, temperature, humidity
 * DataTable data = new DataTable(Double.class, Double.class, Double.class);
 * …
 * // Two series over the same x column. The series columns are numbered from 0
 * // again, so in both series column 0 is the time and column 1 the value.
 * DataSeries temperature = new DataSeries("Temperature", data, 0, 1);
 * DataSeries humidity    = new DataSeries("Humidity",    data, 0, 2);
 *
 * XYPlot plot = new XYPlot(temperature, humidity);
 * </pre>
 *
 * <p>The same column of the original may be used more than once, and columns
 * may appear in any order. Passing no column indexes at all selects every
 * column of the original.</p>
 *
 * <p>A series is a view, not a copy: it reads through to the original data
 * source and forwards its change notifications, so a plot showing the series
 * updates when the underlying table is modified.</p>
 *
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
	 * Creates an unnamed series. The columns of the series are numbered from
	 * {@code 0} in the order they are listed here, while the values given are
	 * the column numbers in the original data source.
	 * @param data Data source to select columns from.
	 * @param cols Column numbers in {@code data}. If empty, all columns are
	 *        selected.
	 */
	public DataSeries(DataSource data, int... cols) {
		this(null, data, cols);
	}

	/**
	 * Creates a named series. The columns of the series are numbered from
	 * {@code 0} in the order they are listed here, while the values given are
	 * the column numbers in the original data source.
	 * @param name Descriptive name, shown by legends.
	 * @param data Data source to select columns from.
	 * @param cols Column numbers in {@code data}. If empty, all columns are
	 *        selected.
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
		DataChangeEvent[] mappedEvents = mapEvents(events);
		if (mappedEvents.length > 0) {
			notifyDataAdded(mappedEvents);
		}
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
		DataChangeEvent[] mappedEvents = mapEvents(events);
		if (mappedEvents.length > 0) {
			notifyDataUpdated(mappedEvents);
		}
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
		DataChangeEvent[] mappedEvents = mapEvents(events);
		if (mappedEvents.length > 0) {
			notifyDataRemoved(mappedEvents);
		}
	}

	/**
	 * Translates change events of the underlying data source to change events
	 * of this data series. Events for columns that aren't part of this series
	 * are discarded, the remaining events are re-created with this series as
	 * source and with the column index mapped to the series. A column that is
	 * mapped more than once yields one event per occurrence.
	 * @param events Change events of the underlying data source.
	 * @return Change events of this data series.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	private DataChangeEvent[] mapEvents(DataChangeEvent... events) {
		var mappedEvents = new ArrayList<DataChangeEvent>(events.length);
		for (DataChangeEvent event : events) {
			for (int col = 0; col < cols.size(); col++) {
				if (cols.get(col).intValue() != event.getCol()) {
					continue;
				}
				mappedEvents.add(new DataChangeEvent(this, col, event.getRow(),
					(Comparable) event.getOld(), (Comparable) event.getNew()));
			}
		}
		return mappedEvents.toArray(new DataChangeEvent[0]);
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

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


/**
 * <p>Class that creates a new data source which adds a leading column
 * containing the row number.</p>
 *
 * <p>Example which creates a two column data source from a one column
 * histogram:</p>
 * <pre>
 * DataSource hist = new Histogram2D(data, Orientation.HORIZONTAL, 10);
 * DataSource hist2d = new EnumeratedData(hist);
 * </pre>
 *
 * @see DataSource
 */
public class EnumeratedData extends AbstractDataSource
		implements DataListener {
	/** Version id for serialization. */
	private static final long serialVersionUID = -4952487410608980063L;

	/** Data source which will be used as base for enumeration. */
	private final DataSource original;
	/** Value to start counting from. */
	private final double offset;
	/** Width of enumeration steps. */
	private final double steps;

	/**
	 * Initializes a new data source based on an original data source which
	 * will contain an additional column which enumerates all rows. The
	 * enumeration will start at a specified offset and will have a specified
	 * step size.
	 * @param original Original data source.
	 * @param offset Offset of enumeration
	 * @param steps Scaling of enumeration
	 */
	@SuppressWarnings("unchecked")
	public EnumeratedData(DataSource original, double offset, double steps) {
		this.original = original;
		this.offset = offset;
		this.steps = steps;

		Class<? extends Comparable<?>>[] typesOrig = original.getColumnTypes();
		Class<? extends Comparable<?>>[] types = new Class[typesOrig.length + 1];
		System.arraycopy(typesOrig, 0, types, 1, typesOrig.length);
		types[0] = Double.class;
		setColumnTypes(types);

		original.addDataListener(this);
	}

	/**
	 * Initializes a new data source based on an original data source which
	 * will contain an additional column which enumerates all rows.
	 * @param original Original data source.
	 */
	public EnumeratedData(DataSource original) {
		this(original, 0, 1);
	}

	/**
	 * Returns the row with the specified index.
	 * @param col index of the column to return
	 * @param row index of the row to return
	 * @return the specified value of the data cell
	 */
	public Comparable<?> get(int col, int row) {
		if (col < 1) {
			return row*steps + offset;
		}
		return original.get(col - 1, row);
	}

	/**
	 * Returns the number of rows of the data source.
	 * @return number of rows in the data source.
	 */
	public int getRowCount() {
		return original.getRowCount();
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
		notifyDataAdded(takeEvents(events));
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
		notifyDataUpdated(takeEvents(events));
	}

	/**
	 * Method that is invoked when data has been added.
	 * This method is invoked by objects that provide support for
	 * {@code DataListener}s and should not be called manually.
	 * @param source Data source that has been changed.
	 * @param events Optional event object describing the data values that
	 *        have been removed.
	 */
	public void dataRemoved(DataSource source, DataChangeEvent... events) {
		notifyDataRemoved(takeEvents(events));
	}

	/**
	 * Changes the source and the columns of the specified event objects to
	 * make them look as if they originated from this data source.
	 * @param events Original events.
	 * @return Changed events.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private DataChangeEvent[] takeEvents(DataChangeEvent[] events) {
		if (events == null || events.length == 0) {
			return new DataChangeEvent[] {
				new DataChangeEvent(this, 0, 0, null, null)
			};
		}
		DataChangeEvent[] eventsTx = new DataChangeEvent[events.length + 1];
		for (int i = 0; i < eventsTx.length; i++) {
			DataChangeEvent event;
			int col, row;
			if (i == 0) {
				// Insert an event for the generated column
				event = events[0];
				col = 0;
				row = event.getRow();
			} else {
				// Process the columns of the original source
				event = events[i - 1];
				col = event.getCol() + 1;
				row = event.getRow();
			}
			Comparable valOld = event.getOld();
			Comparable valNew = event.getNew();
			eventsTx[i] = new DataChangeEvent(
				this, col, row, valOld, valNew);
		}
		return eventsTx;
	}
}

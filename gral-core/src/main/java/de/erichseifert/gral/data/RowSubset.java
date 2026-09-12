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
 * <p>A view that hides some of the rows of another data source. Subclasses
 * implement {@link #accept(Row)} to decide, row by row, which rows the view
 * contains. The columns are unchanged.</p>
 *
 * <p>Example that keeps only every second row:</p>
 * <pre>
 * DataSource filtered = new RowSubset(data) {
 *     public boolean accept(Row row) {
 *         return row.getIndex()%2 == 0;
 *     }
 * };
 * </pre>
 *
 * <p>The view listens to the original data source and re-evaluates
 * {@code accept} for every row whenever the data changes, so it stays in step
 * with the source. The decision must therefore depend only on the row that is
 * passed in; it must not, for instance, count how often it has been called.</p>
 *
 * <p>The row indexes of the view are its own: row 0 of the view is the first
 * accepted row of the original, whatever index it had there. Note that this is
 * one of the few places where an anonymous class is still required, since
 * {@code RowSubset} is a class and not a functional interface.</p>
 */
public abstract class RowSubset extends AbstractDataSource
		implements DataListener {
	/** Version id for serialization. */
	private static final long serialVersionUID = -5396152732545986903L;

	/** Original data source. */
	private final DataSource original;
	/** List of row indexes that are stored in this filtered data source. */
	private transient List<Integer> accepted;

	/**
	 * Creates a new instance with the specified data source. The constructor
	 * already evaluates {@link #accept(Row)} for every row, so a subclass must
	 * not rely on its own fields being initialized at that point.
	 * @param original DataSource to be filtered.
	 */
	@SuppressWarnings("unchecked")
	public RowSubset(DataSource original) {
		accepted = new ArrayList<>();
		this.original = original;
		this.original.addDataListener(this);
		dataUpdated(this.original);
	}

	@Override
	public Row getRow(int row) {
		int rowOrig = accepted.get(row);
		return original.getRow(rowOrig);
	}

	/**
	 * Returns the row with the specified index.
	 * @param col index of the column to return
	 * @param row index of the row to return
	 * @return the specified value of the data cell
	 */
	public Comparable<?> get(int col, int row) {
		int rowOrig = accepted.get(row);
		return original.get(col, rowOrig);
	}

	@Override
	public int getColumnCount() {
		return original.getColumnCount();
	}

	/**
	 * Returns the number of rows of the data source.
	 * @return number of rows in the data source.
	 */
	public int getRowCount() {
		return accepted.size();
	}

	@Override
	public Class<? extends Comparable<?>>[] getColumnTypes() {
		return original.getColumnTypes();
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
		dataChanged(source, events);
		notifyDataAdded(events);
	}

	/**
	 * Method that is invoked when data has been updated.
	 * This method is invoked by objects that provide support for
	 * {@code DataListener}s and should not be called manually.
	 * @param source Data source that has been changed.
	 * @param events Optional event object describing the data values that
	 *        have been added
	 */
	public void dataUpdated(DataSource source, DataChangeEvent... events) {
		dataChanged(source, events);
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
		dataChanged(source, events);
		notifyDataRemoved(events);
	}

	/**
	 * Method that is invoked when data has been added, updated, or removed.
	 * This method is invoked by objects that provide support for
	 * {@code DataListener}s and should not be called manually.
	 * @param source Data source that has been changed.
	 * @param events Optional event object describing the data values that
	 *        have been changed.
	 */
	private void dataChanged(DataSource source, DataChangeEvent... events) {
		update();
	}

	/**
	 * Updates the list of accepted rows.
	 */
	private void update() {
		accepted.clear();
		for (int rowIndex = 0; rowIndex < original.getRowCount(); rowIndex++) {
			Row row = original.getRow(rowIndex);
			if (accept(row)) {
				accepted.add(rowIndex);
			}
		}
	}

	/**
	 * Tests whether the specified row is accepted by this DataSubset or not.
	 * @param row Row to be tested.
	 * @return True if the row should be kept.
	 */
	public abstract boolean accept(Row row);

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
		accepted = new ArrayList<>();

		// Update caches
		dataUpdated(original);

		// Restore listeners
		original.addDataListener(this);
	}
}

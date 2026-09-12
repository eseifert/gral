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

/**
 * <p>Receives notifications when the values of a {@link DataSource} change.
 * Plots register themselves on their data sources this way, which is why
 * modifying a {@link DataTable} that is already on screen updates the
 * display.</p>
 *
 * <pre>
 * data.addDataListener(new DataListener() {
 *     public void dataAdded(DataSource source, DataChangeEvent... events) {
 *         System.out.println(events.length + " values added");
 *     }
 *     public void dataUpdated(DataSource source, DataChangeEvent... events) { }
 *     public void dataRemoved(DataSource source, DataChangeEvent... events) { }
 * });
 * </pre>
 *
 * <p>The three methods are called by the data source itself and should not be
 * invoked by application code. One modification may report many events: adding
 * a row to a table of five columns produces five
 * {@link DataChangeEvent}s, one per cell. A source may also pass no events at
 * all, so implementations must not assume the array is non-empty and should
 * treat that as "something changed, re-read everything".</p>
 *
 * <p>Notifications arrive on whichever thread made the change. Because
 * {@code DataListener} has three methods it cannot be a lambda; an anonymous
 * class is required.</p>
 *
 * @see DataSource
 */
public interface DataListener {
	/**
	 * Method that is invoked when data has been added.
	 * This method is invoked by objects that provide support for
	 * {@code DataListener}s and should not be called manually.
	 * @param source Data source that has been changed.
	 * @param events Optional event object describing the data values that
	 *        have been added.
	 */
	void dataAdded(DataSource source, DataChangeEvent... events);

	/**
	 * Method that is invoked when data has been updated.
	 * This method is invoked by objects that provide support for
	 * {@code DataListener}s and should not be called manually.
	 * @param source Data source that has been changed.
	 * @param events Optional event object describing the data values that
	 *        have been updated.
	 */
	void dataUpdated(DataSource source, DataChangeEvent... events);

	/**
	 * Method that is invoked when data has been removed.
	 * This method is invoked by objects that provide support for
	 * {@code DataListener}s and should not be called manually.
	 * @param source Data source that has been changed.
	 * @param events Optional event object describing the data values that
	 *        have been removed.
	 */
	void dataRemoved(DataSource source, DataChangeEvent... events);
}

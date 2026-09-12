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

import java.util.List;

import de.erichseifert.gral.data.comparators.DataComparator;

/**
 * <p>A {@link DataSource} that can also be written to: rows can be added and
 * removed, individual cells can be changed, and the rows can be sorted.</p>
 *
 * <p>Rows are always appended and removed as a whole; the set of columns is
 * fixed when the source is created. Every value must match the declared type of
 * its column, or an {@code IllegalArgumentException} is thrown. Each successful
 * modification notifies the registered
 * {@link DataListener}s, which is what makes a displayed plot follow the
 * data.</p>
 *
 * <pre>
 * DataTable data = new DataTable(Double.class, String.class);
 * data.add(1.0, "one");
 * data.add(2.0, "two");
 * data.set(1, 0, "uno");                 // column 1, row 0
 * data.sort(new Ascending(0), new Descending(1));
 * </pre>
 *
 * @see DataSource
 * @see DataTable
 */
public interface MutableDataSource extends DataSource {
	/**
	 * Adds a row with the specified comparable values. The values are added in
	 * the order they are specified. If the types of the data sink columns and
	 * the values do not match, an {@code IllegalArgumentException} is thrown.
	 * @param values values to be added as a row.
	 * @return Index of the row that has been added.
	 */
	int add(Comparable<?>... values);

	/**
	 * Adds a row with the specified container's elements to the data sink. The
	 * values are added in the order they are specified. If the types of the
	 * data sink columns and the values do not match, an
	 * {@code IllegalArgumentException} is thrown.
	 * @param values values to be added as a row.
	 * @return Index of the row that has been added.
	 */
	int add(List<? extends Comparable<?>> values);

	/**
	 * Adds the specified row to the data sink. The values are added in the
	 * order they are specified. If the types of the data sink columns and the
	 * values do not match, an {@code IllegalArgumentException} is thrown.
	 * @param row Row to be added.
	 * @return Index of the row that has been added.
	 */
	int add(Row row);

	/**
	 * Removes a specified row from the data sink.
	 * @param row Index of the row to remove.
	 */
	void remove(int row);

	/**
	 * Removes the last row from the data sink.
	 */
	void removeLast();

	/**
	 * Deletes all rows this data sink contains.
	 */
	void clear();

	/**
	 * Sets the value of a cell specified by its column and row indexes. Note
	 * that the column index comes first. Setting a cell to the value it already
	 * holds changes nothing and fires no notification.
	 * @param <T> Data type of the cell.
	 * @param col Column of the cell to change.
	 * @param row Row of the cell to change.
	 * @param value New value to be set.
	 * @return Old value that was replaced, possibly {@code null}.
	 */
	<T> Comparable<T> set(int col, int row, Comparable<T> value);

	/**
	 * Sorts the rows in place. The first comparator decides the order; each
	 * following one is only consulted for rows the previous ones considered
	 * equal. Passing no comparator leaves the order unchanged. Sorting does not
	 * fire a change notification.
	 * @param comparators Comparators used for sorting.
	 */
	void sort(final DataComparator... comparators);

	/**
	 * Sets the name of this data source. Legends display this name for the
	 * series they show.
	 * @param name name to be set, or {@code null} for no name
	 */
	void setName(String name);
}

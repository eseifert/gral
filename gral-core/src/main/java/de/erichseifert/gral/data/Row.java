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
 * <p>A live view of one row of a data source. Reading from it reads through to
 * the source, so a row reflects later changes to the data &mdash; in contrast
 * to {@link Record}, which is a snapshot, and to {@link Column}, which copies
 * its values.</p>
 *
 * <pre>
 * Row row = data.getRow(2);
 * Comparable&lt;?&gt; value = row.get(3);   // column 3 of row 2
 * int columns = row.size();
 * </pre>
 *
 * <p>Because the row keeps only the source and an index, it does not notice
 * when rows are inserted or removed before it: it then refers to whatever row
 * now sits at that index. Nothing checks that the index is still valid, so a
 * row that outlives the data it points into will return {@code null} or throw
 * when read.</p>
 *
 * @see DataSource#getRow(int)
 * @see Record
 */
public class Row extends DataAccessor {
	/** Version id for serialization. */
	private static final long serialVersionUID = 2725146484866525573L;

	/**
	 * Initializes a new view on the specified row of a data source. The index
	 * is not validated here; an invalid one only shows when the row is read.
	 * @param source Data source to read through to.
	 * @param row Row index, starting at 0.
	 */
	public Row(DataSource source, int row) {
		super(source, row);
	}

	@Override
	public Comparable<?> get(int col) {
		DataSource source = getSource();
		if (source == null) {
			return null;
		}
		return source.get(col, getIndex());
	}

	@Override
	public int size() {
		return getSource().getColumnCount();
	}

	/**
	 * Returns whether the column at the specified index contains numbers.
	 * @param columnIndex Index of the column to test.
	 * @return {@code true} if the column is numeric, otherwise {@code false}.
	 */
	public boolean isColumnNumeric(int columnIndex) {
		return getSource().isColumnNumeric(columnIndex);
	}
}

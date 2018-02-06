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
 * <p>Class for easily accessing a row of a data source.</p>
 *
 * <p>Example:</p>
 * <pre>
 * Row row = new Row(data, 2);
 * Number value = row.get(3);
 * </pre>
 *
 * @see DataSource
 */
public class Row extends DataAccessor {
	/** Version id for serialization. */
	private static final long serialVersionUID = 2725146484866525573L;

	/**
	 * Initializes a new instances with the specified data source and
	 * row index.
	 * @param source Data source.
	 * @param row Row index.
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

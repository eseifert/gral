/**
 * GRAL: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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
 * Lesser GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.data;


/**
 * Class for storing a row of a data source.
 */
public class Row {
	private final DataSource source;
	private final int row;

	/**
	 * Creates a new <code>Row</code> object with the specified DataSource and row index.
	 * @param source DataSource.
	 * @param row Row index.
	 */
	public Row(DataSource source, int row) {
		this.source = source;
		this.row = row;
	}

	/**
	 * Returns the DataSource containing this row.
	 * @return DataSource containing this row.
	 */
	public DataSource getSource() {
		return source;
	}

	/**
	 * Returns the index of this row in the DataSource.
	 * @return Row index.
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Returns the value of this row for the specified column.
	 * @param col Column index.
	 * @return Value of the cell.
	 */
	public Number get(int col) {
		if (getSource() == null) {
			return null;
		}
		return getSource().get(col, getRow());
	}

	/**
	 * Returns the number of elements in this row.
	 * @return Number of elements
	 */
	public int size() {
		return getSource().getColumnCount();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Row)) {
			return false;
		}
		Row r = (Row)obj;
		int size = size();
		if (r.size() != size) {
			return false;
		}
		for (int col = 0; col < size; col++) {
			if (!r.get(col).equals(get(col))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return source.hashCode() ^ row;
	}

	@Override
	public String toString() {
		return String.format("%s[source=%s,row=%d]",
				getClass().getName(), getSource(), getRow());
	}
}

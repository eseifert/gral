/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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

import de.erichseifert.gral.util.Orientation;



/**
 * <p>Class for accessing a specific column of a data source. The data of the
 * column can be accessed using the <code>get(int)</code> method.</p>
 *
 * <p>Example for accessing value at column 2, row 3 of a data source:</p>
 * <pre>
 * Column col = new Column(dataSource, 2);
 * Number v = col.get(3);
 * </pre>
 *
 * @see DataSource
 */
public class Column extends DataAccessor {
	/**
	 * Initializes a new instance with the specified data source and column
	 * index.
	 * @param source Data source.
	 * @param col Column index.
	 */
	public Column(DataSource source, int col) {
		super(source, col);
	}

	@Override
	public Number get(int row) {
		if (getSource() == null) {
			return null;
		}
		return getSource().get(getIndex(), row);
	}

	@Override
	public int size() {
		return getSource().getRowCount();
	}

	@Override
	public double getStatistics(String key) {
		return getSource().getStatistics()
			.get(key, Orientation.VERTICAL, getIndex());
	}
}

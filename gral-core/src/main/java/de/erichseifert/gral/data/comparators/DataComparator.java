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
package de.erichseifert.gral.data.comparators;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Abstract implementation of a <code>Comparator</code> for
 * <code>Number[]</code> arrays. This class allows to specify the position of
 * the arrays that should be compared.
 * @see de.erichseifert.gral.data.DataTable#sort(DataComparator...)
 */
public abstract class DataComparator implements Comparator<Number[]>, Serializable {
	/** Version id for serialization. */
	private static final long serialVersionUID = 1L;
	/** Column that should be used for comparing. */
	private int column;

	/**
	 * Constructor.
	 * @param col index of the column to be compared
	 */
	public DataComparator(int col) {
		this.column = col;
	}

	/**
	 * Returns the column to be compared.
	 * @return column index
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Sets the column to be compared to the specified index.
	 * @param col column index
	 */
	public void setColumn(int col) {
		this.column = col;
	}
}

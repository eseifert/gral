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
package de.erichseifert.gral.data.comparators;

import java.io.Serializable;
import java.util.Comparator;

import de.erichseifert.gral.data.Record;

/**
 * Abstract implementation of a {@code Comparator} for {@code Record} objects.
 * This class allows to specify the index at which the records should be
 * compared.
 * @see de.erichseifert.gral.data.DataTable#sort(DataComparator...)
 */
public abstract class DataComparator implements Comparator<Record>, Serializable {
	/** Version id for serialization. */
	private static final long serialVersionUID = -982173906879554838L;

	/** Column that should be used for comparing. */
	private final int column;

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
}

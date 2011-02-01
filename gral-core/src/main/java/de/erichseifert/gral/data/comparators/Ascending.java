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

/**
 * Class that represents a <code>DataComparator</code> sorting a specific
 * column in ascending order.
 * @see de.erichseifert.gral.data.DataTable#sort(DataComparator...)
 */
public class Ascending extends DataComparator {
	/** Version id for serialization. */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new Ascending object sorting according to the specified
	 * column.
	 * @param col Column index to be compared.
	 */
	public Ascending(int col) {
		super(col);
	}

	@Override
	public int compare(Number[] o1, Number[] o2) {
		if (o1 == o2) {
			return 0;
		}
		double o1Val = o1[getColumn()].doubleValue();
		double o2Val = o2[getColumn()].doubleValue();
		return Double.compare(o1Val, o2Val);
	}

}

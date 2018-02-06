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

import de.erichseifert.gral.data.Record;

/**
 * Class that represents a {@code DataComparator} for comparing two records
 * at a defined index for ascending order.
 * @see de.erichseifert.gral.data.DataTable#sort(DataComparator...)
 */
public class Ascending extends DataComparator {
	/** Version id for serialization. */
	private static final long serialVersionUID = -5206241300478408303L;

	/**
	 * Creates a new Ascending object for sorting according to the specified
	 * column.
	 * @param col Column index to be compared.
	 */
	public Ascending(int col) {
		super(col);
	}

	/**
	 * <p>Compares the values of two records at the specified column for order and
	 * returns a corresponding integer:</p>
	 * <ul>
	 *   <li>a negative value means {@code record1} is smaller than {@code record2}</li>
	 *   <li>0 means {@code record1} is equal to {@code record2}</li>
	 *   <li>a positive value means {@code record1} is larger than {@code record2}</li>
	 * </ul>
	 * @param record1 First record
	 * @param record2 Second record
	 * @return An integer number describing the order:
	 *         a negative value if {@code record1} is smaller than {@code record2},
	 *         0 if {@code record1} is equal to {@code record2},
	 *         a positive value if {@code record1} is larger than {@code record2},
	 */
	@SuppressWarnings("unchecked")
	public int compare(Record record1, Record record2) {
		Comparable<Object> value1 = record1.get(getColumn());
		Comparable<Object> value2 = record2.get(getColumn());

		// null values sort as if larger than non-null values
		if (value1 == null && value2 == null) {
			return 0;
		} else if (value1 == null) {
			return 1;
		} else if (value2 == null) {
			return -1;
		}

		return value1.compareTo(value2);
	}

}

/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.data.comparators;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Abstract implementation of a Comparator for Number arrays.
 * This class makes it possible to specify the position of the Number
 * arrays that should be compared.
 */
public abstract class DataComparator implements Comparator<Number[]>, Serializable {
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

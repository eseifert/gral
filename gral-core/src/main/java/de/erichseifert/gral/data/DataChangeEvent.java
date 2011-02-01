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

import java.util.EventObject;

/**
 * Class that stores information on a change of a specific data value in a
 * data source.
 * @see DataListener
 * @see DataSource
 */
public class DataChangeEvent extends EventObject {
	/** Version number for serialization. */
	private static final long serialVersionUID = 1L;

	/** Column of the value that has changed. */
	private final int col;
	/** Row of the value that has changed. */
	private final int row;
	/** Value before changes have been applied. */
	private final Number valOld;
	/** Changed value. */
	private final Number valNew;

	/**
	 * Initializes a new event with data source, position of the data value,
	 * and the values.
	 * @param source Data source
	 * @param col Columns of the value
	 * @param row Row of the value
	 * @param valOld Old value
	 * @param valNew New value
	 */
	public DataChangeEvent(DataSource source, int col, int row,
			Number valOld, Number valNew) {
		super(source);
		this.col = col;
		this.row = row;
		this.valOld = valOld;
		this.valNew = valNew;
	}

	/**
	 * Returns the column index of the value that was changed.
	 * @return Column index of the changed value.
	 */
	public int getCol() {
		return col;
	}

	/**
	 * Returns the row index of the value that was changed.
	 * @return Row index of the changed value.
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Returns the old value before it has changed.
	 * @return Value before the change.
	 */
	public Number getOld() {
		return valOld;
	}

	/**
	 * Returns the new value after the change has been applied.
	 * @return Value after the change.
	 */
	public Number getNew() {
		return valNew;
	}
}

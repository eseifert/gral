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

/**
 * <p>Class that creates a new data source which adds a leading column
 * containing the row number.</p>
 *
 * <p>Example which creates a two column data source from a one column
 * histogram:</p>
 * <pre>
 * DataSource hist = new Histogram1D(data, Orientation.HORIZONTAL, 10);
 * DataSource hist2d = new EnumeratedData(hist);
 * </pre>
 *
 * @see DataSource
 */
public class EnumeratedData extends AbstractDataSource {
	/** Data source which will be used as base for enumeration. */
	private final DataSource original;
	/** Value to start counting from. */
	private final double offset;
	/** Width of enumeration steps. */
	private final double steps;

	/**
	 * Initializes a new data source based on an original data source which
	 * will contain an additional column which enumerates all rows. The
	 * enumeration will start at a specified offset and will have a specified
	 * step size.
	 * @param original Original data source.
	 * @param offset Offset of enumeration
	 * @param steps Scaling of enumeration
	 */
	public EnumeratedData(DataSource original, double offset, double steps) {
		this.original = original;
		this.offset = offset;
		this.steps = steps;
	}

	/**
	 * Initializes a new data source based on an original data source which
	 * will contain an additional column which enumerates all rows.
	 * @param original Original data source.
	 */
	public EnumeratedData(DataSource original) {
		this(original, 0, 1);
	}

	@Override
	public Number get(int col, int row) {
		if (col < 1) {
			return row*steps + offset;
		}
		return original.get(col - 1, row);
	}

	@Override
	public int getColumnCount() {
		return original.getColumnCount() + 1;
	}

	@Override
	public int getRowCount() {
		return original.getRowCount();
	}

}

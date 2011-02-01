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

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Abstract class that represents a view on several rows of a data source.
 * Implementations must implement the method <code>accept(Row)</code> which
 * decides whether a specific row should be contained in this filtered data
 * source.</p>
 *
 * <p>Example that keeps only every second row:</p>
 * <pre>
 * DataSource filtered = new RowSubset() {
 *     public boolean accept(Row row) {
 *         return row.getIndex()%2 == 0;
 *     }
 * };
 * </pre>
 */
public abstract class RowSubset extends AbstractDataSource
		implements DataListener {
	/** Original data source. */
	private final DataSource original;
	/** List of column indexes that are stored in this filtered data source. */
	private final List<Integer> accepted;

	/**
	 * Creates a new instance with the specified data source.
	 * @param original DataSource to be filtered.
	 */
	public RowSubset(DataSource original) {
		accepted = new ArrayList<Integer>();
		this.original = original;
		this.original.addDataListener(this);
		dataChanged(this.original);
	}

	@Override
	public Row getRow(int row) {
		int rowOrig = accepted.get(row);
		return original.getRow(rowOrig);
	}

	@Override
	public Number get(int col, int row) {
		int rowOrig = accepted.get(row);
		return original.get(col, rowOrig);
	}

	@Override
	public int getColumnCount() {
		return original.getColumnCount();
	}

	@Override
	public int getRowCount() {
		return accepted.size();
	}

	@Override
	public void dataChanged(DataSource source, DataChangeEvent... events) {
		accepted.clear();
		for (int rowIndex = 0; rowIndex < original.getRowCount(); rowIndex++) {
			Row row = original.getRow(rowIndex);
			if (accept(row)) {
				accepted.add(rowIndex);
			}
		}
		notifyDataChanged(events);
	}

	/**
	 * Tests whether the specified row is accepted by this DataSubset or not.
	 * @param row Row to be tested.
	 * @return True if the row should be kept.
	 */
	public abstract boolean accept(Row row);
}

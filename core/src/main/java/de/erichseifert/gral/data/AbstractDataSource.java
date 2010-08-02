/*
 * GRAL: GRAphing Library for Java(R)
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
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.data;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import de.erichseifert.gral.data.statistics.Statistics;


/**
 * Abstract implementation of DataSource.
 * This class provides:
 * <ul>
 * <li>the administration of DataListeners</li>
 * <li>the administration of the Statistics capabilities</li>
 * <li>capability of notification of DataListeners</li>
 * <li>a basic iterator</li>
 * </ul>
 */
public abstract class AbstractDataSource implements DataSource {
	private final Set<DataListener> dataListeners;
	private Statistics statistics;

	/**
	 * Iterator that returns each row of the DataSource.
	 */
	private class DataSourceIterator implements Iterator<Number> {
		private int col;
		private int row;

		public DataSourceIterator() {
			col = 0;
			row = 0;
		}

		@Override
		public boolean hasNext() {
			return (col < getColumnCount()) && (row < getRowCount());
		}

		@Override
		public Number next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			Number value = get(col, row);
			if (++col >= getColumnCount()) {
				col = 0;
				++row;
			}
			return value;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Creates a new AbstractDataSource object.
	 */
	public AbstractDataSource() {
		dataListeners = new HashSet<DataListener>();
	}

	@Override
	public Statistics getStatistics() {
		if (statistics == null) {
			statistics = new Statistics(this);
		}
		return statistics;
	}

	@Override
	public void addDataListener(DataListener dataListener) {
		dataListeners.add(dataListener);
	}

	@Override
	public void removeDataListener(DataListener dataListener) {
		dataListeners.remove(dataListener);
	}

	@Override
	public Iterator<Number> iterator() {
		return new DataSourceIterator();
	}

	/**
	 * Notifies all registered DataListeners that the data of this DataSource has
	 * changed.
	 */
	protected void notifyDataChanged() {
		for (DataListener dataListener : dataListeners) {
			dataListener.dataChanged(this);
		}
	}

	@Override
	public Column getColumn(int col) {
		return new Column(this, col);
	}

	@Override
	public Row getRow(int row) {
		return new Row(this, row);
	}
}

/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2010 Erich Seifert <dev[at]richseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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

package de.erichseifert.gral.data.statistics;

import de.erichseifert.gral.data.AbstractDataSource;
import de.erichseifert.gral.data.DataListener;
import de.erichseifert.gral.data.DataSource;

/**
 * Abstract base class for histograms.
 */
public abstract class Histogram extends AbstractDataSource implements DataListener {
	private final DataSource data;

	/**
	 * Initializes a new histograms with a data source.
	 * @param data Data source to be analyzed.
	 */
	public Histogram(DataSource data) {
		this.data = data;
		this.data.addDataListener(this);
	}

	/**
	 * Recalculates the histogram values.
	 */
	protected abstract void rebuildCells();

	@Override
	public void dataChanged(DataSource data) {
		rebuildCells();
		notifyDataChanged();
	}
	
	/**
	 * Returns the data source associated to this histogram.
	 * @return Data source
	 */
	public DataSource getData() {
		return data;
	}

}

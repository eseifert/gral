/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.gral.plots.legends;

import java.util.LinkedList;
import java.util.List;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.Row;

/**
 * A legend implementation that displays an item for each data series that are
 * added to the legend.
 */
public abstract class SeriesLegend extends AbstractLegend {
	/** Version id for serialization. */
	private static final long serialVersionUID = 1092110896986707546L;

	@Override
	protected Iterable<Row> getEntries(DataSource source) {
		List<Row> items = new LinkedList<Row>();
		Row row = new Row(source, 0);
		items.add(row);
		return items;
	}

	@Override
	protected String getLabel(Row row) {
		return row.getSource().toString();
	}
}

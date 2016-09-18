/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2016 Erich Seifert <dev[at]erichseifert.de>,
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

import java.awt.Font;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.graphics.Drawable;

/**
 * A legend implementation that displays an item for each data series that are
 * added to the legend.
 */
public abstract class SeriesLegend extends AbstractLegend {
	/** Version id for serialization. */
	private static final long serialVersionUID = 1092110896986707546L;
	/** Mapping of data rows to drawable components. */
	private final Map<Row, Drawable> components;

	public SeriesLegend() {
		components = new HashMap<Row, Drawable>();
	}

	@Override
	protected Iterable<Row> getEntries(DataSource source) {
		List<Row> items = new LinkedList<Row>();
		Row row = new Row(source, 0);
		items.add(row);
		return items;
	}

	@Override
	public void add(DataSource source) {
		super.add(source);
		for (Row row : getEntries(source)) {
			String label = getLabel(source);
			Font font = getFont();
			Item item = new Item(getSymbol(source), label, font);
			add(item);
			components.put(row, item);
		}
	}

	@Override
	public void remove(DataSource source) {
		super.remove(source);
		Set<Row> rows = new HashSet<Row>(components.keySet());
		for (Row row : rows) {
			if (row.getSource() != source) {
				continue;
			}
			Drawable item = components.remove(row);
			if (item != null) {
				remove(item);
			}
		}
		invalidate();
	}

	/**
	 * Returns the label text for the specified data source.
	 * @param data Data source.
	 * @return Label text.
	 */
	protected String getLabel(DataSource data) {
		return data.toString();
	}

	/**
	 * Returns a symbol for rendering a legend item.
	 * @param data Data source.
	 * @return A drawable object that can be used to display the symbol.
	 */
	protected abstract Drawable getSymbol(DataSource data);
}

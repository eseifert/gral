/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2012 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael[at]erichseifert.de>
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
package de.erichseifert.gral.plots;

import java.text.Format;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.plots.settings.Key;
import de.erichseifert.gral.plots.settings.SettingChangeEvent;

/**
 * A legend implementation that displays items for all data values of all data
 * series that are added to the legend.
 */
public abstract class ValueLegend extends AbstractLegend {
	/** Version id for serialization. */
	private static final long serialVersionUID = -4274009997506638823L;

	/** Key for specifying a {@link Integer} value for the index of
	the column that contains the labels for the values. */
	public static final Key LABEL_COLUMN =
		new Key("valueLegend.label.column"); //$NON-NLS-1$
	/** Key for specifying the {@link java.text.Format} instance to be used to
	format the displayed data values. */
	public static final Key LABEL_FORMAT =
		new Key("valueLegend.label.format"); //$NON-NLS-1$

	/**
	 * Initializes a new instance with default values.
	 */
	public ValueLegend() {
		setSettingDefault(LABEL_COLUMN, 0);
	}

	@Override
	protected Iterable<Row> getEntries(DataSource source) {
		List<Row> items = new LinkedList<Row>();
		for (int rowIndex = 0; rowIndex < source.getRowCount(); rowIndex++) {
			Row row = new Row(source, rowIndex);
			items.add(row);
		}
		return items;
	}

	@Override
	protected String getLabel(Row row) {
		int col = this.<Integer>getSetting(LABEL_COLUMN);
		Comparable<?> value = row.get(col);
		if (value == null) {
			return "";
		}

		// Formatting
		Format format = getSetting(LABEL_FORMAT);
		if ((format == null) && row.isColumnNumeric(col)) {
			format = NumberFormat.getInstance();
		}

		// Text to display
		String text = (format != null) ? format.format(value) : value.toString();
		return text;
	}

	@Override
	public void settingChanged(SettingChangeEvent event) {
		super.settingChanged(event);
		Key key = event.getKey();
		if (LABEL_COLUMN.equals(key) || LABEL_FORMAT.equals(key)) {
			refresh();
		}
	}
}

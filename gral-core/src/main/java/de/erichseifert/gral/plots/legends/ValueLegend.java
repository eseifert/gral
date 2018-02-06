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
package de.erichseifert.gral.plots.legends;

import java.awt.Font;
import java.text.Format;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.erichseifert.gral.data.DataChangeEvent;
import de.erichseifert.gral.data.DataListener;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.graphics.Drawable;

/**
 * A legend implementation that displays items for all data values of all data
 * series that are added to the legend.
 */
public abstract class ValueLegend extends AbstractLegend
		implements DataListener {
	/** Version id for serialization. */
	private static final long serialVersionUID = -4274009997506638823L;

	/** Mapping of data rows to drawable components. */
	private final Map<Row, Drawable> components;
	/** Column index containing the labels. */
	private int labelColumn;
	/** Format for data to label text conversion. */
	private Format labelFormat;

	/**
	 * Initializes a new instance with default values.
	 */
	public ValueLegend() {
		components = new HashMap<>();
		labelColumn = 0;
	}

	/**
	 * Returns a sequence of items for the specified data source that should be
	 * added to the legend.
	 * @param source Data source.
	 * @return A sequence of items for the specified data source.
	 */
	protected Iterable<Row> getEntries(DataSource source) {
		List<Row> items = new LinkedList<>();
		for (int rowIndex = 0; rowIndex < source.getRowCount(); rowIndex++) {
			Row row = new Row(source, rowIndex);
			items.add(row);
		}
		return items;
	}

	/**
	 * Returns the label text for the specified row.
	 * @param row Data row.
	 * @return Label text.
	 */
	protected String getLabel(Row row) {
		int col = getLabelColumn();
		Comparable<?> value = row.get(col);
		if (value == null) {
			return "";
		}

		// Formatting
		Format format = getLabelFormat();
		if ((format == null) && row.isColumnNumeric(col)) {
			format = NumberFormat.getInstance();
		}

		// Text to display
		return (format != null) ? format.format(value) : value.toString();
	}

	@Override
	public void add(DataSource source) {
		super.add(source);
		refresh();
		source.addDataListener(this);
	}

	@Override
	public void remove(DataSource source) {
		super.remove(source);
		Set<Row> rows = new HashSet<>(components.keySet());
		for (Row row : rows) {
			if (row.getSource() != source) {
				continue;
			}
			Drawable item = components.remove(row);
			if (item != null) {
				remove(item);
			}
		}
		refresh();
		source.removeDataListener(this);
	}

	/**
	 * Method that is invoked when data has been added.
	 * This method is invoked by objects that provide support for
	 * {@code DataListener}s and should not be called manually.
	 * @param source Data source that has been changed.
	 * @param events Optional event object describing the data values that
	 *        have been added.
	 */
	public void dataAdded(DataSource source, DataChangeEvent... events) {
		dataChanged(source, events);
	}

	/**
	 * Method that is invoked when data has been updated.
	 * This method is invoked by objects that provide support for
	 * {@code DataListener}s and should not be called manually.
	 * @param source Data source that has been changed.
	 * @param events Optional event object describing the data values that
	 *        have been updated.
	 */
	public void dataUpdated(DataSource source, DataChangeEvent... events) {
		dataChanged(source, events);
	}

	/**
	 * Method that is invoked when data has been removed.
	 * This method is invoked by objects that provide support for
	 * {@code DataListener}s and should not be called manually.
	 * @param source Data source that has been changed.
	 * @param events Optional event object describing the data values that
	 *        have been removed.
	 */
	public void dataRemoved(DataSource source, DataChangeEvent... events) {
		dataChanged(source, events);
	}

	/**
	 * Method that is invoked when data has been added, updated, or removed.
	 * This method is invoked by objects that provide support for
	 * {@code DataListener}s and should not be called manually.
	 * @param source Data source that has been changed.
	 * @param events Optional event object describing the data values that
	 *        have been changed.
	 */
	private void dataChanged(DataSource source, DataChangeEvent... events) {
		refresh();
	}

	/**
	 * Returns the index of the column that contains the labels for the values.
	 * @return Column index containing the labels.
	 */
	public int getLabelColumn() {
		return labelColumn;
	}

	/**
	 * Sets the index of the column that contains the labels for the values.
	 * @param labelColumn Column index containing the labels.
	 */
	public void setLabelColumn(int labelColumn) {
		this.labelColumn = labelColumn;
		refresh();
	}

	/**
	 * Returns the format used to display data values.
	 * @return Format for data to label text conversion.
	 */
	public Format getLabelFormat() {
		return labelFormat;
	}

	/**
	 * Sets the format used to display data values.
	 * @param labelFormat Format for data to label text conversion.
	 */
	public void setLabelFormat(Format labelFormat) {
		this.labelFormat = labelFormat;
		refresh();
	}

	/**
	 * Returns a symbol for rendering a legend item.
	 * @param row Data row.
	 * @return A drawable object that can be used to display the symbol.
	 */
	protected abstract Drawable getSymbol(Row row);

	private void refresh() {
		for (Drawable drawable : components.values()) {
			remove(drawable);
		}
		components.clear();
		for (DataSource source : getSources()) {
			for (Row row : getEntries(source)) {
				String label = getLabel(row);
				Font font = getFont();
				Item item = new Item(getSymbol(row), label, font);
				add(item);
				components.put(row, item);
			}
		}
	}
}

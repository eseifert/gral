/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2026 Erich Seifert <dev[at]erichseifert.de>,
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
import java.util.Map;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.graphics.Drawable;

/**
 * <p>A legend that shows one entry per data source, labeled with the name of
 * that source. This is what the ordinary plot types use, since there one data
 * source is one series.</p>
 *
 * <p>Subclasses supply the symbol drawn beside the label by implementing
 * {@link #getSymbol(DataSource)}, which is how a plot makes the symbol look
 * like the marks and lines it actually draws; {@link #getLabel(DataSource)} can
 * be overridden to derive the text from something other than the name of the
 * source.</p>
 *
 * @see ValueLegend
 */
public abstract class SeriesLegend extends AbstractLegend {
	/** Version id for serialization. */
	private static final long serialVersionUID = 1092110896986707546L;
	/** Mapping of data rows to drawable components. */
	private final Map<DataSource, Drawable> drawableByDataSource;

	/**
	 * Initializes a new, empty legend.
	 */
	public SeriesLegend() {
		drawableByDataSource = new HashMap<>();
	}

	@Override
	public void add(DataSource source) {
		super.add(source);
		String label = getLabel(source);
		Font font = getFont();
		var item = new Item(getSymbol(source), label, font);
		add(item);
		drawableByDataSource.put(source, item);
	}

	@Override
	public void remove(DataSource source) {
		super.remove(source);
		Drawable drawable = drawableByDataSource.remove(source);
		if (drawable != null) {
			remove(drawable);
		}
	}

	/**
	 * Returns the label text for the specified data source.
	 * @param data Data source.
	 * @return Label text.
	 */
	protected String getLabel(DataSource data) {
		return data.getName();
	}

	/**
	 * Returns a symbol for rendering a legend item.
	 * @param data Data source.
	 * @return A drawable object that can be used to display the symbol.
	 */
	protected abstract Drawable getSymbol(DataSource data);
}

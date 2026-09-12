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

/**
 * <p>Legends: the key that names the data series of a plot.</p>
 *
 * <p>A {@link de.erichseifert.gral.plots.legends.Legend} is a
 * {@link de.erichseifert.gral.graphics.Container} of entries, one per data
 * source added to it. Each entry pairs a small symbol with a label. A plot
 * creates and fills its own legend; usually all that is needed is to make it
 * visible and to give the series a name, since that name is what the entry
 * shows:</p>
 *
 * <pre>
 * DataSeries series = new DataSeries("Temperature", data, 0, 1);
 * XYPlot plot = new XYPlot(series);
 * plot.setLegendVisible(true);
 * plot.setLegendLocation(Location.NORTH_EAST);
 * plot.getLegend().setOrientation(Orientation.HORIZONTAL);
 * </pre>
 *
 * <p>{@link de.erichseifert.gral.plots.legends.AbstractLegend} implements the
 * layout and the styling and leaves the two interesting decisions to
 * subclasses: which items a data source contributes, and what symbol to draw
 * for an item. {@link de.erichseifert.gral.plots.legends.SeriesLegend} produces
 * one entry per data source, which is what most plots use;
 * {@link de.erichseifert.gral.plots.legends.ValueLegend} produces one entry per
 * row, which is what {@code PiePlot} needs, since there each row is a
 * slice.</p>
 */
package de.erichseifert.gral.plots.legends;

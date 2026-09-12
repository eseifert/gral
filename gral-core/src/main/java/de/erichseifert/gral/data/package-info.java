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
 * <p>The tabular data model that plots read from.</p>
 *
 * <h2>Reading and writing</h2>
 * <p>{@link de.erichseifert.gral.data.DataSource} is the read-only interface:
 * a rectangular grid of cells addressed by column and row, where every value is
 * a {@code Comparable}. {@link de.erichseifert.gral.data.MutableDataSource}
 * adds the write operations. {@link de.erichseifert.gral.data.DataTable} is the
 * ordinary in-memory implementation and the class most programs start with:</p>
 * <pre>
 * DataTable data = new DataTable(Double.class, Double.class, String.class);
 * data.add(1.0, 2.5, "first");
 * data.add(2.0, 3.5, "second");
 * </pre>
 * <p>The column types given to the constructor are enforced:
 * {@code add} throws {@code IllegalArgumentException} if the number of values
 * or one of their types does not match.</p>
 *
 * <h2>Views</h2>
 * <p>Several classes wrap an existing data source instead of copying it, so
 * that changes to the underlying data show through:</p>
 * <ul>
 *   <li>{@link de.erichseifert.gral.data.DataSeries} picks a subset of columns,
 *   in any order, and carries the name a legend displays. This is how a table
 *   with many columns is split into the series of a plot.</li>
 *   <li>{@link de.erichseifert.gral.data.RowSubset} filters rows through an
 *   {@code accept(Row)} method implemented by a subclass.</li>
 *   <li>{@link de.erichseifert.gral.data.EnumeratedData} prepends a column of
 *   row numbers, which turns a single-column table into something an
 *   {@code XYPlot} can draw.</li>
 * </ul>
 * <p>{@link de.erichseifert.gral.data.DummyData} is a cheap constant source
 * that is useful in tests, and {@link de.erichseifert.gral.data.JdbcData}
 * exposes a JDBC {@code ResultSet} as a data source.</p>
 *
 * <h2>Accessors</h2>
 * <p>{@link de.erichseifert.gral.data.Row} and
 * {@link de.erichseifert.gral.data.Column} give one-dimensional access to a
 * data source. A {@code Row} is a live view that reads through to its source; a
 * {@code Column} holds a snapshot of the values it was created with.
 * {@link de.erichseifert.gral.data.Record} is an immutable tuple of values and
 * is what a {@code DataTable} stores internally.</p>
 *
 * <h2>Change notification</h2>
 * <p>Registering a {@link de.erichseifert.gral.data.DataListener} on a data
 * source reports added, removed and updated values as
 * {@link de.erichseifert.gral.data.DataChangeEvent}s. Plots use this to
 * repaint and to recompute their axis ranges, so modifying a
 * {@code DataTable} that is already being displayed is enough to update the
 * plot.</p>
 *
 * <h2>Statistics</h2>
 * <p>{@link de.erichseifert.gral.data.DataSource#getStatistics()} returns a
 * {@link de.erichseifert.gral.data.statistics.Statistics} object whose measures
 * are looked up by string key, for example
 * {@code data.getStatistics().get(Statistics.MEAN)}. The object is computed
 * lazily and discarded whenever the data changes.</p>
 *
 * @see de.erichseifert.gral.data.filters
 * @see de.erichseifert.gral.data.statistics
 * @see de.erichseifert.gral.io.data
 */
package de.erichseifert.gral.data;

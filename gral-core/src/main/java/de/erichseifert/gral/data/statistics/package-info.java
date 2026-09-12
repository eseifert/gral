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
 * <p>Summary statistics and frequency distributions of data values.</p>
 *
 * <p>{@link de.erichseifert.gral.data.statistics.Statistics} computes measures
 * over any {@code Iterable} of comparable values &mdash; a whole data source, a
 * single {@link de.erichseifert.gral.data.Column}, or one
 * {@link de.erichseifert.gral.data.Record}. Measures are named by string
 * constants on the class and computed on first request, then cached:</p>
 *
 * <pre>
 * Statistics stats = data.getStatistics();
 * double mean = stats.get(Statistics.MEAN);
 * double iqr  = stats.get(Statistics.QUARTILE_3) - stats.get(Statistics.QUARTILE_1);
 *
 * // Per column rather than over the whole table:
 * DataSource means = data.getColumnStatistics(Statistics.MEAN);
 * </pre>
 *
 * <p>Non-numeric and {@code null} values are skipped, so the element count
 * {@link de.erichseifert.gral.data.statistics.Statistics#N} counts only the
 * values that contributed.</p>
 *
 * <p>{@link de.erichseifert.gral.data.statistics.Histogram} counts how many
 * values fall into each of a series of bins, either at breaks you supply or at
 * equidistant breaks derived from the data:</p>
 *
 * <pre>
 * Iterable&lt;Comparable&lt;?&gt;&gt; values =
 *     Arrays.&lt;Comparable&lt;?&gt;&gt;asList(1.0, 2.0, 2.5, 7.0);
 * Histogram histogram = new Histogram(values, 10);
 * for (Integer count : histogram) {
 *     // …one count per bin, in order
 * }
 * </pre>
 *
 * <p>{@link de.erichseifert.gral.data.statistics.Histogram2D} is the older,
 * data-source-shaped variant: it is itself a
 * {@link de.erichseifert.gral.data.DataSource}, so it can be handed straight to
 * a plot.</p>
 */
package de.erichseifert.gral.data.statistics;

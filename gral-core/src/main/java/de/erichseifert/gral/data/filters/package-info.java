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
 * <p>Filters that derive new values from the values of a data source, for
 * example to smooth a noisy series.</p>
 *
 * <p>Two generations of filter API live here side by side. Neither is
 * deprecated, but new filters should follow the newer one.</p>
 *
 * <h2>{@code Filter}: filters as sequences</h2>
 * <p>{@link de.erichseifert.gral.data.filters.Filter} is an
 * {@code Iterable<Double>} built on top of another {@code Iterable}. The result
 * is computed once, up front, and simply iterated:</p>
 * <pre>
 * Iterable&lt;Double&gt; values = Arrays.asList(3.0, 8.0, 5.0, 6.0, 4.0, 9.0);
 *
 * // A running total: 3, 11, 16, 22, 26, 35.
 * for (double sum : new Accumulation&lt;&gt;(values)) { … }
 *
 * // Smoothing with a window of five values.
 * for (double smoothed : new MedianFilter&lt;&gt;(values, 5)) { … }
 *
 * // Convolution with a binomial kernel, which is already normalized.
 * Kernel kernel = Kernel.getBinomial(5);
 * for (double smoothed : new ConvolutionFilter&lt;&gt;(values, kernel)) { … }
 * </pre>
 * <p>Filters that need a window of neighboring values produce fewer results
 * than they consume: a window of size <i>n</i> over <i>m</i> values yields
 * <i>m&nbsp;&minus;&nbsp;n&nbsp;+&nbsp;1</i> results, with no padding at the
 * ends.</p>
 *
 * <h2>{@code Filter2D}: filters as data sources</h2>
 * <p>{@link de.erichseifert.gral.data.filters.Filter2D} wraps a whole
 * {@link de.erichseifert.gral.data.DataSource} and is one itself, so a filtered
 * table can be passed to a plot directly. Filtered columns are named at
 * construction time and buffered; unfiltered columns are passed through
 * unchanged. The filter listens to the original source and recomputes when it
 * changes.</p>
 * <pre>
 * // Window of 3, offset 1, column 1 only, repeating the edge values beyond
 * // the ends of the column.
 * DataSource smoothed = new Median(data, 3, 1, Filter2D.Mode.REPEAT, 1);
 * XYPlot plot = new XYPlot(smoothed);
 * </pre>
 * <p>Because a {@code Filter2D} has to produce a value for every row, it needs
 * a {@link de.erichseifert.gral.data.filters.Filter2D.Mode} that says what
 * stands in for the missing neighbors at the start and the end of a column:
 * {@code OMIT} (result is {@code null}), {@code ZERO}, {@code REPEAT},
 * {@code MIRROR} or {@code CIRCULAR}. Implementations are
 * {@link de.erichseifert.gral.data.filters.Convolution},
 * {@link de.erichseifert.gral.data.filters.Median} and
 * {@link de.erichseifert.gral.data.filters.Resize}.</p>
 *
 * <p>{@link de.erichseifert.gral.data.filters.Kernel} is shared by both
 * generations: it is the weight vector of a convolution, with an offset that
 * marks which weight sits on the current value.</p>
 */
package de.erichseifert.gral.data.filters;

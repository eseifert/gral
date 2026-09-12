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
package de.erichseifert.gral.data.filters;

/**
 * <p>A sequence of {@code double} values derived from another sequence, for
 * example a smoothed or accumulated version of a data column. This is the newer
 * of the two filter APIs in this package; the older one,
 * {@link Filter2D}, decorates a whole data source instead.</p>
 *
 * <pre>
 * Iterable&lt;Double&gt; values = Arrays.asList(3.0, 8.0, 5.0, 6.0, 4.0, 9.0);
 * for (double accumulated : new Accumulation&lt;&gt;(values)) {
 *     // 3.0, 11.0, 16.0, 22.0, 26.0, 35.0
 * }
 * </pre>
 *
 * <p>A filter is not required to produce as many values as it consumes. Filters
 * that need a window of neighboring values, such as
 * {@link MedianFilter} and {@link ConvolutionFilter}, yield
 * <i>m&nbsp;&minus;&nbsp;n&nbsp;+&nbsp;1</i> results for <i>m</i> input values
 * and a window of <i>n</i>: there is no padding at the ends, and the result is
 * empty if the input is shorter than the window. Use {@link Filter2D} where a
 * value is needed for every row.</p>
 *
 * @param <T> Type of the values being filtered.
 */
public interface Filter<T extends Comparable<T>> extends Iterable<Double> {
}

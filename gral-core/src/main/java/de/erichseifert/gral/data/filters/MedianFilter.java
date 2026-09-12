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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.util.WindowIterator;

/**
 * <p>A {@link Filter} that replaces each value by the median of a window of
 * neighboring values. Unlike averaging, this removes isolated outliers without
 * smearing them over their neighbors, which makes it a good first choice for
 * spiky data.</p>
 *
 * <pre>
 * for (double smoothed : new MedianFilter&lt;&gt;(values, 3)) {
 *     …
 * }
 * </pre>
 *
 * <p>There is no padding at the ends: <i>m</i> input values and a window of
 * <i>n</i> yield <i>m&nbsp;&minus;&nbsp;n&nbsp;+&nbsp;1</i> results, and none
 * at all if the input is shorter than the window. Use
 * {@link Median} if a value is needed for every row of a data source.</p>
 *
 * @param <T> Type of the values being filtered.
 */
public class MedianFilter<T extends Number & Comparable<T>> implements Filter<T> {
	/** Filtered values. */
	private final List<Double> filtered;
	private final Iterator<List<T>> windowIterator;

	/**
	 * Initializes a new filter and computes all of its values immediately.
	 * @param data Values to be filtered.
	 * @param windowSize Number of values the median is taken over.
	 */
	public MedianFilter(Iterable<T> data, int windowSize) {
		filtered = new LinkedList<>();

		windowIterator = new WindowIterator<>(data.iterator(), windowSize);

		while (windowIterator.hasNext()) {
			List<T> window = windowIterator.next();
			var windowStatistics = new Statistics(window);
			double median = windowStatistics.get(Statistics.MEDIAN);
			filtered.add(median);
		}
	}

	@Override
	public Iterator<Double> iterator() {
		return filtered.iterator();
	}
}

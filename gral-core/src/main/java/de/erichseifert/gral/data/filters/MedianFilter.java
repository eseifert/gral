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
package de.erichseifert.gral.data.filters;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.util.WindowIterator;

public class MedianFilter<T extends Number & Comparable<T>> implements Filter<T> {
	private final List<Double> filtered;
	private final Iterator<List<T>> windowIterator;

	public MedianFilter(Iterable<T> data, int windowSize) {
		filtered = new LinkedList<>();

		windowIterator = new WindowIterator<>(data.iterator(), windowSize);

		while (windowIterator.hasNext()) {
			List<T> window = windowIterator.next();
			Statistics windowStatistics = new Statistics(window);
			double median = windowStatistics.get(Statistics.MEDIAN);
			filtered.add(median);
		}
	}

	@Override
	public Iterator<Double> iterator() {
		return filtered.iterator();
	}
}

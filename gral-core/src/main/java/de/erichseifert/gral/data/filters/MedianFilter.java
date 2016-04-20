/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2016 Erich Seifert <dev[at]erichseifert.de>,
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.WindowIterator;

public class MedianFilter<T extends Number> implements Iterable<Double> {
	private final List<Double> filtered;
	private final Iterator<List<T>> windowIterator;

	public MedianFilter(Iterable<T> data, int windowSize) {
		filtered = new LinkedList<Double>();

		windowIterator = new WindowIterator<T>(data.iterator(), windowSize);

		while (windowIterator.hasNext()) {
			List<T> window = windowIterator.next();
			double median = median(window);
			filtered.add(median);
		}
	}

	/**
	 * Calculates the median for the specified values in the window.
	 * @param w List of values the median will be calculated for.
	 * @return Median.
	 */
	private double median(List<T> w) {
		if (w.size() == 1) {
			return w.get(0).doubleValue();
		}
		List<Double> window = new ArrayList<Double>(w.size());
		for (T v : w) {
			if (!MathUtils.isCalculatable(v)) {
				return Double.NaN;
			}
			window.add(v.doubleValue());
		}
		int medianIndex = MathUtils.randomizedSelect(
				window, 0, window.size() - 1, window.size()/2 + 1);
		double median = window.get(medianIndex);
		if ((window.size() & 1) == 0) {
			int medianUpperIndex = MathUtils.randomizedSelect(
					window, 0, window.size() - 1, window.size()/2 + 1);
			double medianUpper = window.get(medianUpperIndex);
			median = (median + medianUpper)/2.0;
		}
		return median;
	}

	@Override
	public Iterator<Double> iterator() {
		return filtered.iterator();
	}
}

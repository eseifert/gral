/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2019 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.gral.data.statistics;

import java.util.Arrays;
import java.util.Iterator;

/**
 * <p>Counts how many of the specified values fall into each of a series of
 * bins. The bins are defined by their breaks: the first bin reaches from the
 * first to the second break, the second bin from the second to the third
 * break, and so on. A bin contains all values that are greater than or equal
 * to its lower limit and smaller than its upper limit. The last bin also
 * contains the values that are equal to its upper limit.</p>
 */
public class Histogram implements Iterable<Integer> {
	private Iterable<Comparable<?>> data;
	private Number[] breaks;
	private Integer[] bins;

	public Histogram(Iterable<Comparable<?>> data, int binCount) {
		this(data, getEquidistantBreaks(data, binCount + 1));
	}

	public Histogram(Iterable<Comparable<?>> data, Number... breaks) {
		if (breaks.length < 2) {
			throw new IllegalArgumentException("Invalid break count: " + breaks.length +
					" A histogram requires at least two breaks to form a bucket.");
		}
		this.data = data;
		this.breaks = breaks;
		int binCount = breaks.length - 1;
		bins = new Integer[binCount];
		Arrays.fill(bins, 0);

		computeDistribution();
	}

	private static Number[] getEquidistantBreaks(Iterable<Comparable<?>> data, int breakCount) {
		Number[] breaks = new Number[breakCount];
		Statistics statistics = new Statistics(data);
		double minValue = statistics.get(Statistics.MIN);
		double maxValue = statistics.get(Statistics.MAX);
		double range = maxValue - minValue;
		int binCount = breakCount - 1;
		double binWidth = range/binCount;
		for (int breakIndex = 0; breakIndex < breaks.length; breakIndex++) {
			breaks[breakIndex] = minValue + breakIndex*binWidth;
		}
		return breaks;
	}

	private void computeDistribution() {
		for (Comparable<?> value : data) {
			if (!(value instanceof Number)) {
				continue;
			}
			double doubleValue = ((Number) value).doubleValue();
			for (int binIndex = 0; binIndex < bins.length; binIndex++) {
				double lowerBinLimit = breaks[binIndex].doubleValue();
				double upperBinLimit = breaks[binIndex + 1].doubleValue();
				// All bins but the last one exclude their upper limit. The
				// last bin includes it, so that the largest value is counted.
				boolean isLastBin = binIndex == bins.length - 1;
				boolean isBelowUpperLimit = isLastBin
					? doubleValue <= upperBinLimit
					: doubleValue < upperBinLimit;
				if (doubleValue >= lowerBinLimit && isBelowUpperLimit) {
					bins[binIndex]++;
				}
			}
		}
	}

	public int size() {
		return breaks.length - 1;
	}

	public int get(int binIndex) {
		return bins[binIndex];
	}

	@Override
	public Iterator<Integer> iterator() {
		return Arrays.asList(bins).iterator();
	}
}

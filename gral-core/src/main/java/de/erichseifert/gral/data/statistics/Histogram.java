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
package de.erichseifert.gral.data.statistics;

import java.util.Arrays;
import java.util.Iterator;

/**
 * <p>Counts how many of the specified values fall into each of a series of
 * bins. The bins are defined by their breaks: the first bin reaches from the
 * first to the second break, the second bin from the second to the third
 * break, and so on. A bin contains all values that are greater than or equal
 * to its lower limit and smaller than its upper limit. The last bin also
 * contains the values that are equal to its upper limit, so that the largest
 * value is counted.</p>
 *
 * <pre>
 * Iterable&lt;Comparable&lt;?&gt;&gt; values =
 *     Arrays.&lt;Comparable&lt;?&gt;&gt;asList(0.5, 1.5, 1.7, 3.0);
 *
 * // Four equally wide bins spanning the range of the data.
 * Histogram equal = new Histogram(values, 4);
 *
 * // Three bins with explicit, unequal limits: [0,1), [1,2), [2,3].
 * Histogram custom = new Histogram(values, 0.0, 1.0, 2.0, 3.0);
 * for (int count : custom) {
 *     // 1, 2, 1
 * }
 * </pre>
 *
 * <p>The counts are computed once, in the constructor; a histogram is a
 * snapshot and does not follow later changes to the data. Non-numeric and
 * {@code null} values are skipped. Iterating a histogram visits the bin counts
 * in order.</p>
 *
 * <p>{@link Histogram2D} is the older variant that is itself a
 * {@link de.erichseifert.gral.data.DataSource} and can therefore be passed
 * straight to a plot.</p>
 */
public class Histogram implements Iterable<Integer> {
	private Iterable<Comparable<?>> data;
	private Number[] breaks;
	private Integer[] bins;

	/**
	 * Initializes a new histogram with the specified number of equally wide
	 * bins, spanning the range from the smallest to the largest value of the
	 * data. The counts are computed immediately.
	 * @param data Values to be counted. Non-numeric and {@code null} entries
	 *        are ignored.
	 * @param binCount Number of bins, at least one.
	 * @throws IllegalArgumentException if fewer than one bin is requested.
	 */
	public Histogram(Iterable<Comparable<?>> data, int binCount) {
		this(data, getEquidistantBreaks(data, binCount + 1));
	}

	/**
	 * Initializes a new histogram with bins of the specified, possibly unequal,
	 * widths. Each pair of consecutive breaks forms one bin, so <i>n</i> breaks
	 * define <i>n</i>&nbsp;&minus;&nbsp;1 bins. Values outside the outermost
	 * breaks are not counted anywhere. The counts are computed immediately.
	 * @param data Values to be counted. Non-numeric and {@code null} entries
	 *        are ignored.
	 * @param breaks Bin limits in ascending order, at least two of them.
	 * @throws IllegalArgumentException if fewer than two breaks are given.
	 */
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
		var breaks = new Number[breakCount];
		var statistics = new Statistics(data);
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

	/**
	 * Returns the number of bins, which is one less than the number of breaks.
	 * @return Number of bins.
	 */
	public int size() {
		return breaks.length - 1;
	}

	/**
	 * Returns how many values fell into the specified bin.
	 * @param binIndex Index of the bin, from 0 to {@link #size()}&nbsp;&minus;&nbsp;1.
	 * @return Number of values in that bin.
	 * @throws ArrayIndexOutOfBoundsException if the bin does not exist.
	 */
	public int get(int binIndex) {
		return bins[binIndex];
	}

	@Override
	public Iterator<Integer> iterator() {
		return Arrays.asList(bins).iterator();
	}
}

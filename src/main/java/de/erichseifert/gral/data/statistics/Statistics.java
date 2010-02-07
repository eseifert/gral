/**
 * GRAL : Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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
 * Lesser GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.data.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.erichseifert.gral.data.DataListener;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.util.MathUtils;


/**
 * A class that computes and stores various statistical information
 * on a data source.
 */
public class Statistics implements DataListener {
	/** Key for specifying the sum of all values. */
	public static final String SUM = "sum";
	/** Key for specifying the sum of all value squares. */
	public static final String SUM2 = "sum2";
	/** Key for specifying the sum of all value cubics. */
	public static final String SUM3 = "sum3";
	/** Key for specifying the sum of all value quads. */
	public static final String SUM4 = "sum4";
	/** Key for specifying the arithmetic mean of all values. */
	public static final String MEAN = "mean";
	/** Key for specifying the minimum, i.e. the smallest value. */
	public static final String MIN = "min";
	/** Key for specifying the maximum, i.e. the largest value. */
	public static final String MAX = "max";
	/** Key for specifying the total number of elements. This is the zeroth central moment: E((x - µ)^0) */
	public static final String N = "n";
	/** Key for specifying the expected value. This is the first central moment: E((x - E(x))^1) */
	public static final String MEAN_DEVIATION = "mean deviation";
	/** Key for specifying the variance. This is the second central moment: E((x - E(x))^2) */
	public static final String VARIANCE = "variance";
	/** Key for specifying the skewness. This is the third central moment: E((x - E(x))^3) */
	public static final String SKEWNESS = "skewness";
	/** Key for specifying the kurtosis. This is the fourth central moment: E((x - E(x))^4) */
	public static final String KURTOSIS = "kurtosis";
	/** Key for specifying the median (or 50% quantile). */
	public static final String MEDIAN = "median";

	private DataSource data;
	private final ArrayList<Map<String, Double>> statistics;

	/**
	 * Creates a new Statistics object with the specified DataSource.
	 * @param data DataSource to be analyzed.
	 */
	public Statistics(DataSource data) {
		statistics = new ArrayList<Map<String, Double>>();

		this.data = data;
		dataChanged(this.data);
		this.data.addDataListener(this);
	}

	/**
	 * Returns the specified information for the specified column.
	 * @param key Requested information.
	 * @param col Column index.
	 * @return Calculated value.
	 */
	public double get(String key, int col) {
		Map<String, Double> colStats = statistics.get(col);
		return colStats.get(key);
	}

	@Override
	public void dataChanged(DataSource data) {
		int colCount = data.getColumnCount();
		int rowCount = data.getRowCount();

		statistics.clear();
		statistics.ensureCapacity(colCount);

		// Calculate statistics
		for (int colIndex = 0; colIndex < colCount; colIndex++) {
			Map<String, Double> colStats = new HashMap<String, Double>();
			List<Double> col = new ArrayList<Double>(rowCount);

			colStats.put(N, 0.0);
			colStats.put(SUM, 0.0);
			colStats.put(SUM2, 0.0);
			colStats.put(SUM3, 0.0);
			colStats.put(SUM4, 0.0);

			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				Number cell = data.get(colIndex, rowIndex);
				double value = cell.doubleValue();
				double value2 = value*value;

				// Store value of column row
				col.add(value);

				// N (element count, zeroth moment)
				colStats.put(N, colStats.get(N) + 1.0);

				if (Double.isNaN(value)) {
					continue;
				}

				// Sum
				colStats.put(SUM, colStats.get(SUM) + value);
				// Sum of value squares
				colStats.put(SUM2, colStats.get(SUM2) + value2);
				// Sum of value cubics
				colStats.put(SUM3, colStats.get(SUM3) + value2*value);
				// Sum of value quads
				colStats.put(SUM4, colStats.get(SUM4) + value2*value2);

				// Minimum
				if (!colStats.containsKey(MIN) || value < colStats.get(MIN)) {
					colStats.put(MIN, value);
				}
				// Maximum
				if (!colStats.containsKey(MAX) || value > colStats.get(MAX)) {
					colStats.put(MAX, value);
				}
			}

			// Check for empty data source
			if (rowCount == 0) {
				continue;
			}

			// Mean
			double mean = colStats.get(SUM) / colStats.get(N);
			double mean2 = mean*mean;
			colStats.put(MEAN, mean);

			// Mean deviation (first moment) for expected uniform distribution is always 0.
			colStats.put(MEAN_DEVIATION, 0.0);
			// Variance (second moment)
			colStats.put(VARIANCE, colStats.get(SUM2) - mean*colStats.get(SUM));
			// Skewness (third moment)
			colStats.put(SKEWNESS, colStats.get(SUM3) - 3.0*mean*colStats.get(SUM2) + 2.0*mean2*colStats.get(SUM));
			// Kurtosis (fourth moment)
			colStats.put(KURTOSIS, colStats.get(SUM4) - 4.0*mean*colStats.get(SUM3) + 6.0*mean2*colStats.get(SUM2) - 3.0*mean2*mean*colStats.get(SUM));

			// Median
			int medianIndex = MathUtils.randomizedSelect(col, 0, col.size() - 1, col.size()/2);
			double median = col.get(medianIndex);
			if ((rowCount & 1) == 0) {
				double medianUpper = col.get(medianIndex + 1);
				median = (median + medianUpper)/2.0;
			}
			colStats.put(MEDIAN, median);

			// Add a Map for each column
			statistics.add(colStats);
		}
	}

	/**
	 * Returns the specified percentile of the specified list of values.
	 * @param <T> Type of values.
	 * @param col Column index.
	 * @param percentile Value of percentile.
	 * @return Item representing the percentile.
	 */
	protected static <T extends Comparable<T>> T getPercentile(List<T> col, double percentile) {
		int i = (int)(percentile*col.size());
		int index = MathUtils.randomizedSelect(col, 0, col.size() - 1, i);
		return col.get(index);
	}
}

/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.erichseifert.gral.data.DataChangeEvent;
import de.erichseifert.gral.data.DataListener;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.Orientation;
import de.erichseifert.gral.util.SortedList;


/**
 * A class that computes and stores various statistical information
 * on a data source.
 */
public class Statistics implements DataListener {
	/** Key for specifying the total number of elements.
	This is the zeroth central moment: E((x - µ)^0) */
	public static final String N = "n"; //$NON-NLS-1$

	/** Key for specifying the sum of all values. */
	public static final String SUM = "sum"; //$NON-NLS-1$
	/** Key for specifying the sum of all value squares. */
	public static final String SUM2 = "sum2"; //$NON-NLS-1$
	/** Key for specifying the sum of all value cubics. */
	public static final String SUM3 = "sum3"; //$NON-NLS-1$
	/** Key for specifying the sum of all value quads. */
	public static final String SUM4 = "sum4"; //$NON-NLS-1$

	/** Key for specifying the minimum, i.e. the smallest value. */
	public static final String MIN = "min"; //$NON-NLS-1$
	/** Key for specifying the maximum, i.e. the largest value. */
	public static final String MAX = "max"; //$NON-NLS-1$

	/** Key for specifying the arithmetic mean of all values. */
	public static final String MEAN = "mean"; //$NON-NLS-1$
	/** Key for specifying the expected value.
	This is the first central moment: E((x - E(x))^1) */
	public static final String MEAN_DEVIATION = "mean deviation"; //$NON-NLS-1$
	/** Key for specifying the non-normalized variance.
	This is the second central moment: E((x - E(x))^2) */
	public static final String VARIANCE_BASE = "variance base"; //$NON-NLS-1$
	/** Key for specifying the variance of a sample.
	1/(N - 1) * sum of (x[i] - mean)^2 where i=1..N */
	public static final String VARIANCE = "sample variance"; //$NON-NLS-1$
	/** Key for specifying the population variance.
	1/N * sum of (x[i] - mean)^2 where i=1..N */
	public static final String POPULATION_VARIANCE = "population variance"; //$NON-NLS-1$
	/** Key for specifying the non-normalized skewness.
	This is the third central moment: E((x - E(x))^3) */
	public static final String SKEWNESS = "skewness"; //$NON-NLS-1$
	/** Key for specifying the non-normalized kurtosis.
	This is the fourth central moment: E((x - E(x))^4) */
	public static final String KURTOSIS = "kurtosis"; //$NON-NLS-1$

	/** Key for specifying the median (or 50% quantile). */
	public static final String MEDIAN = "quantile50"; //$NON-NLS-1$
	/** Key for specifying the 1st quartile (or 25th quantile). */
	public static final String QUARTILE_1 = "quantile25"; //$NON-NLS-1$
	/** Key for specifying the 2nd quartile (or 50th quantile). */
	public static final String QUARTILE_2 = "quantile50"; //$NON-NLS-1$
	/** Key for specifying the 3rd quartile (or 75th quantile). */
	public static final String QUARTILE_3 = "quantile75"; //$NON-NLS-1$

	/** Data values that are used to build statistical aggregates. */
	private final DataSource data;
	/** Table statistics stored by key. */
	private final Map<String, Double> statistics;
	/** Column statistics stored by key. */
	private final ArrayList<Map<String, Double>> statisticsByCol;
	/** Row statistics stored by key. */
	private final ArrayList<Map<String, Double>> statisticsByRow;

	/**
	 * Initializes a new object with the specified data source.
	 * @param data Data source to be analyzed.
	 */
	public Statistics(DataSource data) {
		statistics = new HashMap<String, Double>();

		statisticsByCol = new ArrayList<Map<String, Double>>(data.getColumnCount());
		for (int col = 0; col < data.getColumnCount(); col++) {
			statisticsByCol.add(new HashMap<String, Double>());
		}

		statisticsByRow = new ArrayList<Map<String, Double>>(data.getRowCount());
		for (int row = 0; row < data.getRowCount(); row++) {
			statisticsByRow.add(new HashMap<String, Double>());
		}

		this.data = data;
		this.data.addDataListener(this);
	}

	/**
	 * Utility method that calculates basic statistics like element count, sum,
	 * or mean.
	 * @param data Data values used to calculate statistics
	 * @param stats A <code>Map</code> that should store the new statistics.
	 */
	private void createBasicStats(Iterable<Number> data, Map<String, Double> stats) {
		stats.put(N, 0.0);
		stats.put(SUM, 0.0);
		stats.put(SUM2, 0.0);
		stats.put(SUM3, 0.0);
		stats.put(SUM4, 0.0);

		for (Number v : data) {
			double val = Double.NaN;
			if (v != null) {
				val = v.doubleValue();
			}
			if (!MathUtils.isCalculatable(val)) {
				continue;
			}

			if (!stats.containsKey(MIN) || val < stats.get(MIN)) {
				stats.put(MIN, val);
			}
			if (!stats.containsKey(MAX) || val > stats.get(MAX)) {
				stats.put(MAX, val);
			}

			double val2 = val*val;
			stats.put(N, stats.get(N) + 1.0);
			stats.put(SUM, stats.get(SUM) + val);
			stats.put(SUM2, stats.get(SUM2) + val2);
			stats.put(SUM3, stats.get(SUM3) + val2*val);
			stats.put(SUM4, stats.get(SUM4) + val2*val2);
		}
	}

	/**
	 * Utility method that calculates statistical values that can be derived
	 * from other statistics.
	 * @param data Data values used to calculate statistics
	 * @param stats A <code>Map</code> that should store the new statistics.
	 */
	private void createDerivedStats(Iterable<Number> data, Map<String, Double> stats) {
		// Calculate mean
		double mean = get(data, stats, SUM) / get(data, stats, N);
		stats.put(MEAN, mean);

		// Calculate statistical moments
		double mean2 = mean*mean;
		// Mean deviation (first moment) for expected uniform distribution is always 0.
		stats.put(MEAN_DEVIATION, 0.0);
		// Variance (second moment)
		stats.put(VARIANCE_BASE, stats.get(SUM2) - mean*stats.get(SUM));
		// Sample variance
		stats.put(VARIANCE, stats.get(VARIANCE_BASE)/(stats.get(N) - 1));
		// Population variance
		stats.put(POPULATION_VARIANCE, stats.get(VARIANCE_BASE)/stats.get(N));
		// Skewness (third moment)
		stats.put(SKEWNESS, stats.get(SUM3)
			- 3.0*mean*stats.get(SUM2) + 2.0*mean2*stats.get(SUM));
		// Kurtosis (fourth moment)
		stats.put(KURTOSIS, stats.get(SUM4)
			- 4.0*mean*stats.get(SUM3) + 6.0*mean2*stats.get(SUM2)
			- 3.0*mean2*mean*stats.get(SUM));
	}

	/**
	 * Utility method that calculates quantiles for the given data values and
	 * stores the result in <code>stats</code>.
	 * @param stats <code>Map</code> for storing result
	 * @see de.erichseifert.gral.util.MathUtils.quantile(List<Double>,double)
	 */
	private void createDistributionStats(Iterable<Number> data, Map<String, Double> stats) {
		// Create sorted list of data
		List<Double> values = new SortedList<Double>();
		for (Number cell : data) {
			double value = cell.doubleValue();
			if (MathUtils.isCalculatable(value)) {
				values.add(value);
			}
		}

		stats.put(QUARTILE_1, MathUtils.quantile(values, 0.25));
		stats.put(QUARTILE_2, MathUtils.quantile(values, 0.50));
		stats.put(QUARTILE_3, MathUtils.quantile(values, 0.75));
		stats.put(MEDIAN, stats.get(QUARTILE_2));
	}

	/**
	 * Returns the specified information for the whole data source.
	 * @param key Requested information.
	 * @return The value for the specified key as  value, or <i>NaN</i>
	 *         if the specified statistical value does not exist
	 */
	public double get(String key) {
		return get(data, statistics, key);
	}

	/**
	 * Returns the specified information for the offset index in the specified
	 * direction.
	 * @param key Requested information.
	 * @param orientation Direction of the values the statistical is built from.
	 * @param index Column or row index.
	 * @return The value for the specified key as  value, or <i>NaN</i>
	 *         if the specified statistical value does not exist
	 */
	public double get(String key, Orientation orientation, int index) {
		Map<String, Double> stats = null;
		Iterable<Number> statsData = null;
		if (orientation == Orientation.VERTICAL) {
			stats = statisticsByCol.get(index);
			statsData = data.getColumn(index);
		} else {
			stats = statisticsByRow.get(index);
			statsData = data.getRow(index);
		}
		return get(statsData, stats, key);
	}

	/**
	 * Returns the specified information for the specified column or row.
	 * If the specified statistical value does not exist <i>NaN</i>
	 * is returned
	 * @param data Data values.
	 * @param stats <code>Map</code> with statistics.
	 * @param key Requested information.
	 * @return The value for the specified key as  value, or <i>NaN</i>
	 *         if the specified statistical value does not exist
	 */
	private double get(Iterable<Number> data, Map<String, Double> stats, String key) {
		if (!stats.containsKey(key)) {
			if (
					(N.equals(key) && !stats.containsKey(N)) ||
					(MIN.equals(key) && !stats.containsKey(MIN)) ||
					(MAX.equals(key) && !stats.containsKey(MAX)) ||
					(SUM.equals(key) && !stats.containsKey(SUM)) ||
					(SUM2.equals(key) && !stats.containsKey(SUM2)) ||
					(SUM3.equals(key) && !stats.containsKey(SUM3)) ||
					(SUM4.equals(key) && !stats.containsKey(SUM4))) {
				createBasicStats(data, stats);
			} else if (
					(MEAN.equals(key) && !stats.containsKey(MEAN)) ||
					(MEAN_DEVIATION.equals(key) && !stats.containsKey(MEAN_DEVIATION)) ||
					(VARIANCE_BASE.equals(key) && !stats.containsKey(VARIANCE_BASE)) ||
					(SKEWNESS.equals(key) && !stats.containsKey(SKEWNESS)) ||
					(KURTOSIS.equals(key) && !stats.containsKey(KURTOSIS))) {
				createDerivedStats(data, stats);
			} if (
					(MEDIAN.equals(key) && !stats.containsKey(MEDIAN)) ||
					(QUARTILE_1.equals(key) && !stats.containsKey(QUARTILE_1)) ||
					(QUARTILE_2.equals(key) && !stats.containsKey(QUARTILE_2)) ||
					(QUARTILE_3.equals(key) && !stats.containsKey(QUARTILE_3))) {
				createDistributionStats(data, stats);
			}
		}

		Double v = stats.get(key);
		return (v != null) ? v : Double.NaN;
	}

	/**
	 * Method that is invoked when data has been added.
	 * This method is invoked by objects that provide support for
	 * <code>DataListener</code>s and should not be called manually.
	 * @param source Data source that has changed
	 * @param events Optional event object describing the data values that
	 *        have been added
	 */
	public void dataAdded(DataSource source, DataChangeEvent... events) {
		for (DataChangeEvent event : events) {
			int col = event.getCol();
			int row = event.getRow();

			// Create new empty entry for column and row statistics
			if (col >= statisticsByCol.size()) {
				statisticsByCol.add(new HashMap<String, Double>());
			}
			if (row >= statisticsByRow.size()) {
				statisticsByRow.add(new HashMap<String, Double>());
			}

			// Mark statistics as invalid
			invalidate(col, row);
		}
	}

	/**
	 * Method that is invoked when data has been updated.
	 * This method is invoked by objects that provide support for
	 * <code>DataListener</code>s and should not be called manually.
	 * @param source Data source that has changed
	 * @param events Optional event object describing the data values that
	 *        have been added
	 */
	public void dataUpdated(DataSource source, DataChangeEvent... events) {
		for (DataChangeEvent event : events) {
			// Mark statistics as invalid
			invalidate(event.getCol(), event.getRow());
		}
	}

	/**
	 * Method that is invoked when data has been added.
	 * This method is invoked by objects that provide support for
	 * <code>DataListener</code>s and should not be called manually.
	 * @param source Data source that has changed
	 * @param events Optional event object describing the data values that
	 *        have been added
	 */
	public void dataRemoved(DataSource source, DataChangeEvent... events) {
		for (DataChangeEvent event : events) {
			// Mark statistics as invalid
			invalidate(event.getCol(), event.getRow());

			// TODO Remove obsolete maps of deleted columns and rows
		}
	}

	/**
	 * Invalidates statistics information for a certain data cell.
	 * @param col Column index of the cell.
	 * @param row Row index of the cell.
	 */
	protected void invalidate(int col, int row) {
		statistics.clear();
		statisticsByCol.get(col).clear();
		statisticsByRow.get(row).clear();
	}
}

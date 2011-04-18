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

import de.erichseifert.gral.data.DataAccessor;
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
	/** Key for specifying the sum of all values. */
	public static final String SUM = "sum"; //$NON-NLS-1$
	/** Key for specifying the sum of all value squares. */
	public static final String SUM2 = "sum2"; //$NON-NLS-1$
	/** Key for specifying the sum of all value cubics. */
	public static final String SUM3 = "sum3"; //$NON-NLS-1$
	/** Key for specifying the sum of all value quads. */
	public static final String SUM4 = "sum4"; //$NON-NLS-1$
	/** Key for specifying the arithmetic mean of all values. */
	public static final String MEAN = "mean"; //$NON-NLS-1$
	/** Key for specifying the minimum, i.e. the smallest value. */
	public static final String MIN = "min"; //$NON-NLS-1$
	/** Key for specifying the maximum, i.e. the largest value. */
	public static final String MAX = "max"; //$NON-NLS-1$
	/** Key for specifying the total number of elements.
	This is the zeroth central moment: E((x - µ)^0) */
	public static final String N = "n"; //$NON-NLS-1$
	/** Key for specifying the expected value.
	This is the first central moment: E((x - E(x))^1) */
	public static final String MEAN_DEVIATION = "mean deviation"; //$NON-NLS-1$
	/** Key for specifying the variance.
	This is the second central moment: E((x - E(x))^2) */
	public static final String VARIANCE = "variance"; //$NON-NLS-1$
	/** Key for specifying the skewness.
	This is the third central moment: E((x - E(x))^3) */
	public static final String SKEWNESS = "skewness"; //$NON-NLS-1$
	/** Key for specifying the kurtosis.
	This is the fourth central moment: E((x - E(x))^4) */
	public static final String KURTOSIS = "kurtosis"; //$NON-NLS-1$
	/** Key for specifying the median (or 50% quantile). */
	public static final String MEDIAN = "median"; //$NON-NLS-1$

	/** Data values that are used to build statistical aggregates. */
	private final DataSource data;
	/** Table statistics stored by key. */
	private final Map<String, Double> statistics;
	/** Column statistics stored by key. */
	private final ArrayList<Map<String, Double>> statisticsByCol;
	/** Row statistics stored by key. */
	private final ArrayList<Map<String, Double>> statisticsByRow;

	/**
	 * Creates a new Statistics object with the specified DataSource.
	 * @param data DataSource to be analyzed.
	 */
	public Statistics(DataSource data) {
		statistics = new HashMap<String, Double>();
		statisticsByCol = new ArrayList<Map<String, Double>>();
		statisticsByRow = new ArrayList<Map<String, Double>>();

		this.data = data;
		rebuild(this.data);
		this.data.addDataListener(this);
	}

	/**
	 * Creates initial statistics entries in the specified <code>Map</code>.
	 * If the passed <code>Map</code> is <code>null</code> a new one will be
	 * created.
	 * @param stats <code>Map</code> to initialize.
	 * @return The initialized <code>Map</code> if it was passed, or a new
	 *         <code>Map</code> object
	 */
	private static Map<String, Double> initStatsMap(Map<String, Double> stats) {
		if (stats == null) {
			stats = new HashMap<String, Double>();
		}
		stats.put(N,    0.0);
		stats.put(SUM,  0.0);
		stats.put(SUM2,  0.0);
		stats.put(SUM3,  0.0);
		stats.put(SUM4,  0.0);
		return stats;
	}

	// FIXME: Split the method up
	/**
	 * Utility method that calculates statistical values that can be derived
	 * from other statistics.
	 * @param stats A <code>Map</code> that should store the new statistics.
	 */
	private static void derivedStatistics(Map<String, Double> stats) {
		double mean = getMean(stats);
		double mean2 = mean*mean;
		// Mean deviation (first moment) for expected uniform distribution is always 0.
		stats.put(MEAN_DEVIATION, 0.0);
		// Variance (second moment)
		stats.put(VARIANCE, stats.get(SUM2) - mean*stats.get(SUM));
		// Skewness (third moment)
		stats.put(SKEWNESS, stats.get(SUM3)
				- 3.0*mean*stats.get(SUM2) + 2.0*mean2*stats.get(SUM));
		// Kurtosis (fourth moment)
		stats.put(KURTOSIS, stats.get(SUM4)
				- 4.0*mean*stats.get(SUM3) + 6.0*mean2*stats.get(SUM2)
				- 3.0*mean2*mean*stats.get(SUM));
	}

	private static double getMean(Map<String, Double> stats) {
		return stats.get(SUM) / stats.get(N);
	}

	/**
	 * Returns the specified information for the specified column or row.
	 * @param key Requested information.
	 * @param orientation Direction of the values the statistical is built from.
	 * @param index Column or row index.
	 * @return Calculated value.
	 */
	public double get(String key, Orientation orientation, int index) {
		ArrayList<Map<String, Double>> statsList = statisticsByCol;
		if (orientation == Orientation.HORIZONTAL) {
			 statsList = statisticsByRow;
		}
		Map<String, Double> stats = statsList.get(index);
		if (key.equals(MEDIAN) && !stats.containsKey(MEDIAN)) {
			double median = getMedian(orientation, index);
			stats.put(MEDIAN, median);
		}
		else {
			return get(stats, key);
		}
		return (stats.containsKey(key)) ? stats.get(key) : Double.NaN;
	}

	private double get(Map<String, Double> stats, String key) {
		if (!stats.containsKey(key)) {
			if (MEDIAN.equals(key)) {
				double median = getMedian();
				stats.put(MEDIAN, median);
			}
			else if (MEAN.equals(key)) {
				double mean = getMean(stats);
				stats.put(MEAN, mean);
			}
			else if (MEAN_DEVIATION.equals(key)) {
				derivedStatistics(stats);
			}
			else if (VARIANCE.equals(key)) {
				derivedStatistics(stats);
			}
			else if (SKEWNESS.equals(key)) {
				derivedStatistics(stats);
			}
			else if (KURTOSIS.equals(key)) {
				derivedStatistics(stats);
			}
		}

		return stats.get(key);
	}

	/**
	 * Returns the specified information for the data source.
	 * @param key Requested information.
	 * @return Calculated value.
	 */
	public double get(String key) {
		return get(statistics, key);
	}

	private void rebuild(DataSource data) {
		statistics.clear();

		int colCount = data.getColumnCount();
		statisticsByCol.clear();
		statisticsByCol.ensureCapacity(colCount);

		int rowCount = data.getRowCount();
		statisticsByRow.clear();
		statisticsByRow.ensureCapacity(rowCount);

		// (Re-)Calculate statistics
		Map<String, Double> statsAll = initStatsMap(statistics);

		for (int colIndex = 0; colIndex < colCount; colIndex++) {
			// Add a map for each column
			Map<String, Double> statsCol;
			if (statisticsByCol.size() <= colIndex) {
				statsCol = initStatsMap(null);
				statisticsByCol.add(statsCol);
			} else {
				statsCol = statisticsByCol.get(colIndex);
			}

			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				// Add a new map for each row or query the existing one
				Map<String, Double> statsRow;
				if (statisticsByRow.size() <= rowIndex) {
					statsRow = initStatsMap(null);
					statisticsByRow.add(statsRow);
				} else {
					statsRow = statisticsByRow.get(rowIndex);
				}

				Number cell = data.get(colIndex, rowIndex);
				double value = (cell != null) ? cell.doubleValue() : Double.NaN;

				updateIncremental(value, statsAll, statsCol, statsRow);
			}
		}
	}

	@Override
	public void dataAdded(DataSource source, DataChangeEvent... events) {
		for (DataChangeEvent event : events) {
			double val = event.getNew().doubleValue();
			int col = event.getCol();
			if (statisticsByCol.size() <= col) {
				statisticsByCol.add(initStatsMap(null));
			}
			int row = event.getRow();
			if (statisticsByRow.size() <= row) {
				statisticsByRow.add(initStatsMap(null));
			}
			updateIncremental(val, statisticsByCol.get(col), statisticsByRow.get(row), statistics);
		}
	}

	private static void updateIncremental(double val, Map<String, Double>... stats) {
		for (Map<String, Double> s : stats) {
			// N (element count, zeroth moment)
			s.put(N, s.get(N) + 1.0);

			if (Double.isNaN(val)) {
				continue;
			}

			// Min
			if (!s.containsKey(MIN) || val < s.get(MIN)) {
				s.put(MIN, val);
			}
			// Max
			if (!s.containsKey(MAX) || val > s.get(MAX)) {
				s.put(MAX, val);
			}
			// Sums
			double val2 = val * val;
			s.put(SUM, s.get(SUM) + val);
			s.put(SUM2, s.get(SUM2) + val2);
			s.put(SUM3, s.get(SUM3) + val2*val);
			s.put(SUM4, s.get(SUM4) + val2*val2);
		}
	}

	@Override
	public void dataUpdated(DataSource source, DataChangeEvent... events) {
		dataAdded(source, events);
		dataRemoved(source, events);
	}

	@Override
	public void dataRemoved(DataSource source, DataChangeEvent... events) {
		for (DataChangeEvent event : events) {
			double val = event.getOld().doubleValue();

			// Update column
			int index = event.getCol();
			if (isRebuildNeeded(statisticsByCol.get(index), val)) {
				rebuild(data);
				break;
			}

			// Update row
			index = event.getRow();
			if (isRebuildNeeded(statisticsByRow.get(index), val)) {
				rebuild(data);
				break;
			}

			// Updating the table is not necessary
		}
	}

	private static boolean isRebuildNeeded(Map<String, Double> statistics, double val) {
		if (val == statistics.get(MIN) || val == statistics.get(MAX)) {
			return true;
		}
		return false;
	}

	private double getMedian(Orientation orientation, int index) {
		DataAccessor accessor = (orientation == Orientation.VERTICAL)
				? data.getColumn(index)
				: data.getRow(index);
		List<Double> values = new SortedList<Double>(accessor.size());
		for (Number cell : accessor) {
			double value = cell.doubleValue();
			if (!Double.isNaN(value)) {
				values.add(value);
			}
		}
		return getMedian(values);
	}

	private double getMedian() {
		int valueCount = data.getColumnCount() * data.getRowCount();
		List<Double> values = new SortedList<Double>(valueCount);
		for (Number cell : data) {
			double value = cell.doubleValue();
			if (!Double.isNaN(value)) {
				values.add(value);
			}
		}
		return getMedian(values);
	}

	private static double getMedian(List<Double> values) {
		int middle = values.size()/2;
		double median = values.get(middle);
		if ((values.size() & 1) == 0) {
			double medianLower = values.get(middle - 1);
			median = (medianLower + median)/2.0;
		}
		return median;
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

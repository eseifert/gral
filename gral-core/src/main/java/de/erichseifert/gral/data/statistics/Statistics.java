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
	/** Key for specifying the sum of squared differences.
	This is identical to the second central moment: E((x - mean)^2) */
	public static final String SUM_OF_DIFF_SQUARES = "M2"; //$NON-NLS-1$
	/** Key for specifying the sum of squared differences.
	This is identical to the third central moment: E((x - mean)^3) */
	public static final String SUM_OF_DIFF_CUBICS = "M3"; //$NON-NLS-1$
	/** Key for specifying the sum of squared differences.
	This is identical to the fourth central moment: E((x - mean)^4) */
	public static final String SUM_OF_DIFF_QUADS = "M4"; //$NON-NLS-1$
	/** Key for specifying the variance of a sample. Formula:
	{@code 1/(N - 1) * sumOfSquares} */
	public static final String VARIANCE = "sample variance"; //$NON-NLS-1$
	/** Key for specifying the population variance. Formula:
	{@code 1/N * sumOfSquares} */
	public static final String POPULATION_VARIANCE = "population variance"; //$NON-NLS-1$
	/** Key for specifying the skewness. */
	public static final String SKEWNESS = "skewness"; //$NON-NLS-1$
	/** Key for specifying the kurtosis. */
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
	 *
	 * Notes: Calculation of higher order statistics is based on formulas from
	 * http://people.xiph.org/~tterribe/notes/homs.html
	 *
	 * @param data Data values used to calculate statistics
	 * @param stats A {@code Map} that should store the new statistics.
	 */
	private void createBasicStats(Iterable<Comparable<?>> data, Map<String, Double> stats) {
		double n = 0.0;
		double sum = 0.0;
		double sum2 = 0.0;
		double sum3 = 0.0;
		double sum4 = 0.0;
		double mean = 0.0;
		double sumOfDiffSquares = 0.0;
		double sumOfDiffCubics = 0.0;
		double sumOfDiffQuads = 0.0;

		for (Comparable<?> cell : data) {
			if (!(cell instanceof Number)) {
				continue;
			}
			Number numericCell = (Number) cell;
			if (!MathUtils.isCalculatable(numericCell)) {
				continue;
			}
			double val = numericCell.doubleValue();

			if (!stats.containsKey(MIN) || val < stats.get(MIN)) {
				stats.put(MIN, val);
			}
			if (!stats.containsKey(MAX) || val > stats.get(MAX)) {
				stats.put(MAX, val);
			}

			n++;

			double val2 = val*val;
			sum += val;
			sum2 += val2;
			sum3 += val2*val;
			sum4 += val2*val2;

			double delta = val - mean;
			double deltaN = delta/n;
			double deltaN2 = deltaN*deltaN;
			double term1 = delta*deltaN*(n - 1.0);
			mean += deltaN;
			sumOfDiffQuads += term1*deltaN2*(n*n - 3.0*n + 3.0) +
				6.0*deltaN2*sumOfDiffSquares - 4.0*deltaN*sumOfDiffCubics;
			sumOfDiffCubics += term1*deltaN*(n - 2.0) -
				3.0*deltaN*sumOfDiffSquares;
			sumOfDiffSquares += term1;
		}

		stats.put(N, n);
		stats.put(SUM,  sum);
		stats.put(SUM2, sum2);
		stats.put(SUM3, sum3);
		stats.put(SUM4, sum4);
		stats.put(MEAN, mean);
		stats.put(SUM_OF_DIFF_QUADS, sumOfDiffQuads);
		stats.put(SUM_OF_DIFF_CUBICS, sumOfDiffCubics);
		stats.put(SUM_OF_DIFF_SQUARES, sumOfDiffSquares);

		stats.put(VARIANCE, sumOfDiffSquares/(n - 1.0));
		stats.put(POPULATION_VARIANCE, sumOfDiffSquares/n);
		stats.put(SKEWNESS,
			(sumOfDiffCubics/n)/Math.pow(sumOfDiffSquares/n, 3.0/2.0) - 3.0);
		stats.put(KURTOSIS,
			(n*sumOfDiffQuads)/(sumOfDiffSquares*sumOfDiffSquares) - 3.0);
	}

	/**
	 * Utility method that calculates quantiles for the given data values and
	 * stores the results in {@code stats}.
	 * @param stats {@code Map} for storing results
	 * @see de.erichseifert.gral.util.MathUtils.quantile(List<Double>,double)
	 */
	private void createDistributionStats(Iterable<Comparable<?>> data, Map<String, Double> stats) {
		// Create sorted list of data
		List<Double> values = new SortedList<Double>();
		for (Comparable<?> cell : data) {
			if (!(cell instanceof Number)) {
				continue;
			}
			Number numericCell = (Number) cell;
			double value = numericCell.doubleValue();
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
		Iterable<Comparable<?>> statsData = null;
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
	 * @param stats {@code Map} with statistics.
	 * @param key Requested information.
	 * @return The value for the specified key as  value, or <i>NaN</i>
	 *         if the specified statistical value does not exist
	 */
	private double get(Iterable<Comparable<?>> data, Map<String, Double> stats, String key) {
		if (!stats.containsKey(key)) {
			if (MEDIAN.equals(key) || QUARTILE_1.equals(key) || QUARTILE_2.equals(key) || QUARTILE_3.equals(key)) {
				createDistributionStats(data, stats);
			} else {
				createBasicStats(data, stats);
			}
		}

		Double v = stats.get(key);
		return (v != null) ? v : Double.NaN;
	}

	/**
	 * Method that is invoked when data has been added.
	 * This method is invoked by objects that provide support for
	 * {@code DataListener}s and should not be called manually.
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
	 * {@code DataListener}s and should not be called manually.
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
	 * {@code DataListener}s and should not be called manually.
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

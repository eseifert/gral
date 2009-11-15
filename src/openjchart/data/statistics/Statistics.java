package openjchart.data.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import openjchart.data.DataListener;
import openjchart.data.DataSource;
import openjchart.util.MathUtils;

/**
 * A class that computes and stores various statistical information
 * on a data source.
 */
public class Statistics implements DataListener {
	/** Sum of all values. */
	public static final String SUM = "sum";
	/** Sum of all value squares. */
	public static final String SUM2 = "sum2";
	/** Sum of all value cubics. */
	public static final String SUM3 = "sum3";
	/** Sum of all value quads. */
	public static final String SUM4 = "sum4";
	/** Arithmetic mean of all values. */
	public static final String MEAN = "mean";
	/** Minimum: Smallest value. */
	public static final String MIN = "min";
	/** Maximum: Largest value. */
	public static final String MAX = "max";
	/** Number of elements. This is the zeroth central moment: E((x - µ)^0) */
	public static final String N = "n";
	/** Expected value. This is the first central moment: E((x - E(x))^1) */
	public static final String MEAN_DEVIATION = "mean deviation";
	/** Variance. This is the second central moment: E((x - E(x))^2) */
	public static final String VARIANCE = "variance";
	/** Skewness. This is the third central moment: E((x - E(x))^3) */
	public static final String SKEWNESS = "skewness";
	/** Kurtosis. This is the fourth central moment: E((x - E(x))^4) */
	public static final String KURTOSIS = "kurtosis";
	/** Median / 50% quantile. */
	public static final String MEDIAN = "median";

	private DataSource data;
	private final ArrayList<Map<String, Double>> statistics;

	public Statistics(DataSource data) {
		statistics = new ArrayList<Map<String, Double>>();

		this.data = data;
		dataChanged(this.data);
		this.data.addDataListener(this);
	}

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
			Double[] col = new Double[rowCount];

			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				Number cell = data.get(colIndex, rowIndex);
				double value = cell.doubleValue();
				double value2 = value*value;
				
				// Store value of column row
				col[rowIndex] = value;

				// Sum
				if (!colStats.containsKey(SUM)) colStats.put(SUM, 0.0);
				colStats.put(SUM, colStats.get(SUM) + value);
				// Sum of value squares
				if (!colStats.containsKey(SUM2)) colStats.put(SUM2, 0.0);
				colStats.put(SUM2, colStats.get(SUM2) + value2);
				// Sum of value cubics
				if (!colStats.containsKey(SUM3)) colStats.put(SUM3, 0.0);
				colStats.put(SUM3, colStats.get(SUM3) + value2*value);
				// Sum of value quads
				if (!colStats.containsKey(SUM4)) colStats.put(SUM4, 0.0);
				colStats.put(SUM4, colStats.get(SUM4) + value2*value2);

				// Minimum
				if (!colStats.containsKey(MIN) || value < colStats.get(MIN)) {
					colStats.put(MIN, value);
				}
				// Maximum
				if (!colStats.containsKey(MAX) || value > colStats.get(MAX)) {
					colStats.put(MAX, value);
				}

				// N (element count, zeroth moment)
				if (!colStats.containsKey(N)) colStats.put(N, 0.0);
				colStats.put(N, colStats.get(N) + 1.0);
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
			int medianIndex = MathUtils.randomizedSelect(col, 0, col.length - 1, col.length/2);
			double median = col[medianIndex];
			if ((rowCount & 1) == 0) {
				double medianUpper = col[medianIndex + 1];
				median = (median + medianUpper)/2.0;
			}
			colStats.put(MEDIAN, median);

			// Add a Map for each column
			statistics.add(colStats);
		}
	}

	protected static <T extends Comparable<T>> T getPercentile(T[] col, double percentile) {
		int i = (int)(percentile*col.length);
		int pos = MathUtils.randomizedSelect(col, 0, col.length - 1, i);
		return col[pos];
	}
}

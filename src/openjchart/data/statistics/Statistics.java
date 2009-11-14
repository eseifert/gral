package openjchart.data.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import openjchart.data.DataListener;
import openjchart.data.DataSource;

/**
 * A class that computes and stores various statistical information
 * on a data source.
 */
public class Statistics implements DataListener {
	public static final String SUM = "sum";
	public static final String MEAN = "mean";
	public static final String MIN = "min";
	public static final String MAX = "max";
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
		statistics.clear();
		
		// Add a Map for each column
		int colCount = data.getColumnCount();
		statistics.ensureCapacity(colCount);
		for (int i = 0; i<colCount; i++) {
			Map<String, Double> colStats = new HashMap<String, Double>();
			statistics.add(colStats);
		}

		// Calculate statistics
		int rowCount = data.getRowCount();
		for (Number[] row : data) {
			for (int colIndex = 0; colIndex < row.length; colIndex++) {
				double value = row[colIndex].doubleValue();
				Map<String, Double> colStats = statistics.get(colIndex);
				// Sum
				if (!colStats.containsKey(SUM)) colStats.put(SUM, 0.0);
				colStats.put(SUM, colStats.get(SUM) + value);
				// Mean
				if (!colStats.containsKey(MEAN)) colStats.put(MEAN, 0.0);
				colStats.put(MEAN, colStats.get(MEAN) + value/rowCount);
				// Minimum
				if (!colStats.containsKey(MIN) || value < colStats.get(MIN)) {
					colStats.put(MIN, value);
				}
				// Maximum
				if (!colStats.containsKey(MAX) || value > colStats.get(MAX)) {
					colStats.put(MAX, value);
				}
				// TODO: (Running) Median
			}
		}
	}

}

package openjchart.data.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import openjchart.data.DataSource;

public class Median extends Filter {
	private int windowSize;

	public Median(DataSource original, int windowSize, int... cols) {
		super(original, cols);
		this.windowSize = windowSize;
		filter();
	}

	@Override
	protected void filter() {
		clear();
		if (getWindowSize() <= 0) {
			return;
		}
		List<List<Double>> colWindows = new ArrayList<List<Double>>(getColumnCount());
		for (int colIndex = 0; colIndex < getColumnCount(); colIndex++) {
			colWindows.add(new ArrayList<Double>(getWindowSize()));
		}
		for (int rowIndex = 0; rowIndex < getRowCount(); rowIndex++) {
			double[] filteredRow = new double[getColumnCount()];
			for (int colIndex = 0; colIndex < filteredRow.length; colIndex++) {
				filteredRow[colIndex] = getOriginal(colIndex, rowIndex).doubleValue();
				if (isFiltered(colIndex)) {
					List<Double> window = colWindows.get(colIndex);
					if (window.size() >= getWindowSize()) {
						window.remove(0);
					}
					window.add(filteredRow[colIndex]);
					filteredRow[colIndex] = median(window);
				}
			}
			add(filteredRow);
		}
	}

	private double median(List<Double> w) {
		List<Double> window = new ArrayList<Double>(w.size());
		window.addAll(w);
		Collections.sort(window);
		int medianIndex = (window.size() - 1)/2; //MathUtils.randomizedSelect(window, 0, window.size() - 1, window.size()/2);
		double median = window.get(medianIndex);
		if ((window.size() & 1) == 0) {
			double medianUpper = window.get(medianIndex + 1);
			median = (median + medianUpper)/2.0;
		}
		return median;
	}

	public int getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
		dataChanged(this);
	}

}

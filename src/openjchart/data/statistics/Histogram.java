package openjchart.data.statistics;

import java.util.*;

import openjchart.data.DataListener;
import openjchart.data.DataSource;

/**
 * <p>View that aggregates the column values of an other data source into
 * a histogram with cells. The cells size can be equally sized by defining
 * a number of cells or breakpoints between histogram cells can be passed
 * as an array to create unequally sized cells.</p>
 * <p>For ease of use the histogram is a data source itself.</p>
 * 
 * @author Erich Seifert
 */
public class Histogram implements DataSource {
	private DataSource data;
	private final List<Number[]> colBreaks;
	private final List<long[]> colCells;

	private final Map<Integer, Long> cacheMin;
	private final Map<Integer, Long> cacheMax;

	private class HistogramIterator implements Iterator<Number[]> {
		private int row;

		@Override
		public boolean hasNext() {
			return row < getRowCount();
		}

		@Override
		public Number[] next() {
			return get(row++);
		}

		@Override
		public void remove() {
		}
	}

	private Histogram(DataSource data) {
		this.data = data;
		colBreaks = new ArrayList<Number[]>(data.getColumnCount());
		colCells = new ArrayList<long[]>(data.getColumnCount());
		cacheMin = new HashMap<Integer, Long>();
		cacheMax = new HashMap<Integer, Long>();
	}

	public Histogram(DataSource data, int cellCount) {
		this(data);
		for (int col = 0; col < this.data.getColumnCount(); col++) {
			double min = this.data.getMin(col).doubleValue();
			double max = this.data.getMax(col).doubleValue();
			double delta = (max - min + Double.MIN_VALUE) / cellCount;

			Number[] breaks = new Double[cellCount + 1];
			for (int i = 0; i < breaks.length; i++) {
				breaks[i] = min + i*delta;
			}
			colBreaks.add(breaks);
		}
		rebuildCells();
	}

	public Histogram(DataSource data, Number[]... breaks) {
		this(data);
		for (Number[] brk : breaks) {
			colBreaks.add(brk);
		}
		rebuildCells();
	}

	protected void rebuildCells() {
		// FIXME: Very naive implementation
		colCells.clear();
		cacheMin.clear();
		cacheMax.clear();

		int rowCount = data.getRowCount();
		int col = 0;
		// Iterate over histogram columns
		for (Number[] brk : colBreaks) {
			long[] cells = new long[brk.length - 1];
			long colMin = Long.MAX_VALUE;
			long colMax = Long.MIN_VALUE;
			// Iterate over data rows
			for (int row = 0; row < rowCount; row++) {
				double val = data.get(col, row).doubleValue();
				// Iterate over histogram rows
				for (int i = 0; i < brk.length - 1; i++) {
					// Put the value into corresponding class
					if (val >= brk[i].doubleValue() && val < brk[i + 1].doubleValue()) {
						cells[i]++;
						if (cells[i] > colMax) {
							colMax = cells[i];
						}
						if (cells[i] < colMin) {
							colMin = cells[i];
						}
						break;
					}
				}
			}
			colCells.add(cells);
			cacheMin.put(col, colMin);
			cacheMax.put(col, colMax);
			col++;
		}
	}

	@Override
	public Number[] get(int row) {
		Number[] rowData = new Number[getColumnCount()];
		for (int col = 0; col < rowData.length; col++) {
			rowData[col] = colCells.get(col)[row];
		}
		return rowData;
	}

	@Override
	public Number get(int col, int row) {
		return colCells.get(col)[row];
	}

	@Override
	public Number getMax(int col) {
		// TODO
		return Double.MAX_VALUE;
	}

	@Override
	public Number getMin(int col) {
		// TODO
		return 0;
	}

	@Override
	public int getRowCount() {
		int rowCount = 0;
		for (long[] cells : colCells) {
			rowCount = Math.max(cells.length, rowCount);
		}
		return rowCount;
	}

	@Override
	public int getColumnCount() {
		return colCells.size();
	}

	@Override
	public Iterator<Number[]> iterator() {
		// TODO
		return null;
	}

	@Override
	public void addDataListener(DataListener dataListener) {
		data.addDataListener(dataListener);
	}

	@Override
	public void removeDataListener(DataListener dataListener) {
		data.removeDataListener(dataListener);
	}

	public Number[] getCellLimits(int col, int cell) {
		Number[] breaks = colBreaks.get(col);
		Number lower = breaks[cell];
		Number upper = breaks[cell+1];
		return new Number[] {lower, upper};
	}
}

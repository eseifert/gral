package openjchart.data.filters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import openjchart.data.AbstractDataSource;
import openjchart.data.DataListener;
import openjchart.data.DataSource;

public abstract class Filter extends AbstractDataSource implements DataListener {
	private final DataSource original;
	private final Set<Integer> cols;
	private final ArrayList<double[]> data;

	public Filter(DataSource original, int... cols) {
		data = new ArrayList<double[]>();
		this.cols = new HashSet<Integer>();
		for (int col: cols) {
			this.cols.add(col);
		}
		this.original = original;
		this.original.addDataListener(this);
		dataChanged(this.original);
	}

	@Override
	public Number[] get(int row) {
		double[] d = data.get(row);
		Double[] n = new Double[d.length];
		for (int i = 0; i < n.length; i++) {
			n[i] = d[i];
		}
		return n;
	}

	protected Number getOriginal(int col, int row) {
		return original.get(col, row);
	}

	protected void clear() {
		data.clear();
	}

	protected void add(double[] row) {
		data.add(row);
	}

	@Override
	public Number get(int col, int row) {
		return get(row)[col];
	}

	@Override
	public int getColumnCount() {
		return original.getColumnCount();
	}

	@Override
	public int getRowCount() {
		return original.getRowCount();
	}

	@Override
	public void dataChanged(DataSource data) {
		filter();
		notifyDataChanged();
	}

	protected boolean isFiltered(int col) {
		return cols.contains(col);
	}

	protected abstract void filter();
}
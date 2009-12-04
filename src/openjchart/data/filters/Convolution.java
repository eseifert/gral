package openjchart.data.filters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import openjchart.data.AbstractDataSource;
import openjchart.data.DataListener;
import openjchart.data.DataSource;
import openjchart.util.MathUtils;

public class Convolution extends AbstractDataSource implements DataListener {
	public static final int MODE_ZERO = 0;
	public static final int MODE_REPEAT = 1;
	public static final int MODE_MIRROR = 2;
	public static final int MODE_CIRCULAR = 3;

	private ArrayList<double[]> data;
	private DataSource original;
	private double[] kernel;
	private int kernelOffset;
	private int mode;
	private final Set<Integer> cols;

	public Convolution(DataSource original, double[] kernel, int mode, int... cols) {
		setKernel(kernel);
		setMode(mode);
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
		return original.get(row);
	}

	protected Number getOriginal(int col, int row) {
		int rowLast = original.getRowCount() - 1;
		if (row<0 || row>rowLast) {
			switch (mode) {
			case MODE_ZERO:
				return 0.0;
			case MODE_REPEAT:
				row = MathUtils.limit(row, 0, rowLast);
				break;
			case MODE_MIRROR:
				int rem = Math.abs(row) / rowLast;
				int mod = Math.abs(row) % rowLast;
				if ((rem & 1) == 0) {
					row = mod;
				} else {
					row = rowLast - mod;
				}
				break;
			case MODE_CIRCULAR:
				row %= (rowLast + 1);
				break;
			}
		}
		return original.get(col, row);
	}

	@Override
	public Number get(int col, int row) {
		return data.get(row)[col];
	}

	@Override
	public int getColumnCount() {
		return original.getColumnCount();
	}

	@Override
	public int getRowCount() {
		return original.getRowCount();
	}

	public void setKernel(double[] kernel) {
		this.kernel = new double[kernel.length];
		System.arraycopy(kernel, 0, this.kernel, 0, kernel.length);
		kernelOffset = kernel.length/2;
		filter();
	}

	public double[] getKernel() {
		return kernel;
	}

	public void setMode(int mode) {
		this.mode = mode;
		filter();
	}

	public int getMode() {
		return mode;
	}

	private void filter() {
		if (original == null) {
			return;
		}
		data = new ArrayList<double[]>(original.getRowCount());
		for (int rowIndex = 0; rowIndex < original.getRowCount(); rowIndex++) {
			double[] filteredRow = new double[original.getColumnCount()];
			for (int colIndex = 0; colIndex < filteredRow.length; colIndex++) {
				if (cols.contains(colIndex)) {
					filteredRow[colIndex] = convolve(colIndex, rowIndex);
				} else {
					filteredRow[colIndex] = original.get(colIndex, rowIndex).doubleValue();
				}
			}
			data.add(filteredRow);
		}
	}

	private double convolve(int col, int row) {
		double sum = 0.0;
		for (int k = 0; k < kernel.length; k++) {
			int r = row - kernelOffset + k;
			sum += kernel[k] * getOriginal(col, r).doubleValue();
		}
		return sum;
	}

	@Override
	public void dataChanged(DataSource data) {
		filter();
		notifyDataChanged();
	}

}

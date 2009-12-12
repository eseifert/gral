package openjchart.data.filters;

import openjchart.data.DataSource;
import openjchart.util.MathUtils;

public class Convolution extends Filter {
	public static enum Mode { MODE_OMIT, MODE_ZERO, MODE_REPEAT, MODE_MIRROR, MODE_CIRCULAR };

	private Kernel kernel;
	private Mode mode;
	
	public Convolution(DataSource original, Kernel kernel, Mode mode, int... cols) {
		super(original, cols);
		this.kernel = kernel;
		this.mode = mode;
		filter();
	}

	protected Number getOriginal(int col, int row) {
		int rowLast = getRowCount() - 1;
		if (row<0 || row>rowLast) {
			if (Mode.MODE_OMIT.equals(mode)) {
				return Double.NaN;
			} else if (Mode.MODE_ZERO.equals(mode)) {
				return 0.0;
			} else if (Mode.MODE_REPEAT.equals(mode)) {
				row = MathUtils.limit(row, 0, rowLast);
			} else if (Mode.MODE_MIRROR.equals(mode)) {
				int rem = Math.abs(row) / rowLast;
				int mod = Math.abs(row) % rowLast;
				if ((rem & 1) == 0) {
					row = mod;
				} else {
					row = rowLast - mod;
				}
			} else if (Mode.MODE_CIRCULAR.equals(mode)) {
				row %= (rowLast + 1);
			}
		}
		return super.getOriginal(col, row);
	}

	protected final void setKernel(Kernel kernel) {
		this.kernel = kernel;
		dataChanged(this);
	}

	public Kernel getKernel() {
		return kernel;
	}

	protected final void setMode(Mode mode) {
		this.mode = mode;
		dataChanged(this);
	}

	public Mode getMode() {
		return mode;
	}

	protected void filter() {
		clear();
		for (int rowIndex = 0; rowIndex < getRowCount(); rowIndex++) {
			double[] filteredRow = new double[getColumnCount()];
			for (int colIndex = 0; colIndex < filteredRow.length; colIndex++) {
				if (isFiltered(colIndex)) {
					filteredRow[colIndex] = convolve(colIndex, rowIndex);
				} else {
					filteredRow[colIndex] = getOriginal(colIndex, rowIndex).doubleValue();
				}
			}
			add(filteredRow);
		}
	}

	private double convolve(int col, int row) {
		if (kernel == null) {
			return getOriginal(col, row).doubleValue();
		}
		double sum = 0.0;
		for (int k = kernel.getMinIndex(); k <= kernel.getMaxIndex(); k++) {
			int r = row + k;
			double v = getOriginal(col, r).doubleValue();
			if (Double.isNaN(v) || Double.isInfinite(v)) {
				return v;
			}
			sum += kernel.get(k) * v;
		}
		return sum;
	}

}

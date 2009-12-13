package openjchart.data.filters;

import openjchart.data.DataSource;

public class Convolution extends Filter {
	private Kernel kernel;
	private Mode mode;
	
	public Convolution(DataSource original, Kernel kernel, Mode mode, int... cols) {
		super(original, mode, cols);
		this.kernel = kernel;
		filter();
	}

	public Kernel getKernel() {
		return kernel;
	}
	
	protected final void setKernel(Kernel kernel) {
		this.kernel = kernel;
		dataChanged(this);
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

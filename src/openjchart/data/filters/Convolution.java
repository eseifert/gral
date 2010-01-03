/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.data.filters;

import openjchart.data.DataSource;

/**
 * Class that applies a convolution filter with the use of a Kernel.
 * Functionality includes:
 * <ul>
 * <li>Getting and setting the Kernel used for convolution</li>
 * </ul>
 */
public class Convolution extends Filter {
	private Kernel kernel;

	/**
	 * Creates a new Convolution object with the specified DataSource,
	 * Kernel, Mode and columns.
	 * @param original DataSource to be filtered.
	 * @param kernel Kernel to be used.
	 * @param mode Mode of filtering.
	 * @param cols Column indexes.
	 */
	public Convolution(DataSource original, Kernel kernel, Mode mode, int... cols) {
		super(original, mode, cols);
		this.kernel = kernel;
		filter();
	}

	/**
	 * Returns the Kernel.
	 * @return Kernel used for convolution.
	 */
	public Kernel getKernel() {
		return kernel;
	}

	/**
	 * Sets the Kernel to the specified value.
	 * @param kernel Kernel to be set.
	 */
	protected final void setKernel(Kernel kernel) {
		this.kernel = kernel;
		dataChanged(this);
	}

	@Override
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

	/**
	 * Calculates the convolved value of the data with the specified column
	 * and row.
	 * @param col Column index.
	 * @param row Row index.
	 * @return Convolved value using the set kernel.
	 */
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

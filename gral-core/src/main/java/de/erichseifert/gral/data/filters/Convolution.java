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
package de.erichseifert.gral.data.filters;

import de.erichseifert.gral.data.DataSource;

/**
 * <p>Class that applies a specified kernel to a data source to convolve it.</p>
 * <p>Functionality includes:</p>
 * <ul>
 *   <li>Getting and setting the <code>Kernel</code> used for convolution</li>
 * </ul>
 */
public class Convolution extends Filter {
	/** Kernel that provides the values to convolve the data source. */
	private final Kernel kernel;

	/**
	 * Creates a new <code>Convolution</code> object with the specified DataSource,
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
	 * Returns the kernel.
	 * @return Kernel used for convolution.
	 */
	public Kernel getKernel() {
		return kernel;
	}

	@Override
	protected void filter() {
		clear();
		for (int rowIndex = 0; rowIndex < getRowCount(); rowIndex++) {
			Double[] filteredRow = new Double[getColumnCountFiltered()];
			for (int colIndex = 0; colIndex < filteredRow.length; colIndex++) {
				int colIndexOriginal = getIndexOriginal(colIndex);
				filteredRow[colIndex] = convolve(colIndexOriginal, rowIndex);
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
			Number original = getOriginal(col, row);
			return (original != null) ? original.doubleValue() : Double.NaN;
		}
		double sum = 0.0;
		for (int k = kernel.getMinIndex(); k <= kernel.getMaxIndex(); k++) {
			int r = row + k;
			Number original = getOriginal(col, r);
			double v = (original != null) ? original.doubleValue() : Double.NaN;
			if (Double.isNaN(v) || Double.isInfinite(v)) {
				return v;
			}
			sum += kernel.get(k) * v;
		}
		return sum;
	}

}

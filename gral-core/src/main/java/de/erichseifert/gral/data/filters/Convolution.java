/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <mseifert[at]error-reports.org>
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

import java.io.IOException;
import java.io.ObjectInputStream;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.util.DataUtils;
import de.erichseifert.gral.util.MathUtils;

/**
 * <p>Class that applies a specified kernel to a data source to convolve it.</p>
 * <p>Functionality includes:</p>
 * <ul>
 *   <li>Getting and setting the {@code Kernel} used for convolution</li>
 * </ul>
 */
public class Convolution extends Filter2D {
	/** Version id for serialization. */
	private static final long serialVersionUID = 7155205321415314271L;

	/** Kernel that provides the values to convolve the data source. */
	private final Kernel kernel;

	/**
	 * Initialized a new instance with the specified data source, convolution
	 * kernel, edge handling mode, and columns to be filtered.
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
		Kernel kernel = getKernel();
		if (kernel == null) {
			Comparable<?> original = getOriginal(col, row);
			return DataUtils.getValueOrDefault((Number) original, Double.NaN);
		}
		double sum = 0.0;
		for (int k = kernel.getMinIndex(); k <= kernel.getMaxIndex(); k++) {
			int r = row + k;
			Comparable<?> original = getOriginal(col, r);
			double v = DataUtils.getValueOrDefault((Number) original, Double.NaN);
			if (!MathUtils.isCalculatable(v)) {
				return v;
			}
			sum += kernel.get(k) * v;
		}
		return sum;
	}

	/**
	 * Custom deserialization method.
	 * @param in Input stream.
	 * @throws ClassNotFoundException if a serialized class doesn't exist anymore.
	 * @throws IOException if there is an error while reading data from the
	 *         input stream.
	 */
	private void readObject(ObjectInputStream in)
			throws ClassNotFoundException, IOException {
		// Normal deserialization
		in.defaultReadObject();

		// Update caches
		dataUpdated(this);
	}
}

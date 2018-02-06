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
import java.util.ArrayList;
import java.util.List;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.util.MathUtils;


/**
 * <p>Class that calculates the median of a data sequence.</p>
 * <ul>
 *   <li>Setting and getting offset</li>
 *   <li>Setting and getting window size</li>
 * </ul>
 */
public class Median extends Filter2D {
	/** Version id for serialization. */
	private static final long serialVersionUID = -1645928908580026536L;

	/** Number of values in the window that will be used to calculate the
	median. */
	private int windowSize;
	/** Start of the window. */
	private int offset;

	/**
	 * Creates a new Median object with the specified DataSource, window
	 * size, offset, Mode, and columns.
	 * @param original DataSource to be filtered.
	 * @param windowSize Number of rows to be used for the calculation of the
	 *        median.
	 * @param offset Offset from the current filtered value to the last value
	 *        of the window.
	 * @param mode Mode of filtering.
	 * @param cols Column indexes.
	 */
	public Median(DataSource original, int windowSize, int offset,
			Mode mode, int... cols) {
		super(original, mode, cols);
		this.windowSize = windowSize;
		this.offset = offset;
		filter();
	}

	@Override
	protected void filter() {
		clear();
		if (getWindowSize() <= 0) {
			return;
		}
		List<List<Double>> colWindows =
				new ArrayList<>(getColumnCount());
		for (int colIndex = 0; colIndex < getColumnCountFiltered(); colIndex++) {
			int colIndexOriginal = getIndexOriginal(colIndex);
			List<Double> window = new ArrayList<>(getWindowSize());
			colWindows.add(window);
			// Pre-fill window
			for (int rowIndex = getOffset() - getWindowSize(); rowIndex < 0; rowIndex++) {
				Comparable<?> vOrig = getOriginal(colIndexOriginal, rowIndex);
				double v = ((Number) vOrig).doubleValue();
				window.add(v);
			}
		}
		for (int rowIndex = 0; rowIndex < getRowCount(); rowIndex++) {
			Double[] filteredRow = new Double[getColumnCountFiltered()];
			for (int colIndex = 0; colIndex < filteredRow.length; colIndex++) {
				List<Double> window = colWindows.get(colIndex);
				if (window.size() >= getWindowSize()) {
					window.remove(0);
				}
				int colIndexOriginal = getIndexOriginal(colIndex);
				Comparable<?> vOrig = getOriginal(colIndexOriginal,
						rowIndex - getOffset() + getWindowSize());
				double v = ((Number) vOrig).doubleValue();
				window.add(v);
				filteredRow[colIndex] = median(window);
			}
			add(filteredRow);
		}
	}

	/**
	 * Calculates the median for the specified values in the window.
	 * @param w List of values the median will be calculated for.
	 * @return Median.
	 */
	private double median(List<Double> w) {
		if (w.size() == 1) {
			return w.get(0);
		}
		List<Double> window = new ArrayList<>(w.size());
		for (Double v : w) {
			if (!MathUtils.isCalculatable(v)) {
				return Double.NaN;
			}
			window.add(v);
		}
		int medianIndex = MathUtils.randomizedSelect(
				window, 0, window.size() - 1, window.size()/2);
		double median = window.get(medianIndex);
		if ((window.size() & 1) == 0) {
			int medianUpperIndex = MathUtils.randomizedSelect(
					window, 0, window.size() - 1, window.size()/2 + 1);
			double medianUpper = window.get(medianUpperIndex);
			median = (median + medianUpper)/2.0;
		}
		return median;
	}

	/**
	 * Returns the size of the window which is used to calculate the median.
	 * @return Number of rows used.
	 */
	public int getWindowSize() {
		return windowSize;
	}

	/**
	 * Set the size of the window which is used to calculate the median.
	 * @param windowSize Number of rows used.
	 */
	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
		dataUpdated(this);
	}

	/**
	 * Returns the offset from the current value used to calculate the
	 * median to the last value of the window.
	 * @return Offset.
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Sets the offset from the current value used to calculate the
	 * median to the last value of the window.
	 * @param offset Offset.
	 */
	public void setOffset(int offset) {
		this.offset = offset;
		dataUpdated(this);
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

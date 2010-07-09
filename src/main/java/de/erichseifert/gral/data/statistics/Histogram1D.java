/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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

package de.erichseifert.gral.data.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.erichseifert.gral.data.DataSource;


/**
 * <p>View that aggregates the column values of an other data source into
 * a histogram with cells. The cells size can be equally sized by defining
 * a number of cells or breakpoints between histogram cells can be passed
 * as an array to create unequally sized cells.</p>
 * <p>For ease of use the histogram is a data source itself.</p>
 */
public class Histogram1D extends Histogram {
	/**
	 * Data type that describes the direction of the histogram.
	 */
	public static enum Orientation {
		/** Horizontal histogram. */
		HORIZONTAL,
		/** Vertical histogram. */
		VERTICAL
	}
	
	private final Orientation orientation;

	private final List<Number[]> breaks;
	private final List<long[]> cells;

	private final Map<Integer, Long> cacheMin;
	private final Map<Integer, Long> cacheMax;

	private Histogram1D(DataSource data, Orientation orientation) {
		super(data);
		this.orientation = orientation;
		breaks = new ArrayList<Number[]>();
		cells = new ArrayList<long[]>();
		cacheMin = new HashMap<Integer, Long>();
		cacheMax = new HashMap<Integer, Long>();
	}

	/**
	 * Creates a new Histogram object with the specified DataSource and
	 * cell count.
	 * @param data DataSource so be analyzed.
	 * @param orientation Orientation of the histogram values.
	 * @param cellCount Number of subdivisions for analysis.
	 */
	public Histogram1D(DataSource data, Orientation orientation, int cellCount) {
		this(data, orientation);
		Statistics stats = getData().getStatistics();
		for (int col = 0; col < getData().getColumnCount(); col++) {
			double min = stats.get(Statistics.MIN, col);
			double max = stats.get(Statistics.MAX, col);
			double delta = (max - min + Double.MIN_VALUE) / cellCount;

			Number[] breaks = new Double[cellCount + 1];
			for (int i = 0; i < breaks.length; i++) {
				breaks[i] = min + i*delta;
			}
			this.breaks.add(breaks);
		}
		dataChanged(getData());
	}

	/**
	 * Creates a new Histogram object with the specified DataSource and
	 * subdivisions at the specified positions.
	 * @param data DataSource to be analyzed.
	 * @param orientation Orientation of the histogram values.
	 * @param breaks Values of where a subdivision should occur.
	 */
	public Histogram1D(DataSource data, Orientation orientation, Number[]... breaks) {
		this(data, orientation);
		for (Number[] brk : breaks) {
			this.breaks.add(brk);
		}
		dataChanged(getData());
	}

	/**
	 * Repopulates the cells of this Histogram.
	 */
	protected void rebuildCells() {
		// FIXME: Very naive implementation
		cells.clear();
		cacheMin.clear();
		cacheMax.clear();

		int rowCount = getData().getRowCount();
		int col = 0;
		// Iterate over histogram columns
		for (Number[] brk : breaks) {
			long[] cells = new long[brk.length - 1];
			long colMin = Long.MAX_VALUE;
			long colMax = Long.MIN_VALUE;
			// Iterate over data rows
			for (int row = 0; row < rowCount; row++) {
				double val = getData().get(col, row).doubleValue();
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
			this.cells.add(cells);
			cacheMin.put(col, colMin);
			cacheMax.put(col, colMax);
			col++;
		}
	}

	/**
	 * Returns the minimum and maximum value of the specified cell.
	 * @param col Column index.
	 * @param cell Cell index.
	 * @return Extent of the cell.
	 */
	public Number[] getCellLimits(int col, int cell) {
		Number[] breaks = this.breaks.get(col);
		Number lower = breaks[cell];
		Number upper = breaks[cell+1];
		return new Number[] {lower, upper};
	}

	@Override
	public Number get(int col, int row) {
		return cells.get(col)[row];
	}

	@Override
	public int getRowCount() {
		int rowCount = 0;
		for (long[] cells : this.cells) {
			rowCount = Math.max(cells.length, rowCount);
		}
		return rowCount;
	}

	@Override
	public int getColumnCount() {
		return cells.size();
	}

}

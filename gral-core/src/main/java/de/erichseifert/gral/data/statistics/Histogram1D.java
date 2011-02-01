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
package de.erichseifert.gral.data.statistics;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.erichseifert.gral.data.DataAccessor;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.util.Orientation;


/**
 * <p>View that aggregates the column values of an other data source into
 * a histogram with cells. The cells size can be equally sized by defining
 * a number of cells or breakpoints between histogram cells can be passed
 * as an array to create unequally sized cells.</p>
 * <p>For ease of use the histogram is a data source itself.</p>
 */
public class Histogram1D extends Histogram {
	/** Direction in which all values will be aggregated. */
	private final Orientation orientation;

	/** Intervals that will be used for aggregation. */
	private final List<Number[]> breaks;
	/** Bin cells that store all aggregation counts. */
	private final List<long[]> cells;

	/** Minimum values for cells. */
	private final Map<Integer, Long> cacheMin;
	/** Maximum values for cells. */
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
	 * @param breakCount Number of subdivisions for analysis.
	 */
	public Histogram1D(DataSource data, Orientation orientation,
			int breakCount) {
		this(data, orientation);

		// Create equally spaced breaks
		int count = (orientation == Orientation.VERTICAL)
				? getData().getColumnCount() : getData().getRowCount();
		Statistics stats = getData().getStatistics();
		for (int index = 0; index < count; index++) {
			double min = stats.get(Statistics.MIN, orientation, index);
			double max = stats.get(Statistics.MAX, orientation, index);
			double delta = (max - min + Double.MIN_VALUE) / breakCount;

			Number[] breaks = new Double[breakCount + 1];
			for (int i = 0; i < breaks.length; i++) {
				breaks[i] = min + i*delta;
			}
			this.breaks.add(breaks);
		}
		dataChanged(getData());
	}

	/**
	 * Initializes a new histogram with the specified data source and
	 * subdivisions at the specified positions.
	 * @param data Data source to be analyzed.
	 * @param orientation Orientation in which the data should be sampled.
	 * @param breaks Values of where a subdivision should occur.
	 */
	public Histogram1D(DataSource data, Orientation orientation,
			Number[]... breaks) {
		this(data, orientation);
		int count = (orientation == Orientation.VERTICAL)
				? getData().getColumnCount() : getData().getRowCount();
		if (breaks.length != count) {
			throw new IllegalArgumentException(MessageFormat.format(
				"Invalid number of breaks: got {0,number,integer}, expected {1,number,integer}.", //$NON-NLS-1$
				breaks.length, count));
		}
		for (Number[] brk : breaks) {
			this.breaks.add(brk);
		}
		dataChanged(getData());
	}

	/**
	 * (Re-)populates the cells of this Histogram.
	 */
	@Override
	protected void rebuildCells() {
		// FIXME Very naive implementation
		cells.clear();
		cacheMin.clear();
		cacheMax.clear();

		// Iterate over histogram data sets
		int breakIndex = 0;
		for (Number[] brk : breaks) {
			long[] cells = new long[brk.length - 1];
			long colMin = Long.MAX_VALUE;
			long colMax = Long.MIN_VALUE;
			DataAccessor data = (orientation == Orientation.VERTICAL)
					? getData().getColumn(breakIndex) : getData().getRow(breakIndex);
			// Iterate over data cells
			for (Number value : data) {
				double val = value.doubleValue();
				// Iterate over histogram rows
				for (int i = 0; i < brk.length - 1; i++) {
					// Put the value into corresponding class
					if ((val >= brk[i].doubleValue())
							&& (val < brk[i + 1].doubleValue())) {
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
			cacheMin.put(breakIndex, colMin);
			cacheMax.put(breakIndex, colMax);
			breakIndex++;
		}
	}

	/**
	 * Returns the direction in which the histogram values will be accumulated.
	 * @return Horizontal or vertical orientation.
	 */
	public Orientation getOrientation() {
		return orientation;
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
		Number upper = breaks[cell + 1];
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

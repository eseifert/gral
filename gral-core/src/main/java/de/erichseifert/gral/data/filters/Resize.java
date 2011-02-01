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

import java.util.Arrays;

import de.erichseifert.gral.data.Column;
import de.erichseifert.gral.data.DataAccessor;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.Row;

/**
 * Filter to change the size of equally spaced data sources.
 * The values of the scaled result are created using averaging.
 */
public class Resize extends Filter {
	/** Number of columns. */
	private final int cols;
	/** Number of rows. */
	private final int rows;

	/**
	 * Initializes a new data source from an original data source and a
	 * specified number of rows and columns.
	 * @param data Original data source.
	 * @param cols Number of columns for new data source.
	 * @param rows Number of rows for new data source.
	 */
	public Resize(DataSource data, int cols, int rows) {
		super(data, Mode.ZERO);
		this.cols = cols;
		this.rows = rows;
		filter();
	}

	@Override
	public int getColumnCount() {
		if (cols <= 0) {
			return super.getColumnCount();
		}
		return cols;
	}

	@Override
	public int getRowCount() {
		if (rows <= 0) {
			return super.getRowCount();
		}
		return rows;
	}

	@Override
	public Number get(int col, int row) {
		if ((cols <= 0 || cols == original.getColumnCount()) &&
			(rows <= 0 || rows == original.getRowCount())) {
			return getOriginal(col, row);
		}
		return super.get(col, row);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void filter() {
		clear();
		if ((getRowCount() == original.getRowCount())
				&& (getColumnCount() == original.getColumnCount())) {
			return;
		}

		DataSource data = original;
		if (getRowCount() != original.getRowCount()) {
			Class[] dataTypes = new Class[original.getColumnCount()];
			Arrays.fill(dataTypes, Double.class);
			DataTable avgRows = new DataTable(dataTypes);
			fillWithEmptyRows(avgRows, getRowCount());

			double step = original.getRowCount() / (double) getRowCount();
			for (int colIndex = 0; colIndex < original.getColumnCount(); colIndex++) {
				Column colData = original.getColumn(colIndex);
				for (int rowIndex = 0; rowIndex < getRowCount(); rowIndex++) {
					double start = rowIndex*step;
					double end   = (rowIndex + 1)*step;
					avgRows.set(colIndex, rowIndex,
							average(colData, start, end));
				}
			}
			data = avgRows;
		}
		if (getColumnCount() != original.getColumnCount()) {
			Class[] dataTypes = new Class[getColumnCount()];
			Arrays.fill(dataTypes, Double.class);
			DataTable avgCols = new DataTable(dataTypes);
			fillWithEmptyRows(avgCols, data.getRowCount());

			double step = original.getColumnCount() / (double) getColumnCount();
			for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
				Row rowData = data.getRow(rowIndex);
				for (int colIndex = 0; colIndex < getColumnCount(); colIndex++) {
					double start = colIndex*step;
					double end   = (colIndex + 1)*step;
					avgCols.set(colIndex, rowIndex,
							average(rowData, start, end));
				}
			}
			data = avgCols;
		}

		for (int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++) {
			Row row = data.getRow(rowIndex);
			add(row.toArray(null));
		}
	}

	/**
	 * Utility method that fills a data table with empty rows.
	 * @param data Data table that should be filled.
	 * @param count Number of rows that were added.
	 */
	private static void fillWithEmptyRows(DataTable data, int count) {
		while (data.getRowCount() < count) {
			Double[] emptyRow = new Double[data.getColumnCount()];
			Arrays.fill(emptyRow, 0.0);
			data.add(emptyRow);
		}
	}

	/**
	 * Calculates the arithmetic mean of all values between start and end.
	 * @param data Values.
	 * @param start Start index.
	 * @param end End index.
	 * @return Arithmetic mean.
	 */
	private static double average(DataAccessor data, double start, double end) {
		int startFloor = (int) Math.floor(start);
		int startCeil  = (int) Math.ceil(start);
		int endFloor = (int) Math.floor(end);
		int endCeil = (int) Math.ceil(end);

		double sum = 0.0;
		for (int i = startFloor; i < endCeil; i++) {
			double val = data.get(i).doubleValue();
			if (i == startFloor && startCeil != start) {
				sum += (startCeil - start) * val;
			} else if (i == endCeil - 1 && endFloor != end) {
				sum += (end - endFloor) * val;
			} else {
				sum += val;
			}
		}
		return sum / (end - start);
	}

}

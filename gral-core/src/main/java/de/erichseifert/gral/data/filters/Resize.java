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
import java.util.Arrays;
import java.util.Iterator;

import de.erichseifert.gral.data.Column;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.Record;

/**
 * Filter2D to change the size of equally spaced data sources. All columns of the
 * data sources must be numeric, otherwise an {@code IllegalArgumentException}
 * will be thrown. The values of the scaled result are created by averaging.
 */
public class Resize extends Filter2D {
	/** Version id for serialization. */
	private static final long serialVersionUID = -5601162872352170735L;

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
	public Comparable<?> get(int col, int row) {
		if ((cols <= 0 || cols == getOriginal().getColumnCount()) &&
			(rows <= 0 || rows == getOriginal().getRowCount())) {
			return getOriginal(col, row);
		}
		return super.get(col, row);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void filter() {
		clear();
		DataSource original = getOriginal();
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
				Record rowData = data.getRecord(rowIndex);
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
			Record row = data.getRecord(rowIndex);
			Double[] rowValues = new Double[row.size()];
			for (int columnIndex = 0; columnIndex < rowValues.length; columnIndex++) {
				rowValues[columnIndex] = row.get(columnIndex);
			}
			add(rowValues);
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

	private static <T> Iterator<T> advance(Iterator<T> iterator, int elementCount) {
		for (int elementIndex = 0; elementIndex < elementCount; elementIndex++) {
			iterator.next();
		}
		return iterator;
	}

	/**
	 * Calculates the arithmetic mean of all values between start and end.
	 * @param data Values.
	 * @param start Start index.
	 * @param end End index.
	 * @return Arithmetic mean.
	 */
	private static double average(Iterable<? extends Comparable<?>> data, double start, double end) {
		int startFloor = (int) Math.floor(start);
		int startCeil  = (int) Math.ceil(start);
		int endFloor = (int) Math.floor(end);
		int endCeil = (int) Math.ceil(end);

		double sum = 0.0;
		Iterator<? extends Comparable<?>> dataIterator = data.iterator();
		advance(dataIterator, startFloor);
		for (int i = startFloor; i < endCeil; i++) {
			Number number = (Number) dataIterator.next();
			double val = number.doubleValue();
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

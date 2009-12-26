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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import openjchart.data.AbstractDataSource;
import openjchart.data.DataListener;
import openjchart.data.DataSource;
import openjchart.util.MathUtils;

public abstract class Filter extends AbstractDataSource implements DataListener {
	public static enum Mode { MODE_OMIT, MODE_ZERO, MODE_REPEAT, MODE_MIRROR, MODE_CIRCULAR };

	private final DataSource original;
	private final Set<Integer> cols;
	private final ArrayList<double[]> data;
	private Mode mode;

	public Filter(DataSource original, Mode mode, int... cols) {
		this.data = new ArrayList<double[]>();
		this.original = original;
		this.mode = mode;
		this.cols = new HashSet<Integer>();

		for (int col: cols) {
			this.cols.add(col);
		}

		this.original.addDataListener(this);
		dataChanged(this.original);
	}

	@Override
	public Number[] get(int row) {
		double[] d = data.get(row);
		Double[] n = new Double[d.length];
		for (int i = 0; i < n.length; i++) {
			n[i] = d[i];
		}
		return n;
	}

	protected Number getOriginal(int col, int row) {
		int rowLast = getRowCount() - 1;
		if (row < 0 || row > rowLast) {
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
		return original.get(col, row);
	}

	protected void clear() {
		data.clear();
	}

	protected void add(double[] row) {
		data.add(row);
	}

	@Override
	public Number get(int col, int row) {
		return get(row)[col];
	}

	@Override
	public int getColumnCount() {
		return original.getColumnCount();
	}

	@Override
	public int getRowCount() {
		return original.getRowCount();
	}

	@Override
	public void dataChanged(DataSource data) {
		filter();
		notifyDataChanged();
	}

	protected boolean isFiltered(int col) {
		return cols.contains(col);
	}

	protected abstract void filter();

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
		dataChanged(this);
	}
}
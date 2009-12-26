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

package openjchart.data;

import java.util.ArrayList;
import java.util.List;


public abstract class DataSubset extends AbstractDataSource implements DataListener {
	private final DataSource original;
	private final List<Integer> accepted;

	public DataSubset(DataSource original) {
		accepted = new ArrayList<Integer>();
		this.original = original;
		this.original.addDataListener(this);
		dataChanged(this.original);
	}

	@Override
	public Number[] get(int row) {
		int rowOrig = accepted.get(row);
		return original.get(rowOrig);
	}

	@Override
	public Number get(int col, int row) {
		int rowOrig = accepted.get(row);
		return original.get(col, rowOrig);
	}

	@Override
	public int getColumnCount() {
		return original.getColumnCount();
	}

	@Override
	public int getRowCount() {
		return accepted.size();
	}

	@Override
	public void dataChanged(DataSource data) {
		accepted.clear();
		int rowIndex = 0;
		for (Number[] row : original) {
			if (accept(row)) {
				accepted.add(rowIndex);
			}
			rowIndex++;
		}
		notifyDataChanged();
	}

	public abstract boolean accept(Number[] row);
}

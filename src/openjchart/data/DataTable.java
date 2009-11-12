package openjchart.data;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import openjchart.data.comparators.DataComparator;


public class DataTable implements DataSource {
	private final ArrayList<Number[]> data;
	private Class<?>[] types;

	private final Map<Integer, Double> cacheMin;
	private final Map<Integer, Double> cacheMax;

	private final Set<DataListener> dataListeners;

	private class DataTableIterator implements Iterator<Number[]> {
		private int row;

		@Override
		public boolean hasNext() {
			return row < getRowCount();
		}

		@Override
		public Number[] next() {
			return get(row++);
		}

		@Override
		public void remove() {
		}
	}

	public DataTable(Class<? extends Number>... types) {
		this.types = new Class[types.length];
		System.arraycopy(types, 0, this.types, 0, types.length);
		data = new ArrayList<Number[]>();
		dataListeners = new HashSet<DataListener>();
		cacheMin = new HashMap<Integer, Double>();
		cacheMax = new HashMap<Integer, Double>();
	}

	/**
	 * Adds a row with the specified values to the table.
	 * The values are added in the order they are specified. If the
	 * types of the table column and the value do not match, an exception
	 * is thrown.
	 * @param values values to be added as a row
	 * @throws IllegalArgumentException if the type of the
	 * table column and the type of the value that should be added
	 * do not match {@inheritDoc}
	 */
	public void add(Number... values) {
		Number[] row = new Number[values.length];
		for (int i = 0; i < values.length; i++) {
			Object obj = values[i];
			if (!(types[i].isAssignableFrom(obj.getClass()))) {
				throw new IllegalArgumentException("Expected: "+types[i]+", Got: "+obj.getClass());
			}
			row[i] = values[i];
		}
		data.add(row);
		notifyDataChanged();
	}

	public void clear() {
		data.clear();
		notifyDataChanged();
	}

	/* (non-Javadoc)
	 * @see openjchart.data.DataSource#get(int)
	 */
	public Number[] get(int row) {
		return data.get(row);
	}

	/* (non-Javadoc)
	 * @see openjchart.data.DataSource#get(int, int)
	 */
	public Number get(int col, int row) {
		return data.get(row)[col];
	}

	/* (non-Javadoc)
	 * @see openjchart.data.DataSource#getMin(int)
	 */
	public Number getMin(int col) {
		if (data.isEmpty()) {
			return null;
		}
		if (cacheMin.containsKey(col)) {
			return cacheMin.get(col);
		}

		double valueMin = Double.MAX_VALUE;
		valueMin = Double.MAX_VALUE;
		for (int row = 0; row < data.size(); row++) {
			double value = get(col, row).doubleValue();
			if (value < valueMin) {
				valueMin = value;
			}
		}
		cacheMin.put(col, valueMin);
		return valueMin;
	}

	/* (non-Javadoc)
	 * @see openjchart.data.DataSource#getMax(int)
	 */
	public Number getMax(int col) {
		if (data.isEmpty()) {
			return null;
		}
		if (cacheMax.containsKey(col)) {
			return cacheMax.get(col);
		}

		double valueMax = -Double.MAX_VALUE;
		for (int row = 0; row < data.size(); row++) {
			double value = get(col, row).doubleValue();
			if (value > valueMax) {
				valueMax = value;
			}
		}
		cacheMax.put(col, valueMax);
		return valueMax;
	}

	/* (non-Javadoc)
	 * @see openjchart.data.DataSource#getRowCount()
	 */
	public int getRowCount() {
		return data.size();
	}

	/* (non-Javadoc)
	 * @see openjchart.data.DataSource#getColumnCount()
	 */
	public int getColumnCount() {
		return types.length;
	}

	public void sort(final DataComparator... comparators) {
		Collections.sort(data, new Comparator<Number[]>() {
			@Override
			public int compare(Number[] o1, Number[] o2) {
				for (DataComparator comp : comparators) {
					int result = comp.compare(o1, o2);
					if (result != 0) {
						return result;
					}
				}
				return 0;
			}
		});
	}

	/* (non-Javadoc)
	 * @see openjchart.data.DataSource#iterator()
	 */
	@Override
	public Iterator<Number[]> iterator() {
		return new DataTableIterator();
	}

	/* (non-Javadoc)
	 * @see openjchart.data.DataSource#addDataListener(DataListener)
	 */
	@Override
	public void addDataListener(DataListener dataListener) {
		dataListeners.add(dataListener);
	}

	/* (non-Javadoc)
	 * @see openjchart.data.DataSource#removeDataListener(DataListener)
	 */
	@Override
	public void removeDataListener(DataListener dataListener) {
		dataListeners.remove(dataListener);
	}

	protected void notifyDataChanged() {
		cacheMin.clear();
		cacheMax.clear();
		for (DataListener dataListener : dataListeners) {
			dataListener.dataChanged(this);
		}
	}
}

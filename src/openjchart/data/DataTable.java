package openjchart.data;
import java.util.ArrayList;
import java.util.Iterator;


public class DataTable implements Iterable<Object[]> {
	private final ArrayList<Number[]> data;

	private Class<?>[] types;

	private static class DataTableIterator implements Iterator<Object[]> {
		private final DataTable data;
		private int row;

		public DataTableIterator(DataTable data) {
			this.data = data;
		}

		@Override
		public boolean hasNext() {
			return row < data.getRowCount();
		}

		@Override
		public Object[] next() {
			return data.get(row++);
		}

		@Override
		public void remove() {
		}
	}

	public DataTable(Class<?>... types) {
		this.types = new Class[types.length];
		System.arraycopy(types, 0, this.types, 0, types.length);

		data = new ArrayList<Number[]>();
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
		for (int i = 0; i < values.length; i++) {
			Object obj = values[i];
			if (!(types[i].isAssignableFrom(obj.getClass()))) {
				throw new IllegalArgumentException("Expected: "+types[i]+", Got: "+obj.getClass());
			}
		}
		Number[] row = new Number[values.length];
		System.arraycopy(values, 0, row, 0, values.length);
		data.add(row);
	}

	public void clear() {
		data.clear();
	}

	/**
	 * Returns the row with the specified index.
	 * @param row index of the row to return
	 * @return the specified row of the table
	 */
	public Number[] get(int row) {
		return data.get(row);
	}

	/**
	 * Returns the row with the specified index.
	 * @param col index of the column to return
	 * @param row index of the row to return
	 * @return the specified value of the table cell
	 */
	public Number get(int col, int row) {
		return data.get(row)[col];
	}

	/**
	 * Retrieves the smallest value of the table's column.
	 * Returns null if there are no rows in the table.
	 * @param col index of the column the minimum value should be searched in
	 * @return the minimum value of the column
	 */
	public Number getMin(int col) {
		if (data.isEmpty()) {
			return null;
		}

		double valueMin = Double.MAX_VALUE;
		for (int row = 0; row < data.size(); row++) {
			double value = get(col, row).doubleValue();
			if (value < valueMin) {
				valueMin = value;
			}
		}

		return valueMin;
	}

	/**
	 * Retrieves the largest value of the table's column.
	 * Returns null if there are no rows in the table.
	 * @param col index of the column the maximum value should be searched in
	 * @return the maximum value of the column
	 */
	public Number getMax(int col) {
		if (data.isEmpty()) {
			return null;
		}

		double valueMax = -Double.MAX_VALUE;
		for (int row = 0; row < data.size(); row++) {
			double value = get(col, row).doubleValue();
			if (value > valueMax) {
				valueMax = value;
			}
		}

		return valueMax;
	}

	/**
	 * Returns the number of rows of the table.
	 * @return number of rows in the table
	 */
	public int getRowCount() {
		return data.size();
	}

	@Override
	public Iterator<Object[]> iterator() {
		return new DataTableIterator(this);
	}
}

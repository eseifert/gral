package openjchart.data;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import openjchart.data.comparators.DataComparator;

/**
 * Creates a DataTable object.
 * DataTable is the basic implementation of DataSource.
 * Implemented functionality includes:
 * <ul>
 * <li>Adding and getting data rows</li>
 * <li>Getting data cells</li>
 * <li>Deleting the table</li>
 * <li>Calculating minimum and maximum values of columns</li>
 * <li>Getting row and column count</li>
 * <li>Sorting the table with a specific DataComparator</li>
 * </ul>
 */
public class DataTable extends AbstractDataSource {
	private final ArrayList<Number[]> data;
	private Class<?>[] types;

	/**
	 * Constructor.
	 * @param types type for each column
	 */
	public DataTable(Class<? extends Number>... types) {
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
	 * do not match
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

	/**
	 * Deletes all data this table contains.
	 */
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

	/**
	 * Sorts the table with the specified DataComparators.
	 * The values are compared in the way the comparators are specified.
	 * @param comparators comparators used for sorting
	 */
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

	@Override
	protected void notifyDataChanged() {
		super.notifyDataChanged();
	}
}

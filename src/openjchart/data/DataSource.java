package openjchart.data;

import openjchart.data.statistics.Statistics;

/**
 * Immutable view on a source for tabular data.
 * @author Erich Seifert
 */
public interface DataSource extends Iterable<Number[]> {

	/**
	 * Returns the row with the specified index.
	 * @param row index of the row to return
	 * @return the specified row of the table
	 */
	Number[] get(int row);

	/**
	 * Returns the row with the specified index.
	 * @param col index of the column to return
	 * @param row index of the row to return
	 * @return the specified value of the table cell
	 */
	Number get(int col, int row);

	/**
	 * Retrieves a object instance that contains various statistical information
	 * on the current data source.
	 * @return statistical information
	 */
	Statistics getStatistics();

	/**
	 * Returns the number of rows of the table.
	 * @return number of rows in the table
	 */
	int getRowCount();

	/**
	 * Returns the number of columns of the table.
	 * @return number of columns in the table
	 */
	int getColumnCount();

	/**
	 * Adds the specified DataListener to this DataSource.
	 * @param dataListener listener to be added
	 */
	public void addDataListener(DataListener dataListener);

	/**
	 * Adds the specified DataListener from this DataSource.
	 * @param dataListener listener to be removed
	 */
	public void removeDataListener(DataListener dataListener);
}

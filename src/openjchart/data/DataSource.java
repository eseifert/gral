package openjchart.data;

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
	 * Retrieves the smallest value of the table's column.
	 * Returns null if there are no rows in the table.
	 * @param col index of the column the minimum value should be searched in
	 * @return the minimum value of the column
	 */
	Number getMin(int col);

	/**
	 * Retrieves the largest value of the table's column.
	 * Returns null if there are no rows in the table.
	 * @param col index of the column the maximum value should be searched in
	 * @return the maximum value of the column
	 */
	Number getMax(int col);

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
	
	public void addDataListener(DataListener dataListener);

	public void removeDataListener(DataListener dataListener);
}

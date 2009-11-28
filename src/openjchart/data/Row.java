package openjchart.data;


/**
 * Class for storing a row of a data source.
 * @author Erich Seifert
 */
public class Row {
	private final DataSource source;
	private final int row;

	public Row(DataSource source, int row) {
		this.source = source;
		this.row = row;
	}

	public DataSource getSource() {
		return source;
	}

	public int getRow() {
		return row;
	}

	public Number get(int col) {
		if (getSource() == null) {
			return null;
		}
		return getSource().get(col, getRow());
	}

}

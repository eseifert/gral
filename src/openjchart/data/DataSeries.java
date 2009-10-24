package openjchart.data;


public class DataSeries {
	public static final String X = "x";
	public static final String Y = "y";

	private int[] cols;

	public DataSeries(int... columns) {
		cols = new int[columns.length];
		System.arraycopy(columns, 0, cols, 0, columns.length);
	}

	public int get(String col) {
		if (X.equals(col)) {
			return cols[0];
		}
		else if (Y.equals(col)) {
			return cols[1];
		}

		return -1;
	}

	public int getColCount() {
		return cols.length;
	}
}

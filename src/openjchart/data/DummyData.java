package openjchart.data;

import java.util.Arrays;


public class DummyData extends AbstractDataSource {
	private Number value;
	private Number[] dummyRow;
	private int cols;
	private int rows;

	public DummyData(int cols, int rows, Number value) {
		this.cols = cols;
		this.rows = rows;
		this.value = value;
		dummyRow = new Number[this.cols];
		Arrays.fill(dummyRow, this.value);
	}

	@Override
	public Number[] get(int row) {
		return dummyRow;
	}

	@Override
	public Number get(int col, int row) {
		return get(row)[col];
	}

	@Override
	public int getColumnCount() {
		return cols;
	}

	@Override
	public int getRowCount() {
		return rows;
	}

}

package openjchart.data.comparators;

import java.util.Comparator;

public abstract class DataComparator implements Comparator<Number[]> {
	private int col;

	public DataComparator(int col) {
		this.col = col;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}
}

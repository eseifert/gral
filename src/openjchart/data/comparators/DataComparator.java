package openjchart.data.comparators;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Abstract implementation of a Comparator for Number arrays.
 * This class makes it possible to specify the position of the Number
 * arrays that should be compared.
 */
public abstract class DataComparator implements Comparator<Number[]>, Serializable {
	private int col;

	/**
	 * Constructor.
	 * @param col index of the column to be compared
	 */
	public DataComparator(int col) {
		this.col = col;
	}

	/**
	 * Returns the column to be compared.
	 * @return column index
	 */
	public int getCol() {
		return col;
	}

	/**
	 * Sets the column to be compared to the specified index.
	 * @param col column index
	 */
	public void setCol(int col) {
		this.col = col;
	}
}

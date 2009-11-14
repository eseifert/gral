package openjchart.data.comparators;

/**
 * DataComparator sorting in ascending order.
 */
public class Ascending extends DataComparator {

	/**
	 * Constructor.
	 * @param col column index to be compared
	 */
	public Ascending(int col) {
		super(col);
	}

	@Override
	public int compare(Number[] o1, Number[] o2) {
		if (o1 == o2) {
			return 0;
		}
		double o1Val = o1[getCol()].doubleValue();
		double o2Val = o2[getCol()].doubleValue();
		return Double.compare(o1Val, o2Val);
	}

}

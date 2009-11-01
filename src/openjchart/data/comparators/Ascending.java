package openjchart.data.comparators;

public class Ascending extends DataComparator {

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

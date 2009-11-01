package openjchart.data.comparators;

public class Descending extends DataComparator {

	public Descending(int col) {
		super(col);
	}

	@Override
	public int compare(Number[] o1, Number[] o2) {
		if (o1 == o2) {
			return 0;
		}
		double o1Val = o1[getCol()].doubleValue();
		double o2Val = o2[getCol()].doubleValue();
		return Double.compare(o2Val, o1Val);
	}

}

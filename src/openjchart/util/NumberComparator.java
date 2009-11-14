package openjchart.util;

import java.util.Comparator;

public class NumberComparator implements Comparator<Number> {

	@Override
	public int compare(Number o1, Number o2) {
		double d1 = o1.doubleValue();
		double d2 = o2.doubleValue();
		return Double.compare(d1, d2);
	}

}

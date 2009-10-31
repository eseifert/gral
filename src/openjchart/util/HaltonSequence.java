package openjchart.util;

import java.util.Iterator;

public class HaltonSequence implements Iterator<Double> {
	private int base;
	private int c;

	public HaltonSequence() {
		this(2);
	}

	public HaltonSequence(int base) {
		this.base = base;
	}

	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public Double next() {
		int i = ++c;
		double h = 0.0;
		double step = 1.0 / base;

		while (i > 0) {
			int digit = i % base;
		    h += digit * step;
		    i = (i - digit) / base;
		    step /= base;
		}

		return h;
	}

	@Override
	public void remove() {
	}

}

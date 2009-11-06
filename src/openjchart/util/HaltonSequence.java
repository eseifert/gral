package openjchart.util;

import java.util.Iterator;

public class HaltonSequence implements Iterator<Double> {
	private int base;
	private long c;

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
		long i, digit;
		double h, step;

		if (++c == Long.MAX_VALUE) {
			c = 0;
		}

		i = c;
		h = 0.0;
		step = 1.0 / base;

		while (i > 0) {
			digit = i % base;
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

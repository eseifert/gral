package openjchart.data.filters;

public class Kernel {
	private final double[] values;
	private final int offset;
	
	public Kernel(int offset, double[] values) {
		this.values = new double[values.length];
		System.arraycopy(values, 0, this.values, 0, values.length);
		this.offset = offset;
	}

	public Kernel(double... values) {
		this(values.length/2, values);
	}

	public double get(int i) {
		if (i < getMinIndex() || i > getMaxIndex()) {
			System.err.println(getMaxIndex()+" > "+i+" < "+getMinIndex());
			return 0.0;
		}
		return values[i - getMinIndex()];
	}

	protected void set(int i, double v) {
		if (i < getMinIndex() || i > getMaxIndex()) {
			return;
		}
		values[i - getMinIndex()] = v;
	}
	
	public int getOffset() {
		return offset;
	}

	public int size() {
		return values.length;
	}

	public int getMinIndex() {
		return -getOffset();
	}
	
	public int getMaxIndex() {
		return size() - getOffset() - 1;
	}

	public Kernel add(double v) {
		for (int i = 0; i < values.length; i++) {
			values[i] += v;
		}
		return this;
	}

	public Kernel add(Kernel k) {
		int min = getMinIndex();
		int max = getMaxIndex();
		if (size() > k.size()) {
			min = k.getMinIndex();
			max = k.getMaxIndex();
		}
		for (int i = min; i <= max; i++) {
			set(i, get(i) + k.get(i));
		}
		return this;
	}

	public Kernel mul(double v) {
		for (int i = 0; i < values.length; i++) {
			values[i] *= v;
		}
		return this;
	}

	public Kernel mul(Kernel k) {
		int min = getMinIndex();
		int max = getMaxIndex();
		if (size() > k.size()) {
			min = k.getMinIndex();
			max = k.getMaxIndex();
		}
		for (int i = min; i <= max; i++) {
			set(i, get(i) * k.get(i));
		}
		return this;
	}

	public Kernel normalize() {
		double sum = 0.0;
		for (double value : values) {
			sum += value;
		}
		return mul(1.0/sum);
	}

	public Kernel negate() {
		for (int i = 0; i < values.length; i++) {
			values[i] = -values[i];
		}
		return this;
	}

}

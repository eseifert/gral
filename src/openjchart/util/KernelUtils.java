package openjchart.util;

import java.util.Arrays;

import openjchart.data.filters.Kernel;


public abstract class KernelUtils {
	public static Kernel getBinomial(double variance) {
		int size = (int) (variance * 4.0) + 1;
		return getBinomial(size);
	}

	public static Kernel getBinomial(int size) {
		double[] values = new double[size];
		values[0] = 1.0;
		for (int i = 0; i < size - 1; i++) {
			values[0] /= 2.0;
		}

		for (int i = 0; i < size; i++) {
			for (int j = i; j > 0; j--) {
				values[j] += values[j - 1];
			}
		}

		return new Kernel(values);
	}

	public static Kernel getUniform(int size, int offset, double value) {
		double[] values = new double[size];
		Arrays.fill(values, value);
		return new Kernel(offset, values);
	}
}

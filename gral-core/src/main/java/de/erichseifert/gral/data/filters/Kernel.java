/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <mseifert[at]error-reports.org>
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.erichseifert.gral.data.filters;

import java.io.Serializable;
import java.util.Arrays;

/**
 * <p>Class that represents an one dimensional array of coefficients for a
 * weighted filtering.</p>
 * <p>Functionality includes:</p>
 * <ul>
 *   <li>Adding of other kernels or scalars</li>
 *   <li>Multiplication with other kernels or scalars</li>
 *   <li>Normalization</li>
 *   <li>Negation</li>
 * </ul>
 */
public class Kernel implements Serializable {
	/** Version id for serialization. */
	private static final long serialVersionUID = 7721293471122850684L;

	/** Kernel values. */
	private final double[] values;
	/** Index of the kernel's center value. */
	private final int offset;

	/**
	 * Creates a new Kernel object with the specified offset and values.
	 * @param offset Offset to the first item in the kernel.
	 * @param values Array of values in the kernel.
	 */
	public Kernel(int offset, double[] values) {
		this.values = Arrays.copyOf(values, values.length);
		this.offset = offset;
	}

	/**
	 * Creates a new kernel object with the specified values and an offset
	 * being half the size of this kernel (rounded down).
	 * @param values Data values for the kernel.
	 */
	public Kernel(double... values) {
		this(values.length/2, values);
	}

	/**
	 * Returns a Kernel of specified variance with binomial coefficients.
	 * @param variance Variance.
	 * @return Kernel.
	 */
	public static Kernel getBinomial(double variance) {
		int size = (int) (variance * 4.0) + 1;
		return getBinomial(size);
	}

	/**
	 * Returns a Kernel of specified size with binomial coefficients.
	 * @param size Size of the Kernel.
	 * @return Kernel.
	 */
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

	/**
	 * Returns a Kernel with the specified size and offset, filled with a
	 * single value.
	 * @param size Size.
	 * @param offset Offset.
	 * @param value Value the Kernel is filled with.
	 * @return Kernel.
	 */
	public static Kernel getUniform(int size, int offset, double value) {
		double[] values = new double[size];
		Arrays.fill(values, value);
		return new Kernel(offset, values);
	}

	/**
	 * Returns the value at the specified position of this kernel.
	 * If the position exceeds the minimum or maximum index, 0.0 is
	 * returned.
	 * @param i Index to be returned.
	 * @return Value at the specified index.
	 */
	public double get(int i) {
		if (i < getMinIndex() || i > getMaxIndex()) {
			return 0.0;
		}
		return values[i - getMinIndex()];
	}

	/**
	 * Sets the specified index of this kernel to the specified value.
	 * @param i Index to be changed.
	 * @param v Value to be set.
	 */
	protected void set(int i, double v) {
		if (i < getMinIndex() || i > getMaxIndex()) {
			return;
		}
		values[i - getMinIndex()] = v;
	}

	/**
	 * Returns the offset of this kernel.
	 * @return Offset.
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Returns the number of values in this kernel.
	 * @return Number of values.
	 */
	public int size() {
		return values.length;
	}

	/**
	 * Returns the index of the "leftmost" value.
	 * @return Minimal index.
	 */
	public int getMinIndex() {
		return -getOffset();
	}

	/**
	 * Returns the index of the "rightmost" value.
	 * @return Maximal index.
	 */
	public int getMaxIndex() {
		return size() - getOffset() - 1;
	}

	/**
	 * Returns a new Kernel, where the specified value was added to each of
	 * the items.
	 * @param v Value to be added.
	 * @return Kernel with new values.
	 */
	public Kernel add(double v) {
		for (int i = 0; i < values.length; i++) {
			values[i] += v;
		}
		return this;
	}

	/**
	 * Returns a new Kernel, where the specified kernel was added.
	 * @param k Kernel to be added.
	 * @return Kernel with new values.
	 */
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

	/**
	 * Returns a new Kernel, where the specified value was multiplied with
	 * each of the items.
	 * @param v Value to be multiplied.
	 * @return Kernel with new values.
	 */
	public Kernel mul(double v) {
		for (int i = 0; i < values.length; i++) {
			values[i] *= v;
		}
		return this;
	}

	/**
	 * Returns a new Kernel, where the specified kernel was multiplied.
	 * @param k Kernel to be multiplied.
	 * @return Kernel with new values.
	 */
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

	/**
	 * Returns a normalized Kernel so that the sum of all values equals 1.
	 * @return Normalized Kernel.
	 */
	public Kernel normalize() {
		double sum = 0.0;
		for (double value : values) {
			sum += value;
		}
		return mul(1.0/sum);
	}

	/**
	 * Returns a Kernel with all values being negated.
	 * @return Negated Kernel.
	 */
	public Kernel negate() {
		mul(-1.0);
		return this;
	}

}

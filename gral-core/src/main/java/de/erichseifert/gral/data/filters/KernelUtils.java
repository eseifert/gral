/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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

import java.util.Arrays;


/**
 * Abstract class that provides utility functions which are useful for
 * kernels.
 */
public abstract class KernelUtils {

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
}

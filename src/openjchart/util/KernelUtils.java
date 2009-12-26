/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

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

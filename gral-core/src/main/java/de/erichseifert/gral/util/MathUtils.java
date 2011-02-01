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
package de.erichseifert.gral.util;

import java.util.List;
import java.util.Random;

/**
 * Abstract class that provides utility functions which are useful for
 * mathematical calculations.
 */
public abstract class MathUtils {
	/** Instance for random values. */
	private static final Random RANDOM = new Random();

	/**
	 * Default constructor that prevents creation of class.
	 */
	private MathUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Check whether two floating point values match with a given precision.
	 * @param a First value
	 * @param b Second value
	 * @param delta Precision
	 * @return <code>true</code> if the difference of <i>a</i> and <b>b</b> is
	 *         smaller or equal than <i>delta</i>, otherwise <code>false</code>
	 */
	public static boolean almostEqual(double a, double b, double delta) {
		return Math.abs(a - b) <= delta;
	}

	/**
	 * Mathematically rounds a number with a defined precision.
	 * @param a Value
	 * @param precision Precision
	 * @return Rounded value
	 */
	public static double round(double a, double precision) {
		return Math.round(a/precision) * precision;
	}

	/**
	 * Returns a rounded number smaller than <code>a</code> with a defined
	 * precision.
	 * @param a Value
	 * @param precision Precision
	 * @return Rounded value
	 */
	public static double floor(double a, double precision) {
		return Math.floor(a/precision) * precision;
	}

	/**
	 * Returns a rounded number larger than <code>a</code> with a defined
	 * precision.
	 * @param a Value
	 * @param precision Precision
	 * @return Rounded value
	 */
	public static double ceil(double a, double precision) {
		return Math.ceil(a/precision) * precision;
	}

	/**
	 * Perform a binary search on a sorted array <code>a</code> to find the
	 * element with the nearest element to <code>key</code>.
	 * @param a Array with ascending values
	 * @param key Pivot value
	 * @return Index of the array element whose value is nearly or exactly
	 *         <code>key</code>
	 */
	public static int binarySearch(double[] a, double key) {
		int l = 0;
		int h = a.length - 1;
		int i;
		do {
			i = (int)(((long)l + (long)h) / 2L);
			if (key > a[i]) {
				l = i + 1;
			} else if (key < a[i]) {
				h = i - 1;
			} else {
				return i;
			}
		} while (l <= h);
		return i;
	}

	/**
	 * Perform a binary search on a sorted array <code>a</code> to find the
	 * element with the smallest distance to <code>key</code>. The returned
	 * element's value is always less than or equal to <code>key</code>.
	 * @param a Array with ascending values
	 * @param key Pivot value
	 * @return Index of the array element whose value is less than or equal to
	 *         <code>key</code>
	 */
	public static int binarySearchFloor(double[] a, double key) {
		if (a.length == 0) {
			return -1;
		}
		int i = binarySearch(a, key);
		if (i >= 0 && a[i] > key) {
			i--;
		}
		return i;
	}

	/**
	 * Perform a binary search on a sorted array <code>a</code> to find the
	 * element with the smallest distance to <code>key</code>. The returned
	 * element's value is always greater than or equal to <code>key</code>.
	 * @param a Array with ascending values
	 * @param key Pivot value
	 * @return Index of the array element whose value is greater than or equal
	 * 		   to <code>key</code>
	 */
	public static int binarySearchCeil(double[] a, double key) {
		if (a.length == 0) {
			return -1;
		}
		int i = binarySearch(a, key);
		if (i >= 0 && a[i] < key) {
			i++;
		}
		return i;
	}

	/**
	 * Clamps a value to specified limits: if <code>value</code> is greater than
	 * <code>max</code> then <code>max</code> will be returned. If
	 * <code>value</code> is greater than <code>min</code> then
	 * <code>min</code> will be returned.
	 * @param <T> Data type of the Value to limit.
	 * @param value Value to be clamped
	 * @param min Minimum
	 * @param max Maximum
	 * @return Clamped value
	 */
	public static <T extends Comparable<T>> T limit(T value, T min, T max) {
		if (value.compareTo(max) > 0) {
			return max;
		}
		if (value.compareTo(min) < 0) {
			return min;
		}
		return value;
	}

	/**
	 * <p>Perform a randomized search on an unsorted array <code>a</code> to
	 * find the <i>i</i>th smallest element. The array contents are be modified
	 * during the operation!</p>
	 * <p>See Cormen et al. (2001): Introduction to Algorithms. 2nd edition.
	 * p. 186</p>
	 * @param <T> Data type of the array
	 * @param a Unsorted array
	 * @param lower Starting index
	 * @param upper End index
	 * @param i Smallness rank of value to search
	 * @return Index of the element that is the <i>i</i>th smallest in array
	 * <i>a</i>
	 */
	public static <T extends Comparable<T>> int randomizedSelect(List<T> a,
			int lower, int upper, int i) {
		if (a.isEmpty()) {
			return -1;
		}
		if (lower == upper) {
			return lower;
		}
		int q = randomizedPartition(a, lower, upper);
		int k = q - lower + 1;
		if (i == k) {
			return q;
		} else if (i < k) {
			return randomizedSelect(a, lower, q - 1, i);
		} else {
			return randomizedSelect(a, q + 1, upper, i - k);
		}
	}

	/**
	 * Rearranges an array in two partitions using random sampling.
	 * The array is permuted so that the elements of the lower partition
	 * are always smaller than those of the upper partition.
	 * @param <T> Data type of the array
	 * @param a Unsorted array
	 * @param lower Starting index
	 * @param upper End index
	 * @return Pivot point of the partitioned array
	 * @see Cormen et al. (2001): Introduction to Algorithms. 2nd edition.
	 * p. 154
	 */
	private static <T extends Comparable<T>> int randomizedPartition(
			List<T> a, int lower, int upper) {
		int i = lower + RANDOM.nextInt(upper - lower + 1);
		exchange(a, upper, i);
		return partition(a, lower, upper);
	}

	/**
	 * Performs QuickSort partitioning: Rearranges an array in two partitions.
	 * The array is permuted so that the elements of the lower partition are
	 * always smaller than those of the upper partition.
	 * @param <T> Data type of the array
	 * @param a Unsorted array
	 * @param lower Starting index
	 * @param upper End index
	 * @return Pivot point of the partitioned array
	 * @see Cormen et al. (2001): Introduction to Algorithms. 2nd edition.
	 * p. 146
	 */
	private static <T extends Comparable<T>> int partition(
			List<T> a, int lower, int upper) {
		T x = a.get(upper);
		int i = lower - 1;
		for (int j = lower; j < upper; j++) {
			if (a.get(j).compareTo(x) <= 0) {
				i++;
				exchange(a, i, j);
			}
		}
		exchange(a, i + 1, upper);
		return i + 1;
	}

	/**
	 * Swaps two elements at indexes <code>i1</code> and <code>i2</code> of an
	 * array in-place.
	 * @param <T> Data type of the array
	 * @param a Array
	 * @param i1 First element index
	 * @param i2 Second element index
	 */
	private static <T> void exchange(List<T> a, int i1, int i2) {
		T tmp = a.get(i2);
		a.set(i2, a.get(i1));
		a.set(i1, tmp);
	}

	/**
	 * <p>Returns the magnitude of the specified number. Example for magnitude
	 * base 10:</p>
	 * <table><tbody>
	 *   <tr><td align="right">  0.05</td><td align="right">  0.01</td></tr>
	 *   <tr><td align="right">  3.14</td><td align="right">  1.00</td></tr>
	 *   <tr><td align="right"> 54.32</td><td align="right"> 10.00</td></tr>
	 *   <tr><td align="right">123.45</td><td align="right">100.00</td></tr>
	 * </tbody></table>
	 * @param base Base.
	 * @param n Number.
	 * @return Magnitude.
	 */
	public static double magnitude(double base, double n) {
		double logN = Math.log(n)/Math.log(base);
		return Math.pow(base, Math.floor(logN));
	}
}

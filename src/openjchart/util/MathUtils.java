package openjchart.util;



public abstract class MathUtils {
	/**
	 * Check whether two floating point values match with a given precision.
	 * @param a First value
	 * @param b Second value
	 * @param delta Precision
	 * @return <code>true</code> if the difference of <i>a</i> and <b>b</b> is smaller or equal than <i>delta</i>, otherwise <code>false</code>
	 */
	public static boolean almostEqual(double a, double b, double delta) {
		return Math.abs(a - b) <= delta;
	}

	/**
	 * Perform a binary search on a sorted array <code>a</code> to find the
	 * element with the nearest element to <code>key</code>.
	 * @param a Array with ascending values
	 * @param key Pivot value
	 * @return Index of the array element whose value is nearly or exactly <code>key</code>
	 */
	public static int binarySearch(double[] a, double key) {
		int l = 0;
		int h = a.length - 1;
		int i;
		do {
			i = (l + h) / 2;
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
	 * @return Index of the array element whose value is less than or equal to <code>key</code>
	 */
	public static int binarySearchFloor(double[] a, double key) {
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
	 * @return Index of the array element whose value is greater than or equal to <code>key</code>
	 */
	public static int binarySearchCeil(double[] a, double key) {
		int i = binarySearch(a, key);
		if (i >= 0 && a[i] < key) {
			i++;
		}
		return i;
	}

}

package openjchart.util;



public abstract class MathUtils {
	public static boolean almostEqual(double a, double b, double delta) {
		return Math.abs(a - b) <= delta;
	}

}

package openjchart.util;

public abstract class Dimension2D extends java.awt.geom.Dimension2D {

	public static class Double extends Dimension2D {
		private double width;
		private double height;

		public Double() {
			setSize(0.0, 0.0);
		}

		public Double(double width, double height) {
			setSize(width, height);
		}

		@Override
		public double getHeight() {
			return height;
		}
	
		@Override
		public double getWidth() {
			return width;
		}
	
		@Override
		public void setSize(double width, double height) {
			this.width = width;
			this.height = height;
		}
		
		@Override
		public String toString() {
			return getClass().toString() + "[width=" + width + ", height=" + height + "]";
		}
	}
}

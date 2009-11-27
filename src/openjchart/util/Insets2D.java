package openjchart.util;


public abstract class Insets2D {
	public abstract double getTop();
	public abstract double getLeft();
	public abstract double getBottom();
	public abstract double getRight();
	public abstract void setInsets(Insets2D insets);
	public abstract void setInsets(double top, double left, double bottom, double right);

	public static class Double extends Insets2D {
		private double top;
		private double left;
		private double bottom;
		private double right;

		public Double() {
			this(0.0);
		}

		public Double(double inset) {
			this(inset, inset, inset, inset);
		}

		public Double(double top, double left, double bottom, double right) {
			setInsets(top, left, bottom, right);
		}

		@Override
		public double getTop() {
			return top;
		}

		@Override
		public double getLeft() {
			return left;
		}

		@Override
		public double getBottom() {
			return bottom;
		}

		@Override
		public double getRight() {
			return right;
		}

		@Override
		public void setInsets(Insets2D insets) {
			if (insets == null) {
				return;
			}
			setInsets(insets.getTop(), insets.getLeft(), insets.getBottom(), insets.getRight());
		}
		
		@Override
		public void setInsets(double top, double left, double bottom, double right) {
			this.top = top;
			this.left = left;
			this.bottom= bottom;
			this.right = right;
		}

	}

}

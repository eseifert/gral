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
			this.bottom = bottom;
			this.right = right;
		}

		@Override
		public String toString() {
			return getClass().toString() + "[top=" + top + ", left=" + left + ", bottom=" + bottom + ", right=" + right + "]";
		}
	}

}

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
			return getClass().getName() + "[width=" + width + ", height=" + height + "]";
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof java.awt.geom.Dimension2D)) {
				return false;
			}
			java.awt.geom.Dimension2D dim = (java.awt.geom.Dimension2D) obj;
			return (getWidth() == dim.getWidth()) && (getHeight() == dim.getHeight());
		}

		@Override
		public int hashCode() {
			long bits = java.lang.Double.doubleToLongBits(getWidth());
			bits ^= java.lang.Double.doubleToLongBits(getHeight()) * 31;
			return (((int) bits) ^ ((int) (bits >> 32)));
		}
	}
}

/**
 * GRAL: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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
 * Lesser GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.util;

/**
 * Class that stores the horizontal and vertical extent of an object.
 * <p>Please use this instead of java.awt.geom.Dimension2D, as the java class
 * does not support double values.</p>
 */
public abstract class Dimension2D extends java.awt.geom.Dimension2D {

	/**
	 * Creates a new Dimension2D object.
	 */
	public Dimension2D() {
	}

	/**
	 * Class that stores double values.
	 */
	public static class Double extends Dimension2D {
		private double width;
		private double height;

		/**
		 * Creates a new Dimension2D object with zero width and height.
		 */
		public Double() {
			setSize(0.0, 0.0);
		}

		/**
		 * Creates a new Dimension2D object with the specified width and
		 * height.
		 * @param width Width.
		 * @param height Height.
		 */
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

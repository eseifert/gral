/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2026 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <mseifert[at]error-reports.org>
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
package de.erichseifert.gral.graphics;

import java.io.Serializable;
import java.util.Locale;

/**
 * <p>A width and a height in {@code double} precision. The JDK declares
 * {@code java.awt.geom.Dimension2D} as abstract but ships only the integer
 * {@code java.awt.Dimension} as an implementation, so GRAL provides its own.</p>
 *
 * <pre>
 * Dimension2D size = new de.erichseifert.gral.graphics.Dimension2D.Double(
 *     100.0, 50.0);
 * </pre>
 *
 * <p>This is what {@link Drawable#getPreferredSize()} returns. Note that the
 * name collides with {@code java.awt.geom.Dimension2D}, which is its
 * superclass, so one of the two usually has to be qualified.</p>
 */
public abstract class Dimension2D extends java.awt.geom.Dimension2D
		implements Serializable {
	/** Version id for serialization. */
	private static final long serialVersionUID = 6961198271520384282L;

	/**
	 * Creates a new Dimension2D object.
	 */
	public Dimension2D() {
	}

	/**
	 * Class that stores double values.
	 */
	public static class Double extends Dimension2D {
		/** Version id for serialization. */
		private static final long serialVersionUID = -4341712269787906650L;

		/** Horizontal extension. */
		private double width;
		/** Vertical extension. */
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
			return String.format(Locale.US,
					"%s[width=%f, height=%f]", //$NON-NLS-1$
					getClass().getName(), width, height);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof java.awt.geom.Dimension2D)) {
				return false;
			}
			java.awt.geom.Dimension2D dim = (java.awt.geom.Dimension2D) obj;
			return (getWidth() == dim.getWidth()) &&
				(getHeight() == dim.getHeight());
		}

		@Override
		public int hashCode() {
			long bits = java.lang.Double.doubleToLongBits(getWidth());
			bits ^= java.lang.Double.doubleToLongBits(getHeight()) * 31;
			return ((int) bits) ^ ((int) (bits >> 32));
		}
	}
}

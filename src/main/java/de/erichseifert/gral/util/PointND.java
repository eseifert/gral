/*
 * GRAL: GRAphing Library for Java(R)
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
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.util;

import java.awt.geom.Point2D;
import java.util.Arrays;

/**
 * Class for storing n-dimensional points.
 */
public class PointND {
	private final Number[] coordinates;

	/**
	 * Constructor that initializes the point with a list of coordinates.
	 * @param coordinates Coordinate values.
	 */
	public PointND(Number... coordinates) {
		this.coordinates = Arrays.copyOf(coordinates, coordinates.length);
	}

	/**
	 * Constructor that initializes the point with another two-dimensional point.
	 * @param point2d Two-dimensional point object.
	 */
	public PointND(Point2D point2d) {
		this(point2d.getX(), point2d.getY());
	}

	/**
	 * Returns the number of dimensions.
	 * @return Number of dimensions.
	 */
	public int getDimensions() {
		return coordinates.length;
	}

	/**
	 * Returns the value of a specified dimension.
	 * @param dimension Dimension.
	 * @return Coordinate value.
	 */
	public Number get(int dimension) {
		return coordinates[dimension];
	}

	/**
	 * Sets the value of a specified dimension.
	 * @param dimension Dimension.
	 * @param coordinate New coordinate value.
	 */
	public void set(int dimension, Number coordinate) {
		coordinates[dimension] = coordinate;
	}

	/**
	 * Sets all coordinate values at once.
	 * @param coordinates Coordinate values.
	 */
	public void setLocation(Number... coordinates) {
		if (getDimensions() != coordinates.length) {
			throw new IllegalArgumentException(
					"Wrong number of dimensions: You have to provide " +
					getDimensions() + " values for this point (" +
					coordinates.length + " given)");
		}
		System.arraycopy(coordinates, 0, this.coordinates, 0, getDimensions());
	}

	/**
	 * Creates a two-dimensional point from the specified dimensions.
	 * @param dimX Dimension for x coordinate.
	 * @param dimY Dimension for y coordinate.
	 * @return Two-dimensional point.
	 */
	public Point2D getPoint2D(int dimX, int dimY) {
		if (getDimensions() < 2) {
			throw new ArrayIndexOutOfBoundsException(
					"Can't create two-dimensional point from " +
					getDimensions() + "D data.");
		}
		return new Point2D.Double(get(dimX).doubleValue(), get(dimY).doubleValue());
	}

	/**
	 * Creates a two-dimensional point from dimensions 0 and 1.
	 * @return Two-dimensional point.
	 */
	public Point2D getPoint2D() {
		return getPoint2D(0, 1);
	}

	@Override
	public String toString() {
		StringBuilder coordStr = new StringBuilder();
		for (int dim = 0; dim < coordinates.length; dim++) {
			if (dim > 0) {
				coordStr.append(", ");
			}
			coordStr.append(coordinates[dim]);
		}
		return getClass().getName()+"["+coordStr.toString()+"]";
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PointND)) {
			return false;
		}
		PointND p = (PointND) obj;
		if (getDimensions() != p.getDimensions()) {
			return false;
		}
		for (int dim = 0; dim < coordinates.length; dim++) {
			if (!coordinates[dim].equals(p.coordinates[dim])) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hashCode = 0;
		for (Number coordinate : coordinates) {
			hashCode ^= coordinate.hashCode();
		}
		return hashCode;
	}

}

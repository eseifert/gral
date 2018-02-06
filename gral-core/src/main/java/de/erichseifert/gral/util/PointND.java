/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.gral.util;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Arrays;

/**
 * Class for storing n-dimensional points.
 * @param <T> Data type of the coordinates.
 */
public class PointND<T extends Number> implements Serializable {
	/** Version id for serialization. */
	private static final long serialVersionUID = 3552680202450906771L;

	/** Constant for accessing x-coordinate. */
	public static final int X = 0;
	/** Constant for accessing y-coordinate. */
	public static final int Y = 1;
	/** Constant for accessing z-coordinate. */
	public static final int Z = 2;

	/** Coordinates along the axes that describe this point. */
	private final T[] coordinates;

	/**
	 * Constructor that initializes the point with a list of coordinates.
	 * @param coordinates Coordinate values.
	 */
	public PointND(T... coordinates) {
		this.coordinates = Arrays.copyOf(coordinates, coordinates.length);
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
	public T get(int dimension) {
		return coordinates[dimension];
	}

	/**
	 * Sets the value of a specified dimension.
	 * @param dimension Dimension.
	 * @param coordinate New coordinate value.
	 */
	public void set(int dimension, T coordinate) {
		coordinates[dimension] = coordinate;
	}

	/**
	 * Sets all coordinate values at once.
	 * @param coordinates Coordinate values.
	 */
	public void setLocation(T... coordinates) {
		if (getDimensions() != coordinates.length) {
			throw new IllegalArgumentException(MessageFormat.format(
				"Wrong number of dimensions: Expected {0,number,integer} values, got {1,number,integer}.", //$NON-NLS-1$
				getDimensions(), coordinates.length));
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
					"Can't create two-dimensional point from " + //$NON-NLS-1$
					getDimensions() + "D data."); //$NON-NLS-1$
		}
		return new Point2D.Double(
				get(dimX).doubleValue(), get(dimY).doubleValue());
	}

	/**
	 * Creates a two-dimensional point from dimensions 0 and 1.
	 * @return Two-dimensional point.
	 */
	public Point2D getPoint2D() {
		return getPoint2D(X, Y);
	}

	@Override
	public String toString() {
		return getClass().getName() + Arrays.deepToString(coordinates);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PointND<?>)) {
			return false;
		}
		PointND<?> p = (PointND<?>) obj;
		if (getDimensions() != p.getDimensions()) {
			return false;
		}
		for (int dim = 0; dim < coordinates.length; dim++) {
			Number dimA = get(dim);
			Number dimB = p.get(dim);
			if (dimA != null && dimB != null) {
				if (!dimA.equals(dimB)) {
					return false;
				}
			} else if (dimA != dimB) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hashCode = 0;
		for (T coordinate : coordinates) {
			hashCode ^= coordinate.hashCode();
		}
		return hashCode;
	}

}

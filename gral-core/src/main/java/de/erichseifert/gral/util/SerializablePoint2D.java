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

/**
 * A wrapper for creating serializable objects from instances of
 * {@link java.awt.geom.Point2D.Double} and {@link java.awt.geom.Point2D.Float}.
 */
public class SerializablePoint2D implements SerializationWrapper<Point2D> {
	/** Version id for serialization. */
	private static final long serialVersionUID = -8849270838795846599L;

	/** x coordinate. */
	private final double x;
	/** y coordinate. */
	private final double y;
	/** Flag to determine whether the class was of type Point2D.Double or
	Point2D.Float. */
	private final boolean isDouble;

	/**
	 * Initializes a new wrapper with a {@code Point2D} instance.
	 * @param point Wrapped object.
	 */
	public SerializablePoint2D(Point2D point) {
		x = point.getX();
		y = point.getY();
		isDouble = point instanceof Point2D.Double;
	}

	/**
	 * Creates a new point instance of the wrapped class using the data from
	 * the wrapper. This is used for deserialization.
	 * @return A point instance containing the data from the wrapper.
	 */
	public Point2D unwrap() {
		if (isDouble) {
			return new Point2D.Double(x, y);
		} else {
			return new Point2D.Float((float) x, (float) y);
		}
	}
}

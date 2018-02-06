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

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.List;

import de.erichseifert.gral.util.GeometryUtils.PathSegment;

/**
 * A wrapper for creating serializable objects from instances of
 * {@link java.awt.Shape} (e.g. {@link java.awt.geom.Path2D}).
 */
public class SerializableShape implements SerializationWrapper<Shape> {
	/** Version id for serialization. */
	private static final long serialVersionUID = -8849270838795846599L;

	/** Shape segments. */
	private final List<PathSegment> segments;
	/** Flag to determine whether the class was of type Path2D.Double or
	Path2D.Float. */
	private final boolean isDouble;

	/**
	 * Initializes a new wrapper with a {@code Shape} instance.
	 * @param shape Wrapped object.
	 */
	public SerializableShape(Shape shape) {
		segments = GeometryUtils.getSegments(shape);
		isDouble = !(shape instanceof Path2D.Float);
	}

	/**
	 * Creates a new instance of the wrapped class using the data from the
	 * wrapper. This is used for deserialization.
	 * @return An instance containing the data from the wrapper.
	 */
	public Shape unwrap() {
		return GeometryUtils.getShape(segments, isDouble);
	}
}

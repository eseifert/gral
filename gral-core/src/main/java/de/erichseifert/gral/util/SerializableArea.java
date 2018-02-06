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

import java.awt.geom.Area;

/**
 * A wrapper for creating serializable objects from instances of
 * {@link java.awt.geom.Area}.
 */
public class SerializableArea implements SerializationWrapper<Area>  {
	/** Version id for serialization. */
	private static final long serialVersionUID = -2861579645195882742L;

	/** Serialized instance. */
	private final SerializableShape shape;

	/**
	 * Initializes a new wrapper with an {@code Area} instance.
	 * @param area Wrapped object.
	 */
	public SerializableArea(Area area) {
		shape = new SerializableShape(area);
	}

	/**
	 * Creates a new instance of the wrapped class using the data from the
	 * wrapper. This is used for deserialization.
	 * @return An instance containing the data from the wrapper.
	 */
	public Area unwrap() {
		return new Area(shape.unwrap());
	}
}

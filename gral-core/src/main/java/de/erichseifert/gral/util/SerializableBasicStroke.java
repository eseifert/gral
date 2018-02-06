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

import java.awt.BasicStroke;

/**
 * A wrapper for creating serializable objects from instances of
 * {@link java.awt.BasicStroke}.
 */
public class SerializableBasicStroke
		implements SerializationWrapper<BasicStroke> {
	/** Version id for serialization. */
	private static final long serialVersionUID = -9087891720495398485L;

	/** Line width. */
	private final float width;
	/** End cap. */
	private final int cap;
	/** Line join mode. */
	private final int join;
	/** Miter limit. */
	private final float miterlimit;
	/** Dash array. */
	private final float[] dash;
	/** Dash phase. */
	private final float dash_phase;

	/**
	 * Initializes a new wrapper with a {@code BasicStroke} instance.
	 * @param stroke Wrapped object.
	 */
	public SerializableBasicStroke(BasicStroke stroke) {
		width = stroke.getLineWidth();
		cap = stroke.getEndCap();
		join = stroke.getLineJoin();
		miterlimit = stroke.getMiterLimit();
		dash = stroke.getDashArray();
		dash_phase = stroke.getDashPhase();

	}

	/**
	 * Creates a new stroke instance of the wrapped class using the data from
	 * the wrapper. This is used for deserialization.
	 * @return A stroke instance containing the data from the wrapper.
	 */
	public BasicStroke unwrap() {
		return new BasicStroke(width, cap, join, miterlimit, dash, dash_phase);
	}
}

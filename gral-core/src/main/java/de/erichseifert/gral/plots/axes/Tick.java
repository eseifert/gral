/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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
package de.erichseifert.gral.plots.axes;

import java.awt.Shape;

import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.plots.DataPoint;
import de.erichseifert.gral.util.PointND;


/**
 * Class for storing the tick mark of an axis.
 */
public class Tick extends DataPoint {
	/** Type of tick mark. */
	public static enum TickType {
		/** Major tick mark. */
		MAJOR,
		/** Minor tick mark. */
		MINOR,
		/** User-defined tick mark. */
		CUSTOM
	};

	/** The type of tick mark (major/minor/custom). */
	private final TickType type;
	/** The normal of the tick mark. */
	private final PointND<Double> normal;
	/** Label text associated with this tick mark. */
	private final String label;

	/**
	 * Creates a new instance with the specified position, normal,
	 * <code>Drawable</code>, point and label.
	 * @param type Type of the tick mark.
	 * @param position Coordinates.
	 * @param normal Normal.
	 * @param drawable Representation.
	 * @param point Point.
	 * @param label Description.
	 */
	public Tick(TickType type, PointND<Double> position, PointND<Double> normal,
			Drawable drawable, Shape point, String label) {
		super(position, drawable, point);
		this.type = type;
		this.normal = normal;
		this.label = label;
	}

	/**
	 * Returns the kind of tick this object represents.
	 * @return Tick type
	 */
	public TickType getType() {
		return type;
	}

	/**
	 * Returns the normal vector of this tick mark.
	 * @return Normal.
	 */
	public PointND<Double> getNormal() {
		return normal;
	}

	/**
	 * Returns the label of this tick mark.
	 * @return Label.
	 */
	public String getLabel() {
		return label;
	}

}

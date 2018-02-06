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
package de.erichseifert.gral.plots.axes;

import java.awt.Shape;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.plots.DataPoint;
import de.erichseifert.gral.util.PointND;


/**
 * Class for storing the tick mark of an axis.
 */
public class Tick extends DataPoint {
	/** Type of tick mark. */
	public enum TickType {
		/** Major tick mark. */
		MAJOR,
		/** Minor tick mark. */
		MINOR,
		/** User-defined tick mark. */
		CUSTOM
	}

	/** The type of tick mark (major/minor/custom). */
	public final TickType type;
	/** The normal of the tick mark. */
	public final PointND<Double> normal;
	/** Drawable that will be used to render the tick. */
	public final Drawable drawable;
	/** Shape describing the tick. */
	public final Shape shape;
	/** Label text associated with this tick mark. */
	public final String label;

	/**
	 * Creates a new instance with the specified position, normal,
	 * {@code Drawable}, point and label.
	 * @param type Type of the tick mark.
	 * @param position Coordinates.
	 * @param normal Normal.
	 * @param drawable Representation.
	 * @param point Point.
	 * @param label Description.
	 */
	public Tick(TickType type, PointND<Double> position, PointND<Double> normal,
			Drawable drawable, Shape point, String label) {
		super(null, position);
		this.type = type;
		this.normal = normal;
		this.drawable = drawable;
		this.shape = point;
		this.label = label;
	}
}

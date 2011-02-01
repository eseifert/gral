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
package de.erichseifert.gral.plots;

import java.awt.Shape;

import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.util.PointND;


/**
 * Class for storing points of a plot.
 */
public class DataPoint {
	/** Position of the data point (n-dimensional). */
	private final PointND<Double> position;
	/** Drawable that will be used to render the data point. */
	private final Drawable drawable;
	/** Shape describing the data point. */
	private final Shape point;

	/**
	 * Creates a new <code>DataPoint</code> object with the specified position,
	 * <code>Drawable</code>, and shape.
	 * @param position Coordinates.
	 * @param drawable Representation.
	 * @param point Point.
	 */
	public DataPoint(PointND<Double> position, Drawable drawable, Shape point) {
		this.position = position;
		this.drawable = drawable;
		this.point = point;
	}

	/**
	 * Returns the coordinates of this <code>DataPoint</code>.
	 * @return Position.
	 */
	public PointND<Double> getPosition() {
		return position;
	}

	/**
	 * Returns the <code>Drawable</code> which represents this
	 * <code>DataPoint</code>.
	 * @return <code>Drawable</code> instance.
	 */
	public Drawable getDrawable() {
		return drawable;
	}

	/**
	 * Returns the point of this <code>DataPoint</code>.
	 * @return Point.
	 */
	public Shape getPoint() {
		return point;
	}

}

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
package de.erichseifert.gral.plots;

import de.erichseifert.gral.plots.points.PointData;
import de.erichseifert.gral.util.PointND;


/**
 * Class for storing points of a plot.
 */
public class DataPoint {
	/** Axes and data values that were used to create the data point. */
	public final PointData data;
	/** Position of the data point (n-dimensional). */
	public final PointND<Double> position;

	/**
	 * Creates a new {@code DataPoint} object with the specified position,
	 * {@code Drawable}, and shape.
	 * @param data Data that this point was created from.
	 * @param position Coordinates in view/screen units.
	 */
	public DataPoint(PointData data, PointND<Double> position) {
		this.data = data;
		this.position = position;
	}
}

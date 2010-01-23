/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.plots.axes;

import java.awt.Shape;
import java.awt.geom.Point2D;

import openjchart.Drawable;
import openjchart.plots.DataPoint2D;

/**
 * Class for storing points of a plot.
 */
public class Tick2D extends DataPoint2D {
	public static enum TickType { MAJOR, MINOR, CUSTOM };

	private final TickType type;
	private final Point2D normal;
	private final String label;

	/**
	 * Creates a new <code>Tick2D</code> object with the specified position, normal,
	 * <code>Drawable</code>, shape and label.
	 * @param position Coordinates.
	 * @param normal Normal.
	 * @param drawable Representation.
	 * @param shape Shape.
	 * @param label Description.
	 */
	public Tick2D(TickType type, Point2D position, Point2D normal, Drawable drawable, Shape shape, String label) {
		super(position, drawable, shape);
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
	 * Returns the normal vector of this <code>DataPoint2D</code>.
	 * @return Normal.
	 */
	public Point2D getNormal() {
		return normal;
	}

	/**
	 * Returns the label of this <code>DataPoint2D</code>.
	 * @return Label.
	 */
	public String getLabel() {
		return label;
	}

}

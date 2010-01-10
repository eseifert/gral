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

package openjchart.plots;

import java.awt.Shape;
import java.awt.geom.Point2D;

import openjchart.Drawable;

/**
 * Class for storing points of a plot.
 */
public class DataPoint2D {
	private final Point2D position;
	private final Point2D normal;
	private final Drawable drawable;
	private final Shape shape;
	private final String label;

	/**
	 * Creates a new <code>DataPoint2D</code> object with the specified position, normal,
	 * drawable, shape and label.
	 * @param position Coordinates.
	 * @param normal Normal.
	 * @param drawable Representation.
	 * @param shape Shape.
	 * @param label Description.
	 */
	public DataPoint2D(Point2D position, Point2D normal, Drawable drawable, Shape shape, String label) {
		this.position = position;
		this.normal = normal;
		this.drawable = drawable;
		this.shape = shape;
		this.label = label;
	}

	/**
	 * Returns the coordinates of this <code>DataPoint2D</code>.
	 * @return Position.
	 */
	public Point2D getPosition() {
		return position;
	}

	/**
	 * Returns the normal vector of this <code>DataPoint2D</code>.
	 * @return Normal.
	 */
	public Point2D getNormal() {
		return normal;
	}

	/**
	 * Returns the <code>Drawable</code> which represents this <code>DataPoint2D</code>.
	 * @return <code>Drawable</code> instance.
	 */
	public Drawable getDrawable() {
		return drawable;
	}

	/**
	 * Returns the shape of this <code>DataPoint2D</code>.
	 * @return <code>Shape</code>.
	 */
	public Shape getShape() {
		return shape;
	}

	/**
	 * Returns the label of this <code>DataPoint2D</code>.
	 * @return Label.
	 */
	public String getLabel() {
		return label;
	}

}

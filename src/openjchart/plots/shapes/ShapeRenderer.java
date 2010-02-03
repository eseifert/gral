/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
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

package openjchart.plots.shapes;

import java.awt.Shape;

import openjchart.Drawable;
import openjchart.data.Row;
import openjchart.util.SettingsStorage;


/**
 * An interface providing functions for rendering shapes in a diagram.
 * It defines methods for:
 * <ul>
 * <li>Retrieving the shape of a certain cell in a DataTable</li>
 * <li>Getting and setting the shapes color</li>
 * <li>Getting and setting the bounds of the shape</li>
 * </ul>
 */
public interface ShapeRenderer extends SettingsStorage {
	/** Key for specifying the {@link java.awt.Shape} instance defining the form of the point. */
	static final String KEY_SHAPE = "shape";
	/** Key for specifying the {@link java.awt.Paint} instance to be used to paint the point shape. */
	static final String KEY_COLOR = "shape.color";
	/** Key for specifying whether the data value of a point is displayed or not. */
	static final String KEY_VALUE_DISPLAYED = "shape.valuedisplayed";
	/** Key for specifying the {@link java.text.Format} instance to be used to format the displayed data values. */
	static final String KEY_FORMAT = "shape.format";

	/**
	 * Returns the shape to be drawn for the specified data value.
	 * @param row Row data at point
	 * @return Drawable that represents the shape
	 */
	Drawable getShape(Row row);

	/**
	 * Returns a <code>Shape</code> instance that can be used
	 * for further calculations.
	 * @param row Row data at point
	 * @return Outline that describes the shape
	 */
	Shape getShapePath(Row row);
}

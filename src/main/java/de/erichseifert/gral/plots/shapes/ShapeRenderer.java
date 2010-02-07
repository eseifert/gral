/* GRAL : a free graphing library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.plots.shapes;

import java.awt.Shape;

import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.util.SettingsStorage;



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
	static final String KEY_VALUE_DISPLAYED = "shape.value.displayed";
	/** Key for specifying the {@link java.text.Format} instance to be used to format the displayed data values. */
	static final String KEY_VALUE_FORMAT = "shape.value.format";
	/** Key for specifying the {@link java.lang.Float} value that positions the value horizontally. */
	static final String KEY_VALUE_ALIGNMENT_X = "shape.value.alignment.x";
	/** Key for specifying the {@link java.lang.Float} value that positions the value vertically. */
	static final String KEY_VALUE_ALIGNMENT_Y = "shape.value.alignment.y";
	/** Key for specifying the {@link java.awt.Paint} instance to be used to paint the value. */
	static final String KEY_VALUE_COLOR = "shape.value.paint";

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

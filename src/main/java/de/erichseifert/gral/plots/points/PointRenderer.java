/**
 * GRAL: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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
 * Lesser GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.plots.points;

import java.awt.Shape;

import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer2D;
import de.erichseifert.gral.util.SettingsStorage;



/**
 * An interface providing functions for rendering points in a diagram.
 * It defines methods for:
 * <ul>
 * <li>Retrieving the point of a certain cell in a DataTable</li>
 * <li>Getting and setting the points color</li>
 * <li>Getting and setting the bounds of the points</li>
 * </ul>
 */
public interface PointRenderer extends SettingsStorage {
	/** Key for specifying the {@link java.awt.Shape} instance defining the form of the point. */
	static final String KEY_SHAPE = "point";
	/** Key for specifying the {@link java.awt.Paint} instance to be used to paint the point shape. */
	static final String KEY_COLOR = "point.color";

	/** Key for specifying whether the data value of a point is displayed or not. */
	static final String KEY_VALUE_DISPLAYED = "point.value.displayed";
	/** Key for specifying the {@link java.text.Format} instance to be used to format the displayed data values. */
	static final String KEY_VALUE_FORMAT = "point.value.format";
	/** Key for specifying the {@link java.lang.Float} value that positions the value horizontally. */
	static final String KEY_VALUE_ALIGNMENT_X = "point.value.alignment.x";
	/** Key for specifying the {@link java.lang.Float} value that positions the value vertically. */
	static final String KEY_VALUE_ALIGNMENT_Y = "point.value.alignment.y";
	/** Key for specifying the {@link java.awt.Paint} instance to be used to paint the value. */
	static final String KEY_VALUE_COLOR = "point.value.paint";

	/** Key for specifying whether the error value is displayed. */
	static final String KEY_ERROR_DISPLAYED = "point.error.displayed";
	/** Key for specifying the {@link java.awt.Paint} instance to be used to paint the error bars. */
	static final String KEY_ERROR_COLOR = "point.error.color";
	/** Key for specifying the {@link java.awt.Shape} instance defining the form of the points at the end of the error bars. */
	static final String KEY_ERROR_SHAPE = "point.error.shape";
	/** Key for specifying the {@link java.awt.Stroke} instance defining the error bars. */
	static final String KEY_ERROR_STROKE = "point.error.stroke";

	/**
	 * Returns the shape to be drawn for the specified data value.
	 * @param row Row data at point
	 * @return Drawable that represents the point
	 */
	Drawable getPoint(Row row, Axis axisY, AxisRenderer2D axisYRenderer);

	/**
	 * Returns a <code>Shape</code> instance that can be used
	 * for further calculations.
	 * @param row Row data at point
	 * @return Outline that describes the point.
	 */
	Shape getPointPath(Row row);
}

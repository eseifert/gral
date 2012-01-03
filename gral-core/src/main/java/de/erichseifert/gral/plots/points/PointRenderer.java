/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2012 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael[at]erichseifert.de>
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
package de.erichseifert.gral.plots.points;

import java.awt.Shape;

import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.util.SettingsStorage;



/**
 * <p>An interface providing functions for rendering points in a plot.
 * It defines methods for:</p>
 * <ul>
 *   <li>Retrieving the point of a certain row in a DataTable</li>
 *   <li>Getting and setting the points color</li>
 *   <li>Getting and setting the bounds of the points</li>
 * </ul>
 */
public interface PointRenderer extends SettingsStorage {
	/** Key for specifying the {@link java.awt.Shape} instance defining the
	form of the point. */
	static final Key SHAPE =
		new Key("point"); //$NON-NLS-1$
	/** Key for specifying an instance either of
	{@link de.erichseifert.gral.colors.ColorMapper} or {@link java.awt.Paint}
	that will be used to paint the point shapes. */
	static final Key COLOR =
		new Key("point.color"); //$NON-NLS-1$

	/** Key for specifying a {@link Boolean} value whether the data
	value of a point is displayed or not. */
	static final Key VALUE_DISPLAYED =
		new Key("point.value.displayed"); //$NON-NLS-1$
	/** Key for specifying a {@link Integer} value for the index of
	the column that contains the displayed values. */
	static final Key VALUE_COLUMN =
		new Key("point.value.column"); //$NON-NLS-1$
	/** Key for specifying the {@link java.text.Format} instance to be used to
	format the displayed data values. */
	static final Key VALUE_FORMAT =
		new Key("point.value.format"); //$NON-NLS-1$
	/** Key for specifying a {@link de.erichseifert.gral.util.Location} value for
	the positioning of the data value relative to the data point. */
	public static final Key VALUE_LOCATION =
		new Key("point.value.location"); //$NON-NLS-1$
	/** Key for specifying a {@link Number} value that positions
	the value horizontally. */
	static final Key VALUE_ALIGNMENT_X =
		new Key("point.value.alignment.x"); //$NON-NLS-1$
	/** Key for specifying a {@link Number} value that positions
	the value vertically. */
	static final Key VALUE_ALIGNMENT_Y =
		new Key("point.value.alignment.y"); //$NON-NLS-1$
	/** Key for specifying a {@link Number} value for setting the
	rotation of the value in degrees. */
	static final Key VALUE_ROTATION =
		new Key("point.value.rotation"); //$NON-NLS-1$
	/** Key for specifying a {@link Number} value for the distance
	 of values to the point. The distance is specified relative to the font
	 height. */
	static final Key VALUE_DISTANCE =
		new Key("point.value.distance"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	paint the value. */
	static final Key VALUE_COLOR =
		new Key("point.value.paint"); //$NON-NLS-1$
	/** Key for specifying an instance either of
	{@link de.erichseifert.gral.colors.ColorMapper} or {@link java.awt.Paint}
	that will be used to paint the value text. */
	static final Key VALUE_FONT =
		new Key("point.value.font"); //$NON-NLS-1$

	/** Key for specifying a {@link Boolean} value whether the error
	value is displayed. */
	static final Key ERROR_DISPLAYED =
		new Key("point.error.displayed"); //$NON-NLS-1$
	/** Key for specifying a {@link Integer} value for the index of
	the column that contains the upper error value. */
	static final Key ERROR_COLUMN_TOP =
		new Key("point.error.columnTop"); //$NON-NLS-1$
	/** Key for specifying a {@link Integer} value for the index of
	the column that contains the lower error value. */
	static final Key ERROR_COLUMN_BOTTOM =
		new Key("point.error.columnBottom"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	paint the error bars. */
	static final Key ERROR_COLOR =
		new Key("point.error.paint"); //$NON-NLS-1$
	/** Key for specifying an instance either of
	{@link de.erichseifert.gral.colors.ColorMapper} or {@link java.awt.Paint}
	that will be used to paint the error bars. */
	static final Key ERROR_SHAPE =
		new Key("point.error.shape"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Stroke} instance defining the
	error bars. */
	static final Key ERROR_STROKE =
		new Key("point.error.stroke"); //$NON-NLS-1$

	/**
	 * Returns the graphical representation to be drawn for the specified data
	 * value.
	 * @param axis that is used to project the point.
	 * @param axisRenderer Renderer for the axis.
	 * @param row Data row containing the point.
	 * @param col Index of the column that will be projected on the axis.
	 * @return Component that can be used to draw the point.
	 */
	Drawable getPoint(Axis axis, AxisRenderer axisRenderer, Row row, int col);

	/**
	 * Returns a {@code Shape} instance that can be used for further
	 * calculations.
	 * @param row Data row containing the point.
	 * @return Outline that describes the point's shape.
	 */
	Shape getPointPath(Row row);
}

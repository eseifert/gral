/**
 * GRAL : Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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

package de.erichseifert.gral.plots.lines;

import java.awt.Shape;

import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.plots.DataPoint2D;
import de.erichseifert.gral.util.SettingsStorage;


/**
 * Interface that provides functions for rendering a line in 2-dimensional
 * space.
 * Functionality includes:
 * <ul>
 * <li>Punching data points out of the line's shape</li>
 * <li>Administration of settings</li>
 * </ul>
 */
public interface LineRenderer2D extends SettingsStorage {
	/** Key for specifying the {@link java.awt.Stroke} instance to be used to define the line shape. */
	public static final String KEY_STROKE = "line.stroke";
	/** Key for specifying the distance between the line and a shape. */
	public static final String KEY_GAP = "line.gap.size";
	/** Key for specifying whether the gaps should have rounded corners. */
	public static final String KEY_GAP_ROUNDED = "line.gap.rounded";
	/** Key for specifying the {@link java.awt.Paint} instance to be used to paint the line shape. */
	public static final String KEY_COLOR = "line.color";

	/**
	 * Returns a <code>Drawable</code> for the line represented by the specified points.
	 * @param points Points to be used for creating the line.
	 * @return Representation of the line.
	 */
	Drawable getLine(DataPoint2D... points);

	/**
	 * Returns the shape of a line from which the shapes of the specified
	 * points was subtracted.
	 * @param lineShape Shape of the line.
	 * @param points Data points on the line.
	 * @return Punched shape.
	 */
	Shape punchShapes(Shape lineShape, DataPoint2D... points);
}

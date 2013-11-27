/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.gral.plots.areas;

import java.awt.Shape;
import java.util.List;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.plots.DataPoint;
import de.erichseifert.gral.plots.settings.Key;
import de.erichseifert.gral.plots.settings.SettingsStorage;

/**
 * Interface for renderers that display areas in plots.
 */
public interface AreaRenderer extends SettingsStorage {
	/** Key for specifying the {@link java.awt.Paint} instance used to fill the
	area shape. */
	Key COLOR = new Key("area.color"); //$NON-NLS-1$

	/**
	 * Returns the shape used for rendering the area of a data points.
	 * @param points Data points.
	 * @return Geometric shape for the area of the specified data points.
	 */
	Shape getAreaShape(List<DataPoint> points);

	/**
	 * Returns the graphical representation to be drawn for the specified data
	 * points.
	 * @param points Points that define the shape of the area.
	 * @param shape Geometric shape of the area.
	 * @return Representation of the area.
	 */
	Drawable getArea(List<DataPoint> points, Shape shape);

	// TODO: Mention which unit the Gap property has (pixels?)
	/**
	 * Returns the value for the gap between the area and a data point.
	 * @return Gap between area and data point.
	 */
	Number getGap();

	/**
	 * Sets the value for the gap between the area and a data point.
	 * @param gap Gap between area and data point.
	 */
	void setGap(Number gap);

	/**
	 * Returns whether the gaps should have rounded corners.
	 * @return {@code true}, if the gaps should have rounded corners.
	 */
	boolean isGapRounded();
	/**
	 * Sets a value which decides whether the gaps should have rounded corners.
	 * @param gapRounded {@code true}, if the gaps should have rounded corners.
	 */
	void setGapRounded(boolean gapRounded);
}

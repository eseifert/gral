/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2010 Erich Seifert <dev[at]richseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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

package de.erichseifert.gral.plots.lines;

import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.plots.DataPoint;
import de.erichseifert.gral.util.SettingsStorage;
import de.erichseifert.gral.util.Settings.Key;


/**
 * <p>Interface that provides functions for rendering a line in 2-dimensional
 * space.</p>
 * <p>Functionality includes:<p>
 * <ul>
 *   <li>Punching data points out of the line's shape</li>
 *   <li>Administration of settings</li>
 * </ul>
 */
public interface LineRenderer extends SettingsStorage {
	/** Key for specifying the {@link java.awt.Stroke} instance to be used to define the line shape. */
	static final Key STROKE = new Key("line.stroke");
	/** Key for specifying a {@link java.lang.Number} value for the gap
	between the line and a point. If the gap value is <=0 no gap will be used. */
	static final Key GAP = new Key("line.gap.size");
	/** Key for specifying a {@link java.lang.Boolean} value which decides
	whether the gaps should have rounded corners. */
	static final Key GAP_ROUNDED = new Key("line.gap.rounded");
	/** Key for specifying the {@link java.awt.Paint} instance to be used to paint the line shape. */
	static final Key COLOR = new Key("line.color");

	/**
	 * Returns a graphical representation for the line defined by <code>points</code>.
	 * @param points Points to be used for creating the line.
	 * @return Representation of the line.
	 */
	Drawable getLine(Iterable<DataPoint> points);

}

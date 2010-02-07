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

package de.erichseifert.gral;

import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;


/**
 * Interface providing functions for a lightweight component that can be drawn on the screen.
 * Functions include:
 * <ul>
 * <li>Getting and setting the bounds</li>
 * <li>Getting the preferred size</li>
 * <li>A drawing routine</li>
 * </ul>
 */
public interface Drawable {
	/**
	 * Returns the bounds of this Drawable.
	 * @return a bounding rectangle
	 */
	Rectangle2D getBounds();
	/**
	 * Sets the bounds to the specified bounding rectangle.
	 * @param bounds rectangle containing the component.
	 */
	void setBounds(Rectangle2D bounds);
	/**
	 * Sets the bounds to the specified coordinates, width and height.
	 * @param x horizontal position of the upper-left corner
	 * @param y vertical position of the upper-left corner
	 * @param width horizontal extent
	 * @param height vertical extent
	 */
	void setBounds(double x, double y, double width, double height);

	/**
	 * Returns the x-position of the bounds.
	 * @return horizontal position of the upper-left corner of the bounding rectangle
	 */
	double getX();
	/**
	 * Returns the y-position of the bounds.
	 * @return vertical position of the upper-left corner of the bounding rectangle
	 */
	double getY();

	/**
	 * Returns the width of the bounds.
	 * @return horizontal extent
	 */
	double getWidth();
	/**
	 * Returns the height of the bounds.
	 * @return vertical extent
	 */
	double getHeight();

	/**
	 * Returns the preferred size of the Drawable.
	 * @return horizontal and vertical extent that wants to be reached
	 */
	Dimension2D getPreferredSize();

	/**
	 * Draws the Drawable with the specified Graphics2D object.
	 * @param g2d graphics object to be provided
	 */
	void draw(Graphics2D g2d);
}

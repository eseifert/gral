/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <mseifert[at]error-reports.org>
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
package de.erichseifert.gral.graphics;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;


/**
 * Interface providing functions for a lightweight component that can be drawn
 * on the screen. Functions include management of the bounding rectangle,
 * returning a preferred size for layout operations, or drawing using a
 * specified context.
 */
public interface Drawable {
	/**
	 * Returns the bounds of this {@code Drawable}.
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
	 * This method should be used when overriding functionality.
	 * @param x horizontal position of the upper-left corner
	 * @param y vertical position of the upper-left corner
	 * @param width horizontal extent
	 * @param height vertical extent
	 */
	void setBounds(double x, double y, double width, double height);

	/**
	 * Returns the x-position of the bounds.
	 * @return horizontal position of the upper-left corner of the bounding
	 * rectangle
	 */
	double getX();
	/**
	 * Returns the y-position of the bounds.
	 * @return vertical position of the upper-left corner of the bounding
	 * rectangle
	 */
	double getY();

	/**
	 * Sets the position to the specified coordinates.
	 * @param x Coordinate on the x-axis.
	 * @param y Coordinate on the y-axis.
	 */
	void setPosition(double x, double y);

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
	 * Returns the preferred size of the {@code Drawable}.
	 * @return horizontal and vertical extent that wants to be reached
	 */
	Dimension2D getPreferredSize();

	/**
	 * Draws the {@code Drawable} with the specified drawing context.
	 * @param context Environment used for drawing
	 */
	void draw(DrawingContext context);
}

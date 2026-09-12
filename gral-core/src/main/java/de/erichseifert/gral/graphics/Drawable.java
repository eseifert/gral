/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2026 Erich Seifert <dev[at]erichseifert.de>,
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
 * <p>Something that can paint itself into a rectangle. This is the common
 * interface of everything visible in GRAL: plots, axes, labels, legends and the
 * shapes produced by the renderers.</p>
 *
 * <p>A {@code Drawable} deliberately does <em>not</em> extend
 * {@code java.awt.Component}. It carries no peer, no event dispatching and no
 * toolkit state; all it needs is a {@code Graphics2D} to draw on. That is what
 * allows the same object to be painted into a Swing panel, a
 * {@code BufferedImage}, a PDF document or a printer page, on a headless
 * machine as well as on a desktop.</p>
 *
 * <p>A drawable has two pieces of geometry. Its <em>bounds</em> say where and
 * how large it is; the owner &mdash; a container's layout, a panel, or an
 * exporting writer &mdash; sets them, and {@code draw} has to honor them. Its
 * <em>preferred size</em> is advisory: it says how large the drawable would
 * like to be, and a layout may or may not grant it.</p>
 *
 * <p>Coordinates are absolute, not relative to the drawable: an implementation
 * of {@code draw} starts from {@link #getX()} and {@link #getY()} rather than
 * from the origin, and must not leave the {@code Graphics2D} with a modified
 * transform, clip, paint or stroke when it returns.</p>
 *
 * <pre>
 * Drawable box = new AbstractDrawable() {
 *     public void draw(DrawingContext context) {
 *         Graphics2D graphics = context.getGraphics();
 *         Paint paintOld = graphics.getPaint();
 *         graphics.setPaint(Color.BLUE);
 *         graphics.fill(getBounds());
 *         graphics.setPaint(paintOld);
 *     }
 * };
 * box.setBounds(10.0, 10.0, 100.0, 50.0);
 * box.draw(new DrawingContext(graphics2D));
 * </pre>
 *
 * <p>Implementations normally extend {@link AbstractDrawable}, which already
 * handles the bounds. Drawing is not thread-safe: a drawable is expected to be
 * painted by one thread at a time.</p>
 *
 * @see AbstractDrawable
 * @see DrawableContainer
 * @see DrawingContext
 */
public interface Drawable {
	/**
	 * Returns the area this {@code Drawable} occupies. The returned rectangle
	 * is a copy; modifying it does not move the drawable.
	 * @return a bounding rectangle
	 */
	Rectangle2D getBounds();
	/**
	 * Sets the bounds to the specified bounding rectangle. Only the position
	 * and size are read from the rectangle; it is not retained.
	 * @param bounds rectangle containing the component.
	 */
	void setBounds(Rectangle2D bounds);
	/**
	 * Sets the bounds to the specified coordinates, width and height. This is
	 * the method the other bounds setters funnel into, so it is the one to
	 * override in order to react to a change of size or position.
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
	 * Returns the size this {@code Drawable} would like to have. This is only a
	 * hint for layout managers; the actual size is whatever
	 * {@link #setBounds(double, double, double, double)} was given, and may be
	 * smaller or larger.
	 * @return horizontal and vertical extent the drawable asks for
	 */
	Dimension2D getPreferredSize();

	/**
	 * Paints this {@code Drawable} within its current bounds. The drawable must
	 * restore any property of the context's {@code Graphics2D} that it changes,
	 * so that siblings drawn afterwards are unaffected.
	 * @param context Environment used for drawing
	 */
	void draw(DrawingContext context);
}

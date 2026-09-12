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
import java.io.Serializable;


/**
 * <p>Base class for {@link Drawable} implementations. It stores the bounding
 * rectangle and implements every accessor for it, so that a subclass only has
 * to provide {@link #draw(DrawingContext)} and, in most cases,
 * {@link #getPreferredSize()}.</p>
 *
 * <pre>
 * public class Cross extends AbstractDrawable {
 *     public void draw(DrawingContext context) {
 *         Graphics2D graphics = context.getGraphics();
 *         graphics.draw(new Line2D.Double(
 *             getX(), getY(), getX() + getWidth(), getY() + getHeight()));
 *         graphics.draw(new Line2D.Double(
 *             getX(), getY() + getHeight(), getX() + getWidth(), getY()));
 *     }
 *
 *     public Dimension2D getPreferredSize() {
 *         return new de.erichseifert.gral.graphics.Dimension2D.Double(10.0, 10.0);
 *     }
 * }
 * </pre>
 *
 * <p>A subclass that has to react to a change of size or position should
 * override {@link #setBounds(double, double, double, double)}, which the other
 * bounds setters delegate to, and call the super implementation.</p>
 */
public abstract class AbstractDrawable implements Drawable, Serializable {
	/** Version id for serialization. */
	private static final long serialVersionUID = -684598008467326484L;

	/** Boundaries of the drawable object. */
	private final Rectangle2D bounds;

	/**
	 * Creates a drawable with empty bounds at the origin. An owner is expected
	 * to set the bounds before the drawable is painted.
	 */
	public AbstractDrawable() {
		bounds = new Rectangle2D.Double();
	}

	/**
	 * Returns the area this {@code Drawable} occupies. The returned rectangle
	 * is a fresh copy, so modifying it does not move the drawable.
	 * @return a bounding rectangle
	 */
	public Rectangle2D getBounds() {
		var b = new Rectangle2D.Double();
		b.setFrame(bounds);
		return b;
	}

	/**
	 * Returns the x-position of the bounds.
	 * @return horizontal position of the upper-left corner of the bounding
	 *         rectangle.
	 */
	public double getX() {
		return bounds.getX();
	}
	/**
	 * Returns the y-position of the bounds.
	 * @return vertical position of the upper-left corner of the bounding
	 *         rectangle.
	 */
	public double getY() {
		return bounds.getY();
	}

	/**
	 * Returns the width of the bounds.
	 * @return horizontal extent.
	 */
	public double getWidth() {
		return bounds.getWidth();
	}
	/**
	 * Returns the height of the bounds.
	 * @return vertical extent.
	 */
	public double getHeight() {
		return bounds.getHeight();
	}

	/**
	 * Sets the bounds to the specified bounding rectangle.
	 * @param bounds rectangle containing the component.
	 */
	public void setBounds(Rectangle2D bounds) {
		setBounds(bounds.getX(), bounds.getY(),
			bounds.getWidth(), bounds.getHeight());
	}
	/**
	 * Sets the bounds to the specified coordinates, width and height. All other
	 * ways of changing the bounds end up here, so this is the method to
	 * override in order to react to a change of size or position.
	 * @param x horizontal position of the upper-left corner
	 * @param y vertical position of the upper-left corner
	 * @param width horizontal extent
	 * @param height vertical extent
	 */
	public void setBounds(double x, double y, double width, double height) {
		bounds.setFrame(x, y, width, height);
	}

	/**
	 * Returns the size this {@code Drawable} would like to have. The default is
	 * an empty size, which tells a layout that the drawable makes no demand;
	 * subclasses that know their natural size should override this.
	 * @return horizontal and vertical extent the drawable asks for
	 */
	public Dimension2D getPreferredSize() {
		return new de.erichseifert.gral.graphics.Dimension2D.Double();
	}

	@Override
	public void setPosition(double x, double y) {
		bounds.setFrame(x, y, bounds.getWidth(), bounds.getHeight());
	}
}

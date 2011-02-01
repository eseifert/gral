/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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
package de.erichseifert.gral;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import de.erichseifert.gral.util.BasicSettingsStorage;


/**
 * Abstract implementation of the {@link Drawable} interface.
 * This class implements common functionality like the different ways for
 * getting and setting the bounding rectangle of the drawable object.
 */
public abstract class AbstractDrawable extends BasicSettingsStorage
		implements Drawable {
	/** Boundaries of the drawable object. */
	private final Rectangle2D bounds;

	/**
	 * Creates an AbstractDrawable.
	 */
	public AbstractDrawable() {
		bounds = new Rectangle2D.Double();
	}

	@Override
	public Rectangle2D getBounds() {
		Rectangle2D b = new Rectangle2D.Double();
		b.setFrame(bounds);
		return b;
	}

	@Override
	public double getHeight() {
		return bounds.getHeight();
	}

	@Override
	public double getWidth() {
		return bounds.getWidth();
	}

	@Override
	public double getX() {
		return bounds.getX();
	}

	@Override
	public double getY() {
		return bounds.getY();
	}

	@Override
	public void setBounds(Rectangle2D bounds) {
		setBounds(bounds.getX(), bounds.getY(),
				bounds.getWidth(), bounds.getHeight());
	}

	@Override
	public void setBounds(double x, double y, double width, double height) {
		bounds.setFrame(x, y, width, height);
	}

	@Override
	public Dimension2D getPreferredSize() {
		return new de.erichseifert.gral.util.Dimension2D.Double();
	}
}

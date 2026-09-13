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
package de.erichseifert.gral.graphics.layout;

/**
 * Base class for {@link Layout} implementations. It stores the horizontal and
 * vertical gaps between components and leaves
 * {@link Layout#layout(de.erichseifert.gral.graphics.Container)} and
 * {@link Layout#getPreferredSize(de.erichseifert.gral.graphics.Container)} to
 * subclasses.
 */
public abstract class AbstractLayout implements Layout {
	/** Horizontal spacing of components. */
	private double gapX;
	/** Vertical spacing of components. */
	private double gapY;

	/**
	 * Initializes a layout with the specified space between components.
	 * @param gapX Horizontal gap in pixels.
	 * @param gapY Vertical gap in pixels.
	 */
	public AbstractLayout(double gapX, double gapY) {
		this.gapX = gapX;
		this.gapY = gapY;
	}

	@Override
	public double getGapX() {
		return gapX;
	}

	@Override
	public void setGapX(double gapX) {
		this.gapX = gapX;
	}

	@Override
	public double getGapY() {
		return gapY;
	}

	@Override
	public void setGapY(double gapY) {
		this.gapY = gapY;
	}
}

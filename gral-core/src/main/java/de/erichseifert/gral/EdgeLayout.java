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

import de.erichseifert.gral.util.Insets2D;


/**
 * Implementation of Layout that arranges a {@link Container}'s components
 * according to a certain grid. This is similar to Java's
 * {@link java.awt.BorderLayout}, but also allows components to be placed in
 * each of the corners.
 */
public class EdgeLayout implements Layout {
	// TODO Add setters and getters.
	// FIXME Use SettingsStorage?
	// FIXME Extract abstract superclass as an additional layer between Layout and EdgeLayout?
	/** Horizontal spacing. */
	private final double hgap;
	/** Vertical spacing. */
	private final double vgap;

	/**
	 * Initializes a layout manager object with the specified distances between the
	 * components.
	 * @param hgap Horizontal gap.
	 * @param vgap Vertical gap.
	 */
	public EdgeLayout(double hgap, double vgap) {
		this.hgap = hgap;
		this.vgap = vgap;
	}

	@Override
	public void layout(Container container) {
		Insets2D insets = container.getInsets();
		if (insets == null) {
			insets = new Insets2D.Double();
		}

		Rectangle2D bounds = container.getBounds();

		// Fetch components
		Drawable center = null,
		         north = null, northEast = null, east = null, southEast = null,
		         south = null, southWest = null, west = null, northWest = null;
		for (Drawable d: container) {
			Location constraints = (Location) container.getConstraints(d);
			if (Location.CENTER.equals(constraints)) {
				center = d;
			} else if (Location.NORTH.equals(constraints)) {
				north = d;
			} else if (Location.NORTH_EAST.equals(constraints)) {
				northEast = d;
			} else if (Location.EAST.equals(constraints)) {
				east = d;
			} else if (Location.SOUTH_EAST.equals(constraints)) {
				southEast = d;
			} else if (Location.SOUTH.equals(constraints)) {
				south = d;
			} else if (Location.SOUTH_WEST.equals(constraints)) {
				southWest = d;
			} else if (Location.WEST.equals(constraints)) {
				west = d;
			} else if (Location.NORTH_WEST.equals(constraints)) {
				northWest = d;
			}
		}

		// Calculate maximum widths and heights
		double widthWest    = getMaxWidth(northWest,  west,   southWest);
		double widthEast    = getMaxWidth(northEast,  east,   southEast);
		double heightNorth  = getMaxHeight(northWest, north,  northEast);
		double heightSouth  = getMaxHeight(southWest, south,  southEast);

		double hgapEast  = (widthWest > 0.0 && center != null) ? hgap : 0.0;
		double hgapWest  = (widthEast > 0.0 && center != null) ? hgap : 0.0;
		double vgapNorth = (heightNorth > 0.0 && center != null) ? vgap : 0.0;
		double vgapSouth = (heightSouth > 0.0 && center != null) ? vgap : 0.0;

		double xWest   = bounds.getMinX() + insets.getLeft();
		double xCenter = xWest + widthWest + hgapEast;
		double xEast   = bounds.getMaxX() - insets.getRight() - widthEast;
		double yNorth  = bounds.getMinY() + insets.getTop();
		double yCenter = yNorth + heightNorth + vgapNorth;
		double ySouth  = bounds.getMaxY() - insets.getBottom() - heightSouth;

		layoutComponent(northWest,
			xWest, yNorth,
			widthWest, heightNorth
		);

		layoutComponent(north,
			xCenter, yNorth,
			bounds.getWidth() - insets.getLeft() - widthWest - widthEast - insets.getRight(),
			heightNorth
		);

		layoutComponent(northEast,
			xEast, yNorth,
			widthEast, heightNorth
		);

		layoutComponent(east,
			xEast, yCenter,
			widthEast,
			bounds.getHeight() - insets.getTop() - heightSouth - insets.getBottom()
		);

		layoutComponent(southEast,
			xEast, ySouth,
			widthEast,
			heightSouth
		);

		layoutComponent(south,
			xCenter, ySouth,
			bounds.getWidth() - insets.getLeft() - widthWest - widthEast - insets.getRight(),
			heightSouth
		);

		layoutComponent(southWest,
			xWest, ySouth,
			widthWest,
			heightSouth
		);

		layoutComponent(west,
			xWest, yCenter,
			widthWest,
			bounds.getHeight() - insets.getTop() - heightNorth - heightSouth - insets.getBottom()
		);

		layoutComponent(center,
			xCenter, yCenter,
				bounds.getWidth()
					- insets.getLeft() - widthWest - widthEast
					- insets.getRight() - hgapEast - hgapWest,
				bounds.getHeight()
					- insets.getTop() - heightNorth - heightSouth
					- insets.getBottom() - vgapNorth - vgapSouth
		);
	}

	@Override
	public Dimension2D getPreferredSize(Container container) {
		// Fetch components
		Drawable north = null, northEast = null, east = null, southEast = null,
		         south = null, southWest = null, west = null, northWest = null,
		         center = null;
		for (Drawable d: container) {
			Object constraints = container.getConstraints(d);
			if (Location.NORTH.equals(constraints)) {
				north = d;
			} else if (Location.NORTH_EAST.equals(constraints)) {
				northEast = d;
			} else if (Location.EAST.equals(constraints)) {
				east = d;
			} else if (Location.SOUTH_EAST.equals(constraints)) {
				southEast = d;
			} else if (Location.SOUTH.equals(constraints)) {
				south = d;
			} else if (Location.SOUTH_WEST.equals(constraints)) {
				southWest = d;
			} else if (Location.WEST.equals(constraints)) {
				west = d;
			} else if (Location.NORTH_WEST.equals(constraints)) {
				northWest = d;
			} else if (Location.CENTER.equals(constraints)) {
				center = d;
			}
		}

		// Calculate maximum widths and heights
		double widthWest    = getMaxWidth(northWest,  west,   southWest);
		double widthCenter  = getMaxWidth(north,      center, south);
		double widthEast    = getMaxWidth(northEast,  east,   southEast);
		double heightNorth  = getMaxHeight(northWest, north,  northEast);
		double heightCenter = getMaxHeight(west,      center, east);
		double heightSouth  = getMaxHeight(southWest, south,  southEast);

		double hgapEast  = (widthWest > 0.0 && center != null) ? hgap : 0.0;
		double hgapWest  = (widthEast > 0.0 && center != null) ? hgap : 0.0;
		double vgapNorth = (heightNorth > 0.0 && center != null) ? vgap : 0.0;
		double vgapSouth = (heightSouth > 0.0 && center != null) ? vgap : 0.0;

		// Calculate preferred dimensions
		Insets2D insets = container.getInsets();
		return new de.erichseifert.gral.util.Dimension2D.Double(
			insets.getLeft() + widthEast + hgapEast + widthCenter +
				hgapWest + widthWest + insets.getRight(),
			insets.getTop() + heightNorth + vgapNorth + heightCenter +
				vgapSouth + heightSouth + insets.getBottom()
		);
	}

	/**
	 * Returns the maximum width of an array of Drawables.
	 * @param drawables Drawables to be measured.
	 * @return Maximum horizontal extent.
	 */
	private static double getMaxWidth(Drawable... drawables) {
		double width = 0.0;
		for (Drawable d : drawables) {
			if (d == null) {
				continue;
			}
			width = Math.max(width, d.getPreferredSize().getWidth());
		}

		return width;
	}

	/**
	 * Returns the maximum height of an array of Drawables.
	 * @param drawables Drawables to be measured.
	 * @return Maximum vertical extent.
	 */
	private static double getMaxHeight(Drawable... drawables) {
		double height = 0.0;
		for (Drawable d : drawables) {
			if (d == null) {
				continue;
			}
			height = Math.max(height, d.getPreferredSize().getHeight());
		}

		return height;
	}

	/**
	 * Sets the bounds of the specified Drawable to the specified values.
	 * @param d Drawable to be aligned.
	 * @param x X-coordinate.
	 * @param y Y-coordinate.
	 * @param w Width.
	 * @param h Height.
	 */
	private static void layoutComponent(Drawable d,
			double x, double y, double w, double h) {
		if (d == null) {
			return;
		}
		d.setBounds(x, y, w, h);
	}
}

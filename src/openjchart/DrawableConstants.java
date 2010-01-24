/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart;

/**
 * Interface that solely stores constants concerning Drawables.
 * Stored constants include location and orientation.
 */
public interface DrawableConstants {
	/**
	 * Indicates the location of a Drawable.
	 */
	static enum Location {
		/** Central location. */
		CENTER,
		/** Northern location. */
		NORTH,
		/** North-eastern location. */
		NORTH_EAST,
		/** Eastern location. */
		EAST,
		/** South-eastern location. */
		SOUTH_EAST,
		/** Southern location. */
		SOUTH,
		/** South-western location. */
		SOUTH_WEST,
		/** Western location. */
		WEST,
		/** North-western location. */
		NORTH_WEST
	};

	/**
	 * Orientation of a Drawable.
	 */
	static enum Orientation {
		/** Horizontal orientation. */
		HORIZONTAL,
		/** Vertical orientation. */
		VERTICAL
	};

}

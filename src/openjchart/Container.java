/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
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

import java.awt.geom.Rectangle2D;

import openjchart.util.Insets2D;

/**
 * An interface that provides functions to store and layout Drawables.
 */
public interface Container extends Iterable<Drawable> {
	/**
	 * Return the space that this DrawableContainer must leave at each of its edges.
	 * @return The insets of this DrawableContainer
	 */
	Insets2D getInsets();

	/**
	 * Sets the space that this DrawableContainer must leave at each of its edges.
	 * @param insets Insets to be set.
	 */
	void setInsets(Insets2D insets);

	/**
	 * Returns the bounds of this container.
	 * @return bounds
	 */
	Rectangle2D getBounds();

	/**
	 * Sets the bounds of this container.
	 * @param bounds Bounds
	 */
	void setBounds(Rectangle2D bounds);

	/**
	 * Returns the layout associated with this container.
	 * @return Layout manager
	 */
	Layout getLayout();

	/**
	 * Sets the layout associated with this container.
	 * @param layout Layout to be set.
	 */
	void setLayout(Layout layout);

	/**
	 * Adds a new component to this container.
	 * @param drawable Component
	 */
	void add(Drawable drawable);

	/**
	 * Adds a new component to this container.
	 * @param drawable Component
	 * @param constraints Additional information (e.g. for layout)
	 */
	void add(Drawable drawable, Object constraints);

	/**
	 * Return additional information on component
	 * @param drawable Component
	 * @return Information object or <code>null</code>
	 */
	Object getConstraints(Drawable drawable);

	/**
	 * Removes a component from this container.
	 * @param drawable Component
	 */
	void remove(Drawable drawable);

	/**
	 * Returns the number of components that are stored in this container.
	 * @return total number of components
	 */
	int size();
}

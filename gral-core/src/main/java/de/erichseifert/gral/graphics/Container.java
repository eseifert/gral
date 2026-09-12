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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import de.erichseifert.gral.graphics.layout.Layout;


/**
 * <p>A group of {@link Drawable}s that are positioned by a {@link Layout}. Each
 * component may carry a constraint object whose type is decided by the layout
 * &mdash; a {@link Location} for an
 * {@link de.erichseifert.gral.graphics.layout.EdgeLayout}, for instance.</p>
 *
 * <p>A container also reserves {@link Insets2D} at its edges that the layout has
 * to keep free. Iterating a container visits its components in the order they
 * were added, which is also the order in which they are drawn.</p>
 *
 * <p>{@link DrawableContainer} is the implementation, and it is a
 * {@code Drawable} itself, so containers nest.</p>
 *
 * @see DrawableContainer
 * @see Layout
 */
public interface Container extends Iterable<Drawable> {
	/**
	 * Returns the space that this container must preserve at each of its
	 * edges.
	 * @return The insets of this DrawableContainer
	 */
	Insets2D getInsets();

	/**
	 * Sets the space that this container must preserve at each of its
	 * edges.
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
	 * Recalculates this container's layout.
	 */
	void layout();

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
	 * Returns whether the specified {@code Drawable} is stored.
	 * @param drawable Element to be checked.
	 * @return {@code true} if the element is stored in the {@code Container},
	 * {@code false} otherwise.
	 */
	boolean contains(Drawable drawable);

	/**
	 * Returns the components at the specified point.
	 * The first component in the result {@code List} is the most
	 * specific component, i.e. the component with the deepest nesting level.
	 * If no component could be found an empty {@code List} will be returned.
	 * @param point Two-dimensional point.
	 * @return Components at the specified point, with the deepest nested component first.
	 */
	List<Drawable> getDrawablesAt(Point2D point);

	/**
	 * Returns a list of stored components.
	 * @return Contained drawables.
	 */
	List<Drawable> getDrawables();

	/**
	 * Returns the layout constraints the specified component was added with.
	 * @param drawable Component
	 * @return Constraints object, or {@code null} if the component was added
	 *         without any or is not in this container
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

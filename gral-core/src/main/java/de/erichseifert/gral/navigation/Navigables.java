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
package de.erichseifert.gral.navigation;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import de.erichseifert.gral.graphics.Container;
import de.erichseifert.gral.graphics.Drawable;

/**
 * <p>Static helpers for finding the {@link Navigable} object behind a point.
 * The class is not meant to be instantiated or extended.</p>
 *
 * <p>This is the step a binding to a user interface toolkit needs before it can
 * hand a gesture to a {@link Navigator}: a mouse event carries a position, and
 * what is navigated is the plot area under it. The lookup itself refers to no
 * toolkit, so every binding can share it.</p>
 */
public abstract class Navigables {
	/**
	 * Returns the navigable object at the specified point, or {@code null} if
	 * there is none. If the specified drawable is a container, its children are
	 * checked recursively.
	 * @param drawable Drawable to check, possibly a container of others.
	 * @param point Position that has to hit the navigable object.
	 * @return The navigable object at the position, or {@code null}.
	 */
	public static Navigable getNavigableAt(Drawable drawable, Point2D point) {
		List<Drawable> componentsToCheck;
		if (drawable instanceof Container) {
			componentsToCheck = ((Container) drawable).getDrawablesAt(point);
		} else {
			componentsToCheck = new ArrayList<>(1);
			componentsToCheck.add(drawable);
		}
		for (Drawable component : componentsToCheck) {
			if ((component instanceof Navigable) && component.getBounds().contains(point)) {
				return (Navigable) component;
			}
		}
		return null;
	}
}

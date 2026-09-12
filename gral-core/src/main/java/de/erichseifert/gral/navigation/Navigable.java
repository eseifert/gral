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

/**
 * <p>Implemented by objects that can be zoomed and panned. The object itself
 * does not carry the zoom level or the center of view; it hands out a
 * {@link Navigator} that holds them and applies them:</p>
 *
 * <pre>
 * Navigator navigator = plot.getNavigator();
 * navigator.setZoom(2.0);
 * </pre>
 *
 * <p>Implementations are expected to return the same navigator on every call,
 * so that the state of the view is not lost between calls and so that two
 * navigators can be {@link Navigator#connect(Navigator) connected}
 * reliably.</p>
 *
 * @see Navigator
 */
public interface Navigable {
	/**
	 * Returns the navigator that controls the view of this object. The same
	 * instance is returned on every call.
	 * @return A navigator instance.
	 */
	Navigator getNavigator();
}

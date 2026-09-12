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

import de.erichseifert.gral.util.PointND;


/**
 * <p>Receives notifications when an object is zoomed or panned. A
 * {@link Navigator} is itself a listener, which is how two navigators are
 * linked by {@link Navigator#connect(Navigator)}; an application implements
 * this interface to react to navigation, for example to keep a status bar in
 * step with the visible range.</p>
 *
 * <p>Notifications arrive only when a value has really changed, so a listener
 * does not have to filter out no-op events. A listener that changes the
 * navigator again from within a callback has to guard against looping
 * itself.</p>
 *
 * @see Navigator
 */
public interface NavigationListener {
	/**
	 * Called after the center of view has changed, i.e. after panning.
	 * @param event An object describing the change event.
	 */
	void centerChanged(NavigationEvent<PointND<? extends Number>> event);

	/**
	 * Called after the zoom level has changed.
	 * @param event An object describing the change event.
	 */
	void zoomChanged(NavigationEvent<Double> event);
}

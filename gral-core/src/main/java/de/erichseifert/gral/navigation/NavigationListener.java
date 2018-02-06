/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
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
 * An interface for classes that want to be notified on navigation changes like
 * panning or zooming.
 *
 * @see Navigator
 */
public interface NavigationListener {
	/**
	 * A method that gets called after the center of an object in the
	 * {@code PlotNavigator} has changed.
	 * @param event An object describing the change event.
	 */
	void centerChanged(NavigationEvent<PointND<? extends Number>> event);

	/**
	 * A method that gets called after the zoom level of an object in the
	 * {@code PlotNavigator} has changed.
	 * @param event An object describing the change event.
	 */
	void zoomChanged(NavigationEvent<Double> event);
}

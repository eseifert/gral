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
package de.erichseifert.gral.plots;

/**
 * An interface for classes that want to be notified on navigation changes like
 * panning or zooming.
 *
 * @see PlotNavigator
 */
public interface NavigationListener {
	/**
	 * A method that gets called when the center of an axis in the
	 * <code>PlotNavigator</code> has changed.
	 * @param source Object that has caused the change
	 * @param axisName Name of the axis that has changed
	 * @param centerOld Previous value of axis center
	 * @param centerNew New value of axis center
	 */
	void centerChanged(PlotNavigator source, String axisName,
			Number centerOld, Number centerNew);

	/**
	 * A method that gets called when the zoom level of an axis in the
	 * <code>PlotNavigator</code> has changed.
	 * @param source Object that has caused the change
	 * @param axisName Name of the axis that has changed
	 * @param zoomOld Previous zoom level of the axis
	 * @param zoomNew New zoom level of the axis
	 */
	void zoomChanged(PlotNavigator source, String axisName,
			double zoomOld, double zoomNew);
}

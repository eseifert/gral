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
 * An interface for translating navigational interactions, such as zooming
 * panning to control the associated {@link Navigable} object. At the moment
 * the only supported operations are zooming and panning.
 *
 * A navigator stores an default state of the object that can be used to reset
 * the object's state after actions have been performed. This must be
 * implemented with the methods {@link #setDefaultState()} and
 * {@link #reset()}.
 *
 * Zooming and panning may be activated and deactivated using the methods
 * {@link #setZoomable(boolean)} and {@link #setPannable(boolean)}.
 *
 * Additionally, the actions can also be bound to a certain direction—like
 * horizontal or vertical—by the convenience methods {@link #getDirection()}
 * and {@link #setDirection(NavigationDirection)}. The data type, e.g. an enum
 * type, for directions must implement the interface
 * {@link NavigationDirection}.
 *
 * Sometimes, actions performed on an object should be applied to another
 * object synchronously. The methods {@link #connect(Navigator)} and
 * {@link #disconnect(Navigator)} may be implemented to provide functionality
 * for this use case.
 */
public interface Navigator extends NavigationListener {
	/**
	 * Returns whether the associated object can be zoomed.
	 * @return {@code true} if the object can be zoomed,
	 *         {@code false} otherwise.
	 */
	boolean isZoomable();

	/**
	 * Sets whether the associated object can be zoomed.
	 * @param zoomable A value that tells whether it should be possible to zoom
	 *        the associated object.
	 */
	void setZoomable(boolean zoomable);

	/**
	 * Returns the current zoom level of the associated object.
	 * @return Current zoom level.
	 */
	double getZoom();

	/**
	 * Sets the zoom level of the associated object to the specified value.
	 * @param zoom New zoom level.
	 */
	void setZoom(double zoom);

	/**
	 * Increases the current zoom level by the specified zoom factor.
	 * The zoom will only be changed if the navigator is zoomable.
	 *
	 * @see #isZoomable()
	 * @see #setZoomable(boolean)
	 */
	void zoomIn();

	/**
	 * Decreases the current zoom level by the specified zoom factor.
	 * The zoom will only be changed if the navigator is zoomable.
	 *
	 * @see #isZoomable()
	 * @see #setZoomable(boolean)
	 */
	void zoomOut();

	/**
	 * Scale the associated object at the specified point. If zooming is disabled nothing will be done. If panning is
	 * disabled zooming will be applied around the current center.
	 * @param zoom New zoom level.
	 * @param zoomPoint Center point for zooming in world units.
	 */
	void zoomAt(double zoom, PointND<? extends Number> zoomPoint);

	/**
	 * Increases the current zoom level by the specified zoom factor and scales
	 * the associated object at the specified point.
	 * @param zoomPoint Center point for zooming in world units.
	 */
	void zoomInAt(PointND<? extends Number> zoomPoint);

	/**
	 * Decreases the current zoom level by the specified zoom factor and scales
	 * the associated object at the specified point.
	 * @param zoomPoint Center point for zooming in world units.
	 */
	void zoomOutAt(PointND<? extends Number> zoomPoint);

	/**
	 * Returns whether the associated object can be panned.
	 * @return {@code true} if the object can be panned,
	 *         {@code false} otherwise.
	 */
	boolean isPannable();

	/**
	 * Sets whether the associated object can be panned.
	 * @param pannable A value that tells whether it should be possible to pan
	 *        the associated object.
	 */
	void setPannable(boolean pannable);

	/**
	 * Returns the current center point. The returned point contains value in
	 * world units.
	 * @return Center point in world units.
	 */
	PointND<? extends Number> getCenter();

	/**
	 * Sets a new center point. The values of the point are in world units.
	 * The center point will only be changed if the navigator is pannable.
	 * @param center New center point in world units.
	 * @see #isPannable()
	 * @see #setPannable(boolean)
	 */
	void setCenter(PointND<? extends Number> center);

	/**
	 * Moves the center by the relative values of the specified point.
	 * The values of the point are in screen units.
	 * The center point will only be changed if the navigator is pannable.
	 * @param deltas Relative values to use for panning.
	 * @see #isPannable()
	 * @see #setPannable(boolean)
	 */
	void pan(PointND<? extends Number> deltas);

	/**
	 * Sets the current state as the default state of the object.
	 * Resetting the navigator will then return to the default state.
	 */
	void setDefaultState();

	/**
	 * Sets the object's position and zoom level to the default state.
	 */
	void reset();

	/**
	 * Returns the factor which is used to change the zoom level on
	 * zoom in/out actions.
	 * @return The current zoom factor.
	 */
	double getZoomFactor();

	/**
	 * Sets the factor which should be used to change the zoom level on
	 * zoom in/out actions.
	 * @param factor The new zoom factor.
	 */
	void setZoomFactor(double factor);

	/**
	 * Returns the minimal zoom factor.
	 * @return Minimal zoom factor.
	 */
	double getZoomMin();

	/**
	 * Sets the minimal zoom factor.
	 * @param min New minimal zoom factor.
	 */
	void setZoomMin(double min);

	/**
	 * Returns the minimal zoom factor.
	 * @return Maximal zoom factor.
	 */
	double getZoomMax();

	/**
	 * Sets the maximal zoom factor.
	 * @param max New maximal zoom factor.
	 */
	void setZoomMax(double max);

	/**
	 * Adds the specified listener object that gets notified on changes to
	 * navigation information like panning or zooming.
	 * @param l Listener object
	 */
	void addNavigationListener(NavigationListener l);

	/**
	 * Removes the specified listener object, i.e. it doesn't get notified on
	 * changes to navigation information like panning or zooming.
	 * @param l Listener object
	 */
	void removeNavigationListener(NavigationListener l);

	/**
	 * Returns the current direction of the components that will be taken into
	 * account for zooming and panning.
	 * @return Direction.
	 */
	NavigationDirection getDirection();

	/**
	 * Sets the direction of the components that will be taken into account for
	 * zooming and panning.
	 * @param direction Direction.
	 */
	void setDirection(NavigationDirection direction);

	/**
	 * Couples the actions of the current and the specified navigator. All
	 * actions applied to this navigator will be also applied to the specified
	 * navigator and vice versa.
	 * @param navigator Navigator which should be bound to this instance.
	 */
	void connect(Navigator navigator);

	/**
	 * Decouples the actions of the current and the connected specified
	 * navigator. All actions will be applied separately to each navigator.
	 * @param navigator Navigator to be unbound from this instance.
	 */
	void disconnect(Navigator navigator);
}

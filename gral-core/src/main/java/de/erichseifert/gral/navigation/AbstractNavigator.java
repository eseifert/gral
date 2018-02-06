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

import java.util.HashSet;
import java.util.Set;

import de.erichseifert.gral.util.PointND;

/**
 * Abstract base class that can be used to control the zoom and panning of an
 * object. The navigator translates actions to operations on the object.
 * The class provides implementations for zooming using a zoom factor,
 * management of listeners, getting and setting a main direction for actions,
 * and synchronizing actions with another navigator.
 *
 * Derived classes must use the methods
 * {@link #fireCenterChanged(NavigationEvent)} and
 * {@link #fireZoomChanged(NavigationEvent)} to notify listeners of changes to
 * the center or zoom level. To avoid loop states these methods must only be
 * called if a value has really been changed.
 */
public abstract class AbstractNavigator implements Navigator {
	/** Default zoom factor. */
	public static final double DEFAULT_ZOOM_FACTOR = 1.25;
	/** Default minimum of zoom factor. */
	public static final double DEFAULT_ZOOM_MIN = 1e-2;
	/** Default maximum of zoom factor. */
	public static final double DEFAULT_ZOOM_MAX = 1e+2;

	/** Object that will be notified on navigation actions. */
	private final Set<NavigationListener> navigationListeners;

	/** Zoom factor used for zoom in and zoom out actions. */
	private double zoomFactor;
	/** Minimum allowed zoom level. */
	private double zoomMin;
	/** Maximum allowed zoom level. */
	private double zoomMax;

	/** A flag that tells whether to zoom the associated object. */
	private boolean zoomable;
	/** A flag that tells whether to pan the associated object. */
	private boolean pannable;
	/** The current navigation direction. */
	private NavigationDirection direction;

	/**
	 * Initializes a new instance that is responsible for zooming and panning
	 * the axes with the specified names of the specified plot.
	 */
	public AbstractNavigator() {
		navigationListeners = new HashSet<>();
		zoomFactor = DEFAULT_ZOOM_FACTOR;
		zoomMin = DEFAULT_ZOOM_MIN;
		zoomMax = DEFAULT_ZOOM_MAX;
		zoomable = true;
		pannable = true;
	}

	/**
	 * Returns whether the associated object can be zoomed.
	 * @return {@code true} if the object can be zoomed,
	 *         {@code false} otherwise.
	 */
	public boolean isZoomable() {
		return zoomable;
	}

	/**
	 * Sets whether the associated object can be zoomed.
	 * @param zoomable A value that tells whether it should be possible to zoom
	 *        the associated object.
	 */
	public void setZoomable(boolean zoomable) {
		this.zoomable = zoomable;
	}

	/**
	 * Increases the current zoom level by the specified zoom factor.
	 */
	public void zoomIn() {
		zoomInAt(null);
	}

	/**
	 * Decreases the current zoom level by the specified zoom factor.
	 */
	public void zoomOut() {
		zoomOutAt(null);
	}

	@Override
	public void zoomAt(double zoom, PointND<? extends Number> zoomPoint) {
		if (!isZoomable()) {
			return;
		}
		boolean pan = isPannable() && zoomPoint != null;

		PointND<? extends Number> center = null;
		if (pan) {
			center = getCenter();
			setCenter(zoomPoint);
		}
		setZoom(zoom);
		if (pan) {
			setCenter(center);
		}
	}

	@Override
	public void zoomInAt(PointND<? extends Number> zoomPoint) {
		double zoom = getZoom();
		zoomAt(zoom*getZoomFactor(), zoomPoint);
	}

	@Override
	public void zoomOutAt(PointND<? extends Number> zoomPoint) {
		double zoom = getZoom();
		zoomAt(zoom/getZoomFactor(), zoomPoint);
	}

	/**
	 * Returns whether the associated object can be panned.
	 * @return {@code true} if the object can be panned,
	 *         {@code false} otherwise.
	 */
	public boolean isPannable() {
		return pannable;
	}

	/**
	 * Sets whether the associated object can be panned.
	 * @param pannable A value that tells whether it should be possible to pan
	 *        the associated object.
	 */
	public void setPannable(boolean pannable) {
		this.pannable = pannable;
	}

	/**
	 * Returns the factor which is used to change the zoom level on
	 * zoom in/out actions.
	 * @return The current zoom factor.
	 */
	public double getZoomFactor() {
		return zoomFactor;
	}
	/**
	 * Sets the factor which should be used to change the zoom level on
	 * zoom in/out actions.
	 * @param factor The new zoom factor.
	 */
	public void setZoomFactor(double factor) {
		zoomFactor = factor;
	}

	/**
	 * Returns the minimal zoom factor.
	 * @return Minimal zoom factor.
	 */
	public double getZoomMin() {
		return zoomMin;
	}
	/**
	 * Sets the minimal zoom factor.
	 * @param min New minimal zoom factor.
	 */
	public void setZoomMin(double min) {
		this.zoomMin = min;
	}

	/**
	 * Returns the minimal zoom factor.
	 * @return Maximal zoom factor.
	 */
	public double getZoomMax() {
		return zoomMax;
	}
	/**
	 * Sets the maximal zoom factor.
	 * @param max New maximal zoom factor.
	 */
	public void setZoomMax(double max) {
		this.zoomMax = max;
	}

	/**
	 * Adds the specified listener object that gets notified on changes to
	 * navigation information like panning or zooming.
	 * @param l Listener object
	 */
	public void addNavigationListener(NavigationListener l) {
		navigationListeners.add(l);
	}

	/**
	 * Removes the specified listener object, i.e. it doesn't get notified on
	 * changes to navigation information like panning or zooming.
	 * @param l Listener object
	 */
	public void removeNavigationListener(NavigationListener l) {
		navigationListeners.remove(l);
	}

	/**
	 * Returns the current direction of the components that will be taken into
	 * account for zooming and panning.
	 * @return Direction.
	 */
	public NavigationDirection getDirection() {
		return direction;
	}

	/**
	 * Sets the direction of the components that will be taken into account for
	 * zooming and panning.
	 * @param direction Direction.
	 */
	public void setDirection(NavigationDirection direction) {
		this.direction = direction;
	}

	/**
	 * Couples the actions of the current and the specified navigator. All
	 * actions applied to this navigator will be also applied to the specified
	 * navigator and vice versa.
	 * @param navigator Navigator which should be bound to this instance.
	 */
	public void connect(Navigator navigator) {
		if (navigator != null && navigator != this) {
			addNavigationListener(navigator);
			navigator.addNavigationListener(this);
		}
	}

	/**
	 * Decouples the actions of the current and the connected specified
	 * navigator. All actions will be applied separately to each navigator.
	 * @param navigator Navigator to be bound to this instance.
	 */
	public void disconnect(Navigator navigator) {
		if (navigator != null && navigator != this) {
			removeNavigationListener(navigator);
			navigator.removeNavigationListener(this);
		}
	}

	/**
	 * A method that gets called after the center of an object in a connected
	 * {@code PlotNavigator} has changed.
	 * @param event An object describing the change event.
	 */
	public void centerChanged(NavigationEvent<PointND<? extends Number>> event) {
		if (event.getSource() != this) {
			setCenter(event.getValueNew());
		}
	}

	/**
	 * A method that gets called after the zoom level of an object in a
	 * connected {@code PlotNavigator} has changed.
	 * @param event An object describing the change event.
	 */
	public void zoomChanged(NavigationEvent<Double> event) {
		if (event.getSource() != this) {
			setZoom(event.getValueNew());
		}
	}

	/**
	 * Notifies all navigation listeners that the center of one or more
	 * components have been changed.
	 * @param event An object describing the change event.
	 */
	protected void fireCenterChanged(NavigationEvent<PointND<? extends Number>> event) {
		for (NavigationListener l : navigationListeners) {
			l.centerChanged(event);
		}
	}

	/**
	 * Notifies all navigation listeners that the zoom level of all components
	 * has been changed.
	 * @param event An object describing the change event.
	 */
	protected void fireZoomChanged(NavigationEvent<Double> event) {
		for (NavigationListener l : navigationListeners) {
			l.zoomChanged(event);
		}
	}
}

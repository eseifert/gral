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
package de.erichseifert.gral.plots;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.erichseifert.gral.navigation.AbstractNavigator;
import de.erichseifert.gral.navigation.NavigationEvent;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.PointND;

/**
 * <p>Abstract base class that can be used to control the zoom and panning of a
 * plot. The navigator translates the interaction to operations on a defined
 * set of axes: Zooming is translated as scaling, panning is done by uniformly
 * changing the minimum and maximum values of the axes.</p>
 *
 * <p>Additionally, the actions can also be bound to a certain direction by
 * defining a more restricted set of axes. The methods {@link #getDirection()}
 * and {@link #setDirection(de.erichseifert.gral.navigation.NavigationDirection)}
 * provide a convenient way for setting predefined sets of axes.</p>
 */
public abstract class PlotNavigator extends AbstractNavigator {
	/** AbstractPlot that will be navigated. */
	private final Plot plot;
	/** Mapping of axis name to information on center and zoom. */
	private final Map<String, NavigationInfo> infos;
	/** Axes affected by navigation. */
	private final List<String> axes;

	/**
	 * Data class for storing navigational information for an axis.
	 */
	protected static final class NavigationInfo {
		/** Minimum value of the original axis. */
		private final Number minOriginal;
		/** Maximum value of the original axis. */
		private final Number maxOriginal;
		/** Center value of the original axis. */
		private final double centerOriginal;
		/** Current center value. */
		private double center;
		/** Current zoom level. */
		private double zoom;

		/**
		 * Initializes a new {@code NavigationInfo} instance.
		 * @param min Minimum value in axis units.
		 * @param max Maximum value in axis units.
		 * @param center Center in axis units.
		 */
		public NavigationInfo(Number min, Number max, double center) {
			this.minOriginal = min;
			this.maxOriginal = max;
			this.centerOriginal = center;
			this.center = centerOriginal;
			this.zoom = 1.0;
		}

		/**
		 * Returns the original minimum value.
		 * @return Original minimum value.
		 */
		public Number getMinOriginal() {
			return minOriginal;
		}
		/**
		 * Returns the original maximum value.
		 * @return Original maximum value.
		 */
		public Number getMaxOriginal() {
			return maxOriginal;
		}
		/**
		 * Returns the original center value.
		 * @return Original center value.
		 */
		public double getCenterOriginal() {
			return centerOriginal;
		}

		/**
		 * Returns the current center value.
		 * @return Current center value.
		 */
		public double getCenter() {
			return center;
		}
		/**
		 * Sets the current center value.
		 * @param center New center value.
		 */
		public void setCenter(double center) {
			this.center = center;
		}

		/**
		 * Returns the current zoom factor.
		 * @return Current zoom factor.
		 */
		public double getZoom() {
			return zoom;
		}
		/**
		 * Sets the current zoom factor.
		 * @param zoom New zoom factor.
		 */
		public void setZoom(double zoom) {
			this.zoom = zoom;
		}
	}

	/**
	 * Initializes a new instance that is responsible for zooming and panning
	 * the axes with the specified names of the specified plot.
	 * @param plot AbstractPlot to be zoomed and panned.
	 * @param axesNames Names of the axes that should be controlled by this
	 *        navigator.
	 */
	public PlotNavigator(Plot plot, List<String> axesNames) {
		axes = new LinkedList<>();
		infos = new HashMap<>();

		this.plot = plot;

		setAxes(axesNames);
	}

	/**
	 * Initializes a new instance that is responsible for zooming and panning
	 * the axes with the specified names of the specified plot.
	 * @param plot AbstractPlot to be zoomed and panned.
	 * @param axesNames Names of the axes that should be controlled by this
	 *        navigator.
	 */
	public PlotNavigator(Plot plot, String... axesNames) {
		this(plot, Arrays.asList(axesNames));
	}

	/**
	 * Refreshes the values of all axis to reflect navigation actions.
	 */
	private void refresh() {
		for (String axisName : getAxes()) {
			NavigationInfo info = getInfo(axisName);
			if (info == null) {
				continue;
			}

			AxisRenderer renderer = getPlot().getAxisRenderer(axisName);
			if (renderer == null) {
				continue;
			}

			Axis axis = getPlot().getAxis(axisName);

			// Original range in screen units
			// Most up-to-date view coordinates (axis's layout) must be used
			double minOrig = renderer.worldToView(
				axis, info.getMinOriginal(), true);
			double maxOrig = renderer.worldToView(
				axis, info.getMaxOriginal(), true);
			double rangeOrig = maxOrig - minOrig;

			// New axis scale
			double zoom = info.getZoom();
			double range = rangeOrig/zoom;
			double center = renderer.worldToView(axis, info.getCenter(), true);
			Number min = renderer.viewToWorld(axis, center - 0.5*range, true);
			Number max = renderer.viewToWorld(axis, center + 0.5*range, true);

			// Change axis
			axis.setRange(min, max);
		}
	}

	/**
	 * Returns the plot stored in this instance.
	 * @return Stored plot object.
	 */
	protected Plot getPlot() {
		return plot;
	}

	/**
	 * Returns the current zoom level of the associated object.
	 * @return Current zoom level.
	 */
	public double getZoom() {
		double zoom = 0.0;
		int count = 0;
		for (String axisName : getAxes()) {
			NavigationInfo info = getInfo(axisName);
			if (info == null) {
				continue;
			}
			if (!MathUtils.isCalculatable(info.getZoom())) {
				continue;
			}
			zoom += info.getZoom();
			count++;
		}
		return zoom / count;
	}

	/**
	 * Sets the zoom level of the associated object to the specified value.
	 * @param zoomNew New zoom level.
	 */
	public void setZoom(double zoomNew) {
		if (!isZoomable() || (zoomNew <= 0.0) ||
				!MathUtils.isCalculatable(zoomNew)) {
			return;
		}
		double zoomOld = getZoom();
		zoomNew = MathUtils.limit(zoomNew, getZoomMin(), getZoomMax());
		if (zoomOld == zoomNew) {
			return;
		}
		for (String axisName : getAxes()) {
			NavigationInfo info = getInfo(axisName);
			if (info == null) {
				continue;
			}
			info.setZoom(zoomNew);
		}
		NavigationEvent<Double> event =
				new NavigationEvent<>(this, zoomOld, zoomNew);
		fireZoomChanged(event);
		refresh();
	}

	/**
	 * Returns the current center point. The returned point contains value in
	 * world units.
	 * @return Center point in world units.
	 */
	public PointND<? extends Number> getCenter() {
		List<String> axesNames = getAxes();
		Double[] centerCoords = new Double[axesNames.size()];
		int axisIndex = 0;
		for (String axisName : axesNames) {
			NavigationInfo info = getInfo(axisName);
			if (info != null) {
				double axisCenter = info.getCenter();
				centerCoords[axisIndex] = axisCenter;
			}
			axisIndex++;
		}
		return new PointND<>(centerCoords);
	}

	/**
	 * Sets a new center point. The values of the point are in world units.
	 * @param center New center point in world units.
	 */
	public void setCenter(PointND<? extends Number> center) {
		if (!isPannable()) {
			return;
		}
		PointND<? extends Number> centerOld = getCenter();
		if (centerOld.equals(center)) {
			return;
		}
		List<String> axesNames = getAxes();
		int axisIndex = 0;
		for (String axisName : axesNames) {
			NavigationInfo info = getInfo(axisName);
			if (info != null) {
				Number centerCoordNew = center.get(axisIndex);
				info.setCenter(centerCoordNew.doubleValue());
			}
			axisIndex++;
		}

		NavigationEvent<PointND<? extends Number>> event =
				new NavigationEvent<>(this, centerOld, center);
		fireCenterChanged(event);
		refresh();
	}

	/**
	 * Moves the center by the relative values of the specified point.
	 * The values of the point are in screen units.
	 * @param deltas Relative values to use for panning.
	 */
	public void pan(PointND<? extends Number> deltas) {
		if (!isPannable()) {
			return;
		}
		PointND<? extends Number> centerOld = getCenter();
		Double[] centerCoords = new Double[centerOld.getDimensions()];
		int axisIndex = 0;
		for (String axisName : getAxes()) {
			NavigationInfo info = getInfo(axisName);
			if (info != null) {
				double delta = getDimensionValue(axisName, deltas).doubleValue();
				AxisRenderer renderer =
					getPlot().getAxisRenderer(axisName);
				if (renderer != null) {
					boolean swapped = renderer.isShapeDirectionSwapped();
					if (swapped) {
						delta = -delta;
					}
					Axis axis = getPlot().getAxis(axisName);
					// Fetch current center on screen
					double center = renderer.worldToView(
						axis, info.getCenter(), true);
					// Move center and convert it to axis coordinates
					Number centerNew = renderer.viewToWorld(
						axis, center - delta, true);
					// Change axis (world units)
					info.setCenter(centerNew.doubleValue());
					centerCoords[axisIndex] = centerNew.doubleValue();
				}
			}
			axisIndex++;
		}
		PointND<? extends Number> centerNew = new PointND<>(centerCoords);
		NavigationEvent<PointND<? extends Number>> event =
				new NavigationEvent<>(this, centerOld, centerNew);
		fireCenterChanged(event);
		refresh();
	}

	/**
	 * Sets the current state as the default state of the object.
	 * Resetting the navigator will then return to the default state.
	 */
	public void setDefaultState() {
		infos.clear();
		for (String axisName : getAxes()) {
			Axis axis = getPlot().getAxis(axisName);
			if (axis == null) {
				continue;
			}
			double min;
			double max;
			Number center = 0.0;
			AxisRenderer renderer = getPlot().getAxisRenderer(axisName);
			if (renderer != null && axis.isValid()) {
				min = renderer.worldToView(axis, axis.getMin(), false);
				max = renderer.worldToView(axis, axis.getMax(), false);
				if (MathUtils.isCalculatable(min) && MathUtils.isCalculatable(max)) {
					center = renderer.viewToWorld(axis, (min + max)/2.0, false);
				}
			}
			NavigationInfo info = new NavigationInfo(
				axis.getMin(), axis.getMax(), center.doubleValue());
			infos.put(axisName, info);
		}
	}

	/**
	 * Sets the object's position and zoom level to the default state.
	 */
	public void reset() {
		double zoomOld = getZoom();
		double zoomNew = 1.0;
		PointND<? extends Number> centerOld = getCenter();

		List<String> axesNames = getAxes();
		Double[] centerCoordsOriginal = new Double[centerOld.getDimensions()];
		int axisIndex = 0;
		for (String axisName : axesNames) {
			NavigationInfo info = getInfo(axisName);
			if (info != null) {
				double centerCoordOriginal = info.getCenterOriginal();
				centerCoordsOriginal[axisIndex] = centerCoordOriginal;

				info.setCenter(centerCoordOriginal);
				info.setZoom(zoomNew);
			}
			axisIndex++;
		}
		PointND<Double> centerNew = new PointND<>(centerCoordsOriginal);

		NavigationEvent<PointND<? extends Number>> panEvent =
				new NavigationEvent<>(this, centerOld, centerNew);
		fireCenterChanged(panEvent);

		NavigationEvent<Double> zoomEvent =
				new NavigationEvent<>(this, zoomOld, 1.0);
		fireZoomChanged(zoomEvent);

		refresh();
	}

	/**
	 * Returns navigational information for the axis with specified name.
	 * @param axisName Axis name.
	 * @return Navigational information.
	 */
	protected NavigationInfo getInfo(String axisName) {
		return infos.get(axisName);
	}

	/**
	 * Returns the names of all axes handled by this object.
	 * @return Names of all axes handled by this object.
	 */
	protected List<String> getAxes() {
		return Collections.unmodifiableList(axes);
	}

	/**
	 * Sets the names of the axes that should be handled by this object.
	 * @param axesNames Names of the axes that should be handled.
	 */
	protected void setAxes(List<String> axesNames) {
		axes.clear();
		axes.addAll(axesNames);
		setDefaultState();
	}

	/**
	 * Sets the names of the axes that should be handled by this object.
	 * @param axesNames Names of the axes that should be handled.
	 */
	protected void setAxes(String... axesNames) {
		setAxes(Arrays.asList(axesNames));
	}

	/**
	 * Returns the number dimensions the associated plot can handle. For a
	 * one-dimensional plot like {@link PiePlot} this is 1, for a
	 * two-dimensional plot like {@link XYPlot} this is 2, and so on.
	 * @return Number of dimensions the associated plot can handle.
	 */
	protected abstract int getDimensions();

	/**
	 * Return the index that can be used to access data for the axis with the
	 * specified name. The returned index must be larger than or equal to 0 and
	 * smaller than the result of {@link #getDimensions()}.
	 * @param axisName Name of the axis.
	 * @param values Data values.
	 * @return Dimension index.
	 */
	protected abstract Number getDimensionValue(
		String axisName, PointND<? extends Number> values);
}

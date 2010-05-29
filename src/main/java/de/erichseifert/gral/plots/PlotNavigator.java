/**
 * GRAL: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisListener;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.util.MathUtils;

/**
 * Class that controls the zoom of a Plot.
 */
public class PlotNavigator implements AxisListener {
	private final Plot plot;
	private final Map<Axis, NavigationInfo> infos;

	private double zoomMin;
	private double zoomMax;

	/**
	 * Utility class for storing navigational information for an axis.
	 */
	private static final class NavigationInfo {
		private final Number minOriginal;
		private final Number maxOriginal;
		private final double centerOriginal;
		private final double rangeOriginal;
		private double center;
		private double zoom;

		/**
		 * Constructor which creates an new <code>NavigationInfo</code> instance.
		 * @param center Center in axis units.
		 * @param range Viewing width in axis units.
		 */
		public NavigationInfo(Number min, Number max) {
			this.minOriginal = min;
			this.maxOriginal = max;
			this.centerOriginal = (min.doubleValue() + max.doubleValue())/2.0;
			this.rangeOriginal = max.doubleValue() - min.doubleValue();
			this.center = centerOriginal;
			this.zoom = 1.0;
		}

		public Number getMinOriginal() {
			return minOriginal;
		}
		public Number getMaxOriginal() {
			return maxOriginal;
		}
		public double getCenterOriginal() {
			return centerOriginal;
		}
		public double getRangeOriginal() {
			return rangeOriginal;
		}

		public double getCenter() {
			return center;
		}
		public void setCenter(double center) {
			this.center = center;
		}

		public double getZoom() {
			return zoom;
		}
		public void setZoom(double zoom) {
			this.zoom = zoom;
		}
	}

	/**
	 * Creates a new <code>PlotZoomer</code> object that is responsible for the specified plot.
	 * @param plot Plot to be zoomed.
	 */
	public PlotNavigator(Plot plot) {
		this.plot = plot;
		infos = new HashMap<Axis, NavigationInfo>();
		for (Axis axis : plot.getAxes()) {
			NavigationInfo info = new NavigationInfo(
					axis.getMin().doubleValue(), axis.getMax().doubleValue());
			infos.put(axis, info);
		}
		zoomMin = 1e-2;
		zoomMax = 1e+2;
	}

	private void refresh() {
		for (Entry<Axis, NavigationInfo> entry: infos.entrySet()) {
			Axis axis = entry.getKey();
			AxisRenderer renderer = plot.getAxisRenderer(axis);
			NavigationInfo info = entry.getValue();

			// Original range in screen units
			// Most up-to-date view coordinates (axis's layout) must be used
			double minOrig = renderer.worldToView(axis, info.getMinOriginal(), true);
			double maxOrig = renderer.worldToView(axis, info.getMaxOriginal(), true);
			double rangeOrig = maxOrig - minOrig;

			// New axis scale
			double zoom = info.getZoom();
			double range = rangeOrig*zoom;
			double center = renderer.worldToView(axis, info.getCenter(), true);
			Number min = renderer.viewToWorld(axis, center - 0.5*range, true);
			Number max = renderer.viewToWorld(axis, center + 0.5*range, true);

			// Change axis
			axis.setRange(min, max);
		}
	}

	/**
	 * Returns the average zoom factor of all axes.
	 * @param axis
	 * @return
	 */
	public double getZoom() {
		double zoom = 0.0;
		int count = 0;
		for (NavigationInfo info: infos.values()) {
			if (Double.isNaN(info.getZoom())) {
				continue;
			}
			zoom += info.getZoom();
			count++;
		}
		return zoom / count;
	}

	/**
	 * Sets the plot's zoom to the specified center and zooms it according to the specified values.
	 * @param center Center of the zoom.
	 * @param zoomNew Zoom level.
	 */
	public void setZoom(double zoomNew) {
		if (zoomNew <= 0.0 || Double.isNaN(zoomNew) || Double.isInfinite(zoomNew)) {
			return;
		}
		zoomNew = MathUtils.limit(zoomNew, zoomMin, zoomMax);
		for (NavigationInfo info: infos.values()) {
			info.setZoom(zoomNew);
		}
		refresh();
	}

	/**
	 * Returns the center point of the specified axis.
	 * @param axis Axis.
	 * @return Center point in axis units.
	 */
	public Number getCenter(Axis axis) {
		return infos.get(axis).getCenter();
	}

	/**
	 * Sets a new center point for the specified axis.
	 * @param axis Axis.
	 * @param center New center point in axis units.
	 */
	public void setCenter(Axis axis, Number center) {
		infos.get(axis).setCenter(center.doubleValue());
		refresh();
	}

	/**
	 * Resets the plot's zoom to the original value.
	 */
	public void reset() {
		for (NavigationInfo info: infos.values()) {
			double centerOriginal = info.getCenterOriginal();
			info.setCenter(centerOriginal);
			info.setZoom(1.0);
		}
		refresh();
	}

	public double getZoomMin() {
		return zoomMin;
	}
	public void setZoomMin(double min) {
		this.zoomMin = min;
	}

	public double getZoomMax() {
		return zoomMax;
	}
	public void setZoomMax(double max) {
		this.zoomMax = max;
	}

	@Override
	public void rangeChanged(Axis axis, Number min, Number max) {
		System.out.println(axis+": "+min+" "+max);
	}

}
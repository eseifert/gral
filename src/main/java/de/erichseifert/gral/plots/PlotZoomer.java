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

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.util.MathUtils;

/**
 * Class that controls the zoom of a Plot.
 */
public class PlotZoomer {
	private final Map<Axis, Double> centersOriginal;
	private final Map<Axis, Double> rangesOriginal;

	private final Plot plot;
	private double zoom;
	private double zoomMin;
	private double zoomMax;

	/**
	 * Creates a new <code>PlotZoomer</code> object that is responsible for the specified plot.
	 * @param plot Plot to be zoomed.
	 */
	public PlotZoomer(Plot plot) {
		centersOriginal = new HashMap<Axis, Double>();
		rangesOriginal = new HashMap<Axis, Double>();

		this.plot = plot;
		zoom = 1.0;
		for (Axis axis : plot.getAxes()) {
			double rangeOriginal = axis.getRange();
			rangesOriginal.put(axis, rangeOriginal);
			centersOriginal.put(axis, axis.getMin().doubleValue() + 0.5*rangeOriginal);
		}

		zoomMin = 1e-2;
		zoomMax = 1e+2;
	}

	/**
	 * Sets the plot's zoom to the specified center and zooms it according to the specified values.
	 * @param center Center of the zoom.
	 * @param zoomNew Zoom level.
	 */
	public void zoomTo(Point2D center, double zoomNew) {
		if (zoomNew <= 0.0) {
			return;
		}
		zoom = MathUtils.limit(zoomNew, zoomMin, zoomMax);

		for (Axis axis: plot.getAxes()) {
			double rangeCurrent = axis.getRange();
			double centerCurrent = axis.getMin().doubleValue() + 0.5*rangeCurrent;

			double rangeNew = rangesOriginal.get(axis)*zoom;

			// FIXME: Don't ignore new center
			double posRel = 0.5; //center.getX() / plot.getPlotArea().getWidth();
			double posRelInv = 0.5; //1.0 - posRel;
			Number axisMinNew = centerCurrent - posRel*rangeNew;
			Number axisMaxNew = centerCurrent + posRelInv*rangeNew;

			axis.setRange(axisMinNew, axisMaxNew);
		}
	}

	/**
	 * Resets the plot's zoom to the original value.
	 */
	public void reset() {
		zoom = 1.0;
		for (Axis axis: plot.getAxes()) {
			double centerOriginal = centersOriginal.get(axis);
			double rangeOriginal = rangesOriginal.get(axis);

			centersOriginal.put(axis, centerOriginal);
			rangesOriginal.put(axis, rangeOriginal);

			axis.setRange(centerOriginal - 0.5*rangeOriginal, centerOriginal + 0.5*rangeOriginal);
		}
	}

	public double getZoom() {
		return zoom;
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

}
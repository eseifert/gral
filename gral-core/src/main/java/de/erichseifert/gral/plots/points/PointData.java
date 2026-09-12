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
package de.erichseifert.gral.plots.points;

import java.util.Collections;
import java.util.List;

import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;


/**
 * <p>Everything a {@link PointRenderer} needs in order to draw one data point:
 * the row, which column of it holds the value, and the axes and axis renderers
 * that project it.</p>
 *
 * <p>The lists of axes and axis renderers are parallel and in the order of the
 * columns of the row, so {@code axes.get(0)} and {@code axisRenderers.get(0)}
 * belong to column 0. For an {@code XYPlot} that means index 0 is the x axis
 * and index 1 the y axis. Both lists are unmodifiable.</p>
 *
 * <pre>
 * public Drawable getPoint(PointData data, Shape shape) {
 *     Comparable&lt;?&gt; value = data.row.get(data.col);
 *     Axis axisY = data.axes.get(1);
 *     AxisRenderer rendererY = data.axisRenderers.get(1);
 *     double y = rendererY.worldToView(axisY, (Number) value, true);
 *     …
 * }
 * </pre>
 *
 * <p>Instances are immutable and their fields are public, which is deliberate:
 * one is created for every point of every repaint.</p>
 */
public class PointData {
	/** Axes that will be used to project the point. */
	public final List<Axis> axes;
	/** Renderers for the axes that will be used to project the point. */
	public final List<? extends AxisRenderer> axisRenderers;
	/** The index of the row. */
	public final int index;
	/** The data row that will get projected. */
	public final Row row;
	/** The index of the column in the row that contains the data value. */
	public final int col;

	/**
	 * Initializes a new instance with the specified data.
	 * @param axes Axes that are used to project the point.
	 * @param axisRenderers Renderers for the axes.
	 * @param row Data row containing that will be projected on the axes.
	 * @param rowIndex Index of the row.
	 * @param col Index of the column in the row that contains the data value.
	 */
	public PointData(List<Axis> axes, List<? extends AxisRenderer> axisRenderers,
			Row row, int rowIndex, int col) {
		this.axes = Collections.unmodifiableList(axes);
		this.axisRenderers = Collections.unmodifiableList(axisRenderers);
		this.row = row;
		this.index = rowIndex;
		this.col = col;
	}
}

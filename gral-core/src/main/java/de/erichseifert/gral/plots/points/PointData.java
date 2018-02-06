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
package de.erichseifert.gral.plots.points;

import java.util.Collections;
import java.util.List;

import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;


/**
 * Class for storing data that will be used to create a data point in a plot.
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

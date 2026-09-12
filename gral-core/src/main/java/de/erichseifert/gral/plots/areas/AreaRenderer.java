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
package de.erichseifert.gral.plots.areas;

import java.awt.Paint;
import java.awt.Shape;
import java.util.List;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.plots.DataPoint;

/**
 * <p>Fills the region between a data series and the baseline of the dependent
 * axis. Like a {@link de.erichseifert.gral.plots.lines.LineRenderer}, an area
 * renderer receives the already projected {@link DataPoint}s and works in view
 * coordinates.</p>
 *
 * <p>Rendering happens in two steps: {@link #getAreaShape(List)} builds the
 * geometry and {@link #getArea(List, Shape)} wraps it in a {@link Drawable}
 * that fills it.</p>
 *
 * <pre>
 * DefaultAreaRenderer2D area = new DefaultAreaRenderer2D();
 * area.setColor(new Color(0, 0, 255, 64));
 * area.setGap(2.0);          // keep a hole around each data point
 * plot.setAreaRenderers(series, area);
 * </pre>
 *
 * <p>Areas are drawn before lines and points, so a translucent fill does not
 * hide the marks on top of it. As with the other renderers, one instance serves
 * the whole series and must not keep per-point state.</p>
 */
public interface AreaRenderer {
	/**
	 * Returns the shape used for rendering the area of a data points.
	 * @param points Data points.
	 * @return Geometric shape for the area of the specified data points.
	 */
	Shape getAreaShape(List<DataPoint> points);

	/**
	 * Returns the graphical representation to be drawn for the specified data
	 * points.
	 * @param points Points that define the shape of the area.
	 * @param shape Geometric shape of the area.
	 * @return Representation of the area.
	 */
	Drawable getArea(List<DataPoint> points, Shape shape);

	/**
	 * Returns the size of the hole that is punched out of the area around each
	 * data point, in view units (pixels). A value of 0.0 means no hole.
	 * @return Gap between area and data point.
	 */
	double getGap();

	/**
	 * Sets the size of the hole that is punched out of the area around each
	 * data point, in view units (pixels). A value of 0.0 means no hole.
	 * @param gap Gap between area and data point.
	 */
	void setGap(double gap);

	/**
	 * Returns whether the gaps should have rounded corners.
	 * @return {@code true}, if the gaps should have rounded corners.
	 */
	boolean isGapRounded();

	/**
	 * Sets a value which decides whether the gaps should have rounded corners.
	 * @param gapRounded {@code true}, if the gaps should have rounded corners.
	 */
	void setGapRounded(boolean gapRounded);

	/**
	 * Returns the paint used to fill the area shape.
	 * @return Paint for the area shape.
	 */
	Paint getColor();

	/**
	 * Sets the paint used to fill the area shape.
	 * @param color Paint for the area shape.
	 */
	void setColor(Paint color);
}

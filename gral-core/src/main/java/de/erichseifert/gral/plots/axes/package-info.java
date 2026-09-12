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

/**
 * <p>Axes, and the projection between data values and pixels.</p>
 *
 * <p>The responsibility is split in two. An
 * {@link de.erichseifert.gral.plots.axes.Axis} is a plain value object: it
 * holds the minimum and maximum of the displayed range, whether that range is
 * scaled automatically, and a list of listeners. It knows nothing about
 * drawing. An {@link de.erichseifert.gral.plots.axes.AxisRenderer} owns
 * everything else &mdash; the shape the axis follows, the tick marks and their
 * labels, and, most importantly, the coordinate transform:</p>
 *
 * <pre>
 * AxisRenderer rendererX = plot.getAxisRenderer(XYPlot.AXIS_X);
 * Axis axisX = plot.getAxis(XYPlot.AXIS_X);
 *
 * // Where does the data value 4.2 end up on screen?
 * double pixels = rendererX.worldToView(axisX, 4.2, false);
 *
 * // And the other way round, e.g. to turn a mouse position into a value.
 * Number value = rendererX.viewToWorld(axisX, pixels, false);
 * </pre>
 *
 * <p>The {@code extrapolate} flag decides what happens outside the axis range:
 * with {@code false} the result is clamped to the ends of the axis, with
 * {@code true} the transform is continued beyond them.</p>
 *
 * <p>Because the scale lives in the renderer, changing from a linear to a
 * logarithmic axis is a matter of substituting one object:</p>
 * <pre>
 * plot.setAxisRenderer(XYPlot.AXIS_X, new LogarithmicRenderer2D());
 * </pre>
 *
 * <p>{@link de.erichseifert.gral.plots.axes.AbstractAxisRenderer2D} implements
 * the two-dimensional case and is the class to extend for a custom scale; the
 * two shipped subclasses are
 * {@link de.erichseifert.gral.plots.axes.LinearRenderer2D} and
 * {@link de.erichseifert.gral.plots.axes.LogarithmicRenderer2D}. Ticks are
 * produced as {@link de.erichseifert.gral.plots.axes.Tick} objects and come in
 * three flavors (major, minor and custom); custom ticks are supplied as a map
 * from value to label text.</p>
 */
package de.erichseifert.gral.plots.axes;

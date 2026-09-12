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
 * <p>Renderers that draw the individual data points of a series.</p>
 *
 * <p>A {@link de.erichseifert.gral.plots.points.PointRenderer} is asked, once
 * per data row, for two things: the {@code Shape} that represents the point,
 * and a {@link de.erichseifert.gral.graphics.Drawable} that paints it. The row
 * being rendered, together with the axes and axis renderers needed to project
 * it, is handed over as a
 * {@link de.erichseifert.gral.plots.points.PointData} record.</p>
 *
 * <pre>
 * DefaultPointRenderer2D points = new DefaultPointRenderer2D();
 * points.setShape(new Ellipse2D.Double(-4.0, -4.0, 8.0, 8.0));
 * points.setColor(Color.RED);
 * points.setValueVisible(true);   // print the value next to the mark
 * plot.setPointRenderers(series, points);
 * </pre>
 *
 * <p>The shape is expressed in the point's own coordinate system, with the
 * origin at the data point, so shapes are usually centered on (0,&nbsp;0).
 * Colors come from a {@link de.erichseifert.gral.plots.colors.ColorMapper},
 * which is what allows a point's color to depend on its value; the
 * {@code setColor(Paint)} overload is a shortcut for a constant color.</p>
 *
 * <p>Implementations:
 * {@link de.erichseifert.gral.plots.points.DefaultPointRenderer2D} draws a
 * fixed shape, optionally with error bars;
 * {@link de.erichseifert.gral.plots.points.SizeablePointRenderer} scales that
 * shape by the value of a further column;
 * {@link de.erichseifert.gral.plots.points.LabelPointRenderer} draws text
 * instead of a mark. Custom renderers usually extend
 * {@link de.erichseifert.gral.plots.points.AbstractPointRenderer}.</p>
 */
package de.erichseifert.gral.plots.points;

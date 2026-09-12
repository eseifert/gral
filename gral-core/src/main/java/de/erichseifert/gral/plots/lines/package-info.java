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
 * <p>Renderers that connect the data points of a series with a line.</p>
 *
 * <p>A {@link de.erichseifert.gral.plots.lines.LineRenderer} receives the
 * already projected {@link de.erichseifert.gral.plots.DataPoint}s of a series
 * and returns the {@code Shape} of the connecting line, followed by a
 * {@link de.erichseifert.gral.graphics.Drawable} that strokes it. A series has
 * no line unless a renderer is set for it:</p>
 *
 * <pre>
 * DefaultLineRenderer2D line = new DefaultLineRenderer2D();
 * line.setColor(Color.BLACK);
 * line.setStroke(new BasicStroke(2f));
 * // Leave a small hole around each data point so that the marks stay visible.
 * line.setGap(2.0);
 * line.setGapRounded(true);
 * plot.setLineRenderers(series, line);
 * </pre>
 *
 * <p>Implementations:
 * {@link de.erichseifert.gral.plots.lines.DefaultLineRenderer2D} draws straight
 * segments; {@link de.erichseifert.gral.plots.lines.DiscreteLineRenderer2D}
 * draws a step function; and
 * {@link de.erichseifert.gral.plots.lines.SmoothLineRenderer2D} draws a cubic
 * curve through the points. Custom renderers usually extend
 * {@link de.erichseifert.gral.plots.lines.AbstractLineRenderer2D}, which
 * already implements the gap punching and the stroking.</p>
 */
package de.erichseifert.gral.plots.lines;

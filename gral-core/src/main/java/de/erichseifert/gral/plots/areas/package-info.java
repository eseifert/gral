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
 * <p>Renderers that fill the region between a data series and a baseline.</p>
 *
 * <p>An {@link de.erichseifert.gral.plots.areas.AreaRenderer} works like a
 * {@link de.erichseifert.gral.plots.lines.LineRenderer}: it turns the projected
 * {@link de.erichseifert.gral.plots.DataPoint}s into a {@code Shape} and then
 * into a {@link de.erichseifert.gral.graphics.Drawable}. Areas are drawn behind
 * lines and points.</p>
 *
 * <pre>
 * DefaultAreaRenderer2D area = new DefaultAreaRenderer2D();
 * area.setColor(new Color(0, 0, 255, 64));
 * plot.setAreaRenderers(series, area);
 * </pre>
 *
 * <p>{@link de.erichseifert.gral.plots.areas.DefaultAreaRenderer2D} fills down
 * to the position where the axis of the dependent variable is crossed;
 * {@link de.erichseifert.gral.plots.areas.LineAreaRenderer2D} instead draws one
 * stroked line per data point down to that baseline, which produces an impulse
 * or stem plot. Both extend
 * {@link de.erichseifert.gral.plots.areas.AbstractAreaRenderer}.</p>
 */
package de.erichseifert.gral.plots.areas;

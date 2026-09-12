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
package de.erichseifert.gral.navigation;

/**
 * <p>Marker interface for the directions in which an object can be zoomed and
 * panned. The set of directions depends on the object, so each implementation
 * defines its own, usually as an enum:
 * {@link de.erichseifert.gral.plots.XYPlot.XYNavigationDirection} offers
 * {@code HORIZONTAL}, {@code VERTICAL} and {@code ARBITRARY}.</p>
 *
 * <pre>
 * navigator.setDirection(XYPlot.XYNavigationDirection.HORIZONTAL);
 * </pre>
 *
 * <p>Passing a direction that the navigator does not recognize causes an
 * {@code IllegalArgumentException}.</p>
 */
public interface NavigationDirection {
}

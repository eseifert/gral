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
 * <p>Runnable examples for every plot type of the library. Each one is a
 * self-contained {@code JPanel} with a {@code main} method, so it can be
 * started on its own, and {@link de.erichseifert.gral.examples.Browser}
 * collects all of them in one window:</p>
 *
 * <pre>
 * ./gradlew :gral-examples:run
 * </pre>
 *
 * <p>{@link de.erichseifert.gral.examples.xyplot.ScatterPlot} is the shortest
 * complete example and the best place to start reading. The examples are
 * grouped by plot type in the sub-packages; those under
 * {@link de.erichseifert.gral.examples.io} need no display at all and write
 * their output to files.</p>
 */
package de.erichseifert.gral.examples;

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
 * <p>Zooming and panning, kept independent of any toolkit and of the plot
 * classes.</p>
 *
 * <p>An object that can be navigated implements
 * {@link de.erichseifert.gral.navigation.Navigable} and hands out a
 * {@link de.erichseifert.gral.navigation.Navigator}. The navigator holds the
 * zoom factor and the center of view, applies limits, and notifies
 * {@link de.erichseifert.gral.navigation.NavigationListener}s of every change.
 * Nothing in this package refers to AWT events; a caller decides what a mouse
 * drag or a key press means and calls the navigator accordingly. The Swing
 * bindings live in {@link de.erichseifert.gral.ui.InteractivePanel}.</p>
 *
 * <pre>
 * Navigator navigator = plot.getNavigator();
 * navigator.setZoom(2.0);
 * navigator.setCenter(new PointND&lt;&gt;(0.0, 0.0));
 *
 * // Restrict interaction to one direction, or switch it off entirely.
 * navigator.setDirection(XYPlot.XYNavigationDirection.HORIZONTAL);
 * navigator.setPannable(false);
 *
 * // Go back to the default state, i.e. the one recorded at construction time
 * // or by the last call to setDefaultState().
 * navigator.reset();
 * </pre>
 *
 * <p>Two navigators can be
 * {@link de.erichseifert.gral.navigation.Navigator#connect(
 * de.erichseifert.gral.navigation.Navigator)
 * connected}, after which each applies the other's movements to its own object.
 * That is how several plots are made to pan and zoom together.</p>
 *
 * <p>{@link de.erichseifert.gral.navigation.AbstractNavigator} implements the
 * bookkeeping and leaves the object-specific part &mdash; reading and writing
 * the actual zoom and center &mdash; to subclasses;
 * {@link de.erichseifert.gral.plots.PlotNavigator} is the implementation for
 * plots, and it works by changing the ranges of the axes.</p>
 */
package de.erichseifert.gral.navigation;

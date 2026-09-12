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
 * <p>The plot types and the machinery they share.</p>
 *
 * <h2>The plot types</h2>
 * <ul>
 *   <li>{@link de.erichseifert.gral.plots.XYPlot} &ndash; points, lines and
 *   areas in a Cartesian coordinate system. It is also the base class of
 *   {@code BarPlot}, {@code BoxPlot} and {@code RasterPlot}.</li>
 *   <li>{@link de.erichseifert.gral.plots.BarPlot} &ndash; vertical bars from a
 *   baseline to the data value.</li>
 *   <li>{@link de.erichseifert.gral.plots.BoxPlot} &ndash; box-and-whisker
 *   plots; see {@code BoxPlot.createBoxData(DataSource)} for turning raw
 *   observations into the five-number summary the plot expects.</li>
 *   <li>{@link de.erichseifert.gral.plots.PiePlot} &ndash; slices sized by
 *   value. It does not extend {@code XYPlot} and has no axes in the usual
 *   sense.</li>
 *   <li>{@link de.erichseifert.gral.plots.RasterPlot} &ndash; a grid of cells
 *   colored by value, i.e. a heat map.</li>
 * </ul>
 *
 * <h2>Structure of a plot</h2>
 * <p>{@link de.erichseifert.gral.plots.AbstractPlot} extends
 * {@link de.erichseifert.gral.graphics.DrawableContainer}, so a plot is a
 * container of drawables laid out by an
 * {@link de.erichseifert.gral.graphics.layout.EdgeLayout}: the
 * {@link de.erichseifert.gral.plots.PlotArea} in the center, the title, the
 * legend and the axis components around it.</p>
 *
 * <p>Axes are held <em>by name</em>. The names are string constants on the
 * concrete plot class, for example {@link de.erichseifert.gral.plots.XYPlot#AXIS_X}
 * and {@link de.erichseifert.gral.plots.XYPlot#AXIS_Y}, which is why adding a
 * secondary axis is just a matter of using another name. Each name maps to an
 * {@link de.erichseifert.gral.plots.axes.Axis} (the value range) and an
 * {@link de.erichseifert.gral.plots.axes.AxisRenderer} (how it is drawn and how
 * values are projected to pixels). Which column of which data source belongs to
 * which axis is recorded with
 * {@link de.erichseifert.gral.plots.Plot#setMapping(
 * de.erichseifert.gral.data.DataSource, String...)}.</p>
 *
 * <h2>Configuring a plot</h2>
 * <p>Since version 0.10 there is no settings map: everything is an ordinary
 * bean property on the plot, the plot area, the axis renderer or the point,
 * line and area renderers.</p>
 * <pre>
 * XYPlot plot = new XYPlot(series);
 * plot.getTitle().setText("Measurements");
 * plot.setInsets(new Insets2D.Double(20.0, 60.0, 40.0, 20.0));
 * plot.setLegendVisible(true);
 *
 * // Rendering of a series is decided by renderer objects, not by plot options.
 * PointRenderer points = new DefaultPointRenderer2D();
 * points.setColor(Color.BLUE);
 * plot.setPointRenderers(series, points);
 * plot.setLineRenderers(series, new DefaultLineRenderer2D());
 *
 * // An empty list switches that layer off for the series.
 * plot.setPointRenderers(otherSeries, Collections.&lt;PointRenderer&gt;emptyList());
 * </pre>
 * <p>Since version 0.11 a series may have more than one renderer of the same
 * kind; they are drawn in the order given, which is how effects like drop
 * shadows or layered marks are built.</p>
 *
 * <h2>Interaction</h2>
 * <p>{@link de.erichseifert.gral.plots.PlotNavigator} implements
 * {@link de.erichseifert.gral.navigation.Navigator} for plots and performs
 * zooming and panning by changing the axis ranges. Several navigators can be
 * connected so that multiple plots move together.</p>
 *
 * @see de.erichseifert.gral.plots.axes
 * @see de.erichseifert.gral.plots.points
 * @see de.erichseifert.gral.plots.lines
 * @see de.erichseifert.gral.plots.areas
 * @see de.erichseifert.gral.plots.colors
 * @see de.erichseifert.gral.plots.legends
 */
package de.erichseifert.gral.plots;

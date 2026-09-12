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
 * <p>GRAL is a library for plotting data with Java 2D. It is built around three
 * ideas:</p>
 *
 * <ul>
 *   <li><b>Data is separate from presentation.</b> A
 *   {@link de.erichseifert.gral.data.DataSource} holds the numbers; a plot
 *   reads them and knows nothing about where they came from.</li>
 *   <li><b>Drawing is separate from the toolkit.</b> Everything that appears on
 *   screen is a {@link de.erichseifert.gral.graphics.Drawable}, which paints
 *   itself onto a {@code java.awt.Graphics2D}. Because {@code Drawable} is not
 *   a {@code java.awt.Component}, the same plot can be painted into a Swing
 *   panel, a {@code BufferedImage}, a PDF file, or a printer page.</li>
 *   <li><b>Details are pluggable.</b> How points, lines, areas, axes, legends
 *   and colors look is decided by small strategy objects that can be replaced
 *   or subclassed, rather than by a fixed set of options.</li>
 * </ul>
 *
 * <p>A minimal plot:</p>
 * <pre>
 * // 1. Put the data into a table: one column per variable.
 * DataTable data = new DataTable(Double.class, Double.class);
 * for (double x = 0.0; x &lt; 10.0; x += 0.25) {
 *     data.add(x, Math.sin(x));
 * }
 *
 * // 2. Say which columns are x and y, and give the series a name for the legend.
 * DataSeries series = new DataSeries("sin(x)", data, 0, 1);
 *
 * // 3. Create a plot and configure it with plain bean setters.
 * XYPlot plot = new XYPlot(series);
 * plot.setLineRenderers(series, new DefaultLineRenderer2D());
 *
 * // 4a. Show it in a window…
 * JFrame frame = new JFrame();
 * frame.getContentPane().add(new InteractivePanel(plot));
 * frame.setSize(600, 400);
 * frame.setVisible(true);
 *
 * // 4b. …or write it to a file without any window at all. The writer sets the
 * //     bounds of the plot for the duration of the call, so no window, no
 * //     screen and no toolkit is involved.
 * DrawableWriter writer = DrawableWriterFactory.getInstance().get("image/png");
 * try (OutputStream out = new FileOutputStream("plot.png")) {
 *     writer.write(plot, out, 600.0, 400.0);
 * }
 * </pre>
 *
 * <h2>Where to look next</h2>
 * <ul>
 *   <li>{@link de.erichseifert.gral.data} &ndash; tables, series, views and
 *   change notification.</li>
 *   <li>{@link de.erichseifert.gral.plots} &ndash; the plot types
 *   ({@code XYPlot}, {@code BarPlot}, {@code BoxPlot}, {@code PiePlot},
 *   {@code RasterPlot}) and their sub-packages of renderers.</li>
 *   <li>{@link de.erichseifert.gral.graphics} &ndash; the {@code Drawable}
 *   abstraction, containers and layout managers.</li>
 *   <li>{@link de.erichseifert.gral.ui} &ndash; Swing components that display a
 *   {@code Drawable}.</li>
 *   <li>{@link de.erichseifert.gral.io.plots} &ndash; writing plots to bitmap
 *   and vector files.</li>
 * </ul>
 */
package de.erichseifert.gral;

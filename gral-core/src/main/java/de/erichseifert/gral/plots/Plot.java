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
package de.erichseifert.gral.plots;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.Collection;
import java.util.List;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.graphics.Container;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.legends.Legend;
import de.erichseifert.gral.graphics.Location;

/**
 * <p>A {@link Drawable} that displays one or more {@link DataSource}s. Since a
 * plot is also a {@link Container}, it can be drawn into a Swing panel, an
 * image or a vector document like any other drawable, and it holds its title,
 * legend, plot area and axis components as children.</p>
 *
 * <p>Axes are addressed by name rather than by number. The names are string
 * constants on the concrete plot class &mdash; {@link XYPlot#AXIS_X},
 * {@link XYPlot#AXIS_Y}, {@link XYPlot#AXIS_X2} and {@link XYPlot#AXIS_Y2} for
 * an {@code XYPlot} &mdash; and each name is associated with two things: an
 * {@link Axis}, which is the value range, and an {@link AxisRenderer}, which
 * decides how that range is drawn and how values are projected to pixels.
 * Adding a further axis is a matter of using a further name.</p>
 *
 * <p>Which column of which data source is shown on which axis is recorded with
 * {@link #setMapping(DataSource, String...)}; the axis names are listed in
 * column order, and {@code null} leaves a column unmapped.</p>
 *
 * <pre>
 * // Columns 0 and 1 of the series are the x and y coordinates.
 * plot.setMapping(series, XYPlot.AXIS_X, XYPlot.AXIS_Y);
 *
 * // Put a second series on the secondary y axis.
 * plot.setAxis(XYPlot.AXIS_Y2, new Axis(0.0, 100.0));
 * plot.setAxisRenderer(XYPlot.AXIS_Y2, new LinearRenderer2D());
 * plot.setMapping(otherSeries, XYPlot.AXIS_X, XYPlot.AXIS_Y2);
 * </pre>
 *
 * <p>An axis that is set to auto-scale takes its range from the data; see
 * {@link #autoscaleAxis(String)} and {@link Axis#setAutoscaled(boolean)}. A
 * plot listens to its data sources, so a range recomputes and the plot
 * repaints when the data changes.</p>
 *
 * @see AbstractPlot
 * @see XYPlot
 */
public interface Plot extends Drawable, Container {
	/**
	 * Returns the axis registered under the specified name.
	 * @param name Name of the axis, e.g. {@link XYPlot#AXIS_X}.
	 * @return The axis, or {@code null} if no axis is registered under that
	 *         name.
	 */
	Axis getAxis(String name);

	/**
	 * Registers an axis under the specified name, replacing any axis that was
	 * registered under it before. An axis renderer has to be set separately
	 * with {@link #setAxisRenderer(String, AxisRenderer)}; without one the axis
	 * holds a range but is not drawn and cannot project values.
	 * @param name Name of the axis, e.g. {@link XYPlot#AXIS_X}.
	 * @param axis Axis. Passing {@code null} removes the axis.
	 */
	void setAxis(String name, Axis axis);

	/**
	 * Removes the axis with the specified name.
	 * @param name Name of the axis to be removed.
	 */
	void removeAxis(String name);

	/**
	 * Returns a collection of all names of the axes stored in this plot.
	 * @return The names of all axes stored in this plot.
	 */
	Collection<String> getAxesNames();

	/**
	 * Recomputes the range of the named axis from the values of every column
	 * mapped to it. Axes that are not set to auto-scale are left alone, so
	 * calling this does not undo a range that was set by hand.
	 * @param axisName Name of the axis that should be scaled.
	 * @see Axis#setAutoscaled(boolean)
	 */
	void autoscaleAxis(String axisName);

	/**
	 * Returns the renderer for the axis with the specified name.
	 * @param axisName Axis name.
	 * @return Instance that renders the axis.
	 */
	AxisRenderer getAxisRenderer(String axisName);

	/**
	 * Sets the renderer for the axis with the specified name.
	 * @param axisName Name of the axis to be rendered.
	 * @param renderer Instance to render the axis.
	 */
	void setAxisRenderer(String axisName, AxisRenderer renderer);

	/**
	 * Returns the region in which the data itself is drawn, i.e. the plot
	 * without its title, legend and axes. Its background, border and clipping
	 * are configured on the returned object.
	 * @return The plot area of this plot.
	 */
	PlotArea getPlotArea();

	/**
	 * Returns the title of this plot. The title is always present; it is simply
	 * empty and takes no space until text is set on it with
	 * {@link Label#setText(String)}.
	 * @return Label representing the title.
	 */
	Label getTitle();

	/**
	 * Returns the legend of this plot. The legend is filled automatically from
	 * the visible data sources, but only shown if
	 * {@link #setLegendVisible(boolean)} was called with {@code true}.
	 * @return Legend.
	 */
	Legend getLegend();

	/**
	 * Adds a new data series to the plot which is visible by default.
	 * @param source Data series.
	 */
	void add(DataSource source);

	/**
	 * Adds a new data series to the plot.
	 * @param source Data series.
	 * @param visible {@code true} if the series should be displayed,
	 *        {@code false} otherwise.
	 */
	void add(DataSource source, boolean visible);

	/**
	 * Inserts the specified data series to the plot at a specified position.
	 * @param index Position.
	 * @param source Data series.
	 * @param visible {@code true} if the series should be displayed,
	 *        {@code false} otherwise.
	 */
	void add(int index, DataSource source, boolean visible);

	/**
	 * Returns whether the plot contains the specified data series.
	 * @param source Data series.
	 * @return {@code true} if the specified element is stored in the
	 *         plot, otherwise {@code false}
	 */
	boolean contains(DataSource source);

	/**
	 * Returns the data series at a specified index.
	 * @param index Position of the data series.
	 * @return Instance of the data series.
	 */
	DataSource get(int index);

	/**
	 * Deletes the specified data series from the plot.
	 * @param source Data series.
	 * @return {@code true} if the series existed,
	 *         otherwise {@code false}.
	 */
	boolean remove(DataSource source);

	/**
	 * Removes all data series from this plot.
	 */
	void clear();

	/**
	 * Returns the mapping of data source columns to axis names. The elements
	 * of returned array equal the column indexes, i.e. the first element (axis
	 * name) matches the first column of {@code source}. If no mapping exists
	 * {@code null} will be stored in the array.
	 * @param source Data source.
	 * @return Array containing axis names in the order of the columns,
	 *         or {@code null} if no mapping exists for the column.
	 */
	String[] getMapping(DataSource source);

	/**
	 * Sets the mapping of data source columns to axis names. The column index
	 * is taken from the order of the axis names, i.e. the first column of
	 * {@code source} will be mapped to first element of {@code axisNames}.
	 * Axis names with value {@code null} will be ignored.
	 * @param source Data source.
	 * @param axisNames Sequence of axis names in the order of the columns.
	 */
	void setMapping(DataSource source, String... axisNames);

	/**
	 * Returns a list of all data series stored in the plot.
	 * @return List of all data series.
	 */
	List<DataSource> getData();

	/**
	 * Returns a list of all visible data series stored in the plot.
	 * @return List of all visible data series.
	 */
	List<DataSource> getVisibleData();

	/**
	 * Returns whether the specified data series is drawn.
	 * @param source Data series.
	 * @return {@code true} if visible, {@code false} otherwise.
	 */
	boolean isVisible(DataSource source);

	/**
	 * Changes the visibility of the specified data series.
	 * @param source Data series.
	 * @param visible {@code true} if the series should be visible,
	 *        {@code false} otherwise.
	 */
	void setVisible(DataSource source, boolean visible);

	/**
	 * Returns the paint which is used to fill the background of the plot.
	 * @return Paint which is used to fill the background of the plot.
	 */
	Paint getBackground();

	/**
	 * Sets the paint which will be used to fill the background of the plot.
	 * @param background Paint which will be used to fill the background of the
	 * plot.
	 */
	void setBackground(Paint background);

	/**
	 * Returns the stroke which is used to paint the border of the plot.
	 * @return Stroke which is used to paint the border of the plot.
	 */
	Stroke getBorderStroke();

	/**
	 * Sets the stroke which will be used to paint the border of the plot.
	 * @param border Stroke which will be used to paint the border of the plot.
	 */
	void setBorderStroke(Stroke border);

	/**
	 * Returns the paint which is used to fill the border of the plot.
	 * @return Paint which is used to fill the border of the plot.
	 */
	Paint getBorderColor();

	/**
	 * Sets the paint which will be used to fill the border of the plot.
	 * @param color Paint which will be used to fill the border of the plot.
	 */
	void setBorderColor(Paint color);

	/**
	 * Returns the base font used by the plot.
	 * @return Font used by the plot.
	 */
	Font getFont();

	/**
	 * Sets the base font that will be used by the plot.
	 * @param font Font that will used by the plot.
	 */
	void setFont(Font font);

	/**
	 * Returns whether the legend is shown.
	 * @return {@code true} if the legend is shown,
	 *         {@code false} if the legend is hidden.
	 */
	boolean isLegendVisible();

	/**
	 * Sets whether the legend will be shown.
	 * @param legendVisible {@code true} if the legend should be shown,
	 *         {@code false} if the legend should be hidden.
	 */
	void setLegendVisible(boolean legendVisible);

	/**
	 * Returns the current positioning of the legend inside the plot.
	 * @return Current positioning of the legend inside the plot.
	 */
	Location getLegendLocation();

	/**
	 * Sets the positioning of the legend inside the plot.
	 * @param location Positioning of the legend inside the plot.
	 */
	void setLegendLocation(Location location);

	/**
	 * Returns the spacing between the plot area and the legend.
	 * @return Spacing between the plot area and the legend relative to font
	 * height.
	 */
	double getLegendDistance();

	/**
	 * Sets the spacing between the plot area and the legend.
	 * The distance is defined in font height.
	 * @param distance Spacing between the plot area and the legend relative to font
	 * height.
	 */
	void setLegendDistance(double distance);
}

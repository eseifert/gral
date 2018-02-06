/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
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
 * <p>Interface for classes that display data in a plot.</p>
 * <p>Functionality includes:</p>
 * <ul>
 *   <li>Adding axes to the plot</li>
 *   <li>Adding a title to the plot</li>
 *   <li>Adding a legend to the plot</li>
 *   <li>Administration of settings</li>
 * </ul>
 */
public interface Plot extends Drawable, Container {
	/**
	 * Returns the axis with the specified name.
	 * @param name Name of the axis.
	 * @return Axis.
	 */
	Axis getAxis(String name);

	/**
	 * Sets the axis with the specified name and the associated
	 * {@code AxisRenderer}.
	 * @param name Name of the axis.
	 * @param axis Axis.
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
	 * Tries to automatically set the ranges of the axes specified by the name
	 * if it is set to auto-scale.
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
	 * Returns the drawing area of this plot.
	 * @return {@code PlotArea2D}.
	 */
	PlotArea getPlotArea();

	/**
	 * Returns the title component of this plot.
	 * @return Label representing the title.
	 */
	Label getTitle();

	/**
	 * Returns the legend component.
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

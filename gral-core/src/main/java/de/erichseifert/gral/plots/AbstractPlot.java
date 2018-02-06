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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.erichseifert.gral.data.Column;
import de.erichseifert.gral.data.DataChangeEvent;
import de.erichseifert.gral.data.DataListener;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.graphics.Container;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.graphics.layout.EdgeLayout;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.graphics.layout.OuterEdgeLayout;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.legends.Legend;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.graphics.Location;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.SerializationUtils;


/**
 * Basic implementation of a plot that can listen to changes of data sources
 * and settings.
 */
public abstract class AbstractPlot extends DrawableContainer
		implements Plot, DataListener {
	/** Version id for serialization. */
	private static final long serialVersionUID = -6609155385940228771L;

	/** Default size of the plot title relative to the size of the base font. */
	private static final float DEFAULT_TITLE_FONT_SIZE = 1.5f;
	/** Default space between layout components relative to the size of the base font. */
	private static final float DEFAULT_LAYOUT_GAP = 2f;

	/** Data sources. */
	private final List<DataSource> data;
	/** Set of all data sources that are visible (not hidden). */
	private final Set<DataSource> dataVisible;

	/** Mapping of axis names to axis objects. */
	private final Map<String, Axis> axes;
	/** Mapping of axis names to axis renderer objects. */
	private final Map<String, AxisRenderer> axisRenderers;
	/** Mapping of axis names to drawable objects. */
	private final Map<String, Drawable> axisDrawables;

	/** Mapping of data source columns to axes. **/
	private final Map<DataSource, Map<Integer, String>> columnToAxisMappingByDataSource;
	/** Minimum values of axes. **/
	private final Map<String, Double> axisMin;
	/** Maximum values of axes. **/
	private final Map<String, Double> axisMax;

	/** Title text of the plot. */
	private final Label title;
	/** AbstractPlot area used to render the data. */
	private PlotArea plotArea;
	/** Container that will store and layout the plot legend. */
	private final Container legendContainer;
	/** AbstractPlot legend. */
	private Legend legend;

	/** Paint to fill the plot background. */
	private Paint background;
	/** Stroke to draw the plot border. */
	private transient Stroke borderStroke;
	/** Paint to fill the plot border. */
	private Paint borderColor;

	/** Base font which is used as default for other elements of the plot and
	for calculation of relative sizes. */
	private Font font;

	/** Decides whether a legend will be shown. */
	private boolean legendVisible;
	/** Positioning of the legend. */
	private Location legendLocation;
	/** Distance of the legend to the plot area. */
	private double legendDistance;

	/**
	 * Initializes a new {@code AbstractPlot} instance with the specified data series.
	 * The series will be visible by default.
	 * @param series Initial data series to be displayed.
	 */
	public AbstractPlot(DataSource... series) {
		super(new EdgeLayout());

		dataVisible = new HashSet<>();

		axes = new HashMap<>();
		axisRenderers = new HashMap<>();
		axisDrawables = new HashMap<>();

		columnToAxisMappingByDataSource = new HashMap<>();
		axisMin = new HashMap<>();
		axisMax = new HashMap<>();

		data = new LinkedList<>();
		for (DataSource source : series) {
			add(source);
		}

		// No background or border by default
		background = null;
		borderStroke = null;
		borderColor = Color.BLACK;

		// Use system standard font as base font
		font = Font.decode(null);
		updateBaseFont();

		// Create title
		title = new Label();
		title.setFont(font.deriveFont(DEFAULT_TITLE_FONT_SIZE*font.getSize2D()));
		add(title, Location.NORTH);

		// Create legend, but don't show it by default
		legendContainer = new DrawableContainer(new OuterEdgeLayout(0.0));
		legendLocation = Location.CENTER;
		legendDistance = 2.0;
		legendVisible = false;
		refreshLegendLayout();
	}

	/**
	 * Draws the {@code Drawable} with the specified drawing context.
	 * @param context Environment used for drawing
	 */
	@Override
	public void draw(DrawingContext context) {
		Graphics2D graphics = context.getGraphics();

		Paint bg = getBackground();
		if (bg != null) {
			GraphicsUtils.fillPaintedShape(graphics, getBounds(), bg, null);
		}

		Stroke stroke = getBorderStroke();
		if (stroke != null) {
			Paint fg = getBorderColor();
			GraphicsUtils.drawPaintedShape(
					graphics, getBounds(), fg, null, stroke);
		}

		drawComponents(context);
	}

	/**
	 * Draws the plot's axes into the specified drawing context.
	 * @param context Environment used for drawing.
	 */
	protected void drawAxes(DrawingContext context) {
		for (Drawable d : axisDrawables.values()) {
			if (d != null) {
				d.draw(context);
			}
		}
	}

	/**
	 * Draws the plot's legend into the specified drawing context.
	 * @param context Environment used for drawing.
	 */
	protected void drawLegend(DrawingContext context) {
		if (!isLegendVisible() || getLegend() == null) {
			return;
		}
		getLegend().draw(context);
	}

	@Override
	public void layout() {
		super.layout();
		layoutAxes();
		layoutLegend();
	}

	/**
	 * Calculates the bounds of the axes.
	 */
	protected void layoutAxes() {
	}

	/**
	 * Calculates the bounds of the legend component.
	 */
	protected void layoutLegend() {
		if (getPlotArea() == null) {
			return;
		}
		Container legendContainer = getLegendContainer();
		Rectangle2D plotBounds = getPlotArea().getBounds();
		legendContainer.setBounds(plotBounds);
	}

	/**
	 * Returns the axis with the specified name.
	 * @param name Name of the axis.
	 * @return Axis.
	 */
	public Axis getAxis(String name) {
		return axes.get(name);
	}

	/**
	 * Sets the axis with the specified name and the associated
	 * {@code AxisRenderer}.
	 * @param name Name of the axis.
	 * @param axis Axis.
	 */
	public void setAxis(String name, Axis axis) {
		if (axis == null) {
			removeAxis(name);
		} else {
			axes.put(name, axis);
		}
	}

	/**
	 * Removes the axis with the specified name.
	 * @param name Name of the axis to be removed.
	 */
	public void removeAxis(String name) {
		axes.remove(name);
		axisRenderers.remove(name);
		axisDrawables.remove(name);
	}

	/**
	 * Returns a collection of all names of the axes stored in this plot.
	 * @return The names of all axes stored in this plot.
	 */
	public Collection<String> getAxesNames() {
		return axes.keySet();
	}

	/**
	 * Creates all axes that are defined by the current plot type.
	 */
	protected void createDefaultAxes() {
	}

	/**
	 * Creates all axis renderers that are defined by the current plot type.
	 */
	protected void createDefaultAxisRenderers() {
	}

	/**
	 * Tries to automatically set the ranges of all axes that are set to auto-scale.
	 * @see Axis#setAutoscaled(boolean)
	 */
	protected void autoscaleAxes() {
		if (data.isEmpty()) {
			return;
		}
		for (String axisName : getAxesNames()) {
			autoscaleAxis(axisName);
		}
	}

	/**
	 * Tries to automatically set the ranges of the axes specified by the name
	 * if it is set to auto-scale.
	 * @param axisName Name of the axis that should be scaled.
	 * @see Axis#setAutoscaled(boolean)
	 */
	public void autoscaleAxis(String axisName) {
		Axis axis = getAxis(axisName);
		if (axis == null || !axis.isAutoscaled()) {
			return;
		}
		double min = getAxisMin(axisName);
		double max = getAxisMax(axisName);
		double margin = 0.0*(max - min);
		axis.setRange(min - margin, max + margin);
	}

	/**
	 * Returns the renderer for the axis with the specified name.
	 * @param axisName Axis name.
	 * @return Instance that renders the axis.
	 */
	public AxisRenderer getAxisRenderer(String axisName) {
		return axisRenderers.get(axisName);
	}

	/**
	 * Sets the renderer for the axis with the specified name.
	 * @param axisName Name of the axis to be rendered.
	 * @param renderer Instance to render the axis.
	 */
	public void setAxisRenderer(String axisName, AxisRenderer renderer) {
		Drawable comp = null;
		if (renderer == null) {
			axisRenderers.remove(axisName);
		} else {
			axisRenderers.put(axisName, renderer);
			Axis axis = getAxis(axisName);
			comp = renderer.getRendererComponent(axis);
		}
		setAxisComponent(axisName, comp);
		layout();
	}

	/**
	 * Returns the component that is used to draw the specified axis.
	 * @param axisName Name of the axis.
	 * @return Instance that draws the axis.
	 */
	protected Drawable getAxisComponent(String axisName) {
		return axisDrawables.get(axisName);
	}

	/**
	 * Sets the component that should be used for drawing the specified axis.
	 * @param axisName Name of the axis.
	 * @param comp Instance that draws the axis.
	 */
	private void setAxisComponent(String axisName, Drawable comp) {
		if (comp == null) {
			axisDrawables.remove(axisName);
		} else {
			axisDrawables.put(axisName, comp);
		}
	}

	/**
	 * Returns the drawing area of this plot.
	 * @return {@code PlotArea2D}.
	 */
	public PlotArea getPlotArea() {
		return plotArea;
	}

	/**
	 * Sets the drawing area to the specified value.
	 * @param plotArea {@code PlotArea2D} to be set.
	 */
	protected void setPlotArea(PlotArea plotArea) {
		if (this.plotArea != null) {
			remove(this.plotArea);
			this.plotArea.setBaseFont(null);
		}
		this.plotArea = plotArea;
		if (this.plotArea != null) {
			this.plotArea.setBaseFont(font);
			add(this.plotArea, Location.CENTER);
		}
	}

	/**
	 * Returns the title component of this plot.
	 * @return Label representing the title.
	 */
	public Label getTitle() {
		return title;
	}

	/**
	 * Returns the object containing the Legend.
	 * @return Container.
	 */
	protected Container getLegendContainer() {
		return legendContainer;
	}

	/**
	 * Returns the legend component.
	 * @return Legend.
	 */
	public Legend getLegend() {
		return legend;
	}

	/**
	 * Sets the legend to the specified value.
	 * @param legend Legend to be set.
	 */
	protected void setLegend(Legend legend) {
		if (this.legend != null) {
			legendContainer.remove(this.legend);
			this.legend.clear();
			this.legend.setBaseFont(null);
		}
		this.legend = legend;
		if (this.legend != null) {
			this.legend.setBaseFont(font);
			Location constraints = getLegendLocation();
			legendContainer.add(legend, constraints);
			for (DataSource source : getVisibleData()) {
				legend.add(source);
			}
		}
	}

	/**
	 * Refreshes the positioning and spacing of the legend.
	 */
	protected void refreshLegendLayout() {
		double absoluteLegendDistance = 0.0;
		if (MathUtils.isCalculatable(legendDistance)) {
			absoluteLegendDistance = legendDistance*font.getSize2D();
		}

		OuterEdgeLayout layout = new OuterEdgeLayout(absoluteLegendDistance);
		legendContainer.setLayout(layout);
	}

	@Override
	public Paint getBackground() {
		return background;
	}

	@Override
	public void setBackground(Paint background) {
		this.background = background;
	}

	@Override
	public Stroke getBorderStroke() {
		return borderStroke;
	}

	@Override
	public void setBorderStroke(Stroke border) {
		this.borderStroke = border;
	}

	@Override
	public Paint getBorderColor() {
		return borderColor;
	}

	@Override
	public void setBorderColor(Paint color) {
		this.borderColor = color;
	}

	@Override
	public Font getFont() {
		return font;
	}

	@Override
	public void setFont(Font font) {
		this.font = font;
		updateBaseFont();
	}

	private void updateBaseFont() {
		// Update layout
		float gap = DEFAULT_LAYOUT_GAP*font.getSize2D();
		getLayout().setGapX(gap);
		getLayout().setGapY(gap);

		// Update plot area
		if (plotArea != null) {
			plotArea.setBaseFont(font);
		}

		// Update legend
		if (legend != null) {
			legend.setBaseFont(font);
		}
	}

	@Override
	public boolean isLegendVisible() {
		return legendVisible;
	}

	@Override
	public void setLegendVisible(boolean legendVisible) {
		this.legendVisible = legendVisible;
	}

	@Override
	public Location getLegendLocation() {
		return legendLocation;
	}

	@Override
	public void setLegendLocation(Location location) {
		legendLocation = location;
		if (legend != null) {
			legendContainer.remove(legend);
			legendContainer.add(legend, legendLocation);
		}
	}

	@Override
	public double getLegendDistance() {
		return legendDistance;
	}

	@Override
	public void setLegendDistance(double distance) {
		legendDistance = distance;
		refreshLegendLayout();
	}

	/**
	 * Adds a new data series to the plot which is visible by default.
	 * @param source Data series.
	 */
	public void add(DataSource source) {
		add(source, true);
	}

	/**
	 * Adds a new data series to the plot.
	 * @param source Data series.
	 * @param visible {@code true} if the series should be displayed,
	 *        {@code false} otherwise.
	 */
	public void add(DataSource source, boolean visible) {
		add(data.size(), source, visible);
	}

	/**
	 * Inserts the specified data series to the plot at a specified position.
	 * @param index Position.
	 * @param source Data series.
	 * @param visible {@code true} if the series should be displayed,
	 *        {@code false} otherwise.
	 */
	public void add(int index, DataSource source, boolean visible) {
		data.add(index, source);
		if (visible) {
			dataVisible.add(source);
		}
		autoscaleAxes();
		if (getLegend() != null) {
			getLegend().add(source);
		}
		source.addDataListener(this);
		invalidateAxisExtrema();
	}

	/**
	 * Returns whether the plot contains the specified data series.
	 * @param source Data series.
	 * @return {@code true} if the specified element is stored in the
	 *         plot, otherwise {@code false}
	 */
	public boolean contains(DataSource source) {
		return data.contains(source);
	}

	/**
	 * Returns the data series at a specified index.
	 * @param index Position of the data series.
	 * @return Instance of the data series.
	 */
	public DataSource get(int index) {
		return data.get(index);
	}

	/**
	 * Deletes the specified data series from the plot.
	 * @param source Data series.
	 * @return {@code true} if the series existed,
	 *         otherwise {@code false}.
	 */
	public boolean remove(DataSource source) {
		source.removeDataListener(this);
		dataVisible.remove(source);
		if (getLegend() != null) {
			getLegend().remove(source);
		}
		boolean existed = data.remove(source);
		invalidateAxisExtrema();
		return existed;
	}

	/**
	 * Removes all data series from this plot.
	 */
	public void clear() {
		for (DataSource source : data) {
			source.removeDataListener(this);
		}
		dataVisible.clear();
		if (getLegend() != null) {
			getLegend().clear();
		}
		data.clear();
		invalidateAxisExtrema();
	}

	/**
	 * Returns the mapping of a data source column to an axis name. If no
	 * mapping exists {@code null} will be returned.
	 * @param source Data source.
	 * @param col Column index.
	 * @return Axis name or {@code null} if no mapping exists.
	 */
	private String getMapping(DataSource source, int col) {
		Map<Integer, String> columnToAxisMapping = columnToAxisMappingByDataSource.get(source);
		return columnToAxisMapping != null ? columnToAxisMapping.get(col) : null;
	}

	/**
	 * Returns the mapping of data source columns to axis names. The elements
	 * of returned array equal the column indexes, i.e. the first element (axis
	 * name) matches the first column of {@code source}. If no mapping exists
	 * {@code null} will be stored in the array.
	 * @param source Data source.
	 * @return Array containing axis names in the order of the columns,
	 *         or {@code null} if no mapping exists for the column.
	 */
	public String[] getMapping(DataSource source) {
		String[] mapping = new String[source.getColumnCount()];
		for (int col = 0; col < mapping.length; col++) {
			mapping[col] = getMapping(source, col);
		}
		return mapping;
	}

	/**
	 * Sets the mapping of data source columns to axis names. The column index
	 * is taken from the order of the axis names, i.e. the first column of
	 * {@code source} will be mapped to first element of {@code axisNames}.
	 * Axis names with value {@code null} will be ignored.
	 * @param source Data source.
	 * @param axisNames Sequence of axis names in the order of the columns.
	 */
	public void setMapping(DataSource source, String... axisNames) {
		if (!contains(source)) {
			throw new IllegalArgumentException(
				"Data source does not exist in plot."); //$NON-NLS-1$
		}
		if (axisNames.length > source.getColumnCount()) {
			throw new IllegalArgumentException(MessageFormat.format(
				"Data source only has {0,number,integer} column, {1,number,integer} values given.", //$NON-NLS-1$
				source.getColumnCount(), axisNames.length));
		}
		Map<Integer, String> columnToAxisMapping = new HashMap<>();
		for (int col = 0; col < axisNames.length; col++) {
			String axisName = axisNames[col];
			if (axisName != null) {
				columnToAxisMapping.put(col, axisName);
			}
		}
		columnToAxisMappingByDataSource.put(source, columnToAxisMapping);
		invalidateAxisExtrema();
	}

	/**
	 * Returns the minimum value of the axis specified by {@code axisName}.
	 * @param axisName Name of the axis.
	 * @return Minimum value for the specified axis, or {@code 0.0} if no
	 *         minimum value can be determined.
	 */
	protected Double getAxisMin(String axisName) {
		Double min = axisMin.get(axisName);
		if (min == null) {
			revalidateAxisExtrema();
			min = axisMin.get(axisName);
		}
		if (min == null) {
			min = 0.0;
		}
		return min;
	}
	/**
	 * Returns the maximum value of the axis specified by {@code axisName}.
	 * @param axisName Name of the axis.
	 * @return Maximum value for the specified axis, or {@code 0.0} if no
	 *         maximum value can be determined.
	 */
	protected Double getAxisMax(String axisName) {
		Double max = axisMax.get(axisName);
		if (max == null) {
			revalidateAxisExtrema();
			max = axisMax.get(axisName);
		}
		if (max == null) {
			return 0.0;
		}
		return max;
	}

	/**
	 * Returns a list of all data series stored in the plot.
	 * @return List of all data series.
	 */
	public List<DataSource> getData() {
		return Collections.unmodifiableList(data);
	}

	/**
	 * Returns a list of all visible data series stored in the plot.
	 * @return List of all visible data series.
	 */
	public List<DataSource> getVisibleData() {
		List<DataSource> visible = new LinkedList<>();
		for (DataSource s : data) {
			if (dataVisible.contains(s)) {
				visible.add(s);
			}
		}
		return visible;
	}

	/**
	 * Returns whether the specified data series is drawn.
	 * @param source Data series.
	 * @return {@code true} if visible, {@code false} otherwise.
	 */
	public boolean isVisible(DataSource source) {
		return dataVisible.contains(source);
	}

	/**
	 * Changes the visibility of the specified data series.
	 * @param source Data series.
	 * @param visible {@code true} if the series should be visible,
	 *        {@code false} otherwise.
	 */
	public void setVisible(DataSource source, boolean visible) {
		if (visible) {
			if (dataVisible.add(source)) {
				invalidateAxisExtrema();
			}
		} else {
			if (dataVisible.remove(source)) {
				invalidateAxisExtrema();
			}
		}
	}

	/**
	 * Method that is invoked when data has been added.
	 * This method is invoked by objects that provide support for
	 * {@code DataListener}s and should not be called manually.
	 * @param source Data source that has been changed.
	 * @param events Optional event object describing the data values that
	 *        have been added.
	 */
	public void dataAdded(DataSource source, DataChangeEvent... events) {
		dataChanged(source, events);
	}

	/**
	 * Method that is invoked when data has been updated.
	 * This method is invoked by objects that provide support for
	 * {@code DataListener}s and should not be called manually.
	 * @param source Data source that has been changed.
	 * @param events Optional event object describing the data values that
	 *        have been updated.
	 */
	public void dataUpdated(DataSource source, DataChangeEvent... events) {
		dataChanged(source, events);
	}

	/**
	 * Method that is invoked when data has been removed.
	 * This method is invoked by objects that provide support for
	 * {@code DataListener}s and should not be called manually.
	 * @param source Data source that has been changed.
	 * @param events Optional event object describing the data values that
	 *        have been removed.
	 */
	public void dataRemoved(DataSource source, DataChangeEvent... events) {
		dataChanged(source, events);
	}

	/**
	 * Method that is invoked when data has been added, updated, or removed.
	 * @param source Data source that has been changed.
	 * @param events Optional event object describing the data values that
	 *        have been changed.
	 */
	protected void dataChanged(DataSource source, DataChangeEvent... events) {
		invalidateAxisExtrema();
		autoscaleAxes();
		layout();
	}

	/**
	 * Causes cached plot data to be be updated.
	 */
	private void invalidateAxisExtrema() {
		axisMin.clear();
		axisMax.clear();
	}

	/**
	 * Rebuilds cached plot data.
	 */
	private void revalidateAxisExtrema() {
		synchronized (this) {
			for (Entry<DataSource, Map<Integer, String>> entryByDataSource : columnToAxisMappingByDataSource.entrySet()) {
				DataSource dataSource = entryByDataSource.getKey();
				Map<Integer, String> columnToAxisMapping = entryByDataSource.getValue();
				for (Entry<Integer, String> entry : columnToAxisMapping.entrySet()) {
					Integer colIndex = entry.getKey();
					String axisName = entry.getValue();

					Column<?> col = dataSource.getColumn(colIndex);
					Double min = axisMin.get(axisName);
					Double max = axisMax.get(axisName);
					if (min == null || max == null) {
						min = col.getStatistics(Statistics.MIN);
						max = col.getStatistics(Statistics.MAX);
					} else {
						min = Math.min(min, col.getStatistics(Statistics.MIN));
						max = Math.max(max, col.getStatistics(Statistics.MAX));
					}
					axisMin.put(axisName, min);
					axisMax.put(axisName, max);
				}
			}
		}
	}

	/**
	 * Custom deserialization method.
	 * @param in Input stream.
	 * @throws ClassNotFoundException if a serialized class doesn't exist anymore.
	 * @throws IOException if there is an error while reading data from the
	 *         input stream.
	 */
	private void readObject(ObjectInputStream in)
			throws ClassNotFoundException, IOException {
		// Default deserialization
		in.defaultReadObject();
		// Custom deserialization
		borderStroke = (Stroke) SerializationUtils.unwrap(
				(Serializable) in.readObject());

		// Restore listeners
		for (DataSource source : getData()) {
			source.addDataListener(this);
		}
	}

	/**
	 * Custom serialization method.
	 * @param out Output stream.
	 * @throws ClassNotFoundException if a serialized class doesn't exist.
	 * @throws IOException if there is an error while writing data to the
	 *         output stream.
	 */
	private void writeObject(ObjectOutputStream out)
			throws ClassNotFoundException, IOException {
		// Default serialization
		out.defaultWriteObject();
		// Custom serialization
		out.writeObject(SerializationUtils.wrap(borderStroke));

		// Restore listeners
		for (DataSource source : getData()) {
			source.addDataListener(this);
		}
	}
}

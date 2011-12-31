/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import de.erichseifert.gral.data.Column;
import de.erichseifert.gral.data.DataChangeEvent;
import de.erichseifert.gral.data.DataListener;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.graphics.Container;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.graphics.EdgeLayout;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.Location;
import de.erichseifert.gral.util.SettingChangeEvent;
import de.erichseifert.gral.util.SettingsListener;
import de.erichseifert.gral.util.Tuple;


/**
 * Basic implementation of a plot that can listen to changes of data sources
 * and settings.
 */
public abstract class AbstractPlot extends DrawableContainer
		implements DataListener, SettingsListener, Plot {
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
	private final Map<Tuple, String> mapping;
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

	/**
	 * Initializes a new {@code AbstractPlot} instance with the specified data series.
	 * The series will be visible by default.
	 * @param series Initial data series to be displayed.
	 */
	public AbstractPlot(DataSource... series) {
		super(new EdgeLayout(20.0, 20.0));

		title = new Label(); //$NON-NLS-1$
		title.setSetting(Label.FONT, Font.decode(null).deriveFont(18f));

		legendContainer = new DrawableContainer(new EdgeLayout(0.0, 0.0));

		dataVisible = new HashSet<DataSource>();

		axes = new HashMap<String, Axis>();
		axisRenderers = new HashMap<String, AxisRenderer>();
		axisDrawables = new HashMap<String, Drawable>();

		mapping = new HashMap<Tuple, String>();
		axisMin = new HashMap<String, Double>();
		axisMax = new HashMap<String, Double>();

		data = new LinkedList<DataSource>();
		for (DataSource source : series) {
			add(source);
		}

		addSettingsListener(this);
		setSettingDefault(TITLE, null);
		setSettingDefault(TITLE_FONT, Font.decode(null).deriveFont(18f));
		setSettingDefault(BACKGROUND, null);
		setSettingDefault(BORDER, null);
		setSettingDefault(COLOR, Color.BLACK);
		setSettingDefault(ANTIALISING, true);
		setSettingDefault(LEGEND, false);
		setSettingDefault(LEGEND_LOCATION, Location.NORTH_WEST);
		setSettingDefault(LEGEND_MARGIN, new Insets2D.Double(20.0));

		add(title, Location.NORTH);
	}

	@Override
	public void draw(DrawingContext context) {
		Graphics2D graphics = context.getGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				this.<Boolean>getSetting(ANTIALISING)
					? RenderingHints.VALUE_ANTIALIAS_ON
					: RenderingHints.VALUE_ANTIALIAS_OFF);

		Paint bg = getSetting(BACKGROUND);
		if (bg != null) {
			GraphicsUtils.fillPaintedShape(graphics, getBounds(), bg, null);
		}

		Stroke stroke = getSetting(BORDER);
		if (stroke != null) {
			Paint fg = getSetting(COLOR);
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
		boolean isVisible = this.<Boolean>getSetting(LEGEND);
		if (!isVisible || legend == null) {
			return;
		}
		legend.draw(context);
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.plots.Plot#getAxis(java.lang.String)
	 */
	public Axis getAxis(String name) {
		return axes.get(name);
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.plots.Plot#setAxis(java.lang.String, de.erichseifert.gral.plots.axes.Axis)
	 */
	public void setAxis(String name, Axis axis) {
		if (axis == null) {
			removeAxis(name);
		} else {
			axes.put(name, axis);
		}
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.plots.Plot#removeAxis(java.lang.String)
	 */
	public void removeAxis(String name) {
		axes.remove(name);
		axisRenderers.remove(name);
		axisDrawables.remove(name);
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.plots.Plot#getAxesNames()
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
	protected void autoScaleAxes() {
		if (data.isEmpty()) {
			return;
		}
		for (String axisName : getAxesNames()) {
			Axis axis = getAxis(axisName);
			if (axis == null || !axis.isAutoscaled()) {
				continue;
			}
			double min = getAxisMin(axisName);
			double max = getAxisMax(axisName);
			double margin = 0.0*(max - min);
			axis.setRange(min - margin, max + margin);
		}
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.plots.Plot#getAxisRenderer(java.lang.String)
	 */
	public AxisRenderer getAxisRenderer(String axisName) {
		return axisRenderers.get(axisName);
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.plots.Plot#setAxisRenderer(java.lang.String, de.erichseifert.gral.plots.axes.AxisRenderer)
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

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.plots.Plot#getPlotArea()
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
		}
		this.plotArea = plotArea;
		if (this.plotArea != null) {
			add(this.plotArea, Location.CENTER);
		}
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.plots.Plot#getTitle()
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

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.plots.Plot#getLegend()
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
		}
		this.legend = legend;
		if (this.legend != null) {
			Location constraints = getSetting(LEGEND_LOCATION);
			legendContainer.add(legend, constraints);
		}
	}

	/**
	 * Invoked if a setting has changed.
	 * @param event Event containing information about the changed setting.
	 */
	public void settingChanged(SettingChangeEvent event) {
		Key key = event.getKey();
		if (TITLE.equals(key)) {
			String text = getSetting(TITLE);
			if (text == null) {
				text = ""; //$NON-NLS-1$
			}
			title.setText(text);
		} else if (TITLE_FONT.equals(key)) {
			Font font = getSetting(TITLE_FONT);
			if (font == null) {
				font = Font.decode(null).deriveFont(18f);
			}
			title.setSetting(Label.FONT, font);
		} else if (LEGEND_LOCATION.equals(key)) {
			Location constraints = getSetting(LEGEND_LOCATION);
			if (legend != null) {
				legendContainer.remove(legend);
				legendContainer.add(legend, constraints);
			}
		} else if (LEGEND_MARGIN.equals(key)) {
			Insets2D margin = getSetting(LEGEND_MARGIN);
			legendContainer.setInsets(margin);
		}
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.plots.Plot#add(de.erichseifert.gral.data.DataSource)
	 */
	public void add(DataSource source) {
		add(source, true);
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.plots.Plot#add(de.erichseifert.gral.data.DataSource, boolean)
	 */
	public void add(DataSource source, boolean visible) {
		add(data.size(), source, visible);
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.plots.Plot#add(int, de.erichseifert.gral.data.DataSource, boolean)
	 */
	public void add(int index, DataSource source, boolean visible) {
		data.add(index, source);
		if (visible) {
			dataVisible.add(source);
		}
		autoScaleAxes();
		if (getLegend() != null) {
			getLegend().add(source);
		}
		source.addDataListener(this);
		invalidate();
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.plots.Plot#contains(de.erichseifert.gral.data.DataSource)
	 */
	public boolean contains(DataSource source) {
		return data.contains(source);
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.plots.Plot#get(int)
	 */
	public DataSource get(int index) {
		return data.get(index);
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.plots.Plot#remove(de.erichseifert.gral.data.DataSource)
	 */
	public boolean remove(DataSource source) {
		source.removeDataListener(this);
		dataVisible.remove(source);
		if (getLegend() != null) {
			getLegend().remove(source);
		}
		boolean existed = data.remove(source);
		invalidate();
		return existed;
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.plots.Plot#clear()
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
		invalidate();
	}

	/**
	 * Returns the mapping of a data source column to an axis name. If no
	 * mapping exists {@code null} will be returned.
	 * @param source Data source.
	 * @param col Column index.
	 * @return Axis name or {@code null} if no mapping exists.
	 */
	private String getMapping(DataSource source, int col) {
		if (!contains(source)) {
			return null;
		}
		Tuple mapKey = new Tuple(source, col);
		String axisName = mapping.get(mapKey);
		return axisName;
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.plots.Plot#getMapping(de.erichseifert.gral.data.DataSource)
	 */
	public String[] getMapping(DataSource source) {
		String[] mapping = new String[source.getColumnCount()];
		for (int col = 0; col < mapping.length; col++) {
			mapping[col] = getMapping(source, col);
		}
		return mapping;
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.plots.Plot#setMapping(de.erichseifert.gral.data.DataSource, java.lang.String)
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
		for (int col = 0; col < axisNames.length; col++) {
			String axisName = axisNames[col];
			if (axisName != null) {
				Tuple mapKey = new Tuple(source, col);
				mapping.put(mapKey, axisName);
			}
		}
		invalidate();
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
			revalidate();
			min = axisMin.get(axisName);
		}
		if (min == null) {
			return 0.0;
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
			revalidate();
			max = axisMax.get(axisName);
		}
		if (max == null) {
			return 0.0;
		}
		return max;
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.plots.Plot#getData()
	 */
	public List<DataSource> getData() {
		return Collections.unmodifiableList(data);
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.plots.Plot#getVisibleData()
	 */
	public List<DataSource> getVisibleData() {
		List<DataSource> visible = new LinkedList<DataSource>();
		for (DataSource s : data) {
			if (dataVisible.contains(s)) {
				visible.add(s);
			}
		}
		return visible;
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.plots.Plot#isVisible(de.erichseifert.gral.data.DataSource)
	 */
	public boolean isVisible(DataSource source) {
		return dataVisible.contains(source);
	}

	/* (non-Javadoc)
	 * @see de.erichseifert.gral.plots.Plot#setVisible(de.erichseifert.gral.data.DataSource, boolean)
	 */
	public void setVisible(DataSource source, boolean visible) {
		if (visible) {
			if (dataVisible.add(source)) {
				invalidate();
			}
		} else {
			if (dataVisible.remove(source)) {
				invalidate();
			}
		}
	}

	/**
	 * Method that is invoked when data has been added.
	 * This method is invoked by objects that provide support for
	 * {@code DataListener}s and should not be called manually.
	 * @param source Data source that has changed
	 * @param events Optional event object describing the data values that
	 *        have been added
	 */
	public void dataAdded(DataSource source, DataChangeEvent... events) {
		invalidate();
	}

	/**
	 * Method that is invoked when data has been updated.
	 * This method is invoked by objects that provide support for
	 * {@code DataListener}s and should not be called manually.
	 * @param source Data source that has changed
	 * @param events Optional event object describing the data values that
	 *        have been added
	 */
	public void dataUpdated(DataSource source, DataChangeEvent... events) {
		invalidate();
	}

	/**
	 * Method that is invoked when data has been added.
	 * This method is invoked by objects that provide support for
	 * {@code DataListener}s and should not be called manually.
	 * @param source Data source that has changed
	 * @param events Optional event object describing the data values that
	 *        have been added
	 */
	public void dataRemoved(DataSource source, DataChangeEvent... events) {
		invalidate();
	}

	/**
	 * Causes the plot data to be be updated.
	 */
	private void invalidate() {
		axisMin.clear();
		axisMax.clear();
	}

	private void revalidate() {
		synchronized (this) {
			for (Entry<Tuple, String> entry : mapping.entrySet()) {
				Tuple mapKey = entry.getKey();
				DataSource s = (DataSource) mapKey.get(0);
				Column col = s.getColumn((Integer) mapKey.get(1));
				String axisName = entry.getValue();

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

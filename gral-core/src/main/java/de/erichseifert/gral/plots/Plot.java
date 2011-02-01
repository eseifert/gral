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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.erichseifert.gral.Container;
import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.DrawableContainer;
import de.erichseifert.gral.DrawingContext;
import de.erichseifert.gral.EdgeLayout;
import de.erichseifert.gral.Legend;
import de.erichseifert.gral.Location;
import de.erichseifert.gral.PlotArea;
import de.erichseifert.gral.data.DataChangeEvent;
import de.erichseifert.gral.data.DataListener;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.SettingChangeEvent;
import de.erichseifert.gral.util.SettingsListener;


/**
 * <p>Abstract class that displays data in a plot.</p>
 * <p>Functionality includes:</p>
 * <ul>
 *   <li>Adding axes to the plot</li>
 *   <li>Adding a title to the plot</li>
 *   <li>Adding a legend to the plot</li>
 *   <li>Administration of settings</li>
 * </ul>
 */
public abstract class Plot extends DrawableContainer
		implements DataListener, SettingsListener {
	/** Key for specifying the {@link java.lang.String} instance for the title
	of the plot. */
	public static final Key TITLE =
		new Key("plot.title"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	paint the background of the plot. */
	public static final Key BACKGROUND =
		new Key("plot.background"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Stroke} instance to be used to
	paint the border of the plot. */
	public static final Key BORDER =
		new Key("plot.border"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	fill the border of the plot. */
	public static final Key COLOR =
		new Key("plot.color"); //$NON-NLS-1$
	/** Key for specifying the whether antialiasing is enabled. */
	public static final Key ANTIALISING =
		new Key("plot.antialiasing"); //$NON-NLS-1$
	/** Key for specifying whether the legend should be shown. */
	public static final Key LEGEND =
		new Key("plot.legend"); //$NON-NLS-1$
	/** Key for specifying the positioning of the legend using a
	{@link de.erichseifert.gral.Location} value. */
	public static final Key LEGEND_LOCATION =
		new Key("plot.legend.location"); //$NON-NLS-1$
	/** Key for specifying the {@link de.erichseifert.gral.util.Insets2D} that
	describes the legend's margin. */
	public static final Key LEGEND_MARGIN =
		new Key("plot.legend.margin"); //$NON-NLS-1$

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

	/** Title text of the plot. */
	private final Label title;
	/** Plot area used to render the data. */
	private PlotArea plotArea;
	/** Container that will store and layout the plot legend. */
	private final Container legendContainer;
	/** Plot legend. */
	private Legend legend;

	/**
	 * Initializes a new <code>Plot</code> instance with the specified data
	 * series. The series will be visible by default.
	 * @param series Initial data series to be displayed.
	 */
	public Plot(DataSource... series) {
		super(new EdgeLayout(20.0, 20.0));

		title = new Label(""); //$NON-NLS-1$
		title.setSetting(Label.FONT, Font.decode(null).deriveFont(18f));

		legendContainer = new DrawableContainer(new EdgeLayout(0.0, 0.0));

		dataVisible = new HashSet<DataSource>();

		axes = new HashMap<String, Axis>();
		axisRenderers = new HashMap<String, AxisRenderer>();
		axisDrawables = new HashMap<String, Drawable>();

		data = new LinkedList<DataSource>();
		for (DataSource source : series) {
			add(source);
		}

		addSettingsListener(this);
		setSettingDefault(TITLE, null);
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
	 * Draws the plot's axes into the specified <code>Graphics2D</code> object.
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
	 * Draws the plot's legend into the specified <code>Graphics2D</code>
	 * object.
	 * @param context Environment used for drawing.
	 */
	protected void drawLegend(DrawingContext context) {
		boolean isVisible = this.<Boolean>getSetting(LEGEND);
		if (!isVisible || legend == null) {
			return;
		}
		legend.draw(context);
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
	 * <code>AxisRenderer</code>.
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
	 * @return <code>PlotArea2D</code>.
	 */
	public PlotArea getPlotArea() {
		return plotArea;
	}

	/**
	 * Sets the drawing area to the specified value.
	 * @param plotArea <code>PlotArea2D</code> to be set.
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

	/**
	 * Returns the title of this plot.
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
	 * Returns the legend.
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
		}
		this.legend = legend;
		if (this.legend != null) {
			Location constraints = getSetting(LEGEND_LOCATION);
			legendContainer.add(legend, constraints);
		}
	}

	@Override
	public void settingChanged(SettingChangeEvent event) {
		Key key = event.getKey();
		if (TITLE.equals(key)) {
			String text = getSetting(TITLE);
			title.setText((text != null) ? text : ""); //$NON-NLS-1$
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
	 * @param visible <code>true</code> if the series should be displayed,
	 *        <code>false</code> otherwise.
	 */
	public void add(DataSource source, boolean visible) {
		add(data.size(), source, visible);
	}

	/**
	 * Inserts the specified data series to the plot at a specified position.
	 * @param index Position.
	 * @param source Data series.
	 * @param visible <code>true</code> if the series should be displayed,
	 *        <code>false</code> otherwise.
	 */
	public void add(int index, DataSource source, boolean visible) {
		data.add(index, source);
		if (getLegend() != null) {
			getLegend().add(source);
		}
		if (visible) {
			dataVisible.add(source);
		}
		source.addDataListener(this);
		refresh();
	}

	/**
	 * Returns whether the plot contains the specified data series.
	 * @param source Data series.
	 * @return <code>true</code> if the specified element is stored in the
	 *         plot, otherwise <code>false</code>
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
	 * @return <code>true</code> if the series existed,
	 *         otherwise <code>false</code>.
	 */
	public boolean remove(DataSource source) {
		source.removeDataListener(this);
		dataVisible.remove(source);
		if (getLegend() != null) {
			getLegend().remove(source);
		}
		boolean existed = data.remove(source);
		refresh();
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
		refresh();
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
		List<DataSource> visible = new LinkedList<DataSource>();
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
	 * @return <code>true</code> if visible, <code>false</code> otherwise.
	 */
	public boolean isVisible(DataSource source) {
		return dataVisible.contains(source);
	}

	/**
	 * Changes the visibility of the specified data series.
	 * @param source Data series.
	 * @param visible <code>true</code> if the series should be visible,
	 *        <code>false</code> otherwise.
	 */
	public void setVisible(DataSource source, boolean visible) {
		if (visible) {
			if (dataVisible.add(source)) {
				refresh();
			}
		} else {
			if (dataVisible.remove(source)) {
				refresh();
			}
		}
	}

	@Override
	public void dataChanged(DataSource data, DataChangeEvent... events) {
		refresh();
	}

	/**
	 * Causes the plot data to be be updated.
	 */
	public void refresh() {
	}
}

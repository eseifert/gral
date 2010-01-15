/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.plots;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import openjchart.Container;
import openjchart.Drawable;
import openjchart.DrawableContainer;
import openjchart.EdgeLayout;
import openjchart.Legend;
import openjchart.PlotArea2D;
import openjchart.DrawableConstants.Location;
import openjchart.data.DataSource;
import openjchart.plots.axes.Axis;
import openjchart.util.GraphicsUtils;
import openjchart.util.Insets2D;
import openjchart.util.SettingChangeEvent;
import openjchart.util.Settings;
import openjchart.util.SettingsListener;
import openjchart.util.SettingsStorage;

/**
 * Abstract class that displays data in a plot.
 * Functionality includes:
 * <ul>
 * <li>Adding axes to the plot</li>
 * <li>Adding a title to the plot</li>
 * <li>Adding a legend to the plot</li>
 * <li>Administration of settings</li>
 * </ul>
 */
public abstract class Plot extends DrawableContainer implements SettingsStorage, SettingsListener {
	/** Key for specifying the {@link java.lang.String} instance for the title of the plot. */
	public static final String KEY_TITLE = "plot.title";
	/** Key for specifying the {@link java.awt.Paint} instance to be used to paint the background of the plot. */
	public static final String KEY_BACKGROUND = "plot.background";
	/** Key for specifying the {@link java.awt.Stroke} instance to be used to paint the border of the plot. */
	public static final String KEY_BORDER = "plot.border";
	/** Key for specifying the whether antialiasing is enabled. */
	public static final String KEY_ANTIALISING = "plot.antialiasing";
	/** Key for specifying whether the legend should be shown. */
	public static final String KEY_LEGEND = "plot.legend";
	/** Key for specifying the positioning of the legend using a {@link openjchart.DrawableConstants.Location} value. */
	public static final String KEY_LEGEND_LOCATION = "plot.legend.location";
	/** Key for specifying the width of the legend's margin. */
	public static final String KEY_LEGEND_MARGIN = "plot.legend.margin";
	/** Key for specifying the scaling behavior of the plot using a {@link ScaleMode} value. */
	public static final String KEY_SCALING_MODE = "plot.scalingMode";

	/** Constants for specifying the scaling behavior values. */
	public static enum ScaleMode {
		/** Constant for specifying that when a plot is scaled the axis extremes should be kept. */
		KEEP_AXES,
		/** Constant for specifying that when a plot is scaled its scale should be kept and axes can be scaled. */
		KEEP_SCALE
	};

	private final Settings settings;

	protected final List<DataSource> data;

	private final Map<String, Axis> axes;
	private final Map<String, Drawable> axisDrawables;

	private Label title;
	private PlotArea2D plotArea;
	private final Container legendContainer;
	private Legend legend;

	/**
	 * Creates a new <code>Plot</code> object with the specified <code>DataSource</code>s.
	 * @param data Data to be displayed.
	 */
	public Plot(DataSource... data) {
		super(new EdgeLayout(20.0, 20.0));

		title = new Label("");
		title.setSetting(Label.KEY_FONT, Font.decode(null).deriveFont(18f));

		legendContainer = new DrawableContainer(new EdgeLayout(0.0, 0.0));

		this.data = new LinkedList<DataSource>();
		axes = new HashMap<String, Axis>();
		axisDrawables = new HashMap<String, Drawable>();
		for (DataSource source : data) {
			this.data.add(source);
		}

		settings = new Settings(this);
		setSettingDefault(KEY_TITLE, null);
		setSettingDefault(KEY_BACKGROUND, null);
		setSettingDefault(KEY_BORDER, null);
		setSettingDefault(KEY_ANTIALISING, true);
		setSettingDefault(KEY_LEGEND, false);
		setSettingDefault(KEY_LEGEND_LOCATION, Location.NORTH_WEST);
		setSettingDefault(KEY_LEGEND_MARGIN, new Insets2D.Double(20.0));

		add(title, Location.NORTH);
	}

	@Override
	public void draw(Graphics2D g2d) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				this.<Boolean>getSetting(KEY_ANTIALISING) ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);

		Paint bg = getSetting(KEY_BACKGROUND);
		if (bg != null) {
			GraphicsUtils.fillPaintedShape(g2d, getBounds(), bg, null);
		}

		Stroke borderStroke = getSetting(KEY_BORDER);
		if (borderStroke != null) {
			Stroke strokeOld = g2d.getStroke();
			g2d.setStroke(borderStroke);
			g2d.draw(getBounds());
			g2d.setStroke(strokeOld);
		}

		drawComponents(g2d);
	}

	/**
	 * Draws the plot's axes into the specified <code>Graphics2D</code> object.
	 * @param g2d Graphics to be used for drawing.
	 */
	protected void drawAxes(Graphics2D g2d) {
		for (Drawable d : axisDrawables.values()) {
			d.draw(g2d);
		}
	}

	/**
	 * Draws the plot's legend into the specified <code>Graphics2D</code> object.
	 * @param g2d Graphics to be used for drawing.
	 */
	protected void drawLegend(Graphics2D g2d) {
		boolean isVisible = getSetting(KEY_LEGEND);
		if (!isVisible || legend == null) {
			return;
		}
		legend.draw(g2d);
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
	 * Sets the axis with the specified name and the associated <code>Drawable</code>.
	 * @param name Name of the axis.
	 * @param axis Axis.
	 * @param drawable Representation of the axis.
	 */
	public void setAxis(String name, Axis axis, Drawable drawable) {
		if (axis == null || drawable == null) {
			removeAxis(name);
			return;
		}
		axes.put(name, axis);
		axisDrawables.put(name, drawable);
	}

	/**
	 * Removes the axis with the specified name.
	 * @param name Name of the axis to be removed.
	 */
	public void removeAxis(String name) {
		axes.remove(name);
		axisDrawables.remove(name);
	}

	/**
	 * Returns the drawing area of this plot.
	 * @return <code>PlotArea2D</code>.
	 */
	public PlotArea2D getPlotArea() {
		return plotArea;
	}

	/**
	 * Sets the drawing area to the specified value.
	 * @param plotArea <code>PlotArea2D</code> to be set.
	 */
	protected void setPlotArea(PlotArea2D plotArea) {
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
			Location constraints = getSetting(KEY_LEGEND_LOCATION);
			legendContainer.add(legend, constraints);
		}
	}

	@Override
	public <T> T getSetting(String key) {
		return settings.<T>get(key);
	}

	@Override
	public <T> void setSetting(String key, T value) {
		settings.<T>set(key, value);
	}

	@Override
	public <T> void removeSetting(String key) {
		settings.remove(key);
	}

	@Override
	public <T> void setSettingDefault(String key, T value) {
		settings.set(key, value);
	}

	@Override
	public <T> void removeSettingDefault(String key) {
		settings.removeDefault(key);
	}

	@Override
	public void settingChanged(SettingChangeEvent event) {
		String key = event.getKey();
		if (KEY_TITLE.equals(key)) {
			String text = getSetting(KEY_TITLE);
			title.setText((text != null) ? text : "");
		} else if (KEY_LEGEND_LOCATION.equals(key)) {
			Location constraints = getSetting(KEY_LEGEND_LOCATION);
			if (legend != null) {
				legendContainer.remove(legend);
				legendContainer.add(legend, constraints);
			}
		} else if (KEY_LEGEND_MARGIN.equals(key)) {
			Insets2D margin = getSetting(KEY_LEGEND_MARGIN);
			legendContainer.setInsets(margin);
		}
	}

}

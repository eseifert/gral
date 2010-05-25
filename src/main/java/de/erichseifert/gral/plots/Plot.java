/**
 * GRAL: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.erichseifert.gral.Container;
import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.DrawableContainer;
import de.erichseifert.gral.EdgeLayout;
import de.erichseifert.gral.Legend;
import de.erichseifert.gral.PlotArea2D;
import de.erichseifert.gral.DrawableConstants.Location;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.SettingChangeEvent;
import de.erichseifert.gral.util.Settings;
import de.erichseifert.gral.util.SettingsListener;
import de.erichseifert.gral.util.SettingsStorage;
import de.erichseifert.gral.util.Settings.Key;


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
	public static final Key TITLE = new Key("plot.title");
	/** Key for specifying the {@link java.awt.Paint} instance to be used to paint the background of the plot. */
	public static final Key BACKGROUND = new Key("plot.background");
	/** Key for specifying the {@link java.awt.Stroke} instance to be used to paint the border of the plot. */
	public static final Key BORDER = new Key("plot.border");
	/** Key for specifying the {@link java.awt.Paint} instance to be used to fill the border of the plot. */
	public static final Key COLOR = new Key("plot.color");
	/** Key for specifying the whether antialiasing is enabled. */
	public static final Key ANTIALISING = new Key("plot.antialiasing");
	/** Key for specifying whether the legend should be shown. */
	public static final Key LEGEND = new Key("plot.legend");
	/** Key for specifying the positioning of the legend using a {@link de.erichseifert.gral.DrawableConstants.Location} value. */
	public static final Key LEGEND_LOCATION = new Key("plot.legend.location");
	/** Key for specifying the width of the legend's margin. */
	public static final Key LEGEND_MARGIN = new Key("plot.legend.margin");
	/** Key for specifying the scaling behavior of the plot using a {@link ScaleMode} value. */
	public static final Key SCALING_MODE = new Key("plot.scalingMode");

	/** Constants for specifying the scaling behavior values. */
	public static enum ScaleMode {
		/** Constant for specifying that when a plot is scaled
		 *  the axis extremes should be preserved. */
		PRESERVE_AXIS_BOUNDS,
		/** Constant for specifying that when a plot is scaled
		 *  its scale should be preserved and axes can be scaled. */
		PRESERVE_PLOT_SCALE
	};

	private final Settings settings;

	protected final List<DataSource> data;

	private final Map<String, Axis> axes;
	private final Map<String, Drawable> axisDrawables;

	private final Label title;
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
		title.setSetting(Label.FONT, Font.decode(null).deriveFont(18f));

		legendContainer = new DrawableContainer(new EdgeLayout(0.0, 0.0));

		this.data = new LinkedList<DataSource>();
		axes = new HashMap<String, Axis>();
		axisDrawables = new HashMap<String, Drawable>();
		for (DataSource source : data) {
			this.data.add(source);
		}

		settings = new Settings(this);
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
	public void draw(Graphics2D g2d) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				this.<Boolean>getSetting(ANTIALISING) ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);

		Paint bg = getSetting(BACKGROUND);
		if (bg != null) {
			GraphicsUtils.fillPaintedShape(g2d, getBounds(), bg, null);
		}

		Stroke stroke = getSetting(BORDER);
		if (stroke != null) {
			Paint fg = getSetting(COLOR);
			GraphicsUtils.drawPaintedShape(g2d, getBounds(), fg, null, stroke);
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
		boolean isVisible = this.<Boolean>getSetting(LEGEND);
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
	 * Returns a Collection of all axes stored in this plot.
	 * @return All axes stored in this plot.
	 */
	public Collection<Axis> getAxes() {
		return axes.values();
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
			Location constraints = getSetting(LEGEND_LOCATION);
			legendContainer.add(legend, constraints);
		}
	}

	@Override
	public <T> T getSetting(Key key) {
		return settings.<T>get(key);
	}

	@Override
	public <T> void setSetting(Key key, T value) {
		settings.<T>set(key, value);
	}

	@Override
	public <T> void removeSetting(Key key) {
		settings.remove(key);
	}

	@Override
	public <T> void setSettingDefault(Key key, T value) {
		settings.set(key, value);
	}

	@Override
	public <T> void removeSettingDefault(Key key) {
		settings.removeDefault(key);
	}

	@Override
	public void settingChanged(SettingChangeEvent event) {
		Key key = event.getKey();
		if (TITLE.equals(key)) {
			String text = getSetting(TITLE);
			title.setText((text != null) ? text : "");
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

	public List<DataSource> getData() {
		return Collections.unmodifiableList(data);
	}

}

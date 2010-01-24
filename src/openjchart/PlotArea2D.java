/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
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

package openjchart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;

import openjchart.util.GraphicsUtils;
import openjchart.util.SettingChangeEvent;
import openjchart.util.Settings;
import openjchart.util.SettingsListener;
import openjchart.util.SettingsStorage;

/**
 * Abstract class that represents a canvas for the plot which will be drawn.
 */
public abstract class PlotArea2D extends AbstractDrawable implements SettingsStorage, SettingsListener {
	/** Key for specifying the {@link java.awt.Paint} instance to be used to paint the background of the plot area. */
	public static final String KEY_BACKGROUND = "plotarea.background";
	/** Key for specifying the {@link java.awt.Stroke} instance to be used to paint the border of the plot area. */
	public static final String KEY_BORDER = "plotarea.border";
	/** Key for specifying the {@link java.awt.Paint} instance to be used to fill the border of the plot area. */
	public static final String KEY_COLOR = "plotarea.color";

	private final Settings settings;

	/**
	 * Creates a new PlotArea2D object with default background color and
	 * border.
	 */
	public PlotArea2D() {
		settings = new Settings(this);
		setSettingDefault(KEY_BACKGROUND, Color.WHITE);
		setSettingDefault(KEY_BORDER, new BasicStroke(1f));
		setSettingDefault(KEY_COLOR, Color.BLACK);
	}

	/**
	 * Draws the background of this Legend with the specified Graphics2D
	 * object.
	 * @param g2d Graphics object to draw with.
	 */
	protected void drawBackground(Graphics2D g2d) {
		// FIXME: duplicate code! See openjchart.Legend
		Paint paint = getSetting(KEY_BACKGROUND);
		if (paint != null) {
			GraphicsUtils.fillPaintedShape(g2d, getBounds(), paint, null);
		}
	}

	/**
	 * Draws the border of this Legend with the specified Graphics2D
	 * object.
	 * @param g2d Graphics object to draw with.
	 */
	protected void drawBorder(Graphics2D g2d) {
		// FIXME: duplicate code! See openjchart.Legend
		Stroke stroke = getSetting(KEY_BORDER);
		if (stroke != null) {
			Paint paint = getSetting(KEY_COLOR);
			GraphicsUtils.drawPaintedShape(g2d, getBounds(), paint, null, stroke);
		}
	}

	/**
	 * Draws the data using the specified Graphics2D object.
	 * @param g2d Graphics to be drawn into.
	 */
	protected abstract void drawPlot(Graphics2D g2d);

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
	}
}

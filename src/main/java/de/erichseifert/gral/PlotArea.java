/*
 * GRAL: GRAphing Library for Java(R)
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

package de.erichseifert.gral;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;

import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.SettingChangeEvent;
import de.erichseifert.gral.util.Settings;
import de.erichseifert.gral.util.SettingsListener;
import de.erichseifert.gral.util.SettingsStorage;
import de.erichseifert.gral.util.Settings.Key;


/**
 * Abstract class that represents a canvas for the plot which will be drawn.
 */
public abstract class PlotArea extends AbstractDrawable
		implements SettingsStorage, SettingsListener {
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	paint the background of the plot area. */
	public static final Key BACKGROUND = new Key("plotarea.background");
	/** Key for specifying the {@link java.awt.Stroke} instance to be used to
	paint the border of the plot area. */
	public static final Key BORDER = new Key("plotarea.border");
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	fill the border of the plot area. */
	public static final Key COLOR = new Key("plotarea.color");

	private final Settings settings;

	/**
	 * Creates a new PlotArea2D object with default background color and
	 * border.
	 */
	public PlotArea() {
		settings = new Settings(this);
		setSettingDefault(BACKGROUND, Color.WHITE);
		setSettingDefault(BORDER, new BasicStroke(1f));
		setSettingDefault(COLOR, Color.BLACK);
	}

	/**
	 * Draws the background of this Legend with the specified Graphics2D
	 * object.
	 * @param g2d Graphics object to draw with.
	 */
	protected void drawBackground(Graphics2D g2d) {
		// FIXME: duplicate code! See de.erichseifert.gral.Legend
		Paint paint = getSetting(BACKGROUND);
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
		// FIXME: duplicate code! See de.erichseifert.gral.Legend
		Stroke stroke = getSetting(BORDER);
		if (stroke != null) {
			Paint paint = getSetting(COLOR);
			GraphicsUtils.drawPaintedShape(g2d, getBounds(), paint, null, stroke);
		}
	}

	/**
	 * Draws the data using the specified Graphics2D object.
	 * @param g2d Graphics to be drawn into.
	 */
	protected abstract void drawPlot(Graphics2D g2d);

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
	}
}

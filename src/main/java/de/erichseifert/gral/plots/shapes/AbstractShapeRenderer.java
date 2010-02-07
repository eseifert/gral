/* GRAL : a free graphing library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.plots.shapes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.text.Format;
import java.text.NumberFormat;

import de.erichseifert.gral.plots.Label;
import de.erichseifert.gral.util.SettingChangeEvent;
import de.erichseifert.gral.util.Settings;
import de.erichseifert.gral.util.SettingsListener;


/**
 * Abstract class implementing functions for the administration of settings.
 */
public abstract class AbstractShapeRenderer implements ShapeRenderer, SettingsListener {
	private final Settings settings;

	/**
	 * Creates a new AbstractShapeRenderer object with default shape and
	 * color.
	 */
	public AbstractShapeRenderer() {
		settings = new Settings(this);

		setSettingDefault(KEY_SHAPE, new Rectangle2D.Double(-2.5, -2.5, 5.0, 5.0));
		setSettingDefault(KEY_COLOR, Color.BLACK);

		setSettingDefault(KEY_VALUE_DISPLAYED, false);
		setSettingDefault(KEY_VALUE_FORMAT, NumberFormat.getInstance());
		setSettingDefault(KEY_VALUE_ALIGNMENT_X, 0.5);
		setSettingDefault(KEY_VALUE_ALIGNMENT_Y, 0.5);
		setSettingDefault(KEY_COLOR, Color.BLACK);
	}

	/**
	 * Draws the specified value for the specified shape.
	 * @param g2d Graphics2D to be used for drawing.
	 * @param shape Shape to draw into.
	 * @param value Value to be displayed.
	 */
	protected void drawValue(Graphics2D g2d, Shape shape, Object value) {
		Format format = getSetting(KEY_VALUE_FORMAT);
		String text = format.format(value);
		Label valueLabel = new Label(text);
		valueLabel.setSetting(Label.KEY_ALIGNMENT_X, getSetting(KEY_VALUE_ALIGNMENT_X));
		valueLabel.setSetting(Label.KEY_ALIGNMENT_Y, getSetting(KEY_VALUE_ALIGNMENT_Y));
		valueLabel.setSetting(Label.KEY_COLOR, getSetting(KEY_VALUE_COLOR));
		valueLabel.setBounds(shape.getBounds2D());
		valueLabel.draw(g2d);
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
		settings.<T>setDefault(key, value);
	}

	@Override
	public <T> void removeSettingDefault(String key) {
		settings.removeDefault(key);
	}

	@Override
	public void settingChanged(SettingChangeEvent event) {
	}
}
/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2012 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael[at]erichseifert.de>
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

import java.io.IOException;
import java.io.ObjectInputStream;

import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.plots.settings.BasicSettingsStorage;
import de.erichseifert.gral.plots.settings.Key;
import de.erichseifert.gral.plots.settings.SettingChangeEvent;
import de.erichseifert.gral.plots.settings.SettingsListener;
import de.erichseifert.gral.plots.settings.SettingsStorage;


/**
 * Abstract class that represents a drawable object which can be styled using
 * settings.
 */
public abstract class StylableDrawable extends AbstractDrawable
		implements SettingsStorage, SettingsListener {
	/** Version id for serialization. */
	private static final long serialVersionUID = 8679795250803282234L;

	/** Object to store settings. */
	private final BasicSettingsStorage settings;

	/**
	 * Initializes a new instance.
	 */
	public StylableDrawable() {
		settings = new BasicSettingsStorage();
		settings.addSettingsListener(this);
	}

	/**
	 * Invoked if a setting has changed.
	 * @param event Event containing information about the changed setting.
	 */
	public void settingChanged(SettingChangeEvent event) {
	}

	/**
	 * Returns the setting with the specified key.
	 * If no setting is available, the default setting will be returned.
	 * @param <T> Type of setting.
	 * @param key Key.
	 * @return Setting.
	 */
	public <T> T getSetting(Key key) {
		return settings.getSetting(key);
	}

	/**
	 * Sets the setting with the specified key to the specified value.
	 * @param <T> Type of setting.
	 * @param key Key.
	 * @param value Value to be set.
	 */
	public <T> void setSetting(Key key, T value) {
		settings.setSetting(key, value);
	}

	/**
	 * Removes the setting with the specified key.
	 * @param <T> Type of setting.
	 * @param key Key.
	 */
	public <T> void removeSetting(Key key) {
		settings.removeSetting(key);
	}

	/**
	 * Sets a default value for the setting with the specified key.
	 * @param <T> Type of setting.
	 * @param key Key.
	 * @param value Value to be set.
	 */
	public <T> void setSettingDefault(Key key, T value) {
		settings.setSettingDefault(key, value);
	}

	/**
	 * Removes the default setting with the specified key.
	 * @param <T> Type of setting.
	 * @param key Key.
	 */
	public <T> void removeSettingDefault(Key key) {
		settings.removeSettingDefault(key);
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
		// Normal deserialization
		in.defaultReadObject();

		// Restore listeners
		settings.addSettingsListener(this);
	}
}

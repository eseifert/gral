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
package de.erichseifert.gral.plots.settings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class that stores a specific and a default setting for a certain key.
 * It provides support for class which register as a listener to listen
 * for setting changes.
 */
public class BasicSettingsStorage implements SettingsStorage, Serializable {
	/** Version id for serialization. */
	private static final long serialVersionUID = 6918096150217655364L;

	/** Set of listener objects that will notified on changes. */
	private transient Set<SettingsListener> settingsListeners;
	/** Map of user defined settings as (key, value) pairs. */
	private final Map<Key, Object> settings;
	/** Map of default settings as (key, value) pairs. */
	private final Map<Key, Object> defaults;

	/**
	 * Initializes an empty storage.
	 */
	public BasicSettingsStorage() {
		settingsListeners = new HashSet<SettingsListener>();
		settings = new HashMap<Key, Object>();
		defaults = new HashMap<Key, Object>();
	}

	/**
	 * Returns {@code true} if there is a setting for the specified key.
	 * @param key Key of the setting.
	 * @return {@code true} if the key has a setting,
	 *         {@code false} otherwise.
	 */
	public boolean hasSetting(Key key) {
		if (settings.containsKey(key)) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the setting with the specified key.
	 * If no setting is available, the default setting will be returned.
	 * @param <T> Type of setting.
	 * @param key Key.
	 * @return Setting.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getSetting(Key key) {
		if (settings.containsKey(key)) {
			return (T) settings.get(key);
		}
		return (T) defaults.get(key);
	}

	/**
	 * Sets the setting with the specified key to the specified value.
	 * @param <T> Type of setting.
	 * @param key Key.
	 * @param value Value to be set.
	 */
	public <T> void setSetting(Key key, T value) {
		setSetting(key, value, false);
	}

	/**
	 * Removes the setting with the specified key.
	 * @param <T> Type of setting.
	 * @param key Key.
	 */
	@SuppressWarnings("unchecked")
	public <T> void removeSetting(Key key) {
		T valueOld = (T) settings.get(key);
		settings.remove(key);
		notifySettingChanged(key, valueOld, null, false);
	}

	/**
	 * Returns {@code true} if there is a default setting for the
	 * specified key.
	 * @param key Key of the setting.
	 * @return {@code true} if the key has a default setting,
	 *         {@code false} otherwise.
	 */
	public boolean hasSettingDefault(Key key) {
		return defaults.containsKey(key);
	}

	/**
	 * Sets a default value for the setting with the specified key.
	 * @param <T> Type of setting.
	 * @param key Key.
	 * @param value Value to be set.
	 */
	public <T> void setSettingDefault(Key key, T value) {
		setSetting(key, value, true);
	}

	/**
	 * Removes the default setting with the specified key.
	 * @param <T> value type
	 * @param key key of the setting
	 */
	@SuppressWarnings("unchecked")
	public <T> void removeSettingDefault(Key key) {
		T valueOld = (T) defaults.get(key);
		defaults.remove(key);
		notifySettingChanged(key, valueOld, null, true);
	}

	/**
	 * Sets the setting or default setting with the specified key to a new
	 * value.
	 * @param <T> Type of setting.
	 * @param key Key.
	 * @param value Value to be set.
	 * @param isDefault {@code true} when the value should be the default,
	 *        otherwise {@code false}
	 */
	@SuppressWarnings("unchecked")
	protected <T> void setSetting(Key key, T value, boolean isDefault) {
		Map<Key, Object> map = isDefault ? defaults : this.settings;
		T valueOld = (T) defaults.get(key);
		map.put(key, value);
		notifySettingChanged(key, valueOld, value, true);
	}

	/**
	 * Adds a new listener which gets notified if settings have changed.
	 * @param l listener to be added
	 */
	public void addSettingsListener(SettingsListener l) {
		settingsListeners.add(l);
	}

	/**
	 * Removes the specified listener.
	 * @param l listener to be removed
	 */
	public void removeSettingsListener(SettingsListener l) {
		settingsListeners.remove(l);
	}

	/**
	 * Invokes the settingChanged method on all registered listeners.
	 * @param key Key.
	 * @param valueOld Old value.
	 * @param valueNew New value.
	 * @param defaultSetting {@code true} if a default setting has changed,
	 *        {@code false} otherwise.
	 */
	protected void notifySettingChanged(Key key,
			Object valueOld, Object valueNew, boolean defaultSetting) {
		// FIXME Null values for old or new values are no reliable indicator
		// that a setting or default was added or removed.
		SettingChangeEvent event = new SettingChangeEvent(this, key,
				valueOld, valueNew, defaultSetting);
		for (SettingsListener listener : settingsListeners) {
			listener.settingChanged(event);
		}
	}

	private void readObject(ObjectInputStream in)
			throws ClassNotFoundException, IOException {
		// Normal deserialization
		in.defaultReadObject();

		// Handle transient fields
		settingsListeners = new HashSet<SettingsListener>();
	}

}

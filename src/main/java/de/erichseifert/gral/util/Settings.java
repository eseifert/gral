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

package de.erichseifert.gral.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class that stores a specific and a default setting for a certain key.
 * It provides support for class which register as a listener to listen
 * for setting changes.
 * @see SettingsListener
 */
public class Settings {
	private final Set<SettingsListener> settingsListeners;
	private final Map<String, Object> settings;
	private final Map<String, Object> defaults;

	/**
	 * Creates an empty Settings object.
	 */
	public Settings(SettingsListener listener) {
		settingsListeners = new HashSet<SettingsListener>();
		settings = new HashMap<String, Object>();
		defaults = new HashMap<String, Object>();
		
		if (listener != null) {
			addSettingsListener(listener);
		}
	}

	/**
	 * Adds a new listener which gets notified if settings have changed.
	 * @param settingsListener listener to be added
	 */
	public void addSettingsListener(SettingsListener settingsListener) {
		settingsListeners.add(settingsListener);
	}

	/**
	 * Deletes all default settings.
	 */
	public void clearDefaults() {
		for (String key : getDefaults().keySet()) {
			removeDefault(key);
		}
	}

	/**
	 * Deletes all settings.
	 */
	public void clearSettings() {
		for (String key : getSettings().keySet()) {
			remove(key);
		}
	}

	/**
	 * Returns the setting for the specified key.
	 * @param <T> return type
	 * @param key key of the setting
	 * @return the value of the setting
	 */
	public <T> T get(String key) {
		T t;
		if (settings.containsKey(key)) {
			t = (T)settings.get(key);
		} else {
			t = (T)defaults.get(key);
		}
		return t;
	}

	/**
	 * Returns a map containing all default settings.
	 * @return Map of default settings.
	 */
	public Map<String, Object> getDefaults() {
		return new HashMap<String, Object>(this.defaults);
	}

	/**
	 * Returns a map containing all settings.
	 * @return Map of settings.
	 */
	public Map<String, Object> getSettings() {
		return new HashMap<String, Object>(this.settings);
	}

	/**
	 * Returns an unmodifiable set containing all registered listeners.
	 * @return Set of listeners.
	 */
	public Set<SettingsListener> getSettingsListeners() {
		return Collections.unmodifiableSet(settingsListeners);
	}

	/**
	 * Returns <code>true</code> if there is a default setting for the specified key.
	 * @param key Key of the setting.
	 * @return <code>true</code> if the key has a default setting.
	 */
	public boolean hasDefault(String key) {
		if (defaults.containsKey(key)) {
			return true;
		}
		return false;
	}

	/**
	 * Returns <code>true</code> if there is a setting for the specified key.
	 * @param key Key of the setting.
	 * @return <code>true</code> if the key has a setting.
	 */
	public boolean hasSetting(String key) {
		if (settings.containsKey(key)) {
			return true;
		}
		return false;
	}

	/**
	 * Returns <code>true</code> if the specified key is contained.
	 * @param key Key to be checked.
	 * @return <code>true</code> if the key exists.
	 */
	public boolean hasKey(String key) {
		if (hasDefault(key) || hasSetting(key)) {
			return true;
		}
		return false;
	}

	/**
	 * Returns a set containing all keys of this settings object.
	 * Returns an empty set if no keys are set.
	 * @return Set of keys.
	 */
	public Set<String> keySet() {
		Set<String> keys = new HashSet<String>();
		keys.addAll(settings.keySet());
		keys.addAll(defaults.keySet());

		return keys;
	}

	/**
	 * Removes the setting with the specified key.
	 * @param <T> value type
	 * @param key key of the setting
	 */
	public <T> void remove(String key) {
		Object valOld = settings.get(key);
		settings.remove(key);
		notifySettingChanged(key, valOld, null, false);
	}

	/**
	 * Removes the default setting with the specified key.
	 * @param <T> value type
	 * @param key key of the setting
	 */
	public <T> void removeDefault(String key) {
		Object valOld = defaults.get(key);
		defaults.remove(key);
		notifySettingChanged(key, valOld, null, true);
	}

	/**
	 * Removes the specified listener.
	 * @param settingsListener listener to be removed
	 */
	public void removeSettingsListener(SettingsListener settingsListener) {
		settingsListeners.remove(settingsListener);
	}

	/**
	 * Sets the setting for the specified key.
	 * @param <T> value type
	 * @param key key of the setting
	 * @param value value of the setting
	 */
	public <T> void set(String key, T value) {
		Object valOld = settings.get(key);
		settings.put(key, value);
		notifySettingChanged(key, valOld, value, false);
	}

	/**
	 * Sets the default setting for the specified key.
	 * @param <T> value type
	 * @param key key of the setting
	 * @param value default value of the setting
	 */
	public <T> void setDefault(String key, T value) {
		Object valOld = defaults.get(key);
		defaults.put(key, value);
		notifySettingChanged(key, valOld, value, true);
	}

	/**
	 * Returns a collection containing all settings this object would return.
	 * Returns an empty collection if no keys are set.
	 * @return Collection of values.
	 */
	public Collection<Object> values() {
		Set<String> keys = keySet();
		Collection<Object> values = new ArrayList<Object>(keys.size());
		for (String key : keys) {
			values.add(get(key));
		}
		return values;
	}

	/**
	 * Invokes the settingChanged method on all registered listeners.
	 * @param key Key.
	 * @param valOld Old value.
	 * @param valNew New value.
	 * @param defaultSetting <code>true</code> if a default setting has changed.
	 */
	protected void notifySettingChanged(String key, Object valOld, Object valNew, boolean defaultSetting) {
		/* FIXME: Null values for old or new values are no indicator that
		 *	a setting or default is added or removed.
		 */

		SettingChangeEvent event = new SettingChangeEvent(this, key, valOld, valNew, defaultSetting);
		for (SettingsListener listener : settingsListeners) {
			listener.settingChanged(event);
		}
	}
}

package de.erichseifert.gral.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class that stores a specific and a default setting for a certain key.
 * It provides support for class which register as a listener to listen
 * for setting changes.
 */
public class BasicSettingsStorage implements SettingsStorage {
	/** Set of listener objects that will notified on changes. */
	private final Set<SettingsListener> settingsListeners;
	/** Map of user defined settings as <code>(key, value)</code> pairs. */
	private final Map<SettingsStorage.Key, Object> settings;
	/** Map of default settings as <code>(key, value)</code> pairs. */
	private final Map<SettingsStorage.Key, Object> defaults;

	/**
	 * Initializes an empty storage.
	 */
	public BasicSettingsStorage() {
		settingsListeners = new HashSet<SettingsListener>();
		settings = new HashMap<SettingsStorage.Key, Object>();
		defaults = new HashMap<SettingsStorage.Key, Object>();
	}

	/**
	 * Returns <code>true</code> if there is a setting for the specified key.
	 * @param key Key of the setting.
	 * @return <code>true</code> if the key has a setting,
	 *         <code>false</code> otherwise.
	 */
	public boolean hasSetting(Key key) {
		if (settings.containsKey(key)) {
			return true;
		}
		return false;
	}

	@Override
	public <T> T getSetting(Key key) {
		if (settings.containsKey(key)) {
			return (T) settings.get(key);
		}
		return (T) defaults.get(key);
	}

	@Override
	public <T> void setSetting(Key key, T value) {
		Object valueOld = settings.get(key);
		settings.put(key, value);
		notifySettingChanged(key, valueOld, value, false);
	}

	@Override
	public <T> void removeSetting(Key key) {
		Object valueOld = settings.get(key);
		settings.remove(key);
		notifySettingChanged(key, valueOld, null, false);
	}

	/**
	 * Returns <code>true</code> if there is a default setting for the
	 * specified key.
	 * @param key Key of the setting.
	 * @return <code>true</code> if the key has a default setting,
	 *         <code>false</code> otherwise.
	 */
	public boolean hasSettingDefault(Key key) {
		return defaults.containsKey(key);
	}

	@Override
	public <T> void setSettingDefault(Key key, T value) {
		Object valueOld = defaults.get(key);
		defaults.put(key, value);
		notifySettingChanged(key, valueOld, value, true);
	}

	/**
	 * Removes the default setting with the specified key.
	 * @param <T> value type
	 * @param key key of the setting
	 */
	public <T> void removeSettingDefault(Key key) {
		Object valueOld = defaults.get(key);
		defaults.remove(key);
		notifySettingChanged(key, valueOld, null, true);
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
	 * @param defaultSetting <code>true</code> if a default setting has changed,
	 *        <code>false</code> otherwise.
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
}

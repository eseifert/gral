package openjchart.util;

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
	public Settings() {
		settingsListeners = new HashSet<SettingsListener>();
		settings = new HashMap<String, Object>();
		defaults = new HashMap<String, Object>();
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
	 * Adds a new listener which gets notified if settings have changed.
	 * @param settingsListener listener to be added
	 */
	public void addSettingsListener(SettingsListener settingsListener) {
		settingsListeners.add(settingsListener);
	}

	/**
	 * Removes the specified listener.
	 * @param settingsListener listener to be removed
	 */
	public void removeSettingsListener(SettingsListener settingsListener) {
		settingsListeners.remove(settingsListener);
	}

	/**
	 * Invokes the settingChanged method on all registered listeners.
	 * @param key Key.
	 * @param valOld Old value.
	 * @param valNew New value.
	 * @param defaultSetting <code>true</code> if a default setting has changed.
	 */
	protected void notifySettingChanged(String key, Object valOld, Object valNew, boolean defaultSetting) {
		SettingChangeEvent event = new SettingChangeEvent(this, key, valOld, valNew, defaultSetting);
		for (SettingsListener listener : settingsListeners) {
			listener.settingChanged(event);
		}
	}
}

package openjchart.util;

import java.util.ArrayList;
import java.util.Collection;
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

	private int size;

	/**
	 * Creates an empty Settings object.
	 */
	public Settings() {
		settingsListeners = new HashSet<SettingsListener>();
		settings = new HashMap<String, Object>();
		defaults = new HashMap<String, Object>();
		size = -1;
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
	 * Returns the size of this settings object.
	 * @return Number of settings.
	 */
	public int size() {
		if (size == -1) {
			size = keySet().size();
		}
		return size;
	}

	/**
	 * Returns a set containing all keys of this settings object.
	 * Returns an empty set if no keys are set.
	 * @return Set of keys.
	 */
	public Set<String> keySet() {
		Set<String> keys = new HashSet<String>();
		for (String key : settings.keySet()) {
			keys.add(key);
		}
		for (String key : defaults.keySet()) {
			keys.add(key);
		}

		return keys;
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
	 * Returns a map containing all settings.
	 * @return Map of settings.
	 */
	public Map<String, Object> getSettings() {
		return new HashMap<String, Object>(this.settings);
	}

	/**
	 * Returns a map containing all default settings.
	 * @return Map of default settings.
	 */
	public Map<String, Object> getDefaults() {
		return new HashMap<String, Object>(this.defaults);
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
		// Reset size cache if a setting has been added or removed
		/* FIXME: Size cache is reset if a null value is set.
			Null values for old or new values are no indicator that
			a setting or default is added or removed.
		*/
		if (valOld == null || valNew == null) {
			size = -1;
		}

		SettingChangeEvent event = new SettingChangeEvent(this, key, valOld, valNew, defaultSetting);
		for (SettingsListener listener : settingsListeners) {
			listener.settingChanged(event);
		}
	}
}

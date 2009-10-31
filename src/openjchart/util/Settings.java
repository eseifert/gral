package openjchart.util;

import java.util.HashMap;
import java.util.Map;

public class Settings {
	private Map<String, Object> settings = new HashMap<String, Object>();
	private Map<String, Object> defaults = new HashMap<String, Object>();

	public <T> T get(String key) {
		if (!settings.containsKey(key)) {
			return (T)defaults.get(key);
		}
		return (T)settings.get(key);
	}

	public <T> void set(String key, T value) {
		settings.put(key, value);
	}

	public <T> void setDefault(String key, T value) {
		defaults.put(key, value);
	}
}

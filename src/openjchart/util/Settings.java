package openjchart.util;

import java.util.HashMap;
import java.util.Map;

public class Settings {
	private Map<String, Object> settings = new HashMap<String, Object>();
	private Map<String, Object> defaults = new HashMap<String, Object>();

	public <T> T get(String key) {
		T t;
		if (settings.containsKey(key)) {
			t = (T)settings.get(key);
		} else {
			t = (T)defaults.get(key);
		}
		return t;
	}

	public <T> void set(String key, T value) {
		settings.put(key, value);
	}

	public <T> void remove(String key) {
		settings.remove(key);
	}

	public <T> void setDefault(String key, T value) {
		defaults.put(key, value);
	}

	public <T> void removeDefault(String key) {
		defaults.remove(key);
	}

}

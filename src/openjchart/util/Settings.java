package openjchart.util;

import java.util.HashMap;

public class Settings extends HashMap<String, Object> {
	public <T> T get(Object key, T defaultValue) {
		if (!containsKey(key)) {
			return defaultValue;
		}
		return (T)super.get(key);
	}
}

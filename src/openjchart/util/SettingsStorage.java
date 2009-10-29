package openjchart.util;

public interface SettingsStorage {
	<T> T getSetting(String key, T defaultValue);
	<T> void setSetting(String key, T value);
}

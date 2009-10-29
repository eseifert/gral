package openjchart.util;

public interface SettingsStorage {
	<T> T getSetting(String key);
	<T> void setSetting(String key, T value);
	<T> void setSettingDefault(String key, T value);
}

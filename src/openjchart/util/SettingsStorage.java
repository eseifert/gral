package openjchart.util;

public interface SettingsStorage {
	<T> T getSetting(String key);

	<T> void setSetting(String key, T value);
	<T> void removeSetting(String key);

	<T> void setSettingDefault(String key, T value);
	<T> void removeSettingDefault(String key);
}

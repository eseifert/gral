package openjchart.util;

/**
 * Interface providing a function to listen to changes of settings.
 */
public interface SettingsListener {
	/**
	 * Invoked if a setting has changed.
	 * @param event Event containing information about the changed setting.
	 */
	void settingChanged(SettingChangeEvent event);
}

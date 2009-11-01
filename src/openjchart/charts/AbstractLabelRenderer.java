package openjchart.charts;

import openjchart.util.Settings;

public abstract class AbstractLabelRenderer implements LabelRenderer {
	private final Settings settings;

	public AbstractLabelRenderer() {
		this.settings = new Settings();
	}

	@Override
	public <T> T getSetting(String key) {
		return settings.<T>get(key);
	}

	@Override
	public <T> void setSetting(String key, T value) {
		settings.<T>set(key, value);
	}

	@Override
	public <T> void setSettingDefault(String key, T value) {
		settings.<T>setDefault(key, value);
	}

}

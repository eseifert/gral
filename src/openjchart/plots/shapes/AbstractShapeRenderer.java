package openjchart.plots.shapes;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import openjchart.util.SettingChangeEvent;
import openjchart.util.Settings;
import openjchart.util.SettingsListener;

public abstract class AbstractShapeRenderer implements ShapeRenderer, SettingsListener {
	private final Settings settings;

	public AbstractShapeRenderer() {
		settings = new Settings(this);

		setSettingDefault(KEY_SHAPE, new Rectangle2D.Double(-4.0, -4.0, 8.0, 8.0));
		setSettingDefault(KEY_COLOR, Color.BLACK);
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
	public <T> void removeSetting(String key) {
		settings.remove(key);
	}

	@Override
	public <T> void setSettingDefault(String key, T value) {
		settings.<T>setDefault(key, value);
	}

	@Override
	public <T> void removeSettingDefault(String key) {
		settings.removeDefault(key);
	}

	@Override
	public void settingChanged(SettingChangeEvent event) {
	}
}
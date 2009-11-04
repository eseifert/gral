package openjchart.plots.lines;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Insets;

import openjchart.util.Settings;

public abstract class AbstractLineRenderer2D implements LineRenderer2D {
	private final Settings settings;

	public AbstractLineRenderer2D() {
		this.settings = new Settings();

		setSettingDefault(KEY_LINE_STROKE, new BasicStroke(1.5f));
		setSettingDefault(KEY_POINT_INSETS, new Insets(0, 0, 0, 0));
		setSettingDefault(KEY_LINE_COLOR, Color.BLACK);
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

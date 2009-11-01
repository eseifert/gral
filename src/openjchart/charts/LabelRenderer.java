package openjchart.charts;

import openjchart.Drawable;
import openjchart.util.SettingsStorage;

public interface LabelRenderer extends SettingsStorage {
	static final String KEY_ALIGNMENT = "label.alignemnt";
	static final String KEY_FONT = "label.font";

	Drawable getLabel(String label);
}

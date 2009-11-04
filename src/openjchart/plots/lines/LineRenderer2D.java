package openjchart.plots.lines;

import openjchart.Drawable;
import openjchart.util.SettingsStorage;

public interface LineRenderer2D extends SettingsStorage {
	public static final String KEY_LINE_STROKE = "line.stroke";
	public static final String KEY_POINT_INSETS = "line.insets";
	public static final String KEY_LINE_COLOR = "line.color";

	Drawable getLine(double x1, double y1, double x2, double y2);
}

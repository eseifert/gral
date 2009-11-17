package openjchart.plots.lines;

import java.awt.Shape;

import openjchart.Drawable;
import openjchart.plots.DataPoint2D;
import openjchart.util.SettingsStorage;

public interface LineRenderer2D extends SettingsStorage {
	public static final String KEY_LINE_STROKE = "line.stroke";
	public static final String KEY_LINE_GAP = "line.gap";
	public static final String KEY_LINE_COLOR = "line.color";

	Drawable getLine(DataPoint2D p1, DataPoint2D p2);

	Shape punchShapes(Shape lineShape, DataPoint2D p1, DataPoint2D p2);
}

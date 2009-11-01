package openjchart.charts.axes;

import openjchart.Drawable;
import openjchart.util.SettingsStorage;


/**
 * Basic interface for classes that want to display an axis.
 * @author Michael Seifert
 *
 */
public interface AxisRenderer2D extends SettingsStorage {
	/** Shape of the axis. */
	static final String KEY_SHAPE = "axis.shape";
	/** Determines whether normal vector is calculated using clockwise rotation. */
	static final String KEY_SHAPE_NORMAL_ORIENTATION_CLOCKWISE = "axis.shape.normalOrientationClockwise";
	/** Color of axis, ticks and labels. */
	static final String KEY_SHAPE_COLOR = "axis.shape.color";
	/** Stroke style of the axis */
	static final String KEY_SHAPE_STROKE = "axis.shape.stroke";

	/** Interval for ticks. */
	static final String KEY_TICK_SPACING = "axis.tick.spacing";
	/** Length of tick strokes. */
	static final String KEY_TICK_LENGTH = "axis.tick.length";
	/** Stroke style of the tick */
	static final String KEY_TICK_STROKE = "axis.tick.stroke";
	/** Alignment of ticks: 0.0 means outside, 0.5 means centered, 1.0 means inside. */
	static final String KEY_TICK_ALIGNMENT = "axis.tick.alignment";

	/** Format of labels. */
	static final String KEY_LABEL_FORMAT = "axis.label.format";
	/** Distance of labels to their ticks. */
	static final String KEY_LABEL_DISTANCE = "axis.label.distance";
	/** Draw labels outside of the chart. */
	static final String KEY_LABEL_OUTSIDE = "axis.label.outside";

	/**
	 * Returns a component that displays the specified axis.
	 * @param axis axis to be displayed
	 * @return component displaying the axis
	 * @see Axis
	 */
	Drawable getRendererComponent(Axis axis);

	double worldToView(Axis axis, Number value);
	Number viewToWorld(Axis axis, double value);
}

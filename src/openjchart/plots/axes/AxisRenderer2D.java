package openjchart.plots.axes;

import java.awt.geom.Point2D;

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
	/** Determines whether the axis direction will be swapped. */
	static final String KEY_SHAPE_DIRECTION_SWAPPED = "axis.shape.directionSwapped";

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
	/** Draw labels outside of the plot. */
	static final String KEY_LABEL_OUTSIDE = "axis.label.outside";

	/**
	 * Returns a component that displays the specified axis.
	 * @param axis axis to be displayed
	 * @return component displaying the axis
	 * @see Axis
	 */
	Drawable getRendererComponent(Axis axis);

	/**
	 * Returns the position of the specified value on the axis.
	 * The value is returned in view coordinates.
	 * @return Two-dimensional point of the value
	 */
	Point2D worldToViewPos(Axis axis, Number value);

	/**
	 * Converts a world (axis) coordinate value to a view (screen) coordinate value.
	 * @param axis Axis
	 * @param value World coordinate value to convert
	 * @return Screen coordinate value
	 */
	double worldToView(Axis axis, Number value);

	/**
	 * Converts a view (screen) coordinate value to a world (axis) coordinate value.
	 * @param axis Axis
	 * @param value View coordinate value to convert
	 * @return World coordinate value
	 */
	Number viewToWorld(Axis axis, double value);
}

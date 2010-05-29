package de.erichseifert.gral.plots.axes;

import java.util.List;

import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.util.SettingsStorage;
import de.erichseifert.gral.util.Settings.Key;

public interface AxisRenderer extends SettingsStorage {
	/** Key for specifying the intersection point of axis. */
	static final Key INTERSECTION = new Key("axis.intersection");
	/** Key for specifying the {@link java.awt.Shape} instance that defines
	 the shape of the axis. */
	static final Key SHAPE = new Key("axis.shape");
	/** Key for specifying whether the shape of the axis will be drawn.
	 This won't influence ticks or labels. */
	static final Key SHAPE_VISIBLE = new Key("axis.shape.visible");
	/** Key for specifying whether normal vector is calculated using clockwise
	 or counterclockwise rotation. */
	static final Key SHAPE_NORMAL_ORIENTATION_CLOCKWISE =
		new Key("axis.shape.normalOrientationClockwise");
	/** Key for specifying {@link java.awt.Paint} instance to be used to paint
	 the axis, its ticks and its labels. */
	static final Key SHAPE_COLOR = new Key("axis.shape.color");
	/** Key for specifying the {@link java.awt.Stroke} instance which define
	 the shape of the axis. */
	static final Key SHAPE_STROKE = new Key("axis.shape.stroke");
	/** Key for specifying whether the axis direction will be swapped. */
	static final Key SHAPE_DIRECTION_SWAPPED =
		new Key("axis.shape.directionSwapped");
	/** Key for specifying whether major ticks are drawn. */
	static final Key TICKS = new Key("axis.ticks.major");
	/** Key for specifying the interval for major ticks. */
	static final Key TICKS_SPACING = new Key("axis.ticks.major.spacing");
	/** Key for specifying the length of major tick strokes. The length is
	 specified relative to font height. */
	static final Key TICKS_LENGTH = new Key("axis.ticks.major.length");
	/** Key for specifying the {@link java.awt.Stroke} instance which is used
	 to draw all major ticks. */
	static final Key TICKS_STROKE = new Key("axis.ticks.major.stroke");
	/** Key for specifying the alignment of major ticks:
	 0.0 means outside, 0.5 means centered, 1.0 means inside. */
	static final Key TICKS_ALIGNMENT = new Key("axis.ticks.major.alignment");
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	 paint the shapes of major ticks. */
	static final Key TICKS_COLOR = new Key("label.color");
	/** Key for specifying whether tick labels are drawn. */
	static final Key TICK_LABELS = new Key("axis.ticks.major.labels");
	/** Key for specifying the {java.text.Format} instance which converts the
	 tick values to labels. */
	static final Key TICK_LABELS_FORMAT = new Key(
			"axis.ticks.major.labels.format");
	/** Key for specifying the distance of labels to their ticks. The distance
	 is specified relative to font height. */
	static final Key TICK_LABELS_DISTANCE = new Key(
			"axis.ticks.major.labels.distance");
	/** Key for specifying the draw labels outside of the plot. */
	static final Key TICK_LABELS_OUTSIDE = new Key(
			"axis.ticks.major.labels.outside");
	/** Key for specifying the rotation of the tick labels in degrees. */
	static final Key TICK_LABELS_ROTATION = new Key(
			"axis.ticks.major.labels.rotation");
	/** Key for specifying whether minor ticks are drawn. */
	static final Key TICKS_MINOR = new Key("axis.ticks.minor");
	/** Key for specifying the count of minor ticks. */
	static final Key TICKS_MINOR_COUNT = new Key("axis.ticks.minor.count");
	/** Key for specifying the length of minor tick strokes. The length is
	 specified relative to font height. */
	static final Key TICKS_MINOR_LENGTH = new Key("axis.ticks.minor.length");
	/** Key for specifying the {@link java.awt.Stroke} instance which is used
	 to draw all minor ticks. */
	static final Key TICKS_MINOR_STROKE = new Key("axis.ticks.minor.stroke");
	/** Key for specifying the alignment of minor ticks:
	 0.0 means outside, 0.5 means centered, 1.0 means inside. */
	static final Key TICKS_MINOR_ALIGNMENT =
		new Key("axis.ticks.minor.alignment");
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	 paint the the shapes of minor ticks. */
	static final Key TICKS_MINOR_COLOR = new Key("label.color");
	/** Custom labels as a {@link java.util.Map} with a position
	 (<code>Double</code>) as key and a label (<code>String</code>) as value. */
	static final Key TICKS_CUSTOM = new Key("axis.ticks.custom");
	/** Key for specifying the {@link java.lang.String} instance for the label 
	 text of the axis. */
	static final Key LABEL = new Key("axis.label");
	/** Key for specifying the distance from the axis to the label. The length
	 is specified relative to font height. */
	static final Key LABEL_DISTANCE = new Key("axis.label.distance");
	/** Key for specifying the rotation of the axis label in degrees. */
	static final Key LABEL_ROTATION = new Key("axis.label.rotation");
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	 paint the axis label. */
	static final Key LABEL_COLOR = new Key("axis.label.color");

	/**
	 * Returns a component that displays the specified axis.
	 * @param axis axis to be displayed
	 * @return component displaying the axis
	 * @see Axis
	 */
	public abstract Drawable getRendererComponent(Axis axis);

	/**
	 * Converts a world (axis) coordinate value to a view (screen) coordinate value.
	 * @param axis Axis
	 * @param value World coordinate value to convert
	 * @param extrapolate Option to activate extrapolation value that are not on the axis
	 * @return Screen coordinate value
	 */
	public abstract double worldToView(Axis axis, Number value,
			boolean extrapolate);

	/**
	 * Converts a view (screen) coordinate value to a world (axis) coordinate value.
	 * @param axis Axis
	 * @param value View coordinate value to convert
	 * @param extrapolate Option to activate extrapolation value that are not on the axis
	 * @return World coordinate value
	 */
	public abstract Number viewToWorld(Axis axis, double value,
			boolean extrapolate);

	/**
	 * Returns a list of all tick element on the axis.
	 * @param axis Axis
	 * @return A list of <code>Tick2D</code> instances
	 */
	public abstract List<Tick2D> getTicks(Axis axis);

}
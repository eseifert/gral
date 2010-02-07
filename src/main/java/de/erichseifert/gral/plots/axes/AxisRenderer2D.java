/**
 * GRAL : Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.plots.axes;

import java.awt.geom.Point2D;
import java.util.List;

import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.util.SettingsStorage;



/**
 * Basic interface for classes that want to display a 2-dimensional axis.
 */
public interface AxisRenderer2D extends SettingsStorage {
	/** Key for specifying the intersection point of axis. */
	static final String KEY_INTERSECTION = "axis.intersection";

	/** Key for specifying the {@link java.awt.Shape} instance that defines the shape of the axis. */
	static final String KEY_SHAPE = "axis.shape";
	/** Key for specifying whether the shape of the axis will be drawn. This won't influence ticks or labels. */
	static final String KEY_SHAPE_VISIBLE = "axis.shape.visible";
	/** Key for specifying whether normal vector is calculated using clockwise or counterclockwise rotation. */
	static final String KEY_SHAPE_NORMAL_ORIENTATION_CLOCKWISE = "axis.shape.normalOrientationClockwise";
	/** Key for specifying {@link java.awt.Paint} instance to be used to paint the axis, its ticks and its labels. */
	static final String KEY_SHAPE_COLOR = "axis.shape.color";
	/** Key for specifying the {@link java.awt.Stroke} instance which defines the shape of the axis. */
	static final String KEY_SHAPE_STROKE = "axis.shape.stroke";
	/** Key for specifying whether the axis direction will be swapped. */
	static final String KEY_SHAPE_DIRECTION_SWAPPED = "axis.shape.directionSwapped";

	/** Key for specifying whether major ticks are drawn. */
	static final String KEY_TICKS = "axis.ticks.major";
	/** Key for specifying the interval for major ticks. */
	static final String KEY_TICKS_SPACING = "axis.ticks.major.spacing";
	/** Key for specifying the length of major tick strokes. The length is specified relative to font height. */
	static final String KEY_TICKS_LENGTH = "axis.ticks.major.length";
	/** Key for specifying the {@link java.awt.Stroke} instance which is used to draw all major ticks. */
	static final String KEY_TICKS_STROKE = "axis.ticks.major.stroke";
	/** Key for specifying the alignment of major ticks: 0.0 means outside, 0.5 means centered, 1.0 means inside. */
	static final String KEY_TICKS_ALIGNMENT = "axis.ticks.major.alignment";
	/** Key for specifying the {@link java.awt.Paint} instance to be used to paint the shapes of major ticks. */
	static final String KEY_TICKS_COLOR = "label.color";

	/** Key for specifying whether tick labels are drawn. */
	static final String KEY_TICK_LABELS = "axis.ticks.major.labels";
	/** Key for specifying the {java.text.Format} instance which converts the tick values to labels. */
	static final String KEY_TICK_LABELS_FORMAT = "axis.ticks.major.labels.format";
	/** Key for specifying the distance of labels to their ticks. The distance is specified relative to font height. */
	static final String KEY_TICK_LABELS_DISTANCE = "axis.ticks.major.labels.distance";
	/** Key for specifying the draw labels outside of the plot. */
	static final String KEY_TICK_LABELS_OUTSIDE = "axis.ticks.major.labels.outside";
	/** Key for specifying the rotation of the tick labels in degrees. */
	static final String KEY_TICK_LABELS_ROTATION = "axis.ticks.major.labels.rotation";

	/** Key for specifying whether minor ticks are drawn. */
	static final String KEY_TICKS_MINOR = "axis.ticks.minor";
	/** Key for specifying the count of minor ticks. */
	static final String KEY_TICKS_MINOR_COUNT = "axis.ticks.minor.count";
	/** Key for specifying the length of minor tick strokes. The length is specified relative to font height. */
	static final String KEY_TICKS_MINOR_LENGTH = "axis.ticks.minor.length";
	/** Key for specifying the {@link java.awt.Stroke} instance which is used to draw all minor ticks. */
	static final String KEY_TICKS_MINOR_STROKE = "axis.ticks.minor.stroke";
	/** Key for specifying the alignment of minor ticks: 0.0 means outside, 0.5 means centered, 1.0 means inside. */
	static final String KEY_TICKS_MINOR_ALIGNMENT = "axis.ticks.minor.alignment";
	/** Key for specifying the {@link java.awt.Paint} instance to be used to paint the the shapes of minor ticks. */
	static final String KEY_TICKS_MINOR_COLOR = "label.color";

	/** Custom labels as a {@link java.util.Map} with a position (<code>Double</code>) as key and a label (<code>String</code>) as value. */
	static final String KEY_TICKS_CUSTOM = "axis.ticks.custom";

	/** Key for specifying the {@link java.lang.String} instance for the label text of the axis. */
	static final String KEY_LABEL = "axis.label";
	/** Key for specifying the distance from the axis to the label. The length is specified relative to font height. */
	static final String KEY_LABEL_DISTANCE = "axis.label.distance";
	/** Key for specifying the rotation of the axis label in degrees. */
	static final String KEY_LABEL_ROTATION = "axis.label.rotation";
	/** Key for specifying the {@link java.awt.Paint} instance to be used to paint the axis label. */
	static final String KEY_LABEL_COLOR = "axis.label.color";

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
	 * @param axis Axis
	 * @param value World coordinate value to convert
	 * @param extrapolate Option to activate extrapolation value that are not on the axis
	 * @return Two-dimensional point of the value
	 */
	Point2D getPosition(Axis axis, Number value, boolean extrapolate, boolean forceLinear);

	/**
	 * Returns the normal vector at the position of the specified value.
	 * The vector is normalized.
	 * @param axis Axis
	 * @param value World coordinate value to convert
	 * @param extrapolate Option to activate extrapolation value that are not on the axis
	 * @return Two-dimensional normal vector at the position
	 */
	Point2D getNormal(Axis axis, Number value, boolean extrapolate, boolean forceLinear);

	/**
	 * Converts a world (axis) coordinate value to a view (screen) coordinate value.
	 * @param axis Axis
	 * @param value World coordinate value to convert
	 * @param extrapolate Option to activate extrapolation value that are not on the axis
	 * @return Screen coordinate value
	 */
	double worldToView(Axis axis, Number value, boolean extrapolate);

	/**
	 * Converts a view (screen) coordinate value to a world (axis) coordinate value.
	 * @param axis Axis
	 * @param value View coordinate value to convert
	 * @param extrapolate Option to activate extrapolation value that are not on the axis
	 * @return World coordinate value
	 */
	Number viewToWorld(Axis axis, double value, boolean extrapolate);

	/**
	 * Returns a list of all tick element on the axis.
	 * @param axis Axis
	 * @return A list of <code>Tick2D</code> instances
	 */
	List<Tick2D> getTicks(Axis axis);

}

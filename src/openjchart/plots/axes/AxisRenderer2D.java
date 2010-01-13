/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.plots.axes;

import java.awt.geom.Point2D;
import java.util.List;

import openjchart.Drawable;
import openjchart.plots.DataPoint2D;
import openjchart.util.SettingsStorage;


/**
 * Basic interface for classes that want to display a 2-dimensional axis.
 */
public interface AxisRenderer2D extends SettingsStorage {
	/** Key for specifying the intersection point of axis. */
	static final String KEY_INTERSECTION = "axis.intersection";

	/** Key for specifying the {@link java.awt.Shape} instance that defines the shape of the axis. */
	static final String KEY_SHAPE = "axis.shape";
	/** Key for specifying whether normal vector is calculated using clockwise or counterclockwise rotation. */
	static final String KEY_SHAPE_NORMAL_ORIENTATION_CLOCKWISE = "axis.shape.normalOrientationClockwise";
	/** Key for specifying {@link java.awt.Paint} instance to be used to paint the axis, its ticks and its labels. */
	static final String KEY_SHAPE_COLOR = "axis.shape.color";
	/** Key for specifying the {@link java.awt.Stroke} instance which defines the shape of the axis. */
	static final String KEY_SHAPE_STROKE = "axis.shape.stroke";
	/** Key for specifying whether the axis direction will be swapped. */
	static final String KEY_SHAPE_DIRECTION_SWAPPED = "axis.shape.directionSwapped";

	/** Key for specifying the interval for ticks. */
	static final String KEY_TICK_SPACING = "axis.tick.spacing";
	/** Key for specifying the length of tick strokes. */
	static final String KEY_TICK_LENGTH = "axis.tick.length";
	/** Key for specifying the {@link java.awt.Stroke} instance which defines the shape of all axis ticks. */
	static final String KEY_TICK_STROKE = "axis.tick.stroke";
	/** Key for specifying the alignment of ticks: 0.0 means outside, 0.5 means centered, 1.0 means inside. */
	static final String KEY_TICK_ALIGNMENT = "axis.tick.alignment";

	/** Key for specifying the format of labels. */
	static final String KEY_TICK_LABEL_FORMAT = "axis.tick.label.format";
	/** Key for specifying the distance of labels to their ticks. */
	static final String KEY_TICK_LABEL_DISTANCE = "axis.tick.label.distance";
	/** Key for specifying the draw labels outside of the plot. */
	static final String KEY_TICK_LABEL_OUTSIDE = "axis.tick.label.outside";
	/** Key for specifying the rotation of the tick labels in degrees. */
	static final String KEY_TICK_LABEL_ROTATION = "axis.tick.label.rotation";
	/** Custom labels as a {@link java.lang.Map} with a Number as key and String as value. */
	static final String KEY_TICK_LABEL_CUSTOM = "axis.tick.label.custom";

	/** Key for specifying the {@link java.lang.String} instance for the label text of the axis. */
	static final String KEY_LABEL = "axis.label";
	/** Key for specifying the distance from the axis to the label. */
	static final String KEY_LABEL_DISTANCE = "axis.label.distance";
	/** Key for specifying the rotation of the axis label in degrees. */
	static final String KEY_LABEL_ROTATION = "axis.label.rotation";

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
	 * @return A list of <code>Tick</code> instances
	 */
	List<DataPoint2D> getTicks(Axis axis);

}

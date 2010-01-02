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
 * Basic interface for classes that want to display an axis.
 * @author Michael Seifert
 *
 */
public interface AxisRenderer2D extends SettingsStorage {
	/** Intersection point of axis. */
	static final String KEY_INTERSECTION = "axis.intersection";

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
	static final String KEY_TICK_LABEL_FORMAT = "axis.tick.label.format";
	/** Distance of labels to their ticks. */
	static final String KEY_TICK_LABEL_DISTANCE = "axis.tick.label.distance";
	/** Draw labels outside of the plot. */
	static final String KEY_TICK_LABEL_OUTSIDE = "axis.tick.label.outside";
	/** Rotation of the tick labels in degrees. */
	static final String KEY_TICK_LABEL_ROTATION = "axis.tick.label.rotation";

	/** Label text of the axis. */
	static final String KEY_LABEL = "axis.label";
	/** Distance from the axis to the label. */
	static final String KEY_LABEL_DISTANCE = "axis.label.distance";
	/** Rotation of the axis label in degrees. */
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

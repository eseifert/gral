/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.erichseifert.gral.plots.axes;

import java.util.List;

import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.util.PointND;
import de.erichseifert.gral.util.SettingsStorage;

/**
 * Interface for generic renderers of axes.
 */
public interface AxisRenderer extends SettingsStorage {
	/** Key for specifying the intersection point of axis. */
	Key INTERSECTION = new Key("axis.intersection"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Shape} instance that defines
	 the shape of the axis. */
	Key SHAPE = new Key("axis.shape"); //$NON-NLS-1$
	/** Key for specifying a {@link java.lang.Boolean} value which decides
	 whether the shape of the axis will be drawn. This doesn't influence ticks
	 or labels. */
	Key SHAPE_VISIBLE = new Key("axis.shape.visible"); //$NON-NLS-1$
	/** Key for specifying a {@link java.lang.Boolean} value which decides
	 whether normal vector is calculated using clockwise (<code>true</code>)
	 or counterclockwise rotation (<code>false</code>). */
	Key SHAPE_NORMAL_ORIENTATION_CLOCKWISE =
		new Key("axis.shape.normalOrientationClockwise"); //$NON-NLS-1$
	/** Key for specifying {@link java.awt.Paint} instance to be used to paint
	 the axis, its ticks and its labels. */
	Key SHAPE_COLOR = new Key("axis.shape.color"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Stroke} instance which define
	 the shape of the axis. */
	Key SHAPE_STROKE = new Key("axis.shape.stroke"); //$NON-NLS-1$
	/** Key for specifying a {@link java.lang.Boolean} value which decides
	 whether the axis direction will be changed. */
	Key SHAPE_DIRECTION_SWAPPED =
		new Key("axis.shape.directionSwapped"); //$NON-NLS-1$
	/** Key for specifying a {@link java.lang.Boolean} value which decides
	 whether major ticks are drawn. */
	Key TICKS = new Key("axis.ticks.major"); //$NON-NLS-1$
	/** Key for specifying a {@link java.lang.Number} value for the interval
	 for major ticks. */
	Key TICKS_SPACING = new Key("axis.ticks.major.spacing"); //$NON-NLS-1$
	/** Key for specifying a {@link java.lang.Number} value for the length of
	 major tick strokes. The length is specified relative to the font height. */
	Key TICKS_LENGTH = new Key("axis.ticks.major.length"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Stroke} instance which is used
	 to draw all major ticks. */
	Key TICKS_STROKE = new Key("axis.ticks.major.stroke"); //$NON-NLS-1$
	/** Key for specifying a {@link java.lang.Number} value for the alignment
	 of major ticks: 0.0 means outside, 0.5 means centered, 1.0 means inside. */
	Key TICKS_ALIGNMENT = new Key("axis.ticks.major.alignment"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	 paint the shapes of major ticks. */
	Key TICKS_COLOR = new Key("label.color"); //$NON-NLS-1$
	/** Key for specifying whether tick labels are drawn. */
	Key TICK_LABELS = new Key("axis.ticks.major.labels"); //$NON-NLS-1$
	/** Key for specifying the {java.text.Format} instance which converts the
	 tick values to labels. */
	Key TICK_LABELS_FORMAT = new Key(
			"axis.ticks.major.labels.format"); //$NON-NLS-1$
	/** Key for specifying a {@link java.lang.Number} value for the distance
	 of labels to their ticks. The distance is specified relative to the font
	 height. */
	Key TICK_LABELS_DISTANCE = new Key(
			"axis.ticks.major.labels.distance"); //$NON-NLS-1$
	/** Key for specifying a {@link java.lang.Boolean} value which decides
	 whether the tick labels are drawn outside of the plot. */
	Key TICK_LABELS_OUTSIDE = new Key(
			"axis.ticks.major.labels.outside"); //$NON-NLS-1$
	/** Key for specifying a {@link java.lang.Number} value for the rotation
	 of the tick labels in degrees. */
	Key TICK_LABELS_ROTATION = new Key(
			"axis.ticks.major.labels.rotation"); //$NON-NLS-1$
	/** Key for specifying a {@link java.lang.Boolean} value which decides
	 whether minor ticks are drawn. */
	Key TICKS_MINOR = new Key("axis.ticks.minor"); //$NON-NLS-1$
	/** Key for specifying an {@link java.lang.Integer} value for the count
	 of minor ticks. */
	Key TICKS_MINOR_COUNT = new Key("axis.ticks.minor.count"); //$NON-NLS-1$
	/** Key for specifying a {@link java.lang.Number} value for the length
	 of minor tick strokes. The length is specified relative to font height. */
	Key TICKS_MINOR_LENGTH = new Key("axis.ticks.minor.length"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Stroke} instance which is used
	 to draw all minor ticks. */
	Key TICKS_MINOR_STROKE = new Key("axis.ticks.minor.stroke"); //$NON-NLS-1$
	/** Key for specifying a {@link java.lang.Number} value for the alignment
	 of minor ticks: 0.0 means outside, 0.5 means centered, 1.0 means inside. */
	Key TICKS_MINOR_ALIGNMENT =
		new Key("axis.ticks.minor.alignment"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	 paint the the shapes of minor ticks. */
	Key TICKS_MINOR_COLOR = new Key("label.color"); //$NON-NLS-1$
	/** Custom labels as a {@link java.util.Map} with a position
	 (<code>Double</code>) as key and a label (<code>String</code>) as value. */
	Key TICKS_CUSTOM = new Key("axis.ticks.custom"); //$NON-NLS-1$
	/** Key for specifying the {@link java.lang.String} instance for the label
	 text of the axis. */
	Key LABEL = new Key("axis.label"); //$NON-NLS-1$
	/** Key for specifying a {@link java.lang.Number} value for the distance
	from the axis to the label. The length is specified relative to font
	height. */
	Key LABEL_DISTANCE = new Key("axis.label.distance"); //$NON-NLS-1$
	/** Key for specifying a {@link java.lang.Number} value for the rotation of
	 the axis label in degrees. */
	Key LABEL_ROTATION = new Key("axis.label.rotation"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	 paint the axis label. */
	Key LABEL_COLOR = new Key("axis.label.color"); //$NON-NLS-1$

	/**
	 * Returns a component that displays the specified axis.
	 * @param axis axis to be displayed
	 * @return component displaying the axis
	 * @see Axis
	 */
	public abstract Drawable getRendererComponent(Axis axis);

	/**
	 * Converts a world (axis) coordinate value to a view (screen) coordinate
	 * value.
	 * @param axis Axis
	 * @param value World coordinate value to convert
	 * @param extrapolate Option to activate extrapolation value that are not
	 *        on the axis
	 * @return Screen coordinate value
	 */
	public abstract double worldToView(Axis axis, Number value,
			boolean extrapolate);

	/**
	 * Converts a view (screen) coordinate value to a world (axis) coordinate
	 * value.
	 * @param axis Axis
	 * @param value View coordinate value to convert
	 * @param extrapolate Option to activate extrapolation value that are not
	 *        on the axis
	 * @return World coordinate value
	 */
	public abstract Number viewToWorld(Axis axis, double value,
			boolean extrapolate);

	/**
	 * Returns a list of all tick element on the axis.
	 * @param axis Axis
	 * @return A list of <code>Tick</code> instances
	 */
	public abstract List<Tick> getTicks(Axis axis);

	/**
	 * Returns the position of the specified value on the axis.
	 * The value is returned in view coordinates.
	 * @param axis Axis
	 * @param value World coordinate value to convert
	 * @param extrapolate Option to activate extrapolation value that are not
	 *        on the axis
	 * @param forceLinear Force linear interpolation.
	 * @return N-dimensional point of the value
	 */
	PointND<Double> getPosition(Axis axis, Number value, boolean extrapolate, boolean forceLinear);

	/**
	 * Returns the normal vector at the position of the specified value.
	 * The vector is normalized.
	 * @param axis Axis
	 * @param value World coordinate value to convert
	 * @param extrapolate Option to activate extrapolation value that are not
	 *        on the axis
	 * @param forceLinear Force linear interpolation.
	 * @return N-dimensional normal vector at the position
	 */
	PointND<Double> getNormal(Axis axis, Number value, boolean extrapolate, boolean forceLinear);
}

/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael[at]erichseifert.de>
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

import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.text.Format;
import java.util.List;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.plots.settings.Key;
import de.erichseifert.gral.plots.settings.SettingsStorage;
import de.erichseifert.gral.util.PointND;

/**
 * Interface for generic renderers of axes.
 */
public interface AxisRenderer extends SettingsStorage {
	/** Key for specifying a {@link Boolean} value which decides whether minor
	ticks are drawn. */
	Key TICKS_MINOR =
		new Key("axis.ticks.minor"); //$NON-NLS-1$
	/** Key for specifying an {@link Integer} value for the count of minor
	ticks. */
	Key TICKS_MINOR_COUNT =
		new Key("axis.ticks.minor.count"); //$NON-NLS-1$
	/** Key for specifying a {@link Number} value for the length of minor tick
	strokes. The length is specified relative to font height. */
	Key TICKS_MINOR_LENGTH =
		new Key("axis.ticks.minor.length"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Stroke} instance which is used
	to draw all minor ticks. */
	Key TICKS_MINOR_STROKE =
		new Key("axis.ticks.minor.stroke"); //$NON-NLS-1$
	/** Key for specifying a {@link Number} value for the alignment of minor
	ticks: 0.0 means outside, 0.5 means centered, 1.0 means inside. */
	Key TICKS_MINOR_ALIGNMENT =
		new Key("axis.ticks.minor.alignment"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	paint the the shapes of minor ticks. */
	Key TICKS_MINOR_COLOR =
		new Key("label.color"); //$NON-NLS-1$
	/** Custom labels as a {@link java.util.Map} with a position
	({@code Double}) as key and a label ({@code String}) as value. */
	Key TICKS_CUSTOM =
		new Key("axis.ticks.custom"); //$NON-NLS-1$
	/** Key for specifying the {@link String} instance for the label text of
	the axis. */
	Key LABEL =
		new Key("axis.label"); //$NON-NLS-1$
	/** Key for specifying a {@link Number} value for the distance from the
	axis to the label. The length is specified relative to font height. */
	Key LABEL_DISTANCE =
		new Key("axis.label.distance"); //$NON-NLS-1$
	/** Key for specifying a {@link Number} value for the rotation of the axis
	label in degrees. */
	Key LABEL_ROTATION =
		new Key("axis.label.rotation"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Font} instance to be used to
	display the axis label text. */
	Key LABEL_FONT =
		new Key("axis.label.font"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	paint the axis label. */
	Key LABEL_COLOR =
		new Key("axis.label.color"); //$NON-NLS-1$

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
	 * @return A list of {@code Tick} instances
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

	/**
	 * Returns the intersection point of the axis.
	 * @return Point at which this axis intersects other axes.
	 */
	Number getIntersection();

	/**
	 * Sets the intersection point of the axis.
	 * @param intersection Point at which this axis intersects other axes.
	 */
	void setIntersection(Number intersection);

	/**
	 * Returns the shape of the axis.
	 * @return Shape used for drawing.
	 */
	Shape getShape();

	/**
	 * Sets the shape of the axis.
	 * @param shape Shape used for drawing.
	 */
	void setShape(Shape shape);

	/**
	 * Returns whether the shape of the axis will be drawn.
	 * This doesn't influence ticks or labels.
	 * @return {@code true} if the shape should be drawn, false otherwise.
	 */
	boolean isShapeVisible();

	/**
	 * Sets whether the shape of the axis will be drawn.
	 * This doesn't influence ticks or labels.
	 * @param shapeVisible {@code true} if the shape should be drawn, false otherwise.
	 */
	void setShapeVisible(boolean shapeVisible);

	/**
	 * Returns whether the normal vector of the shape is calculated using
	 * clockwise or counterclockwise rotation.
	 * @return {@code true} if the orientation is clockwise, {@code false} if it is
	 * counterclockwise.
	 */
	boolean isShapeNormalOrientationClockwise();

	/**
	 * Sets whether the normal vector of the shape is calculated using
	 * clockwise or counterclockwise rotation.
	 * @param shapeNormalOrientationClockwise {@code true} if the orientation is clockwise,
	 * {@code false} if it is counterclockwise.
	 */
	void setShapeNormalOrientationClockwise(boolean shapeNormalOrientationClockwise);

	/**
	 * Returns the paint used to draw the axis, its ticks and its labels.
	 * @return Paint used for drawing.
	 */
	Paint getShapeColor();

	/**
	 * Sets the paint used to draw the axis, its ticks and its labels.
	 * @param color Paint used for drawing.
	 */
	void setShapeColor(Paint color);

	/**
	 * Returns the stroke which defines the shape of the axis.
	 * @return Stroke used for drawing the shape.
	 */
	Stroke getShapeStroke();

	/**
	 * Sets the stroke which defines the shape of the axis.
	 * @param shapeStroke Stroke used for drawing the shape.
	 */
	void setShapeStroke(Stroke shapeStroke);

	/**
	 * Returns whether the axis direction will be changed.
	 * @return {@code true} if the shape of the axis is inverted,
	 * {@code false} otherwise.
	 */
	boolean isShapeDirectionSwapped();

	/**
	 * Sets whether the axis direction will be changed.
	 * @param shapeDirectionSwapped {@code true} if the shape of the axis is inverted,
	 * {@code false} otherwise.
	 */
	void setShapeDirectionSwapped(boolean shapeDirectionSwapped);

	/**
	 * Returns whether major ticks are drawn.
	 * @return {@code true} if major ticks should be drawn, {@code false} otherwise.
	 */
	boolean isTicksDrawn();

	/**
	 * Sets whether major ticks are drawn.
	 * @param ticksDrawn {@code true} if major ticks should be drawn,
	 * {@code false} otherwise.
	 */
	void setTicksDrawn(boolean ticksDrawn);

	/**
	 * Returns the interval for major ticks.
	 * @return Distance on axis in which major ticks are drawn.
	 */
	Number getTickSpacing();

	/**
	 * Sets the interval for major ticks.
	 * @param tickSpacing Distance on axis in which major ticks are drawn.
	 */
	void setTickSpacing(Number tickSpacing);

	/**
	 * Returns whether the interval for major and minor ticks is chosen automatically.
	 * @return {@code true} if auto-spacing is enabled, {@code false} otherwise.
	 */
	boolean isTicksAutoSpaced();

	/**
	 * Sets whether the interval for major and minor ticks is chosen automatically.
	 * @param ticksAutoSpaced {@code true} if auto-spacing is enabled, {@code false} otherwise.
	 */
	void setTicksAutoSpaced(boolean ticksAutoSpaced);

	/**
	 * Returns the length of major tick strokes.
	 * @return Tick length relative to the font height.
	 */
	Number getTickLength();

	/**
	 * Sets the length of major tick strokes.
	 * @param tickLength Tick length relative to the font height.
	 */
	void setTickLength(Number tickLength);

	/**
	 * Returns the stroke which is used to draw all major ticks.
	 * @return Stroke used for major tick drawing.
	 */
	Stroke getTickStroke();

	/**
	 * Sets the stroke which is used to draw all major ticks.
	 * @param tickStroke Stroke used for major tick drawing.
	 */
	void setTickStroke(Stroke tickStroke);

	/**
	 * Returns the alignment of major ticks relative to the axis.
	 * 0.0 means outside the plotting area, 0.5 means centered on the axis,
	 * 1.0 means inside the plotting area.
	 * @return Major tick alignment relative to the axis.
	 */
	Number getTickAlignment();

	/**
	 * Sets the alignment of major ticks relative to the axis.
	 * 0.0 means outside the plotting area, 0.5 means centered on the axis,
	 * 1.0 means inside the plotting area.
	 * @param tickAlignment Major tick alignment relative to the axis.
	 */
	void setTickAlignment(Number tickAlignment);

	/**
	 * Returns the font used to display the text of major ticks.
	 * @return Font used for tick labels.
	 */
	Font getTickFont();

	/**
	 * Sets the font used to display the text of major ticks.
	 * @param tickFont Font used for tick labels.
	 */
	void setTickFont(Font tickFont);

	/**
	 * Returns the paint used to draw the shapes of major ticks.
	 * @return Paint used for major tick drawing.
	 */
	Paint getTickColor();

	/**
	 * Sets the paint used to draw the shapes of major ticks.
	 * @param tickColor Paint used for major tick drawing.
	 */
	void setTickColor(Paint tickColor);

	/**
	 * Returns whether tick labels will be shown.
	 * @return {@code true} if tick labels will be drawn, {@code false} otherwise.
	 */
	boolean isTickLabelsEnabled();

	/**
	 * Sets whether tick labels will be shown.
	 * @param tickLabelsEnabled {@code true} if tick labels will be drawn, {@code false} otherwise.
	 */
	void setTickLabelsEnabled(boolean tickLabelsEnabled);

	/**
	 * Returns the format which converts the tick values to labels.
	 * @return Format used for tick labels.
	 */
	Format getTickLabelFormat();

	/**
	 * Sets the format which converts the tick values to labels.
	 * @param tickLabelFormat Format used for tick labels.
	 */
	void setTickLabelFormat(Format tickLabelFormat);

	/**
	 * Returns the distance of labels to their ticks.
	 * @return Label distance relative to the font height.
	 */
	Number getTickLabelDistance();

	/**
	 * Sets the distance of labels to their ticks.
	 * @param tickLabelDistance Label distance relative to the font height.
	 */
	void setTickLabelDistance(Number tickLabelDistance);

	/**
	 * Returns whether the tick labels are drawn outside of the plot.
	 * @return {@code true} if the labels are drawn outside of the plot, {@code false} otherwise.
	 */
	boolean isTickLabelsOutside();

	/**
	 * Sets whether the tick labels are drawn outside of the plot.
	 * @param tickLabelsOutside {@code true} if the labels are drawn outside of the plot,
	 * {@code false} otherwise.
	 */
	void setTickLabelsOutside(boolean tickLabelsOutside);

	/**
	 * Returns the rotation of the tick labels.
	 * @return Tick label rotation in degrees.
	 */
	Number getTickLabelRotation();

	/**
	 * Sets the rotation of the tick labels.
	 * @param tickLabelRotation Tick label rotation in degrees.
	 */
	void setTickLabelRotation(Number tickLabelRotation);
}

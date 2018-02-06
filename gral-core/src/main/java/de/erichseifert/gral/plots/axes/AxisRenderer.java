/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <mseifert[at]error-reports.org>
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
import java.util.Map;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.util.PointND;

/**
 * Interface for generic renderers of axes.
 */
public interface AxisRenderer {
	/**
	 * Returns a component that displays the specified axis.
	 * @param axis axis to be displayed
	 * @return component displaying the axis
	 * @see Axis
	 */
	Drawable getRendererComponent(Axis axis);

	/*
	 * TODO: Enforce minimum and maximum values when extrapolation is turned off
	 * by using MathUtils.limit(double, double, double) on the result
	 */
	/**
	 * Converts a world (axis) coordinate value to a view (screen) coordinate
	 * value. If @code{extrapolate == false}, this method should return 0.0 when
	 * value is smaller than @code{axis.getMin()} and {@code getShapeLength()} when
	 * value is larger than @code{axis.getMax(}).
	 * @param axis Axis
	 * @param value World coordinate value to convert
	 * @param extrapolate Option to activate extrapolation value that are not
	 *        on the axis
	 * @return Screen coordinate value
	 */
	double worldToView(Axis axis, Number value,
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
	Number viewToWorld(Axis axis, double value,
					   boolean extrapolate);

	/**
	 * Returns a list of all tick element on the axis.
	 * @param axis Axis
	 * @return A list of {@code Tick} instances
	 */
	List<Tick> getTicks(Axis axis);

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
	 * @param clockwise {@code true} if the orientation is clockwise,
	 * {@code false} if it is counterclockwise.
	 */
	void setShapeNormalOrientationClockwise(boolean clockwise);

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
	 * @param stroke Stroke used for drawing the shape.
	 */
	void setShapeStroke(Stroke stroke);

	/**
	 * Returns whether the axis direction is changed.
	 * @return {@code true} if the shape of the axis is inverted,
	 * {@code false} otherwise.
	 */
	boolean isShapeDirectionSwapped();

	/**
	 * Sets whether the axis direction will be changed.
	 * @param directionSwapped {@code true} if the shape of the axis
	 * should be inverted, {@code false} otherwise.
	 */
	void setShapeDirectionSwapped(boolean directionSwapped);

	/**
	 * Returns whether major ticks are drawn.
	 * @return {@code true} if major ticks are drawn, {@code false} otherwise.
	 */
	boolean isTicksVisible();

	/**
	 * Sets whether major ticks will be drawn.
	 * @param ticksVisible {@code true} if major ticks should be drawn,
	 * {@code false} otherwise.
	 */
	void setTicksVisible(boolean ticksVisible);

	/**
	 * Returns the interval for major ticks.
	 * @return Distance on axis in which major ticks are drawn.
	 */
	Number getTickSpacing();

	/**
	 * Sets the interval for major ticks.
	 * @param spacing Distance on axis in which major ticks are drawn.
	 */
	void setTickSpacing(Number spacing);

	/**
	 * Returns whether the interval for major and minor ticks is chosen automatically.
	 * @return {@code true} if auto-spacing is enabled, {@code false} otherwise.
	 */
	boolean isTicksAutoSpaced();

	/**
	 * Sets whether the interval for major and minor ticks is chosen automatically.
	 * @param autoSpaced {@code true} if auto-spacing is enabled, {@code false} otherwise.
	 */
	void setTicksAutoSpaced(boolean autoSpaced);

	/**
	 * Returns the length of major tick strokes.
	 * @return Tick length relative to the font height.
	 */
	double getTickLength();

	/**
	 * Sets the length of major tick strokes.
	 * @param length Tick length relative to the font height.
	 */
	void setTickLength(double length);

	/**
	 * Returns the stroke which is used to draw all major ticks.
	 * @return Stroke used for major tick drawing.
	 */
	Stroke getTickStroke();

	/**
	 * Sets the stroke which is used to draw all major ticks.
	 * @param stroke Stroke used for major tick drawing.
	 */
	void setTickStroke(Stroke stroke);

	/**
	 * Returns the alignment of major ticks relative to the axis.
	 * 0.0 means outside the plotting area, 0.5 means centered on the axis,
	 * 1.0 means inside the plotting area.
	 * @return Major tick alignment relative to the axis.
	 */
	double getTickAlignment();

	/**
	 * Sets the alignment of major ticks relative to the axis.
	 * 0.0 means outside the plotting area, 0.5 means centered on the axis,
	 * 1.0 means inside the plotting area.
	 * @param alignment Major tick alignment relative to the axis.
	 */
	void setTickAlignment(double alignment);

	/**
	 * Returns the font used to display the text of major ticks.
	 * @return Font used for tick labels.
	 */
	Font getTickFont();

	/**
	 * Sets the font used to display the text of major ticks.
	 * @param font Font used for tick labels.
	 */
	void setTickFont(Font font);

	/**
	 * Returns the paint used to draw the shapes of major ticks.
	 * @return Paint used for major tick drawing.
	 */
	Paint getTickColor();

	/**
	 * Sets the paint used to draw the shapes of major ticks.
	 * @param color Paint used for major tick drawing.
	 */
	void setTickColor(Paint color);

	/**
	 * Returns whether tick labels will be shown.
	 * @return {@code true} if tick labels will be drawn, {@code false} otherwise.
	 */
	boolean isTickLabelsVisible();

	/**
	 * Sets whether tick labels will be shown.
	 * @param tickLabelsVisible {@code true} if tick labels will be drawn, {@code false} otherwise.
	 */
	void setTickLabelsVisible(boolean tickLabelsVisible);

	/**
	 * Returns the format which converts the tick values to labels.
	 * @return Format used for tick labels.
	 */
	Format getTickLabelFormat();

	/**
	 * Sets the format which converts the tick values to labels.
	 * @param format Format used for tick labels.
	 */
	void setTickLabelFormat(Format format);

	/**
	 * Returns the distance of labels to their ticks.
	 * @return Label distance relative to the font height.
	 */
	double getTickLabelDistance();

	/**
	 * Sets the distance of labels to their ticks.
	 * @param distance Label distance relative to the font height.
	 */
	void setTickLabelDistance(double distance);

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
	double getTickLabelRotation();

	/**
	 * Sets the rotation of the tick labels.
	 * @param angle Tick label rotation in degrees.
	 */
	void setTickLabelRotation(double angle);

	/**
	 * Returns whether minor ticks are drawn.
	 * @return {@code true} if minor ticks are drawn, {@code false} otherwise.
	 */
	boolean isMinorTicksVisible();

	/**
	 * Sets whether minor ticks are drawn.
	 * @param minorTicksVisible {@code true} if minor ticks are drawn, {@code false} otherwise.
	 */
	void setMinorTicksVisible(boolean minorTicksVisible);

	/**
	 * Returns the count of minor ticks.
	 * @return Number of minor ticks between two major ticks.
	 */
	int getMinorTicksCount();

	/**
	 * Sets the count of minor ticks.
	 * @param count Number of minor ticks between two major ticks.
	 */
	void setMinorTicksCount(int count);

	/**
	 * Returns the length of minor tick strokes.
	 * @return Tick length relative to font height.
	 */
	double getMinorTickLength();

	/**
	 * Sets the length of minor tick strokes.
	 * @param length Tick length relative to font height.
	 */
	void setMinorTickLength(double length);

	/**
	 * Returns the stroke used to draw all minor ticks.
	 * @return Stroke used for minor tick drawing.
	 */
	Stroke getMinorTickStroke();

	/**
	 * Sets the stroke used to draw all minor ticks.
	 * @param stroke Stroke used for minor tick drawing.
	 */
	void setMinorTickStroke(Stroke stroke);

	/**
	 * Returns the alignment of minor ticks.
	 * 0.0 means outside the plotting area, 0.5 means centered on the axis,
	 * 1.0 means inside the plotting area.
	 * @return Minor tick alignment relative to the axis.
	 */
	double getMinorTickAlignment();

	/**
	 * Sets the alignment of minor ticks.
	 * 0.0 means outside the plotting area, 0.5 means centered on the axis,
	 * 1.0 means inside the plotting area.
	 * @param alignment Minor tick alignment relative to the axis.
	 */
	void setMinorTickAlignment(double alignment);

	/**
	 * Returns the paint used to draw the shapes of minor ticks.
	 * @return Paint used for minor tick drawing.
	 */
	Paint getMinorTickColor();

	/**
	 * Sets the paint used to draw the shapes of minor ticks.
	 * @param ticksMinorColor Paint used for minor tick drawing.
	 */
	void setMinorTickColor(Paint ticksMinorColor);

	/**
	 * Returns custom ticks with their respective position and label.
	 * @return A map of custom tick positions and labels.
	 */
	Map<Double, String> getCustomTicks();

	/**
	 * Sets custom ticks with their respective position and label.
	 * @param positionsAndLabels A map of custom tick positions and labels.
	 */
	void setCustomTicks(Map<Double, String> positionsAndLabels);

	/**
	 * Returns the label of the axis.
	 * @return Axis label.
	 */
	Label getLabel();

	/**
	 * Sets the label of the axis.
	 * @param label Axis label.
	 */
	void setLabel(Label label);

	/**
	 * Returns the distance from the axis to the label.
	 * @return Distance relative to font height.
	 */
	double getLabelDistance();

	/**
	 * Sets the distance from the axis to the label.
	 * @param distance Distance relative to font height.
	 */
	void setLabelDistance(double distance);
}

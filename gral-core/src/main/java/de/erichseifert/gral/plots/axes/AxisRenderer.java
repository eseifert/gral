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
import java.util.Map;

import de.erichseifert.gral.graphics.Drawable;
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
	double getTickLength();

	/**
	 * Sets the length of major tick strokes.
	 * @param tickLength Tick length relative to the font height.
	 */
	void setTickLength(double tickLength);

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
	double getTickAlignment();

	/**
	 * Sets the alignment of major ticks relative to the axis.
	 * 0.0 means outside the plotting area, 0.5 means centered on the axis,
	 * 1.0 means inside the plotting area.
	 * @param tickAlignment Major tick alignment relative to the axis.
	 */
	void setTickAlignment(double tickAlignment);

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

	/**
	 * Returns whether minor ticks are drawn.
	 * @return {@code true} if minor ticks are drawn, {@code false} otherwise.
	 */
	boolean isTicksMinorEnabled();

	/**
	 * Sets whether minor ticks are drawn.
	 * @param ticksMinorEnabled {@code true} if minor ticks are drawn, {@code false} otherwise.
	 */
	void setTicksMinorEnabled(boolean ticksMinorEnabled);

	/**
	 * Returns the count of minor ticks.
	 * @return Number of minor ticks between two major ticks.
	 */
	int getTicksMinorCount();

	/**
	 * Sets the count of minor ticks.
	 * @param ticksMinorCount Number of minor ticks between two major ticks.
	 */
	void setTicksMinorCount(int ticksMinorCount);

	/**
	 * Returns the length of minor tick strokes.
	 * @return Tick length relative to font height.
	 */
	Number getTicksMinorLength();

	/**
	 * Sets the length of minor tick strokes.
	 * @param ticksMinorLength Tick length relative to font height.
	 */
	void setTicksMinorLength(Number ticksMinorLength);

	/**
	 * Returns the stroke used to draw all minor ticks.
	 * @return Stroke used for minor tick drawing.
	 */
	Stroke getTicksMinorStroke();

	/**
	 * Sets the stroke used to draw all minor ticks.
	 * @param ticksMinorStroke Stroke used for minor tick drawing.
	 */
	void setTicksMinorStroke(Stroke ticksMinorStroke);

	/**
	 * Returns the alignment of minor ticks.
	 * 0.0 means outside the plotting area, 0.5 means centered on the axis,
	 * 1.0 means inside the plotting area.
	 * @return Minor tick alignment relative to the axis.
	 */
	Number getTicksMinorAlignment();

	/**
	 * Sets the alignment of minor ticks.
	 * 0.0 means outside the plotting area, 0.5 means centered on the axis,
	 * 1.0 means inside the plotting area.
	 * @param ticksMinorAlignment Minor tick alignment relative to the axis.
	 */
	void setTicksMinorAlignment(Number ticksMinorAlignment);

	/**
	 * Returns the paint used to draw the shapes of minor ticks.
	 * @return Paint used for minor tick drawing.
	 */
	Paint getTicksMinorColor();

	/**
	 * Sets the paint used to draw the shapes of minor ticks.
	 * @param ticksMinorColor Paint used for minor tick drawing.
	 */
	void setTicksMinorColor(Paint ticksMinorColor);

	/**
	 * Returns custom labels with their respective position and text.
	 * @return Custom tick labels.
	 */
	Map<Double, String> getCustomLabels();

	/**
	 * Sets custom labels with their respective position and text.
	 * @param customLabels Custom tick labels.
	 */
	void setCustomLabels(Map<Double, String> customLabels);

	/**
	 * Returns the label text of the axis.
	 * @return Axis label.
	 */
	String getLabel();

	/**
	 * Sets the label text of the axis.
	 * @param label Axis label.
	 */
	void setLabel(String label);

	/**
	 * Returns the distance from the axis to the label.
	 * @return Distance relative to font height.
	 */
	Number getLabelDistance();

	/**
	 * Sets the distance from the axis to the label.
	 * @param labelDistance Distance relative to font height.
	 */
	void setLabelDistance(Number labelDistance);

	/**
	 * Returns the rotation of the axis label.
	 * @return Axis label rotation in degrees.
	 */
	Number getLabelRotation();

	/**
	 * Sets the rotation of the axis label.
	 * @param labelRotation Axis label rotation in degrees.
	 */
	void setLabelRotation(Number labelRotation);

	/**
	 * Returns the font used to display the axis label text.
	 * @return Font for axis label text.
	 */
	Font getLabelFont();

	/**
	 * Sets the font used to display the axis label text.
	 * @param labelFont Font for axis label text.
	 */
	void setLabelFont(Font labelFont);

	/**
	 * Returns the paint used to draw the axis label.
	 * @return Paint for axis label drawing.
	 */
	Paint getLabelColor();

	/**
	 * Sets the paint used to draw the axis label.
	 * @param labelColor Paint for axis label drawing.
	 */
	void setLabelColor(Paint labelColor);
}

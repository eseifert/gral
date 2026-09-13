/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2026 Erich Seifert <dev[at]erichseifert.de>,
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

import java.util.HashSet;
import java.util.Set;

import de.erichseifert.gral.util.MathUtils;

/**
 * <p>The value range that is currently displayed along one axis of a plot. An
 * {@code Axis} holds nothing but a minimum, a maximum, an auto-scaling flag and
 * a list of listeners; it does not know how it is drawn, and it cannot convert
 * a value to a pixel position. That is the job of the
 * {@link AxisRenderer} registered for the same axis name.</p>
 *
 * <pre>
 * // A fixed range: auto-scaling is off, so the plot leaves it alone.
 * plot.setAxis(XYPlot.AXIS_Y, new Axis(0.0, 100.0));
 *
 * // Back to a range derived from the data.
 * Axis axisY = plot.getAxis(XYPlot.AXIS_Y);
 * axisY.setAutoscaled(true);
 * plot.autoscaleAxis(XYPlot.AXIS_Y);
 * </pre>
 *
 * <p>An axis created with {@link #Axis()} auto-scales; one created with
 * {@link #Axis(Number, Number)} does not. Note that
 * {@link #setRange(Number, Number)} does <em>not</em> turn auto-scaling off by
 * itself: a range set by hand on an auto-scaled axis is replaced the next time
 * the plot rescales, so call {@link #setAutoscaled(boolean) setAutoscaled(false)}
 * as well. Until a range has been set, {@link #getMin()} and {@link #getMax()}
 * return {@code null} and {@link #isValid()} is {@code false}.</p>
 *
 * <p>Every change of the range notifies the registered
 * {@link AxisListener}s, which is how a plot learns that it has to repaint. A
 * minimum greater than the maximum is allowed and reverses the direction of the
 * axis.</p>
 */
public class Axis {
	/** Objects that will be notified when axis settings are changing. */
	private Set<AxisListener> axisListeners;

	/** Minimal value on axis. */
	private Number min;
	/** Maximal value on axis. */
	private Number max;
	/** Has the axis a valid range. Used for auto-scaling. */
	private boolean autoscaled;

	/**
	 * Initializes a new instance with a specified automatic scaling mode, but
	 * without minimum and maximum values.
	 * @param autoscaled {@code true} to turn automatic scaling on
	 */
	private Axis(boolean autoscaled) {
		axisListeners = new HashSet<>();
		this.autoscaled = autoscaled;
	}

	/**
	 * Initializes a new instance without minimum and maximum values.
	 */
	public Axis() {
		this(true);
	}

	/**
	 * Initializes a new instance with the specified minimum and maximum values.
	 * @param min minimum value
	 * @param max maximum value
	 */
	public Axis(Number min, Number max) {
		this(false);
		this.min = min;
		this.max = max;
	}

	/**
	 * Adds the specified {@code AxisListener} to this Axis.
	 * The Listeners will be notified if changes to the Axis occur,
	 * for Example if the minimum or maximum value changes.
	 * @param listener Listener to be added
	 * @see AxisListener
	 */
	public void addAxisListener(AxisListener listener) {
		axisListeners.add(listener);
	}

	/**
	 * Removes the specified {@code AxisListener} from this Axis.
	 * @param listener Listener to be removed
	 * @see AxisListener
	 */
	public void removeAxisListener(AxisListener listener) {
		axisListeners.remove(listener);
	}

	/**
	 * Notifies all registered {@code AxisListener}s that the value
	 * range has changed.
	 * @param min new minimum value
	 * @param max new maximum value
	 */
	private void fireRangeChanged(Number min, Number max) {
		for (AxisListener listener : axisListeners) {
			listener.rangeChanged(this, min, max);
		}
	}

	/**
	 * Returns the minimum value to be displayed.
	 * @return Minimum value.
	 */
	public Number getMin() {
		return min;
	}

	/**
	 * Sets the minimum value to be displayed.
	 * @param min Minimum value.
	 */
	public void setMin(Number min) {
		setRange(min, getMax());
	}

	/**
	 * Returns the maximum value to be displayed.
	 * @return Maximum value.
	 */
	public Number getMax() {
		return max;
	}

	/**
	 * Sets the maximum value to be displayed.
	 * @param max Maximum value.
	 */
	public void setMax(Number max) {
		setRange(getMin(), max);
	}

	/**
	 * Returns the range of values to be displayed.
	 * @return Distance between maximum and minimum value.
	 */
	public double getRange() {
		return getMax().doubleValue() - getMin().doubleValue();
	}

	/**
	 * Sets the range of values to be displayed.
	 * @param min Minimum value.
	 * @param max Maximum value.
	 */
	public void setRange(Number min, Number max) {
		if ((getMin() != null) && getMin().equals(min) &&
				(getMax() != null) && getMax().equals(max)) {
			return;
		}
		this.min = min;
		this.max = max;
		fireRangeChanged(min, max);
	}

	/**
	 * Returns the relative position of the specified value on the axis.
	 * The value is returned in view coordinates.
	 * @param value Value whose position is to be determined
	 * @return Position relative to axis range
	 */
	public Number getPosition(Number value) {
		if (value == null) {
			return null;
		}
		return (value.doubleValue() - getMin().doubleValue()) /
				getRange();
	}

	/**
	 * Returns whether the axis range should be determined automatically rather
	 * than using the axis's minimum and a maximum values.
	 * @return whether the axis is scaled automatically to fit the current data
	 */
	public boolean isAutoscaled() {
		return autoscaled;
	}

	/**
	 * Sets whether the axis range should be determined automatically rather
	 * than using the axis's minimum and a maximum values.
	 * @param autoscaled Defines whether the axis should be automatically
	 *                   scaled to fit the current data.
	 */
	public void setAutoscaled(boolean autoscaled) {
		this.autoscaled = autoscaled;
	}

	/**
	 * Returns whether the currently set minimum and maximum values are valid.
	 * @return {@code true} when minimum and maximum values are correct,
	 *         otherwise {@code false}
	 */
	public boolean isValid() {
		return MathUtils.isCalculatable(min) && MathUtils.isCalculatable(max);
	}
}

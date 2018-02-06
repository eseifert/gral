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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import de.erichseifert.gral.util.MathUtils;

/**
 * <p>Class that represents an arbitrary axis.</p>
 * <p>Functionality includes:</p>
 * <ul>
 *   <li>Different ways of setting and getting the range of this axis</li>
 *   <li>Administration of {@link AxisListener AxisListeners}</li>
 * </ul>
 */
public class Axis implements Serializable {
	/** Version id for serialization. */
	private static final long serialVersionUID = 5355772833362614591L;

	/** Objects that will be notified when axis settings are changing. */
	private transient Set<AxisListener> axisListeners;

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

	/**
	 * Custom deserialization method.
	 * @param in Input stream.
	 * @throws ClassNotFoundException if a serialized class doesn't exist anymore.
	 * @throws IOException if there is an error while reading data from the
	 *         input stream.
	 */
	private void readObject(ObjectInputStream in)
			throws ClassNotFoundException, IOException {
		// Normal deserialization
		in.defaultReadObject();

		// Handle transient fields
		axisListeners = new HashSet<>();
	}
}

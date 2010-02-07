/* GRAL : a free graphing library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.plots.axes;

import java.util.HashSet;
import java.util.Set;

/**
 * Class that represents an arbitrary axis.
 * Functionality includes:
 * <ul>
 * <li>Different ways of setting and getting the range of this axis</li>
 * <li>Administration of {@link AxisListener AxisListeners}</li>
 * </ul>
 */
public class Axis {
	/** Key for specifying the x-axis */
	public static final String X = "x";
	/** Key for specifying the y-axis */
	public static final String Y = "y";
	/** Key for specifying the z-axis */
	public static final String Z = "z";

	private final Set<AxisListener> axisListeners;

	private Number min;
	private Number max;

	/**
	 * Creates an <code>Axis</code> object with the specified minimum and maximum values.
	 * @param min minimum value
	 * @param max maximum value
	 */
	public Axis(Number min, Number max) {
		axisListeners = new HashSet<AxisListener>();

		this.min = min;
		this.max = max;
	}

	/**
	 * Adds the specified <code>AxisListener</code> to this Axis.
	 * The Listeners will be notified if changes to the Axis occur,
	 * for Example if the minimum or maximum value changes.
	 * @param listener Listener to be added
	 * @see AxisListener
	 */
	public void addAxisListener(AxisListener listener) {
		axisListeners.add(listener);
	}

	/**
	 * Removes the specified <code>AxisListener</code> from this Axis.
	 * @param listener Listener to be removed
	 * @see AxisListener
	 */
	public void removeAxisListener(AxisListener listener) {
		axisListeners.remove(listener);
	}

	/**
	 * Notifies all registered <code>AxisListener</code>s that the value
	 * range has changed.
	 * @param min new minimum value
	 * @param max new maximum value
	 */
	private void fireRangeChanged(Number min, Number max) {
		for (AxisListener listener : axisListeners) {
			listener.rangeChanged(min, max);
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
		setRange(min, this.max);
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
		setRange(this.min, max);
	}

	/**
	 * Returns the range of values to be displayed.
	 * @return Distance between maximum and minimum value.
	 */
	public double getRange() {
		return max.doubleValue() - min.doubleValue();
	}

	/**
	 * Sets the range of values to be displayed.
	 * @param min Minimum value.
	 * @param max Maximum value.
	 */
	public void setRange(Number min, Number max) {
		this.min = min;
		this.max = max;
		fireRangeChanged(min, max);
	}
}

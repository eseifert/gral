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

import java.util.ArrayList;
import java.util.List;

public class Axis {
	public static final String X = "x";
	public static final String Y = "y";
	public static final String Z = "z";

	private final List<AxisListener> axisListeners;

	private Number min;
	private Number max;

	/**
	 * Creates an <code>Axis</code> object with the specified minimum and maximum values.
	 * @param min minimum value
	 * @param max maximum value
	 */
	public Axis(Number min, Number max) {
		axisListeners = new ArrayList<AxisListener>();

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

	public Number getMin() {
		return min;
	}

	public void setMin(Number min) {
		setRange(min, this.max);
	}

	public Number getMax() {
		return max;
	}

	public void setMax(Number max) {
		setRange(this.min, max);
	}

	public double getRange() {
		return max.doubleValue() - min.doubleValue();
	}

	public void setRange(Number min, Number max) {
		this.min = min;
		this.max = max;
		fireRangeChanged(min, max);
	}
}

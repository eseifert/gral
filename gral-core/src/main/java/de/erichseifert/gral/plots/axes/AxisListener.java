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

/**
 * <p>Receives a notification whenever the value range of an {@link Axis}
 * changes. A plot listens to its own axes this way, which is how zooming and
 * panning &mdash; both of which work by changing axis ranges &mdash; cause a
 * repaint.</p>
 *
 * <p>This is a single-method interface, so a lambda will do:</p>
 * <pre>
 * axis.addAxisListener((a, min, max) -&gt;
 *     System.out.println("Range is now " + min + " to " + max));
 * </pre>
 *
 * <p>The notification is sent after the range has been changed, and only when
 * it really differs from the previous one.</p>
 */
public interface AxisListener {
	/**
	 * Notified if the range of the axis has changed.
	 * @param axis Axis instance that has changed.
	 * @param min New minimum value.
	 * @param max New maximum value.
	 */
	void rangeChanged(Axis axis, Number min, Number max);
}

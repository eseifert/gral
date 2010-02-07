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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisListener;

public class AxisTest {
	private static final double DELTA = 1e-15;
	private Axis axis;

	@Before
	public void setUp() {
		axis = new Axis(-5.0, 5.0);
	}

	@Test
	public void testMin() {
		assertEquals(-5.0, axis.getMin().doubleValue(), DELTA);
		axis.setMin(10.0);
		assertEquals(10.0, axis.getMin().doubleValue(), DELTA);
	}

	@Test
	public void testMax() {
		assertEquals(5.0, axis.getMax().doubleValue(), DELTA);
		axis.setMax(10.0);
		assertEquals(10.0, axis.getMax().doubleValue(), DELTA);
	}

	@Test
	public void testRange() {
		assertEquals(10.0, axis.getRange(), DELTA);
		axis.setRange(1.0, 3.0);
		assertEquals(2.0, axis.getRange(), DELTA);
	}

	private static final class AxisListenerTest implements AxisListener {
		public Number min = null;
		public Number max = null;

		@Override
		public void rangeChanged(Number min, Number max) {
			this.min = min;
			this.max = max;
		}
	}

	@Test
	public void testAxisListeners() {
		AxisListenerTest l = new AxisListenerTest();

		axis.addAxisListener(l);
		axis.setRange(0.0, 1.0);
		assertEquals(0.0, l.min.doubleValue(), DELTA);
		assertEquals(1.0, l.max.doubleValue(), DELTA);

		axis.removeAxisListener(l);
		axis.setRange(2.0, 3.0);
		assertEquals(0.0, l.min.doubleValue(), DELTA);
		assertEquals(1.0, l.max.doubleValue(), DELTA);
	}

}

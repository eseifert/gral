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
package de.erichseifert.gral.plots.colors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.awt.Color;
import java.awt.Paint;

import org.junit.Test;

import de.erichseifert.gral.plots.colors.ColorMapper.Mode;
import de.erichseifert.gral.util.MathUtils;

public class ContinuousColorMapperTest {
	private static final class MockContinuousColorMapper
			extends ContinuousColorMapper {
		@Override
		public Paint get(double value) {
			Double v = applyMode(value, 0.0, 1.0);
			if (!MathUtils.isCalculatable(v)) {
				return null;
			}
			float c = v.floatValue();
			return new Color(c, c, c);
		}
	}

	@Test
	public void testGetNumber() {
		MockContinuousColorMapper c = new MockContinuousColorMapper();
		assertEquals(c.get(0.5), c.get(0.5f));
	}

	@Test
	public void testGetOmit() {
		MockContinuousColorMapper c = new MockContinuousColorMapper();
		c.setMode(Mode.OMIT);
		assertNull(c.get(-0.5));
		assertEquals(new Color(  0,   0,   0), c.get( 0.0));
		assertEquals(new Color(128, 128, 128), c.get( 0.5));
		assertEquals(new Color(255, 255, 255), c.get( 1.0));
		assertNull(c.get( 1.5));
	}

	@Test
	public void testGetRepeat() {
		MockContinuousColorMapper c = new MockContinuousColorMapper();
		c.setMode(Mode.REPEAT);
		assertEquals(new Color(  0,   0,   0), c.get(-0.5));
		assertEquals(new Color(  0,   0,   0), c.get( 0.0));
		assertEquals(new Color(128, 128, 128), c.get( 0.5));
		assertEquals(new Color(255, 255, 255), c.get( 1.0));
		assertEquals(new Color(255, 255, 255), c.get( 1.5));
	}

	@Test
	public void testGetCircular() {
		MockContinuousColorMapper c = new MockContinuousColorMapper();
		c.setMode(Mode.CIRCULAR);
		assertEquals(new Color(128, 128, 128), c.get(-0.5));
		assertEquals(new Color(  0,   0,   0), c.get( 0.0));
		assertEquals(new Color(128, 128, 128), c.get( 0.5));
		assertEquals(new Color(255, 255, 255), c.get( 1.0));
		assertEquals(new Color(128, 128, 128), c.get( 1.5));
	}
}

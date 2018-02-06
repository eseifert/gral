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
package de.erichseifert.gral.plots.colors;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.awt.Paint;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.util.MathUtils;

public class ScaledColorMapperTest {
	private static final double DELTA = 1e-15;
	private ScaledContinuousColorMapper cm;

	private static final class ScaledContinuousColorMapperMock extends ScaledContinuousColorMapper {
		/** Version id for serialization. */
		private static final long serialVersionUID = -3693380550336601398L;

		@Override
		public Paint get(double value) {
			double v = scale(value);
			v = applyMode(v, 0.0, 1.0);
			if (!MathUtils.isCalculatable(v)) {
				return null;
			}
			float i = (float) v;
			return new Color(i, i, i);
		}
	}

	@Before
	public void setUp() {
		cm = new ScaledContinuousColorMapperMock();
	}

	@Test
	public void testOffset() {
		assertEquals(0.0, cm.getOffset(), DELTA);
		cm.setOffset(42.0);
		assertEquals(42.0, cm.getOffset(), DELTA);
	}

	@Test
	public void testScale() {
		assertEquals(1.0, cm.getScale(), DELTA);
		cm.setScale(42.0);
		assertEquals(42.0, cm.getScale(), DELTA);
	}

	private static void assertColor(double expected, Paint p) {
		Color c = (Color) p;
		int e = (int) MathUtils.limit(expected*255.0 + 0.5, 0, 255);
		assertEquals(e, c.getRed());
		assertEquals(e, c.getGreen());
		assertEquals(e, c.getBlue());
	}

	@Test
	public void testScaleOp() {
		for (double x=0.0; x<=1.0; x+=0.5) {
			assertColor(x, cm.get(x));
		}

		cm.setRange(0.25, 0.75);
		for (double x=0.0; x<=1.0; x+=0.5) {
			assertColor((x - 0.25)/0.5, cm.get(x));
		}
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		ScaledContinuousColorMapper original = cm;
		ScaledContinuousColorMapper deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(original.getMode(), deserialized.getMode());
		assertEquals(original.getOffset(), deserialized.getOffset(), DELTA);
		assertEquals(original.getScale(), deserialized.getScale(), DELTA);
		for (double x=0.0; x<=1.0; x+=0.5) {
			assertEquals(original.get(x), deserialized.get(x));
		}
    }
}

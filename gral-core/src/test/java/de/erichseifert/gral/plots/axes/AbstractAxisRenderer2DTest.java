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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;

public class AbstractAxisRenderer2DTest {
	private static final double DELTA = 1e-10;
	private AbstractAxisRenderer2D renderer;

	private static class MockAbstractAxisRenderer2D extends AbstractAxisRenderer2D {
		@Override
		public double worldToView(Axis axis, Number value, boolean extrapolate) {
			return value.doubleValue();
		}

		@Override
		public Number viewToWorld(Axis axis, double value, boolean extrapolate) {
			return value;
		}

		@Override
		protected void createTicks(List<Tick> ticks, Axis axis, double min,
				double max, Set<Double> tickPositions, boolean isAutoSpacing) {
		}
	}

	@Before
	public void setUp() {
		renderer = new MockAbstractAxisRenderer2D();
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		AbstractAxisRenderer2D original = renderer;
		AbstractAxisRenderer2D deserialized = TestUtils.serializeAndDeserialize(original);
		assertEquals(original.getIntersection(), deserialized.getIntersection());
		TestUtils.assertEquals(original.getShape(), deserialized.getShape());
		assertEquals(original.isShapeVisible(), deserialized.isShapeVisible());
		assertEquals(original.isShapeNormalOrientationClockwise(), deserialized.isShapeNormalOrientationClockwise());
		assertEquals(original.getShapeColor(), deserialized.getShapeColor());
		assertEquals(original.getShapeStroke(), deserialized.getShapeStroke());
		assertEquals(original.isShapeDirectionSwapped(), deserialized.isShapeDirectionSwapped());

		assertEquals(original.isTicksVisible(), deserialized.isTicksVisible());
		assertEquals(original.getTickSpacing(), deserialized.getTickSpacing());
		assertEquals(original.isTicksAutoSpaced(), deserialized.isTicksAutoSpaced());
		assertEquals(original.getTickLength(), deserialized.getTickLength(), DELTA);
		assertEquals(original.getTickStroke(), deserialized.getTickStroke());
		assertEquals(original.getTickAlignment(), deserialized.getTickAlignment(), DELTA);
		assertEquals(original.getTickFont(), deserialized.getTickFont());
		assertEquals(original.getTickColor(), deserialized.getTickColor());
		assertEquals(original.isTickLabelsVisible(), deserialized.isTickLabelsVisible());
		assertEquals(original.getTickLabelFormat(), deserialized.getTickLabelFormat());
		assertEquals(original.getTickLabelDistance(), deserialized.getTickLabelDistance(), DELTA);
		assertEquals(original.isTickLabelsOutside(), deserialized.isTickLabelsOutside());
		assertEquals(original.getTickLabelRotation(), deserialized.getTickLabelRotation(), DELTA);

		assertEquals(original.isMinorTicksVisible(), deserialized.isMinorTicksVisible());
		assertEquals(original.getMinorTicksCount(), deserialized.getMinorTicksCount());
		assertEquals(original.getMinorTickLength(), deserialized.getMinorTickLength(), DELTA);
		assertEquals(original.getMinorTickStroke(), deserialized.getMinorTickStroke());
		assertEquals(original.getMinorTickAlignment(), deserialized.getMinorTickAlignment(), DELTA);
		assertEquals(original.getMinorTickColor(), deserialized.getMinorTickColor());

		assertEquals(original.getCustomTicks(), deserialized.getCustomTicks());
		assertEquals(original.getLabel(), deserialized.getLabel());
		assertEquals(original.getLabelDistance(), deserialized.getLabelDistance(), DELTA);
    }
}

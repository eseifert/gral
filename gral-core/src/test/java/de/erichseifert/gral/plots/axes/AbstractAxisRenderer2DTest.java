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

import static org.junit.Assert.assertEquals;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.graphics.Label;

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
	public void testShapeProperties() {
		var shape = new Line2D.Double(0.0, 0.0, 1.0, 2.0);
		renderer.setIntersection(0.5);
		renderer.setShape(shape);
		renderer.setShapeVisible(false);
		renderer.setShapeNormalOrientationClockwise(true);
		renderer.setShapeColor(Color.RED);
		renderer.setShapeStroke(new BasicStroke(2.5f));
		renderer.setShapeDirectionSwapped(true);

		assertEquals(0.5, renderer.getIntersection());
		TestUtils.assertEquals(shape, renderer.getShape());
		assertEquals(false, renderer.isShapeVisible());
		assertEquals(true, renderer.isShapeNormalOrientationClockwise());
		assertEquals(Color.RED, renderer.getShapeColor());
		assertEquals(new BasicStroke(2.5f), renderer.getShapeStroke());
		assertEquals(true, renderer.isShapeDirectionSwapped());
	}

	@Test
	public void testTickProperties() {
		var font = Font.decode(null).deriveFont(13f);
		var format = new DecimalFormat("0.00");
		renderer.setTicksVisible(false);
		renderer.setTickSpacing(0.25);
		renderer.setTicksAutoSpaced(true);
		renderer.setTickLength(3.0);
		renderer.setTickStroke(new BasicStroke(1.5f));
		renderer.setTickAlignment(0.25);
		renderer.setTickFont(font);
		renderer.setTickColor(Color.BLUE);
		renderer.setTickLabelsVisible(false);
		renderer.setTickLabelFormat(format);
		renderer.setTickLabelDistance(2.0);
		renderer.setTickLabelsOutside(false);
		renderer.setTickLabelRotation(45.0);

		assertEquals(false, renderer.isTicksVisible());
		assertEquals(0.25, renderer.getTickSpacing());
		assertEquals(true, renderer.isTicksAutoSpaced());
		assertEquals(3.0, renderer.getTickLength(), DELTA);
		assertEquals(new BasicStroke(1.5f), renderer.getTickStroke());
		assertEquals(0.25, renderer.getTickAlignment(), DELTA);
		assertEquals(font, renderer.getTickFont());
		assertEquals(Color.BLUE, renderer.getTickColor());
		assertEquals(false, renderer.isTickLabelsVisible());
		assertEquals(format, renderer.getTickLabelFormat());
		assertEquals(2.0, renderer.getTickLabelDistance(), DELTA);
		assertEquals(false, renderer.isTickLabelsOutside());
		assertEquals(45.0, renderer.getTickLabelRotation(), DELTA);
	}

	@Test
	public void testMinorTickProperties() {
		renderer.setMinorTicksVisible(false);
		renderer.setMinorTicksCount(5);
		renderer.setMinorTickLength(1.5);
		renderer.setMinorTickStroke(new BasicStroke(0.5f));
		renderer.setMinorTickAlignment(0.75);
		renderer.setMinorTickColor(Color.GREEN);

		assertEquals(false, renderer.isMinorTicksVisible());
		assertEquals(5, renderer.getMinorTicksCount());
		assertEquals(1.5, renderer.getMinorTickLength(), DELTA);
		assertEquals(new BasicStroke(0.5f), renderer.getMinorTickStroke());
		assertEquals(0.75, renderer.getMinorTickAlignment(), DELTA);
		assertEquals(Color.GREEN, renderer.getMinorTickColor());
	}

	@Test
	public void testLabelProperties() {
		var customTicks = Collections.singletonMap(1.0, "one");
		var label = new Label("Axis");
		renderer.setCustomTicks(customTicks);
		renderer.setLabel(label);
		renderer.setLabelDistance(4.0);

		assertEquals(customTicks, renderer.getCustomTicks());
		assertEquals(label, renderer.getLabel());
		assertEquals(4.0, renderer.getLabelDistance(), DELTA);
	}
}

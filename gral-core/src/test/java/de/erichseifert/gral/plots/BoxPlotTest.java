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
package de.erichseifert.gral.plots;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static de.erichseifert.gral.TestUtils.assertNotEmpty;
import static de.erichseifert.gral.TestUtils.createTestImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.DummyData;
import de.erichseifert.gral.data.EnumeratedData;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.plots.axes.Axis;
import org.junit.Before;
import org.junit.Test;

public class BoxPlotTest {
	private static final double DELTA = TestUtils.DELTA;

	private DataSource data;
	private MockBoxPlot plot;

	private static final class MockBoxPlot extends BoxPlot {
		public boolean isDrawn;

		public MockBoxPlot(DataSource data) {
			super(data);
		}

		@Override
		public void draw(DrawingContext context) {
			super.draw(context);
			isDrawn = true;
		}
	}

	@Before
	public void setUp() {
		data = new EnumeratedData(new DummyData(5, 3, 1.0));
		plot = new MockBoxPlot(data);
	}

	@Test
	public void testDraw() {
		plot.getAxis(BarPlot.AXIS_X).setRange(-1.0, 3.0);
		plot.getAxis(BarPlot.AXIS_Y).setRange(-1.0, 2.0);
		BufferedImage image = createTestImage();
		plot.setBounds(0.0, 0.0, image.getWidth(), image.getHeight());
		var context = new DrawingContext((Graphics2D) image.getGraphics());
		plot.draw(context);
		assertTrue(plot.isDrawn);
		assertNotEmpty(image);
	}

	@Test
	public void testAddRemoveData() {
		plot.remove(data);
		assertEquals(0, plot.getData().size());
		plot.add(data);
		assertEquals(1, plot.getData().size());
		try {
			plot.add(data);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testAutoscaleOfConstantObservations() {
		// Every observation has the same value, so the statistics collapse
		// onto one point. The vertical axis must still have an extent.
		var observations = new DataTable(Double.class, Double.class);
		for (int i = 0; i < 16; i++) {
			observations.add(0.0, 0.0);
		}
		var constantPlot = new BoxPlot(BoxPlot.createBoxData(observations));

		Axis axisY = constantPlot.getAxis(BoxPlot.AXIS_Y);
		assertTrue("Vertical axis collapsed to a single point.",
			axisY.getMax().doubleValue() > axisY.getMin().doubleValue());
	}

	@Test
	public void testAutoscaleOfNonPositiveObservations() {
		// Double.MIN_VALUE is the smallest positive value, so using it as the
		// lower bound of a maximum let negative data produce a range that
		// ended just above zero.
		var observations = new DataTable(Double.class);
		for (double value : new double[] {-4.0, -3.0, -2.0, -1.0}) {
			observations.add(value);
		}
		var negativePlot = new BoxPlot(BoxPlot.createBoxData(observations));

		Axis axisY = negativePlot.getAxis(BoxPlot.AXIS_Y);
		assertTrue("Maximum " + axisY.getMax() + " is not near the data.",
			axisY.getMax().doubleValue() < 0.0);
	}
}

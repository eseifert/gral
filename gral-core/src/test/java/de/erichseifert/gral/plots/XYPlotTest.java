/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
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

import static de.erichseifert.gral.TestUtils.assertNotEmpty;
import static de.erichseifert.gral.TestUtils.createTestImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DummyData;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.plots.XYPlot.XYPlotArea2D;
import de.erichseifert.gral.util.Location;

public class XYPlotTest {
	private static final double DELTA = TestUtils.DELTA;

	private List<MockXYPlot> plots;

	private static class MockXYPlot extends XYPlot {
		/** Version id for serialization. */
		private static final long serialVersionUID = -4211015243684983841L;

		private boolean drawn;

		public MockXYPlot(DataSource... data) {
			super(data);
		}

		@Override
		public void draw(DrawingContext context) {
			super.draw(context);
			drawn = true;
		}

		public boolean isDrawn() {
			return drawn;
		}
	};

	@Before
	public void setUp() {
		DataSource data = new DummyData(2, 2, 1.0);

		plots = new LinkedList<MockXYPlot>();
		MockXYPlot plot;

		// XYPlot with all options turned on
		plot = new MockXYPlot(data);
		plot.getTitle().setText("foobar");
		XYPlotArea2D plotArea = (XYPlotArea2D) plot.getPlotArea();
		plotArea.setMajorGridX(true);
		plotArea.setMajorGridY(true);
		plotArea.setMajorGridColor(Color.BLUE);
		plotArea.setMinorGridX(true);
		plotArea.setMinorGridY(true);
		plotArea.setMinorGridColor(Color.BLUE);
		plot.setLegendVisible(true);
		plot.setLegendLocation(Location.SOUTH);
		plot.setLegendDistance(2.0);
		plot.setBorderColor(Color.RED);
		plot.setBorderStroke(new BasicStroke(1.5f));
		plot.getAxisRenderer(XYPlot.AXIS_X).setTickSpacing(0.2);
		plot.getAxisRenderer(XYPlot.AXIS_Y).setTickSpacing(0.2);
		plots.add(plot);

		plot = new MockXYPlot(data);
		plot.getTitle().setText(null);
		((XYPlotArea2D) plot.getPlotArea()).setMajorGridX(false);
		((XYPlotArea2D) plot.getPlotArea()).setMinorGridX(false);
		plot.setLegendVisible(false);
		plot.setBorderColor(null);
		plot.getAxisRenderer(XYPlot.AXIS_X).setTickSpacing(0.0);
		plot.getAxisRenderer(XYPlot.AXIS_Y).setTickSpacing(0.0);
		plots.add(plot);
	}

	@Test
	public void testDraw() {
		for (MockXYPlot plot : plots) {
			BufferedImage image = createTestImage();
			plot.setBounds(0.0, 0.0, image.getWidth(), image.getHeight());
			DrawingContext context = new DrawingContext((Graphics2D) image.getGraphics());
			plot.draw(context);
			assertTrue(plot.isDrawn());
			assertNotEmpty(image);
		}
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		MockXYPlot original = plots.get(0);
		MockXYPlot deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(original.getBackground(), deserialized.getBackground());
		assertEquals(original.getBorderStroke(), deserialized.getBorderStroke());
		assertEquals(original.getBorderColor(), deserialized.getBorderColor());
		assertEquals(original.isLegendVisible(), deserialized.isLegendVisible());
		assertEquals(original.getLegendLocation(), deserialized.getLegendLocation());
		assertEquals(original.getLegendDistance(), deserialized.getLegendDistance(), DELTA);

		testPlotAreaSerialization(original.getPlotArea(), deserialized.getPlotArea());
	}

	private static void testPlotAreaSerialization(PlotArea originalPlotArea, PlotArea deserializedPlotArea) {
		XYPlotArea2D original = (XYPlotArea2D) originalPlotArea;
		XYPlotArea2D deserialized = (XYPlotArea2D) deserializedPlotArea;

		assertEquals(original.isMajorGridX(), deserialized.isMajorGridX());
		assertEquals(original.isMajorGridY(), deserialized.isMajorGridY());
		assertEquals(original.getMajorGridColor(), deserialized.getMajorGridColor());
		assertEquals(original.isMinorGridX(), deserialized.isMinorGridX());
		assertEquals(original.isMinorGridY(), deserialized.isMinorGridY());
		assertEquals(original.getMinorGridColor(), deserialized.getMinorGridColor());
	}
}

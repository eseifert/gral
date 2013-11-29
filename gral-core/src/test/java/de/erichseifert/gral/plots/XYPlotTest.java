/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael[at]erichseifert.de>
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

public class XYPlotTest {
	private static final double DELTA = 1e-7;

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
		plot.setSetting(XYPlot.XYPlotArea2D.GRID_MAJOR_X, true);
		plot.setSetting(XYPlot.XYPlotArea2D.GRID_MINOR_X, true);
		plot.setLegendVisible(true);
		plot.getAxisRenderer(XYPlot.AXIS_X).setTickSpacing(0.2);
		plot.getAxisRenderer(XYPlot.AXIS_Y).setTickSpacing(0.2);
		plots.add(plot);

		plot = new MockXYPlot(data);
		plot.getTitle().setText(null);
		plot.setSetting(XYPlot.XYPlotArea2D.GRID_MAJOR_X, false);
		plot.setSetting(XYPlot.XYPlotArea2D.GRID_MINOR_X, false);
		plot.setLegendVisible(false);
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

		TestUtils.assertSettings(original, deserialized);

		assertEquals(original.getBackground(), deserialized.getBackground());
		assertEquals(original.getBorder(), deserialized.getBorder());
		assertEquals(original.getColor(), deserialized.getColor());
		assertEquals(original.isLegendVisible(), deserialized.isLegendVisible());
		assertEquals(original.getLegendLocation(), deserialized.getLegendLocation());
		assertEquals(original.getLegendDistance(), deserialized.getLegendDistance(), DELTA);
	}
}

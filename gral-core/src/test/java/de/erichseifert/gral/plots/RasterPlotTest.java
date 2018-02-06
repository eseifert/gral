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
package de.erichseifert.gral.plots;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import static de.erichseifert.gral.TestUtils.assertNotEmpty;
import static de.erichseifert.gral.TestUtils.createTestImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DummyData;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.plots.RasterPlot.RasterRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import org.junit.Before;
import org.junit.Test;

public class RasterPlotTest {
	private static final double DELTA = TestUtils.DELTA;

	private DataSource data;
	private MockRasterPlot plot;

	private static final class MockRasterPlot extends RasterPlot {
		/** Version id for serialization. */
		private static final long serialVersionUID = 1043958957664771847L;

		public boolean isDrawn;

		public MockRasterPlot(DataSource data) {
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
		data = new DummyData(2, 12, 1.0);
		plot = new MockRasterPlot(data);
	}

	@Test
	public void testDraw() {
		plot.getAxis(BarPlot.AXIS_X).setRange(-1.0, 3.0);
		plot.getAxis(BarPlot.AXIS_Y).setRange(-1.0, 2.0);
		BufferedImage image = createTestImage();
		plot.setBounds(0.0, 0.0, image.getWidth(), image.getHeight());
		DrawingContext context = new DrawingContext((Graphics2D) image.getGraphics());
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
	public void testSerialization() throws IOException, ClassNotFoundException {
		RasterPlot original = plot;
		RasterPlot deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(original.getBackground(), deserialized.getBackground());
		assertEquals(original.getBorderStroke(), deserialized.getBorderStroke());
		assertEquals(original.getBorderColor(), deserialized.getBorderColor());
		assertEquals(original.isLegendVisible(), deserialized.isLegendVisible());
		assertEquals(original.getLegendLocation(), deserialized.getLegendLocation());
		assertEquals(original.getLegendDistance(), deserialized.getLegendDistance(), DELTA);

		assertEquals(original.getOffset(), deserialized.getOffset());
		assertEquals(original.getDistance(), deserialized.getDistance());
		assertEquals(original.getColors(), deserialized.getColors());

		List<DataSource> dataSourcesOriginal = original.getData();
		List<DataSource> dataSourcesDeserialized = deserialized.getData();
		assertEquals(dataSourcesOriginal.size(), dataSourcesDeserialized.size());
		for (int index = 0; index < dataSourcesOriginal.size(); index++) {
			List<PointRenderer> pointRenderersOriginal = original.getPointRenderers(
					dataSourcesOriginal.get(index));
			List<PointRenderer> pointRenderersDeserialized = deserialized.getPointRenderers(
					dataSourcesDeserialized.get(index));
			testPointRendererSerialization(pointRenderersOriginal, pointRenderersDeserialized);
		}
    }

	private static void testPointRendererSerialization(
			List<PointRenderer> originalRenderers, List<PointRenderer> deserializedRenderers) {
		for (int rendererIndex = 0; rendererIndex < originalRenderers.size(); rendererIndex++) {
			RasterRenderer original = (RasterRenderer) originalRenderers.get(rendererIndex);
			RasterRenderer deserialized = (RasterRenderer) deserializedRenderers.get(rendererIndex);
			assertEquals(original.getXColumn(), deserialized.getXColumn());
			assertEquals(original.getYColumn(), deserialized.getYColumn());
			assertEquals(original.getValueColumn(), deserialized.getValueColumn());
		}
	}
}

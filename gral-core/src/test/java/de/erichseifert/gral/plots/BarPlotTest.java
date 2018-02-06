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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DummyData;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.plots.BarPlot.BarRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import org.junit.Before;
import org.junit.Test;

public class BarPlotTest {
	private static final double DELTA = TestUtils.DELTA;

	private MockBarPlot plot;

	private static final class MockBarPlot extends BarPlot {
		/** Version id for serialization. */
		private static final long serialVersionUID = -6215127935611125964L;

		public boolean isDrawn;

		public MockBarPlot(DataSource... data) {
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
		DataSource data = new DummyData(2, 1, 1.0);
		plot = new MockBarPlot(data);

		BarRenderer pointRenderer = (BarRenderer) plot.getPointRenderers(data).get(0);
		pointRenderer.setBorderStroke(new BasicStroke());
	}

	@Test
	public void testDraw() {
		plot.getAxis(BarPlot.AXIS_X).setRange(-1.0, 3.0);
		plot.getAxis(BarPlot.AXIS_Y).setRange(-1.0, 2.0);
		BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);
		plot.setBounds(0.0, 0.0, image.getWidth(), image.getHeight());
		DrawingContext context = new DrawingContext((Graphics2D) image.getGraphics());
		plot.draw(context);
		assertTrue(plot.isDrawn);
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		BarPlot original = plot;
		BarPlot deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(original.getBackground(), deserialized.getBackground());
		assertEquals(original.getBorderStroke(), deserialized.getBorderStroke());
		assertEquals(original.getBorderColor(), deserialized.getBorderColor());
		assertEquals(original.isLegendVisible(), deserialized.isLegendVisible());
		assertEquals(original.getLegendLocation(), deserialized.getLegendLocation());
		assertEquals(original.getLegendDistance(), deserialized.getLegendDistance(), DELTA);

		assertEquals(original.getBarWidth(), deserialized.getBarWidth(), DELTA);
		assertEquals(original.getBarHeightMin(), deserialized.getBarHeightMin(), DELTA);
		assertEquals(original.isPaintAllBars(), deserialized.isPaintAllBars());

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
			BarRenderer original = (BarRenderer) originalRenderers.get(rendererIndex);
			BarRenderer deserialized = (BarRenderer) deserializedRenderers.get(rendererIndex);
			assertEquals(original.getBorderStroke(), deserialized.getBorderStroke());
			assertEquals(original.getBorderColor(), deserialized.getBorderColor());
		}
	}
}

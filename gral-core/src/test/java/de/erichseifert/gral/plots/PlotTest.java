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

import static de.erichseifert.gral.TestUtils.assertNotEmpty;
import static de.erichseifert.gral.TestUtils.createTestImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.graphics.Insets2D;

public class PlotTest {
	private static final double DELTA = 1e-15;
	private static DataTable table;
	private static DataSeries series1, series2;
	private Plot plot;

	private static class MockPlot extends AbstractPlot  {
		/** Version id for serialization. */
		private static final long serialVersionUID = -6303533550164303679L;

		public boolean drawn;

		public MockPlot(DataSource... data) {
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
	}

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setUpBeforeClass() {
		table = new DataTable(Integer.class, Integer.class, Integer.class);
		table.add(1, 3, 5); // 0
		table.add(2, 8, 2); // 1
		table.add(3, 5, 6); // 2
		table.add(4, 6, 2); // 3
		table.add(5, 4, 1); // 4
		table.add(6, 9, 5); // 5
		table.add(7, 8, 7); // 6
		table.add(8, 1, 9); // 7

		series1 = new DataSeries("series1", table, 0, 1);
		series2 = new DataSeries("series2", table, 1, 2);
	}

	@Before
	public void setUp() {
		plot = new MockPlot(series1, series2);
	}

	@Test
	public void testBounds() {
		Rectangle2D bounds = plot.getBounds();
		assertEquals(0.0, bounds.getX(), DELTA);
		assertEquals(0.0, bounds.getY(), DELTA);
		assertEquals(0.0, bounds.getWidth(), DELTA);
		assertEquals(0.0, bounds.getHeight(), DELTA);
		assertEquals(bounds.getX(), plot.getX(), DELTA);
		assertEquals(bounds.getY(), plot.getY(), DELTA);
		assertEquals(bounds.getWidth(), plot.getWidth(), DELTA);
		assertEquals(bounds.getHeight(), plot.getHeight(), DELTA);
	}

	@Test
	public void testInsets() {
		Insets2D insets = plot.getInsets();
		assertEquals(0.0, insets.getTop(), DELTA);
		assertEquals(0.0, insets.getLeft(), DELTA);
		assertEquals(0.0, insets.getBottom(), DELTA);
		assertEquals(0.0, insets.getRight(), DELTA);
	}

	@Test
	public void testTitle() {
		Label title = plot.getTitle();
		assertNotNull(title);
	}

	@Test
	public void testPlotArea() {
		PlotArea plotArea = plot.getPlotArea();
		assertNull(plotArea);
	}

	@Test
	public void testLegend() {
		// Get
		assertNull(plot.getLegend());
	}

	@Test
	public void testAxis() {
		// Get
		assertNull(plot.getAxis("a"));
		assertNull(plot.getAxis("b"));
		// Set
		Axis a = new Axis();
		Axis b = new Axis();
		a.setRange(0.0, 1.0);
		b.setRange(2.0, 3.0);
		plot.setAxis("a", a);
		plot.setAxis("b", b);
		assertEquals(a, plot.getAxis("a"));
		assertEquals(b, plot.getAxis("b"));
		// Remove
		plot.removeAxis("a");
		plot.setAxis("b", null);
		assertNull(plot.getAxis("a"));
		assertNull(plot.getAxis("b"));
	}

	@Test
	public void testDraw() {
		plot.getTitle().setText("foobar");
		plot.setBackground(Color.WHITE);
		plot.setBorderStroke(new BasicStroke(1f));

		BufferedImage image = createTestImage();
		plot.setBounds(0.0, 0.0, image.getWidth(), image.getHeight());
		DrawingContext context = new DrawingContext((Graphics2D) image.getGraphics());
		plot.draw(context);
		assertTrue(((MockPlot) plot).isDrawn());
		assertNotEmpty(image);
	}

	@Test
	public void testDataContains() {
		// Series
		assertTrue(plot.contains(series1));
		assertTrue(plot.contains(series2));

		// Complete data
		List<DataSource> data = plot.getData();
		assertEquals(2, data.size());
		assertEquals(series1, data.get(0));
		assertEquals(series2, data.get(1));
	}

	@Test
	public void testDataClear() {
		plot.clear();
		assertEquals(0, plot.getData().size());
	}

	@Test
	public void testDataGet() {
		assertEquals(series1, plot.get(0));
		assertEquals(series2, plot.get(1));
	}

	@Test
	public void testDataRemove() {
		int sizeBefore, size;

		// Remove
		sizeBefore = plot.getData().size();
		plot.remove(series1);
		size = plot.getData().size();
		assertEquals(sizeBefore -1, size);
		assertEquals(series2, plot.get(0));

		// Clear
		plot.clear();
		assertEquals(0, plot.getData().size());
	}

	@Test
	public void testDataAdd() {
		int sizeBefore, size;

		// Append
		DataSeries series3 = new DataSeries("series3", table, 0, 2);
		sizeBefore = plot.getData().size();
		plot.add(series3);
		size = plot.getData().size();
		assertEquals(sizeBefore + 1, size);
		assertEquals(series3, plot.get(size - 1));

		// Insert
		DataSeries series4 = new DataSeries("series4", table, 0);
		sizeBefore = plot.getData().size();
		plot.add(0, series4, false);
		size = plot.getData().size();
		assertEquals(sizeBefore + 1, size);
		assertEquals(series4, plot.get(0));
		assertFalse(plot.isVisible(series4));
	}

	@Test
	public void testDataVisibility() {
		// get
		assertTrue(plot.isVisible(series1));
		assertTrue(plot.isVisible(series2));

		// set
		plot.setVisible(series1, false);
		assertFalse(plot.isVisible(series1));
		plot.setVisible(series1, true);
		assertTrue(plot.isVisible(series1));

		// get all
		List<DataSource> all = plot.getData();
		List<DataSource> visible = plot.getVisibleData();
		assertEquals(all.size(), visible.size());
		for (int i = 0; i < all.size(); i++) {
			assertEquals(all.get(i), visible.get(i));
		}
		plot.setVisible(series1, false);
		assertEquals(visible.size() - 1, plot.getVisibleData().size());
		assertEquals(all.get(1), plot.getVisibleData().get(0));
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		Plot original = plot;
		Plot deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(original.getBackground(), deserialized.getBackground());
		assertEquals(original.getBorderStroke(), deserialized.getBorderStroke());
		assertEquals(original.getBorderColor(), deserialized.getBorderColor());
		assertEquals(original.isLegendVisible(), deserialized.isLegendVisible());
		assertEquals(original.getLegendLocation(), deserialized.getLegendLocation());
		assertEquals(original.getLegendDistance(), deserialized.getLegendDistance(), DELTA);
	}
}

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

import static de.erichseifert.gral.TestUtils.assertNotEmpty;
import static de.erichseifert.gral.TestUtils.createTestImage;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.data.AbstractDataSource;
import de.erichseifert.gral.data.Column;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.DummyData;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.plots.PiePlot.PieSliceRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import org.hamcrest.CoreMatchers;

public class PiePlotTest {
	private static final double DELTA = TestUtils.DELTA;

	private DataSource data;
	private MockPiePlot plot;

	private static final class MockPiePlot extends PiePlot {
		/** Version id for serialization. */
		private static final long serialVersionUID = -4466331273825538939L;

		public boolean isDrawn;

		public MockPiePlot(DataSource data) {
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
		data = PiePlot.createPieData(new DummyData(1, 3, 1.0));
		plot = new MockPiePlot(data);
	}

	@Test
	public void testDraw() {
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
	public void testCreatePieDataReplacesNumericColumnWithTwoDoubleAndOneIntegerColumns() {
		var data = new DataTable(Integer.class);

		DataSource pieData = PiePlot.createPieData(data);

		assertArrayEquals(new Class[] {Double.class, Double.class, Boolean.class}, pieData.getColumnTypes());
	}

	@Test
	public void testCreatePieDataContainsPieSliceRanges() {
		var data = new DataTable(Integer.class);
		data.add(1);
		data.add(1);
		data.add(1);

		DataSource pieData = PiePlot.createPieData(data);

		assertThat((Column<Double>) pieData.getColumn(0), CoreMatchers.hasItems(0.0, 1.0, 2.0));
		assertThat((Column<Double>) pieData.getColumn(1), CoreMatchers.hasItems(1.0, 2.0, 3.0));
	}

	@Test
	public void testCreatePieDatasBooleanColumnContainsFalseForEveryNegativeInputValue() {
		var data = new DataTable(Integer.class);
		data.add(2);
		data.add(-5);
		data.add(0);

		DataSource pieData = PiePlot.createPieData(data);

		Column<Boolean> visibilityColumn = (Column<Boolean>) pieData.getColumn(2);
		assertThat(visibilityColumn.get(1), is(false));
	}

	@Test
	public void testCreatePieDatasBooleanColumnContainsTrueForEveryPositiveInputValue() {
		var data = new DataTable(Integer.class);
		data.add(2);
		data.add(-5);
		data.add(0);

		DataSource pieData = PiePlot.createPieData(data);

		Column<Boolean> visibilityColumn = (Column<Boolean>) pieData.getColumn(2);
		assertThat(visibilityColumn.get(0), is(true));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCreatePieDataAccumulatesAbsoluteValues() {
		var data = new DataTable(Integer.class);
		data.add(1);
		data.add(-2);
		data.add(3);

		DataSource pieData = PiePlot.createPieData(data);

		assertThat((Column<Double>) pieData.getColumn(0), CoreMatchers.hasItems(0.0, 1.0, 3.0));
		assertThat((Column<Double>) pieData.getColumn(1), CoreMatchers.hasItems(1.0, 3.0, 6.0));
	}

	@Test
	public void testCreatePieDataSlicesDoNotOverlap() {
		var data = new DataTable(Integer.class);
		data.add(1);
		data.add(-2);
		data.add(3);

		DataSource pieData = PiePlot.createPieData(data);

		// Each slice has to start where the previous one ended and the last
		// slice has to end at the sum of all slice widths
		double sliceWidthSum = 0.0;
		for (int rowIndex = 0; rowIndex < pieData.getRowCount(); rowIndex++) {
			double sliceStart = (Double) pieData.get(0, rowIndex);
			double sliceEnd = (Double) pieData.get(1, rowIndex);
			assertEquals(sliceWidthSum, sliceStart, DELTA);
			assertTrue("Slice " + rowIndex + " has a negative width.",
				sliceEnd >= sliceStart);
			sliceWidthSum += sliceEnd - sliceStart;
		}
		assertEquals(sliceWidthSum,
			(Double) pieData.get(1, pieData.getRowCount() - 1), DELTA);
	}

	/**
	 * A data source that counts how often its values are read.
	 */
	private static final class CountingDataSource extends AbstractDataSource {
		/** Version id for serialization. */
		private static final long serialVersionUID = 1L;

		private final DataSource data;
		private int readCount;

		@SuppressWarnings("unchecked")
		public CountingDataSource(DataSource data) {
			super(data.getColumnTypes());
			this.data = data;
		}

		@Override
		public Comparable<?> get(int col, int row) {
			readCount++;
			return data.get(col, row);
		}

		@Override
		public int getRowCount() {
			return data.getRowCount();
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCreatePieDataReadsEachValueOnlyFewTimes() {
		int rowCount = 100;
		var data = new DataTable(Integer.class);
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			data.add(1);
		}
		var countingData = new CountingDataSource(data);
		DataSource pieData = PiePlot.createPieData(countingData);

		countingData.readCount = 0;
		for (int rowIndex = 0; rowIndex < pieData.getRowCount(); rowIndex++) {
			pieData.get(0, rowIndex);
			pieData.get(1, rowIndex);
			pieData.get(2, rowIndex);
		}

		// Accumulating the values for every single cell would need a number
		// of reads that grows with the square of the row count
		assertTrue("Read " + countingData.readCount + " values for " +
			rowCount + " rows.", countingData.readCount <= 4*rowCount);
	}

	@Test
	public void testCreatePieDataChangesWhenTheUnderlyingDataSourceChanges() {
		var data = new DataTable(Integer.class);
		data.add(2);
		DataSource pieData = PiePlot.createPieData(data);

		data.add(-5);
		data.add(0);

		assertThat(pieData.getRowCount(), is(data.getRowCount()));
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		PiePlot original = plot;
		PiePlot deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(original.getBackground(), deserialized.getBackground());
		assertEquals(original.getBorderStroke(), deserialized.getBorderStroke());
		assertEquals(original.getBorderColor(), deserialized.getBorderColor());
		assertEquals(original.isLegendVisible(), deserialized.isLegendVisible());
		assertEquals(original.getLegendLocation(), deserialized.getLegendLocation());
		assertEquals(original.getLegendDistance(), deserialized.getLegendDistance(), DELTA);

		assertEquals(original.getCenter(), deserialized.getCenter());
		assertEquals(original.getRadius(), deserialized.getRadius(), DELTA);
		assertEquals(original.getStart(), deserialized.getStart(), DELTA);
		assertEquals(original.isClockwise(), deserialized.isClockwise());

		List<DataSource> dataSourcesOriginal = original.getData();
		List<DataSource> dataSourcesDeserialized = deserialized.getData();
		assertEquals(dataSourcesOriginal.size(), dataSourcesDeserialized.size());
		for (int index = 0; index < dataSourcesOriginal.size(); index++) {
			PointRenderer pointRendererOriginal = original.getPointRenderer(
							dataSourcesOriginal.get(index));
			PointRenderer pointRendererDeserialized = deserialized.getPointRenderer(
							dataSourcesDeserialized.get(index));
			testPointRendererSerialization(pointRendererOriginal, pointRendererDeserialized);
		}
    }

	private static void testPointRendererSerialization(
			PointRenderer originalRenderer, PointRenderer deserializedRenderer) {
		PieSliceRenderer original = (PieSliceRenderer) originalRenderer;
		PieSliceRenderer deserialized = (PieSliceRenderer) deserializedRenderer;
		assertEquals(original.getInnerRadius(), deserialized.getInnerRadius(), DELTA);
		assertEquals(original.getOuterRadius(), deserialized.getOuterRadius(), DELTA);
		assertEquals(original.getGap(), deserialized.getGap(), DELTA);
	}
}

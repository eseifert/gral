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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.DummyData;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.graphics.Location;
import de.erichseifert.gral.plots.XYPlot.XYPlotArea2D;
import de.erichseifert.gral.plots.areas.AreaRenderer;
import de.erichseifert.gral.plots.areas.DefaultAreaRenderer2D;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.axes.LogarithmicRenderer2D;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.plots.points.DefaultPointRenderer2D;
import de.erichseifert.gral.plots.points.PointData;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.util.PointND;

public class XYPlotTest {
	private static final double DELTA = TestUtils.DELTA;

	private List<MockXYPlot> plots;

	private static class MockXYPlot extends XYPlot {
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
	}

	@Before
	public void setUp() {
		var data = new DummyData(2, 2, 1.0);

		plots = new LinkedList<>();
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
			var context = new DrawingContext((Graphics2D) image.getGraphics());
			plot.draw(context);
			assertTrue(plot.isDrawn());
			assertNotEmpty(image);
		}
	}

	@Test
	public void testGetPointRenderers() {
		var data = new DummyData(2, 1, 1.0);
		var plot = new MockXYPlot(data);
		var renderer1 = new DefaultPointRenderer2D();
		var renderer2 = new DefaultPointRenderer2D();
		plot.setPointRenderers(data, renderer1, renderer2);
		assertNotNull(plot.getPointRenderers(new DummyData(4, 2, 0.0)));
		assertNotNull(plot.getPointRenderers(null));

		List<PointRenderer> renderers = plot.getPointRenderers(data);
		assertTrue(renderers.contains(renderer1));
		assertTrue(renderers.contains(renderer2));
		assertEquals(renderers.size(), 2);
	}

	@Test
	public void testGetLineRenderers() {
		var data = new DummyData(2, 1, 1.0);
		var plot = new MockXYPlot(data);
		var renderer = new DefaultLineRenderer2D();
		plot.setLineRenderers(data, renderer);
		assertNotNull(plot.getLineRenderers(new DummyData(4, 2, 0.0)));
		assertNotNull(plot.getLineRenderers(null));

		List<LineRenderer> renderers = plot.getLineRenderers(data);
		assertTrue(renderers.contains(renderer));
		assertEquals(renderers.size(), 1);
	}

	@Test
	public void testSetLineRenderers() {
		var data = new DummyData(2, 1, 1.0);
		var plot = new MockXYPlot();
		var renderer1 = new DefaultLineRenderer2D();
		var renderer2 = new DefaultLineRenderer2D();
		plot.setLineRenderers(data, Arrays.asList(renderer1, renderer2));

		List<LineRenderer> renderers = plot.getLineRenderers(data);
		assertTrue(renderers.contains(renderer1));
		assertTrue(renderers.contains(renderer2));
		assertEquals(renderers.size(), 2);
	}

	@Test
	public void testGetAreaRenderers() {
		var data = new DummyData(2, 1, 1.0);
		var plot = new MockXYPlot(data);
		var renderer = new DefaultAreaRenderer2D();
		plot.setAreaRenderers(data, renderer);
		assertNotNull(plot.getAreaRenderers(new DummyData(4, 2, 0.0)));
		assertNotNull(plot.getAreaRenderers(null));

		List<AreaRenderer> renderers = plot.getAreaRenderers(data);
		assertTrue(renderers.contains(renderer));
		assertEquals(renderers.size(), 1);
	}

	@Test
	public void testSetAreaRenderers() {
		var data = new DummyData(2, 1, 1.0);
		var plot = new MockXYPlot();
		var renderer1 = new DefaultAreaRenderer2D();
		var renderer2 = new DefaultAreaRenderer2D();
		plot.setAreaRenderers(data, Arrays.asList(renderer1, renderer2));

		List<AreaRenderer> renderers = plot.getAreaRenderers(data);
		assertTrue(renderers.contains(renderer1));
		assertTrue(renderers.contains(renderer2));
		assertEquals(renderers.size(), 2);
	}

	@Test
	public void testPunch() {
		var plot = new XYPlot();
		Axis axisX = plot.getAxis(XYPlot.AXIS_X);
		Axis axisY = plot.getAxis(XYPlot.AXIS_Y);
		AxisRenderer axisRendererX = plot.getAxisRenderer(XYPlot.AXIS_X);
		AxisRenderer axisRendererY = plot.getAxisRenderer(XYPlot.AXIS_Y);
		var data = new PointData(
				Arrays.asList(axisX, axisY),
				Arrays.asList(axisRendererX, axisRendererY),
				null, 0, 0);


		var line = new Line2D.Double(-1.0, -1.0, 2.0, 2.0);
		List<DataPoint> points = Arrays.asList(
				new DataPoint(data, new PointND<>(0.0, 0.0)),
				new DataPoint(data, new PointND<>(1.0, 1.0))
		);

		XYPlotArea2D plotArea = (XYPlotArea2D) plot.getPlotArea();
		var punchShape = new Ellipse2D.Double(-0.25, -0.25, 0.50, 0.50);
		Shape punched = plotArea.punch(line, points, Arrays.asList(punchShape, punchShape), 1.0, false);
		assertNotSame(line, punched);
	}

	@Test
	public void testAddPointRenderer() {
		var data = new DummyData(2, 1, 1);
		var plot = new MockXYPlot();
		assertTrue(plot.getPointRenderers(data).isEmpty());

		var renderer = new DefaultPointRenderer2D();
		plot.addPointRenderer(data, renderer);
		List<PointRenderer> pointRenderers = plot.getPointRenderers(data);
		assertEquals(pointRenderers.size(), 1);
		assertEquals(pointRenderers.get(0), renderer);
	}

	@Test
	public void testRemovePointRenderer() {
		var data = new DummyData(2, 1, 1);
		var plot = new MockXYPlot();
		plot.removePointRenderer(null, null);
		plot.removePointRenderer(data, null);

		var renderer = new DefaultPointRenderer2D();
		plot.addPointRenderer(data, renderer);
		plot.removePointRenderer(data, renderer);
		assertTrue(plot.getPointRenderers(data).isEmpty());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testRemoveDataSourceUpdatesAxisRange() {
		var data1 = new DataTable(Double.class, Double.class);
		data1.add(1.0, 1.0);
		data1.add(2.0, 2.0);
		var data2 = new DataTable(Double.class, Double.class);
		data2.add(10.0, 20.0);

		var plot = new MockXYPlot(data1, data2);
		plot.remove(data2);
		plot.autoscaleAxis(XYPlot.AXIS_X);
		plot.autoscaleAxis(XYPlot.AXIS_Y);

		Axis axisX = plot.getAxis(XYPlot.AXIS_X);
		assertEquals(1.0, axisX.getMin().doubleValue(), DELTA);
		assertEquals(2.0, axisX.getMax().doubleValue(), DELTA);
		Axis axisY = plot.getAxis(XYPlot.AXIS_Y);
		assertEquals(1.0, axisY.getMin().doubleValue(), DELTA);
		assertEquals(2.0, axisY.getMax().doubleValue(), DELTA);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testClearUpdatesAxisRange() {
		var data = new DataTable(Double.class, Double.class);
		data.add(10.0, 20.0);

		var plot = new MockXYPlot(data);
		plot.clear();
		plot.autoscaleAxis(XYPlot.AXIS_X);

		Axis axisX = plot.getAxis(XYPlot.AXIS_X);
		assertEquals(0.0, axisX.getMin().doubleValue(), DELTA);
		assertEquals(0.0, axisX.getMax().doubleValue(), DELTA);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testHiddenDataSourceIsIgnoredByAutoscaling() {
		var data1 = new DataTable(Double.class, Double.class);
		data1.add(1.0, 1.0);
		data1.add(2.0, 2.0);
		var data2 = new DataTable(Double.class, Double.class);
		data2.add(10.0, 20.0);

		var plot = new MockXYPlot(data1, data2);
		plot.setVisible(data2, false);
		plot.autoscaleAxis(XYPlot.AXIS_X);
		plot.autoscaleAxis(XYPlot.AXIS_Y);

		Axis axisX = plot.getAxis(XYPlot.AXIS_X);
		assertEquals(2.0, axisX.getMax().doubleValue(), DELTA);
		Axis axisY = plot.getAxis(XYPlot.AXIS_Y);
		assertEquals(2.0, axisY.getMax().doubleValue(), DELTA);

		// Showing the data source again has to restore the old range
		plot.setVisible(data2, true);
		plot.autoscaleAxis(XYPlot.AXIS_X);
		plot.autoscaleAxis(XYPlot.AXIS_Y);

		assertEquals(10.0, axisX.getMax().doubleValue(), DELTA);
		assertEquals(20.0, axisY.getMax().doubleValue(), DELTA);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testNavigationUsesNewAxisRenderer() {
		var data = new DataTable(Double.class, Double.class);
		data.add(1.0, 1.0);
		data.add(8.0, 8.0);

		var plot = new MockXYPlot(data);
		drawPlot(plot);

		plot.setAxisRenderer(XYPlot.AXIS_X, new LogarithmicRenderer2D());
		drawPlot(plot);

		plot.getNavigator().setZoom(2.0);

		// On a logarithmic axis the zoomed range has to stay centered around
		// the geometric center of the original range
		Axis axisX = plot.getAxis(XYPlot.AXIS_X);
		assertEquals(Math.pow(8.0, 0.25), axisX.getMin().doubleValue(), 1e-9);
		assertEquals(Math.pow(8.0, 0.75), axisX.getMax().doubleValue(), 1e-9);
	}

	@Test
	public void testAutoscaleOfSinglePoint() {
		// A single data point maps every axis onto one value. The range must
		// still have an extent, or the conversion between world and view
		// coordinates is undefined and produces NaN.
		var data = new DataTable(Double.class, Double.class);
		data.add(2.0, 3.0);
		var plot = new XYPlot(data);

		for (String axisName : new String[] {XYPlot.AXIS_X, XYPlot.AXIS_Y}) {
			Axis axis = plot.getAxis(axisName);
			assertTrue("Axis " + axisName + " collapsed to a single point.",
				axis.getMax().doubleValue() > axis.getMin().doubleValue());
		}
	}

	private static void drawPlot(Plot plot) {
		BufferedImage image = createTestImage();
		plot.setBounds(0.0, 0.0, image.getWidth(), image.getHeight());
		plot.draw(new DrawingContext((Graphics2D) image.getGraphics()));
	}
}

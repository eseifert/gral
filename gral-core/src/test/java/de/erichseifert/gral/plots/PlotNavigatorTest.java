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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.util.PointND;

public class PlotNavigatorTest {
	private static final double DELTA = 1e-15;

	private static DataTable table;
	private static DataSeries series1, series2;

	private Plot plot;
	private PlotNavigator nav;

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
		plot = new XYPlot(series1, series2);
		plot.setBounds(0, 0, 100, 100);
		nav = new XYPlot.XYPlotNavigator((XYPlot) plot);
	}

	@Test
	public void testCreate() {
		// Valid initialization
		assertEquals(plot, nav.getPlot());
		assertEquals(1.0, nav.getZoom(), DELTA);
		assertEquals(PlotNavigator.DEFAULT_ZOOM_FACTOR, nav.getZoomFactor(), DELTA);
		assertEquals(PlotNavigator.DEFAULT_ZOOM_MIN, nav.getZoomMin(), DELTA);
		assertEquals(PlotNavigator.DEFAULT_ZOOM_MAX, nav.getZoomMax(), DELTA);
		Axis axisX = plot.getAxis(XYPlot.AXIS_X);
		Axis axisY = plot.getAxis(XYPlot.AXIS_Y);
		PointND<? extends Number> center = nav.getCenter();
		assertEquals(axisX.getMin().doubleValue() + 0.5*axisX.getRange(),
			center.get(0).doubleValue(), DELTA);
		assertEquals(axisY.getMin().doubleValue() + 0.5*axisY.getRange(),
			center.get(1).doubleValue(), DELTA);

		// Invalid initialization
		try {
			new XYPlot.XYPlotNavigator(null);
			fail("Expected NullPointerException.");
		} catch (NullPointerException e) {
		}
	}

	@Test
	public void testZoomLimits() {
		nav.setZoomMin(1e-3);
		nav.setZoomMax(1e+3);
		assertEquals(1e-3, nav.getZoomMin(), DELTA);
		assertEquals(1e+3, nav.getZoomMax(), DELTA);
	}

	@Test
	public void testZoom() {
		Axis axisX = plot.getAxis(XYPlot.AXIS_X);

		// Valid zoom
		nav.setZoom(2.0);
		assertEquals(2.0, nav.getZoom(), DELTA);
		assertEquals(3.0, axisX.getMin().doubleValue(), DELTA);
		assertEquals(7.0, axisX.getMax().doubleValue(), DELTA);

		// Negative zoom doesn't get set
		nav.setZoom(-1.0);
		assertEquals(2.0, nav.getZoom(), DELTA);
		// Too small zoom is limited to minimum
		nav.setZoom(nav.getZoomMin() / 2.0);
		assertEquals(nav.getZoomMin(), nav.getZoom(), DELTA);
		// Too large zoom is limited to maximum
		nav.setZoom(nav.getZoomMax() * 2.0);
		assertEquals(nav.getZoomMax(), nav.getZoom(), DELTA);
	}

	@Test
	public void testCenter() {
		nav.setCenter(new PointND<>(0.0, 0.0));
		assertEquals(-4.0, plot.getAxis(XYPlot.AXIS_X).getMin().doubleValue(), DELTA);
		assertEquals(4.0, plot.getAxis(XYPlot.AXIS_X).getMax().doubleValue(), DELTA);
	}

	@Test
	public void testReset() {
		nav.setZoom(2.0);
		nav.setCenter(new PointND<>(6.0, 0.0));
		nav.reset();
		assertEquals(1.0, plot.getAxis(XYPlot.AXIS_X).getMin().doubleValue(), DELTA);
		assertEquals(9.0, plot.getAxis(XYPlot.AXIS_X).getMax().doubleValue(), DELTA);
	}
}

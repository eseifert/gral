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
package de.erichseifert.gral.javafx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;

public class InteractiveCanvasTest {
	private XYPlot plot;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		JavaFxToolkit.start();
	}

	@Before
	public void setUp() {
		var data = new DataTable(Double.class, Double.class);
		for (double x = 0.0; x < 10.0; x += 0.25) {
			data.add(x, Math.sin(x));
		}
		plot = new XYPlot(new DataSeries("sin(x)", data, 0, 1));
	}

	/**
	 * Returns a double click at the specified position.
	 * @param x Horizontal position.
	 * @param y Vertical position.
	 * @return A mouse event describing the double click.
	 */
	private static MouseEvent doubleClick(double x, double y) {
		return new MouseEvent(MouseEvent.MOUSE_CLICKED, x, y, x, y,
			MouseButton.PRIMARY, 2,
			false, false, false, false, true, false, false, false, false, true, null);
	}

	@Test
	public void testCreation() throws Exception {
		InteractiveCanvas canvas = JavaFxToolkit.call(() -> new InteractiveCanvas(plot));
		assertTrue(canvas.isZoomable());
		assertTrue(canvas.isPannable());
	}

	@Test
	public void testDoubleClickZoomsIn() throws Exception {
		double zoom = plot.getNavigator().getZoom();
		JavaFxToolkit.call(() -> {
			var canvas = new InteractiveCanvas(plot);
			canvas.resize(320.0, 240.0);
			canvas.fireEvent(doubleClick(160.0, 120.0));
			return canvas;
		});
		assertTrue(plot.getNavigator().getZoom() > zoom);
	}

	@Test
	public void testDoubleClickDoesNothingWhenNotZoomable() throws Exception {
		double zoom = plot.getNavigator().getZoom();
		JavaFxToolkit.call(() -> {
			var canvas = new InteractiveCanvas(plot);
			canvas.setZoomable(false);
			canvas.resize(320.0, 240.0);
			canvas.fireEvent(doubleClick(160.0, 120.0));
			return canvas;
		});
		assertEquals(zoom, plot.getNavigator().getZoom(), TestUtils.DELTA);
	}
}

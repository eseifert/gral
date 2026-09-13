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
import javafx.scene.input.ScrollEvent;

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

	/**
	 * Returns the events that one notch of a mouse wheel produces at the
	 * specified position: the toolkit reports the notch as two events, the
	 * first of which carries no distance.
	 * @param x Horizontal position.
	 * @param y Vertical position.
	 * @param deltaY Distance of the notch, negative when scrolling down.
	 * @return The two scroll events of one notch, in order.
	 */
	private static ScrollEvent[] wheelNotch(double x, double y, double deltaY) {
		return new ScrollEvent[] {
			scroll(x, y, 0.0),
			scroll(x, y, deltaY)
		};
	}

	/**
	 * Returns a scroll event at the specified position.
	 * @param x Horizontal position.
	 * @param y Vertical position.
	 * @param deltaY Vertical scroll distance.
	 * @return A scroll event describing the gesture.
	 */
	private static ScrollEvent scroll(double x, double y, double deltaY) {
		return new ScrollEvent(ScrollEvent.SCROLL, x, y, x, y,
			false, false, false, false, false, false,
			0.0, deltaY, 0.0, deltaY,
			ScrollEvent.HorizontalTextScrollUnits.NONE, 0.0,
			ScrollEvent.VerticalTextScrollUnits.NONE, 0.0,
			0, null);
	}

	/**
	 * Sends the specified events to a new canvas showing the plot.
	 * @param events Events to send.
	 * @throws Exception when the JavaFX application thread fails.
	 */
	private void fire(ScrollEvent... events) throws Exception {
		JavaFxToolkit.call(() -> {
			var canvas = new InteractiveCanvas(plot);
			canvas.resize(320.0, 240.0);
			for (ScrollEvent event : events) {
				canvas.fireEvent(event);
			}
			return canvas;
		});
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
	public void testWheelNotchUpZoomsInOneStep() throws Exception {
		double zoom = plot.getNavigator().getZoom();
		double factor = plot.getNavigator().getZoomFactor();
		fire(wheelNotch(160.0, 120.0, 40.0));
		assertEquals(zoom*factor, plot.getNavigator().getZoom(), TestUtils.DELTA);
	}

	@Test
	public void testWheelNotchDownZoomsOutOneStep() throws Exception {
		double zoom = plot.getNavigator().getZoom();
		double factor = plot.getNavigator().getZoomFactor();
		fire(wheelNotch(160.0, 120.0, -40.0));
		assertEquals(zoom/factor, plot.getNavigator().getZoom(), TestUtils.DELTA);
	}

	@Test
	public void testSmoothScrollingAddsUpToOneStep() throws Exception {
		double zoom = plot.getNavigator().getZoom();
		double factor = plot.getNavigator().getZoomFactor();
		// A touch pad reports many small distances instead of whole notches.
		var events = new ScrollEvent[8];
		for (int i = 0; i < events.length; i++) {
			events[i] = scroll(160.0, 120.0, 5.0);
		}
		fire(events);
		assertEquals(zoom*factor, plot.getNavigator().getZoom(), TestUtils.DELTA);
	}

	@Test
	public void testScrollDoesNothingWhenNotZoomable() throws Exception {
		double zoom = plot.getNavigator().getZoom();
		JavaFxToolkit.call(() -> {
			var canvas = new InteractiveCanvas(plot);
			canvas.setZoomable(false);
			canvas.resize(320.0, 240.0);
			for (ScrollEvent event : wheelNotch(160.0, 120.0, 40.0)) {
				canvas.fireEvent(event);
			}
			return canvas;
		});
		assertEquals(zoom, plot.getNavigator().getZoom(), TestUtils.DELTA);
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

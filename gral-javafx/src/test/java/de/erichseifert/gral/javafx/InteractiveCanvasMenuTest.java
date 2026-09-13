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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;

public class InteractiveCanvasMenuTest {
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
	 * Returns a canvas showing the plot, sized as it would be on screen.
	 * @return A canvas on the JavaFX application thread.
	 * @throws Exception when the JavaFX application thread fails.
	 */
	private InteractiveCanvas createCanvas() throws Exception {
		return JavaFxToolkit.call(() -> {
			var canvas = new InteractiveCanvas(plot);
			canvas.resize(320.0, 240.0);
			return canvas;
		});
	}

	/**
	 * Returns the entries of the context menu that are not separators.
	 * @param canvas Canvas to read the menu of.
	 * @return The menu items.
	 * @throws Exception when the JavaFX application thread fails.
	 */
	private static List<MenuItem> getItems(InteractiveCanvas canvas) throws Exception {
		return JavaFxToolkit.call(() -> canvas.getContextMenu().getItems().stream()
			.filter(item -> !(item instanceof SeparatorMenuItem))
			.collect(Collectors.toList()));
	}

	@Test
	public void testMenuIsEnabledByDefault() throws Exception {
		InteractiveCanvas canvas = createCanvas();
		assertTrue(canvas.isContextMenuEnabled());
		canvas.setContextMenuEnabled(false);
		assertFalse(canvas.isContextMenuEnabled());
	}

	@Test
	public void testMenuEntries() throws Exception {
		List<MenuItem> items = getItems(createCanvas());
		assertEquals(5, items.size());
		for (MenuItem item : items) {
			assertNotNull(item.getText());
			// A key that is missing from the bundle comes back in exclamation marks.
			assertFalse(item.getText(), item.getText().startsWith("!"));
		}
	}

	@Test
	public void testZoomEntries() throws Exception {
		InteractiveCanvas canvas = createCanvas();
		List<MenuItem> items = getItems(canvas);
		double zoom = plot.getNavigator().getZoom();
		double factor = plot.getNavigator().getZoomFactor();

		JavaFxToolkit.call(() -> {
			items.get(0).fire();
			return null;
		});
		assertEquals(zoom*factor, plot.getNavigator().getZoom(), TestUtils.DELTA);

		JavaFxToolkit.call(() -> {
			items.get(1).fire();
			return null;
		});
		assertEquals(zoom, plot.getNavigator().getZoom(), TestUtils.DELTA);
	}

	@Test
	public void testResetViewEntry() throws Exception {
		InteractiveCanvas canvas = createCanvas();
		List<MenuItem> items = getItems(canvas);
		double zoom = plot.getNavigator().getZoom();

		JavaFxToolkit.call(() -> {
			items.get(0).fire();
			items.get(0).fire();
			return null;
		});
		assertTrue(plot.getNavigator().getZoom() > zoom);

		JavaFxToolkit.call(() -> {
			items.get(2).fire();
			return null;
		});
		assertEquals(zoom, plot.getNavigator().getZoom(), TestUtils.DELTA);
	}

	@Test
	public void testNavigationEntriesFollowTheCanvas() throws Exception {
		InteractiveCanvas canvas = JavaFxToolkit.call(() -> {
			var view = new InteractiveCanvas(plot);
			view.setZoomable(false);
			view.setPannable(false);
			view.resize(320.0, 240.0);
			return view;
		});
		List<MenuItem> items = getItems(canvas);
		assertTrue(items.get(0).isDisable());
		assertTrue(items.get(1).isDisable());
		assertTrue(items.get(2).isDisable());
		// Export and print do not depend on navigation.
		assertFalse(items.get(3).isDisable());
		assertFalse(items.get(4).isDisable());
	}

	@Test
	public void testMenuIsCached() throws Exception {
		InteractiveCanvas canvas = createCanvas();
		assertEquals(JavaFxToolkit.call(canvas::getContextMenu),
				JavaFxToolkit.call(canvas::getContextMenu));
	}
}

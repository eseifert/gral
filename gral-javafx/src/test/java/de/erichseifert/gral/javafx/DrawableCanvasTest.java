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

import static de.erichseifert.gral.TestUtils.assertNotEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;

public class DrawableCanvasTest {
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

	@Test
	public void testCreation() throws Exception {
		DrawableCanvas canvas = JavaFxToolkit.call(() -> new DrawableCanvas(plot));
		assertSame(plot, canvas.getDrawable());
		assertTrue(canvas.isResizable());
	}

	@Test
	public void testResize() throws Exception {
		DrawableCanvas canvas = JavaFxToolkit.call(() -> {
			var resized = new DrawableCanvas(plot);
			resized.resize(320.0, 240.0);
			return resized;
		});
		assertEquals(320.0, canvas.getWidth(), TestUtils.DELTA);
		assertEquals(240.0, canvas.getHeight(), TestUtils.DELTA);
		assertEquals(320.0, plot.getBounds().getWidth(), TestUtils.DELTA);
		assertEquals(240.0, plot.getBounds().getHeight(), TestUtils.DELTA);
	}

	@Test
	public void testDraw() throws Exception {
		WritableImage image = JavaFxToolkit.call(() -> {
			var canvas = new DrawableCanvas(plot);
			canvas.resize(320.0, 240.0);
			return canvas.snapshot(null, null);
		});
		assertNotEmpty(SwingFXUtils.fromFXImage(image, null));
	}

	@Test
	public void testPreferredSize() throws Exception {
		DrawableCanvas canvas = JavaFxToolkit.call(() -> new DrawableCanvas(plot));
		assertEquals(plot.getPreferredSize().getWidth(), canvas.prefWidth(-1.0), TestUtils.DELTA);
		assertEquals(plot.getPreferredSize().getHeight(), canvas.prefHeight(-1.0), TestUtils.DELTA);
	}
}

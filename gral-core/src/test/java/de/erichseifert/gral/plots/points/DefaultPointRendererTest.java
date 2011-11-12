/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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
package de.erichseifert.gral.plots.points;

import static de.erichseifert.gral.TestUtils.assertNonEmptyImage;
import static de.erichseifert.gral.TestUtils.createTestImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.DrawingContext;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.Row;
public class DefaultPointRendererTest {
	private static DataTable table;
	private static Row row;

	@BeforeClass
	public static void setUpBeforeClass() {
		table = new DataTable(Integer.class, Integer.class);
		table.add(1, 9); // 0
		table.add(2, 8); // 1
		table.add(3, 7); // 2
		table.add(4, 6); // 3
		table.add(5, 5); // 4
		table.add(6, 4); // 5
		table.add(7, 3); // 6
		table.add(8, 1); // 7

		row = new Row(table, 0);
	}

	private static void assertPointRenderer(PointRenderer r) {
		// Get point
		Drawable point = r.getPoint(null, null, row);
		assertNotNull(point);

		// Draw line
		BufferedImage image = createTestImage();
		DrawingContext context = new DrawingContext((Graphics2D) image.getGraphics());
		point.draw(context);
		assertNonEmptyImage(image);
	}

	@Test
	public void testPoint() {
		PointRenderer r = new DefaultPointRenderer();
		assertPointRenderer(r);
	}

	@Test
	public void testDisplayValue() {
		PointRenderer r = new DefaultPointRenderer();
		r.setSetting(PointRenderer.VALUE_DISPLAYED, true);
		assertPointRenderer(r);
	}

	@Test
	public void testDisplayError() {
		PointRenderer r = new DefaultPointRenderer();
		r.setSetting(PointRenderer.ERROR_DISPLAYED, true);
		assertPointRenderer(r);
	}

	@Test
	public void testSettings() {
		// Get
		PointRenderer r = new DefaultPointRenderer();
		assertEquals(Color.BLACK, r.getSetting(PointRenderer.COLOR));
		// Set
		r.setSetting(PointRenderer.COLOR, Color.RED);
		assertEquals(Color.RED, r.getSetting(PointRenderer.COLOR));
		// Remove
		r.removeSetting(PointRenderer.COLOR);
		assertEquals(Color.BLACK, r.getSetting(PointRenderer.COLOR));
	}

}

/**
 * GRAL: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.Row;

public class DefaultPointRendererTest {
	private static DataTable table;
	private static Row row;

	@BeforeClass
	public static void setUpBeforeClass() {
		table = new DataTable(Integer.class);
		table.add(1); // 0
		table.add(2); // 1
		table.add(3); // 2
		table.add(4); // 3
		table.add(5); // 4
		table.add(6); // 5
		table.add(7); // 6
		table.add(8); // 7

		row = new Row(table, 0);
	}

	@Test
	public void testPoint() {
		// Get line
		PointRenderer r = new DefaultPointRenderer();
		Drawable point = r.getPoint(row, null, null);
		assertNotNull(point);

		// Draw line
		BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		point.draw(g2d);
	}

	@Test
	public void testSettings() {
		// Get
		PointRenderer r = new DefaultPointRenderer();
		assertEquals(Color.BLACK, r.getSetting(PointRenderer.KEY_COLOR));
		// Set
		r.setSetting(PointRenderer.KEY_COLOR, Color.RED);
		assertEquals(Color.RED, r.getSetting(PointRenderer.KEY_COLOR));
		// Remove
		r.removeSetting(PointRenderer.KEY_COLOR);
		assertEquals(Color.BLACK, r.getSetting(PointRenderer.KEY_COLOR));
	}

}

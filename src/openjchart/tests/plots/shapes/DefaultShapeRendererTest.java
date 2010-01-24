/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.tests.plots.shapes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import openjchart.Drawable;
import openjchart.data.DataTable;
import openjchart.data.Row;
import openjchart.plots.shapes.DefaultShapeRenderer;
import openjchart.plots.shapes.ShapeRenderer;

import org.junit.BeforeClass;
import org.junit.Test;

public class DefaultShapeRendererTest {
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
		ShapeRenderer r = new DefaultShapeRenderer();
		Drawable point = r.getShape(row);
		assertNotNull(point);

		// Draw line
		BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		point.draw(g2d);
	}

	@Test
	public void testSettings() {
		// Get
		ShapeRenderer r = new DefaultShapeRenderer();
		assertEquals(Color.BLACK, r.getSetting(ShapeRenderer.KEY_COLOR));
		// Set
		r.setSetting(ShapeRenderer.KEY_COLOR, Color.RED);
		assertEquals(Color.RED, r.getSetting(ShapeRenderer.KEY_COLOR));
		// Remove
		r.removeSetting(ShapeRenderer.KEY_COLOR);
		assertEquals(Color.BLACK, r.getSetting(ShapeRenderer.KEY_COLOR));
	}

}

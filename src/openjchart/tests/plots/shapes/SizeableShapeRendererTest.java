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

import java.awt.Shape;

import openjchart.data.DataTable;
import openjchart.data.Row;
import openjchart.plots.shapes.ShapeRenderer;
import openjchart.plots.shapes.SizeableShapeRenderer;

import org.junit.BeforeClass;
import org.junit.Test;

public class SizeableShapeRendererTest {
	private static DataTable table;
	private static Row row;

	@BeforeClass
	public static void setUpBeforeClass() {
		table = new DataTable(Integer.class, Integer.class, Integer.class);
		table.add(1, 0, 1); // 0
		table.add(2, 0, 1); // 1
		table.add(3, 0, 1); // 2
		table.add(4, 0, 1); // 3
		table.add(5, 0, 1); // 4
		table.add(6, 0, 1); // 5
		table.add(7, 0, 1); // 6
		table.add(8, 0, 1); // 7

		row = new Row(table, 0);
	}

	@Test
	public void testShapePath() {
		// Get line
		ShapeRenderer r = new SizeableShapeRenderer();
		Shape expected = r.<Shape>getSetting(ShapeRenderer.KEY_SHAPE);
		Shape path = r.getShapePath(row);
		assertEquals(expected.getBounds2D(), path.getBounds2D());
	}

}

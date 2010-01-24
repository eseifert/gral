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

package openjchart.tests.data;

import static org.junit.Assert.assertEquals;
import openjchart.data.DummyData;

import org.junit.Test;

public class DummyDataTest {
	private static final double DELTA = 1e-15;

	@Test
	public void testCreation() {
		DummyData data = new DummyData(1, 1, 1.0);
		assertEquals(1, data.getColumnCount());
		assertEquals(1, data.getRowCount());
		assertEquals(1.0, data.get(0, 0).doubleValue(), DELTA);
	}

}

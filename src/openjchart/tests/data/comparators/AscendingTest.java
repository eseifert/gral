/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
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

package openjchart.tests.data.comparators;

import static org.junit.Assert.assertEquals;
import openjchart.data.comparators.Ascending;
import openjchart.data.comparators.DataComparator;

import org.junit.Test;

public class AscendingTest {
	@Test
	public void testCompare() {
		Number[] row1 = { 1.0, 2.0, 3.0 };
		Number[] row2 = { 2.0, 2.0, 2.0 };

		DataComparator comparator1 = new Ascending(0);
		DataComparator comparator2 = new Ascending(1);
		DataComparator comparator3 = new Ascending(2);

		assertEquals(-1, comparator1.compare(row1, row2));
		assertEquals( 0, comparator2.compare(row1, row2));
		assertEquals( 1, comparator3.compare(row1, row2));
	}

}
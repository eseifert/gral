/*
 * GRAL: GRAphing Library for Java(R)
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

package de.erichseifert.gral.data.comparators;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ComparatorTest {

	@Test
	public void testColumn() {
		DataComparator comparator = new Ascending(0);
		assertEquals(0, comparator.getColumn());

		comparator.setColumn(1);
		assertEquals(1, comparator.getColumn());

		comparator.setColumn(2);
		assertEquals(2, comparator.getColumn());
	}

	@Test
	public void testAscending() {
		Number[] row1 = { 1.0, 2.0, 3.0 };
		Number[] row2 = { 2.0, 2.0, 2.0 };

		DataComparator comparator1 = new Ascending(0);
		DataComparator comparator2 = new Ascending(1);
		DataComparator comparator3 = new Ascending(2);

		assertEquals(-1, comparator1.compare(row1, row2));
		assertEquals( 0, comparator2.compare(row1, row2));
		assertEquals( 1, comparator3.compare(row1, row2));

		assertEquals(0, comparator1.compare(row1, row1));
		assertEquals(0, comparator2.compare(row1, row1));
		assertEquals(0, comparator3.compare(row1, row1));
	}

	@Test
	public void testDescending() {
		Number[] row1 = { 1.0, 2.0, 3.0 };
		Number[] row2 = { 2.0, 2.0, 2.0 };

		DataComparator comparator1 = new Descending(0);
		DataComparator comparator2 = new Descending(1);
		DataComparator comparator3 = new Descending(2);

		assertEquals( 1, comparator1.compare(row1, row2));
		assertEquals( 0, comparator2.compare(row1, row2));
		assertEquals(-1, comparator3.compare(row1, row2));

		assertEquals(0, comparator1.compare(row1, row1));
		assertEquals(0, comparator2.compare(row1, row1));
		assertEquals(0, comparator3.compare(row1, row1));
	}

}

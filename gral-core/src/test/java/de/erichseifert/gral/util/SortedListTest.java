/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.gral.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import de.erichseifert.gral.TestUtils;
import org.junit.Test;

public class SortedListTest {
	public static final double DELTA = TestUtils.DELTA;

	@Test
	public void testCreation() {
		SortedList<Double> standard = new SortedList<>();
		assertEquals(0, standard.size());

		SortedList<Double> capacity = new SortedList<>(20);
		assertEquals(0, capacity.size());

		List<Double> data = Arrays.asList(0.0, 2.0, 1.0);
		SortedList<Double> collection = new SortedList<>(data);
		assertEquals(3, collection.size());
		assertEquals(0.0, collection.get(0), DELTA);
		assertEquals(1.0, collection.get(1), DELTA);
		assertEquals(2.0, collection.get(2), DELTA);
	}

	@Test
	public void testSize() {
		SortedList<Double> l = new SortedList<>();
		assertEquals(0, l.size());
		l.add(0.0);
		assertEquals(1, l.size());
		l.add(1.0);
		assertEquals(2, l.size());
		l.add(0.0);
		assertEquals(3, l.size());
	}

	@Test
	public void testAdd() {
		SortedList<Double> l = new SortedList<>();
		l.add(2.0);
		l.add(1.0);
		l.add(2.0);
		assertEquals(3, l.size());
		assertEquals(1.0, l.get(0), DELTA);
		assertEquals(2.0, l.get(1), DELTA);
		assertEquals(2.0, l.get(2), DELTA);
	}

	@Test
	public void testGet() {
		SortedList<Double> l = new SortedList<>();
		l.add(2.0);
		l.add(1.0);
		l.add(2.0);
		l.add(-1.0);
		assertEquals(-1.0, l.get(0), DELTA);
		assertEquals( 1.0, l.get(1), DELTA);
		assertEquals( 2.0, l.get(2), DELTA);
		assertEquals( 2.0, l.get(3), DELTA);
	}

	@Test
	public void testIndexOf() {
		SortedList<Double> l = new SortedList<>();
		l.add(0.0);
		l.add(0.0);
		l.add(1.0);
		assertEquals(1, l.indexOf(0.0));
		assertEquals(2, l.indexOf(1.0));
		assertEquals(-1, l.indexOf(-1.0));
		assertEquals(-1, l.indexOf(null));
		assertEquals(-4, l.indexOf(Double.NaN));
	}

	@Test
	public void testRemove() {
		SortedList<Double> l = new SortedList<>();
		l.add(0.0);
		l.add(0.0);
		l.add(1.0);
		assertEquals(3, l.size());
		l.remove(1);
		l.remove(1);
		assertEquals(1, l.size());
	}
}

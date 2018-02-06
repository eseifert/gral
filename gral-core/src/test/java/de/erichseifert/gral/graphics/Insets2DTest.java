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
package de.erichseifert.gral.graphics;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.erichseifert.gral.TestUtils;
import org.junit.Test;

public class Insets2DTest {
	public static final double DELTA = TestUtils.DELTA;

	@Test
	public void testCreate() {
		Insets2D insets;

		// Standard constructor
		insets = new Insets2D.Double();
		assertEquals(0.0, insets.getTop(), DELTA);
		assertEquals(0.0, insets.getLeft(), DELTA);
		assertEquals(0.0, insets.getBottom(), DELTA);
		assertEquals(0.0, insets.getRight(), DELTA);

		// Constructor with a single value for all sides
		insets = new Insets2D.Double(1.0);
		assertEquals(1.0, insets.getTop(), DELTA);
		assertEquals(1.0, insets.getLeft(), DELTA);
		assertEquals(1.0, insets.getBottom(), DELTA);
		assertEquals(1.0, insets.getRight(), DELTA);

		// Constructor with four values
		insets = new Insets2D.Double(1.0, 2.0, 3.0, 4.0);
		assertEquals(1.0, insets.getTop(), DELTA);
		assertEquals(2.0, insets.getLeft(), DELTA);
		assertEquals(3.0, insets.getBottom(), DELTA);
		assertEquals(4.0, insets.getRight(), DELTA);
	}

	@Test
	public void testChange() {
		Insets2D insets = new Insets2D.Double(1.0, 2.0, 3.0, 4.0);
		Insets2D insets2 = new Insets2D.Double(10.0, 20.0, 30.0, 40.0);

		// setInsets(Insets2D)
		insets.setInsets(insets2);
		assertEquals(insets2.getTop(), insets.getTop(), DELTA);
		assertEquals(insets2.getLeft(), insets.getLeft(), DELTA);
		assertEquals(insets2.getBottom(), insets.getBottom(), DELTA);
		assertEquals(insets2.getRight(), insets.getRight(), DELTA);

		insets.setInsets(null);
		assertEquals(insets2.getTop(), insets.getTop(), DELTA);
		assertEquals(insets2.getLeft(), insets.getLeft(), DELTA);
		assertEquals(insets2.getBottom(), insets.getBottom(), DELTA);
		assertEquals(insets2.getRight(), insets.getRight(), DELTA);

		// setSize(double, double, double, double)
		insets.setInsets(5.0, 6.0, 7.0, 8.0);
		assertEquals(5.0, insets.getTop(), DELTA);
		assertEquals(6.0, insets.getLeft(), DELTA);
		assertEquals(7.0, insets.getBottom(), DELTA);
		assertEquals(8.0, insets.getRight(), DELTA);
	}

	@Test
	public void testToString() {
		Insets2D insets = new Insets2D.Double(1.0, 2.0, 3.0, 4.0);
		assertEquals(insets.getClass().getName() + "[top=1.000000, left=2.000000, bottom=3.000000, right=4.000000]", insets.toString());
	}

	@Test
	public void testEquality() {
		Insets2D insets1 = new Insets2D.Double(1.0, 2.0, 3.0, 4.0);
		Insets2D insets2 = new Insets2D.Double(1.0, 2.0, 3.0, 4.0);
		// Equals
		assertTrue(insets1.equals(insets2));
		assertFalse(insets1.equals(null));
		assertFalse(insets2.equals(null));
		// Hash code
		assertEquals(insets1.hashCode(), insets2.hashCode());
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		Insets2D original = new Insets2D.Double(1.0, 2.0, 3.0, 4.0);
		Insets2D deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(original.getTop(), deserialized.getTop(), DELTA);
		assertEquals(original.getLeft(), deserialized.getLeft(), DELTA);
		assertEquals(original.getBottom(), deserialized.getBottom(), DELTA);
		assertEquals(original.getRight(), deserialized.getRight(), DELTA);
	}
}

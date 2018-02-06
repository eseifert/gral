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

import java.awt.geom.Dimension2D;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.erichseifert.gral.TestUtils;
import org.junit.Test;

public class Dimension2DTest {
	public static final double DELTA = 1e-15;

	@Test
	public void testCreate() {
		Dimension2D dim;

		// Standard constructor
		dim = new de.erichseifert.gral.graphics.Dimension2D.Double();
		assertEquals(0.0, dim.getWidth(), DELTA);
		assertEquals(0.0, dim.getHeight(), DELTA);

		// Constructor with width and height
		dim = new de.erichseifert.gral.graphics.Dimension2D.Double(1.0, 2.0);
		assertEquals(1.0, dim.getWidth(), DELTA);
		assertEquals(2.0, dim.getHeight(), DELTA);
	}

	@Test
	public void testChange() {
		Dimension2D dim = new de.erichseifert.gral.graphics.Dimension2D.Double(1.0, 2.0);
		Dimension2D dim2 = new de.erichseifert.gral.graphics.Dimension2D.Double(3.0, 4.0);

		// setSize(Dimension2D)
		dim.setSize(dim2);
		assertEquals(dim2.getWidth(), dim.getWidth(), DELTA);
		assertEquals(dim2.getHeight(), dim.getHeight(), DELTA);

		// setSize(double, double)
		dim.setSize(5.0, 6.0);
		assertEquals(5.0, dim.getWidth(), DELTA);
		assertEquals(6.0, dim.getHeight(), DELTA);
	}

	@Test
	public void testToString() {
		Dimension2D dim = new de.erichseifert.gral.graphics.Dimension2D.Double(1.0, 2.0);
		assertEquals(dim.getClass().getName() + "[width=1.000000, height=2.000000]", dim.toString());
	}

	@Test
	public void testEquality() {
		Dimension2D dim1 = new de.erichseifert.gral.graphics.Dimension2D.Double(1.0, 2.0);
		Dimension2D dim2 = new de.erichseifert.gral.graphics.Dimension2D.Double(1.0, 2.0);
		// Equals
		assertTrue(dim1.equals(dim2));
		assertFalse(dim1.equals(null));
		assertFalse(dim2.equals(null));
		// Hash code
		assertEquals(dim1.hashCode(), dim2.hashCode());
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		Dimension2D original = new de.erichseifert.gral.graphics.Dimension2D.Double(1.2, 3.4);
		Dimension2D deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(original.getWidth(), deserialized.getWidth(), DELTA);
		assertEquals(original.getHeight(), deserialized.getHeight(), DELTA);
	}
}

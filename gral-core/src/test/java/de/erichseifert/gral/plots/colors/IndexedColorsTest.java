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
package de.erichseifert.gral.plots.colors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.awt.Color;
import java.io.IOException;

import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.plots.colors.ColorMapper.Mode;

public class IndexedColorsTest {
	@Test
	public void testCreate() {
		IndexedColors c = new IndexedColors(Color.RED, Color.GREEN, Color.BLUE);
		assertEquals(3, c.getColors().size());
	}

	@Test
	public void testColor() {
		IndexedColors c = new IndexedColors(Color.RED, Color.GREEN, Color.BLUE);
		assertEquals(3, c.getColors().size());
	}

	@Test
	public void testGetOmit() {
		IndexedColors c = new IndexedColors(Color.RED, Color.GREEN, Color.BLUE);
		c.setMode(Mode.OMIT);
		assertNull(c.get(-1));
		assertEquals(Color.RED,   c.get(0));
		assertEquals(Color.GREEN, c.get(1));
		assertEquals(Color.BLUE,  c.get(2));
		assertNull(c.get(3));
	}

	@Test
	public void testGetRepeat() {
		IndexedColors c = new IndexedColors(Color.RED, Color.GREEN, Color.BLUE);
		c.setMode(Mode.REPEAT);
		assertEquals(Color.RED,   c.get(-1));
		assertEquals(Color.RED,   c.get(0));
		assertEquals(Color.GREEN, c.get(1));
		assertEquals(Color.BLUE,  c.get(2));
		assertEquals(Color.BLUE,  c.get(3));
	}

	@Test
	public void testGetCircular() {
		IndexedColors c = new IndexedColors(Color.RED, Color.GREEN, Color.BLUE);
		c.setMode(Mode.CIRCULAR);
		assertEquals(Color.BLUE,  c.get(-1));
		assertEquals(Color.RED,   c.get(0));
		assertEquals(Color.GREEN, c.get(1));
		assertEquals(Color.BLUE,  c.get(2));
		assertEquals(Color.RED,   c.get(3));
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		IndexedColors original = new IndexedColors(Color.RED, Color.GREEN, Color.BLUE);
		IndexedColors deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(original.getMode(), deserialized.getMode());
		assertEquals(original.getColors().size(), deserialized.getColors().size());
		for (int i = -1; i <= 3; i++) {
			assertEquals(original.get(i), deserialized.get(i));
		}
    }
}

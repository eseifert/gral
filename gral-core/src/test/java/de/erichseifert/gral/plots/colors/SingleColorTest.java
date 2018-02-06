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

import java.awt.Color;
import java.io.IOException;

import org.junit.Test;

import de.erichseifert.gral.TestUtils;

public class SingleColorTest {

	@Test
	public void testCreate() {
		SingleColor c = new SingleColor(Color.WHITE);
		assertEquals(Color.WHITE, c.getColor());
	}

	@Test
	public void testColor() {
		SingleColor c = new SingleColor(Color.BLUE);
		// Get
		assertEquals(Color.BLUE, c.getColor());
		// Set
		c.setColor(Color.RED);
		assertEquals(Color.RED, c.getColor());
	}

	@Test
	public void testGet() {
		SingleColor c = new SingleColor(Color.BLUE);
		for (int i = 0; i <= 10; i++) {
			assertEquals(Color.BLUE, c.get(i));
		}
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		SingleColor original = new SingleColor(new Color(0.12f, 0.34f, 0.56f, 0.78f));
		SingleColor deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(original.getColor(), deserialized.getColor());
		for (int i = 0; i < 5; i++) {
			assertEquals(original.get(i), deserialized.get(i));
		}
    }
}

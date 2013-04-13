/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael[at]erichseifert.de>
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
package de.erichseifert.gral.plots.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;

import org.junit.Test;

import de.erichseifert.gral.TestUtils;

public class SettingsKeyTest {
	@Test
	public void testCreate() {
		Key k = new Key("foobar");
		assertEquals("foobar", k.getName());
	}

	@Test
	public void testEquals() {
		Key k1 = new Key("foo");
		Key k2 = new Key("foo");
		Key k3 = new Key("bar");

		assertEquals(k1.getName(), k2.getName());
		assertEquals(k1.hashCode(), k2.hashCode());
		assertEquals(k1, k2);
		assertFalse(k1.equals(k3));
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		Key original = new Key("foobar");
	    Key deserialized = TestUtils.serializeAndDeserialize(original);

	    assertEquals(original.getName(), deserialized.getName());
	    assertEquals(original.hashCode(), deserialized.hashCode());
	    assertEquals(original, deserialized);
    }
}

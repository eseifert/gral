/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2012 Erich Seifert <dev[at]erichseifert.de>,
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;

public class SettingChangeEventTest {
	private static final Object SOURCE = "foobar";
	private static final Key KEY = new Key("test");
	private SettingChangeEvent event;

	@Before
	public void setUp() {
		event = new SettingChangeEvent(SOURCE, KEY, 0.0, 1.0, true);
		assertEquals(SOURCE, event.getSource());
		assertEquals(KEY, event.getKey());
		assertEquals(0.0, event.getValOld());
		assertEquals(1.0, event.getValNew());
		assertTrue(event.isDefaultSetting());
	}

	@Test
	public void testCreation() {
		assertEquals(SOURCE, event.getSource());
		assertEquals(KEY, event.getKey());
		assertEquals(0.0, event.getValOld());
		assertEquals(1.0, event.getValNew());
		assertTrue(event.isDefaultSetting());
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		SettingChangeEvent original = event;
		SettingChangeEvent deserialized = TestUtils.serializeAndDeserialize(original);

		// Source is transient and should be null after serialization process
		assertNull(deserialized.getSource());
		// All other values should be restored
		assertEquals(original.getKey(), deserialized.getKey());
		assertEquals(original.getValOld(), deserialized.getValOld());
		assertEquals(original.getValNew(), deserialized.getValNew());
		assertEquals(original.isDefaultSetting(), deserialized.isDefaultSetting());
    }
}

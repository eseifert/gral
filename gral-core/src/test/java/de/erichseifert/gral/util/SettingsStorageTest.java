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
package de.erichseifert.gral.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.util.SettingsStorage.Key;

public class SettingsStorageTest {
	private static final Key KEY1 = new Key("1");
	private static final Key KEY2 = new Key("2");
	private static final Key KEY3 = new Key("3");
	private static final Key KEY4 = new Key("4");

	private BasicSettingsStorage settings;

	@Before
	public void setUp() {
		settings = new BasicSettingsStorage();
		settings.setSetting(KEY1, "v1");
		settings.setSettingDefault(KEY1, "v1Default");
		settings.setSettingDefault(KEY2, "v2Default");
		settings.setSetting(KEY3, "v3");
	}

	@Test
	public void testGet() {
		assertEquals("v1", settings.getSetting(KEY1));
		assertEquals("v2Default", settings.getSetting(KEY2));
		assertEquals("v3", settings.getSetting(KEY3));
	}

	@Test
	public void testHasDefault() {
		assertEquals(true, settings.hasSettingDefault(KEY1));
		assertEquals(true, settings.hasSettingDefault(KEY2));
		assertEquals(false, settings.hasSettingDefault(KEY3));
	}

	@Test
	public void testHasSetting() {
		assertEquals(true, settings.hasSetting(KEY1));
		assertEquals(false, settings.hasSetting(KEY2));
		assertEquals(true, settings.hasSetting(KEY3));
	}

	@Test
	public void testSet() {
		settings.setSetting(KEY3, "v3_2");
		settings.setSetting(KEY4, "v4");
		assertEquals("v3_2", settings.getSetting(KEY3));
		assertEquals("v4", settings.getSetting(KEY4));
	}

	@Test
	public void testSetDefault() {
		settings.setSettingDefault(KEY3, "v3Default");
		settings.setSettingDefault(KEY4, "v4Default");
		assertEquals("v3", settings.getSetting(KEY3));
		assertEquals("v4Default", settings.getSetting(KEY4));
	}

	@Test
	public void testRemove() {
		settings.removeSetting(KEY3);
		settings.removeSetting(KEY4);
		assertEquals(null, settings.getSetting(KEY3));
		assertEquals(null, settings.getSetting(KEY4));
	}

	@Test
	public void testRemoveDefault() {
		settings.removeSettingDefault(KEY2);
		settings.removeSettingDefault(KEY3);
		assertEquals(null, settings.getSetting(KEY2));
		assertEquals("v3", settings.getSetting(KEY3));
	}

	@Test
	public void testKey() {
		Key k1a = new Key("test1");
		Key k1b = new Key("test1");
		Key k2 = new Key("test2");

		// Name
		assertEquals(k1a.getName(), k1b.getName());
		assertFalse(k2.getName().equals(k1a.getName()));
		// Equality
		assertFalse(k1a.equals(k1b));
		assertFalse(k1a.equals(k2));
	}
}

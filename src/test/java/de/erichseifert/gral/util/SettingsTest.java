/**
 * GRAL: Vector export for Java(R) Graphics2D
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

package de.erichseifert.gral.util;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.util.Settings.Key;

public class SettingsTest {
	private static final Key KEY1 = new Key("1");
	private static final Key KEY2 = new Key("2");
	private static final Key KEY3 = new Key("3");
	private static final Key KEY4 = new Key("4");

	private Settings settings;

	@Before
	public void setUp() {
		settings = new Settings(null);
		settings.set(KEY1, "v1");
		settings.setDefault(KEY1, "v1Default");
		settings.setDefault(KEY2, "v2Default");
		settings.set(KEY3, "v3");
	}

	@Test
	public void testGet() {
		assertEquals("v1", settings.get(KEY1));
		assertEquals("v2Default", settings.get(KEY2));
		assertEquals("v3", settings.get(KEY3));
	}

	@Test
	public void testGetDefaults() {
		Map<Key, Object> defaults = settings.getDefaults();
		assertEquals(2, defaults.size());
		assertEquals("v1Default", defaults.get(KEY1));
		assertEquals("v2Default", defaults.get(KEY2));
		assertEquals(null, defaults.get(KEY3));
	}

	@Test
	public void testGetSettings() {
		Map<Key, Object> settingsMap = settings.getSettings();
		assertEquals(2, settingsMap.size());
		assertEquals("v1", settingsMap.get(KEY1));
		assertEquals(null, settingsMap.get(KEY2));
		assertEquals("v3", settingsMap.get(KEY3));
	}

	@Test
	public void testClearDefaults() {
		settings.clearDefaults();
		assertEquals(true, settings.getDefaults().isEmpty());
	}

	@Test
	public void testClearSettings() {
		settings.clearSettings();
		assertEquals(true, settings.getSettings().isEmpty());
	}

	@Test
	public void testHasDefault() {
		assertEquals(true, settings.hasDefault(KEY1));
		assertEquals(true, settings.hasDefault(KEY2));
		assertEquals(false, settings.hasDefault(KEY3));
	}

	@Test
	public void testHasSetting() {
		assertEquals(true, settings.hasSetting(KEY1));
		assertEquals(false, settings.hasSetting(KEY2));
		assertEquals(true, settings.hasSetting(KEY3));
	}

	@Test
	public void testHasKey() {
		assertEquals(true, settings.hasKey(KEY1));
		assertEquals(true, settings.hasKey(KEY2));
		assertEquals(true, settings.hasKey(KEY3));
		assertEquals(false, settings.hasKey(KEY4));
	}

	@Test
	public void testKeySet() {
		Collection<Key> keys = new HashSet<Key>(3);
		keys.add(KEY1);
		keys.add(KEY2);
		keys.add(KEY3);
		Set<Key> keysToTest = settings.keySet();
		assertEquals(true, keysToTest.containsAll(keys));
		assertEquals(keys.size(), keysToTest.size());
	}

	@Test
	public void testSet() {
		settings.set(KEY3, "v3_2");
		settings.set(KEY4, "v4");
		assertEquals("v3_2", settings.get(KEY3));
		assertEquals("v4", settings.get(KEY4));
	}

	@Test
	public void testSetDefault() {
		settings.setDefault(KEY3, "v3Default");
		settings.setDefault(KEY4, "v4Default");
		assertEquals("v3", settings.get(KEY3));
		assertEquals("v4Default", settings.get(KEY4));
	}

	@Test
	public void testRemove() {
		settings.remove(KEY3);
		settings.remove(KEY4);
		assertEquals(null, settings.get(KEY3));
		assertEquals(null, settings.get(KEY4));
	}

	@Test
	public void testRemoveDefault() {
		settings.removeDefault(KEY2);
		settings.removeDefault(KEY3);
		assertEquals(null, settings.get(KEY2));
		assertEquals("v3", settings.get(KEY3));
	}

	@Test
	public void testValues() {
		Collection<Object> values = new HashSet<Object>();
		values.add("v1");
		values.add("v2Default");
		values.add("v3");
		Collection<Object> valuesToTest = settings.values();
		assertEquals(true, valuesToTest.containsAll(values));
		assertEquals(values.size(), valuesToTest.size());
	}

}

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

import java.awt.BasicStroke;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;

public class SettingsStorageTest {
	private static final Key KEY1 = new Key("1");
	private static final Key KEY2 = new Key("2");
	private static final Key KEY3 = new Key("3");
	private static final Key KEY4 = new Key("4");

	private BasicSettingsStorage settings;

	@Before
	public void setUp() {
		settings = new BasicSettingsStorage();
		settings.setSettingDefault(KEY1, "v1Default");
		settings.setSettingDefault(KEY2, "v2Default");
		settings.setSetting(KEY1, "v1");
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
	public void testSerialization() throws IOException, ClassNotFoundException {
		BasicSettingsStorage original = settings;
		BasicSettingsStorage deserialized = TestUtils.serializeAndDeserialize(original);

	    int i = 1;
	    for (Key k : Arrays.asList(KEY1, KEY2, KEY3, KEY4)) {
	    	assertEquals(
    			String.format("Error getting setting %d.", i),
    			original.getSetting(k), deserialized.getSetting(k)
			);
	    	i++;
	    }
    }

	@Test
	public void testSerializationWrappers() throws IOException, ClassNotFoundException {
		BasicSettingsStorage original = new BasicSettingsStorage();

		Object[] values = {
			new Point2D.Float(1.23f, 4.56f),
			new Point2D.Double(1.23, 4.56),
			new BasicStroke(),
			new Path2D.Float(),
			new Path2D.Double(),
			new Area()
		};

		Key[] keys = new Key[values.length];
		for (int i = 0; i < values.length; i++) {
			keys[i] = new Key(values[i].getClass().getName());
			original.setSetting(keys[i], values[i]);
		}

		BasicSettingsStorage deserialized = TestUtils.serializeAndDeserialize(original);

	    int i = 0;
	    for (Key key : keys) {
	    	TestUtils.assertSetting(
    			String.format("Expected different value for '%s' (index %d).", key, i),
    			original.getSetting(key), deserialized.getSetting(key)
			);
	    	i++;
	    }
    }
}

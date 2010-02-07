/* GRAL : a free graphing library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.tests.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.erichseifert.gral.util.SettingChangeEvent;

public class SettingChangeEventTest {
	@Test
	public void testCreation() {
		SettingChangeEvent e = new SettingChangeEvent(this, "test", 0.0, 1.0, true);
		assertEquals(this, e.getSource());
		assertEquals("test", e.getKey());
		assertEquals(0.0, e.getValOld());
		assertEquals(1.0, e.getValNew());
		assertTrue(e.isDefaultSetting());
	}

}

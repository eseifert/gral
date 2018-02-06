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
import java.awt.Paint;
import java.io.IOException;

import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.plots.colors.ColorMapper.Mode;
import de.erichseifert.gral.util.MathUtils;

public class IndexedColorMapperTest {
	private static final class MockIndexedColorMapper
			extends IndexedColorMapper {
		/** Version id for serialization. */
		private static final long serialVersionUID = -516834950305649435L;

		@Override
		public Paint get(int value) {
			Integer i = applyMode(value, 0, 255);
			if (!MathUtils.isCalculatable(i)) {
				return null;
			}
			int c = i;
			return new Color(c, c, c);
		}
	}

	@Test
	public void testGetNumber() {
		MockIndexedColorMapper c = new MockIndexedColorMapper();
		assertEquals(c.get(128), c.get(128.0));
	}

	@Test
	public void testGetOmit() {
		MockIndexedColorMapper c = new MockIndexedColorMapper();
		c.setMode(Mode.OMIT);
		assertNull(c.get(-128));
		assertEquals(new Color(0, 0, 0), c.get(0));
		assertEquals(new Color(128, 128, 128), c.get(128));
		assertEquals(new Color(255, 255, 255), c.get(255));
		assertNull(c.get(384));
	}

	@Test
	public void testGetRepeat() {
		MockIndexedColorMapper c = new MockIndexedColorMapper();
		c.setMode(Mode.REPEAT);
		assertEquals(new Color(0, 0, 0), c.get(-128));
		assertEquals(new Color(0, 0, 0), c.get(0));
		assertEquals(new Color(128, 128, 128), c.get(128));
		assertEquals(new Color(255, 255, 255), c.get(255));
		assertEquals(new Color(255, 255, 255), c.get(384));
	}

	@Test
	public void testGetCircular() {
		MockIndexedColorMapper c = new MockIndexedColorMapper();
		c.setMode(Mode.CIRCULAR);
		assertEquals(new Color(128, 128, 128), c.get(-128));
		assertEquals(new Color(0, 0, 0), c.get(0));
		assertEquals(new Color(128, 128, 128), c.get(128));
		assertEquals(new Color(255, 255, 255), c.get(255));
		assertEquals(new Color(128, 128, 128), c.get(384));
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		MockIndexedColorMapper original = new MockIndexedColorMapper();
		MockIndexedColorMapper deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(original.getMode(), deserialized.getMode());
		for (int i = -128; i <= 384; i += 128) {
			assertEquals(original.get(i), deserialized.get(i));
		}
    }
}

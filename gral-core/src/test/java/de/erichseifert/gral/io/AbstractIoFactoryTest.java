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
package de.erichseifert.gral.io;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class AbstractIoFactoryTest {
	private static final class TestIOFactory extends AbstractIOFactory<Object> {
		public TestIOFactory(String propFileName) throws IOException {
			super(propFileName);
		}

		@Override
		public Object get(String mimeType) {
			return null;
		}
	}


	@Test(expected=IOException.class)
	public void testCreation() throws IOException {
		new TestIOFactory("fail");
	}

	@Test
	public void testCapabilities() {
		TestIOFactory f = null;
		try {
			f = new TestIOFactory("datareaders.properties");
		} catch (IOException e) {
			fail("Creation of IOFactory failed: "+e);
		}
		List<IOCapabilities> caps = f.getCapabilities();
		assertTrue(caps.size() > 0);
	}

	@Test
	public void testFormats() {
		TestIOFactory f = null;
		try {
			f = new TestIOFactory("datareaders.properties");
		} catch (IOException e) {
			fail("Creation of IOFactory failed: "+e);
		}
		String[] formats = f.getSupportedFormats();
		assertTrue(formats.length > 0);

		assertFalse(f.isFormatSupported("fail"));
		for (String mimeType : formats) {
			assertTrue(f.isFormatSupported(mimeType));
		}
	}
}

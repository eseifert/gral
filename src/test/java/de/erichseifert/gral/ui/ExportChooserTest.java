/*
 * GRAL: GRAphing Library for Java(R)
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

package de.erichseifert.gral.ui;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.io.IOCapabilities;

public class ExportChooserTest {
	private static IOCapabilities[] capabilities;

	@BeforeClass
	public static void setUpBeforeClass() {
		capabilities = new IOCapabilities[] {
				new IOCapabilities("Text", "Unformatted text", "text/plain", "txt"),
				new IOCapabilities("HTML", "HyperText Markup Language", "text/html", "html", "htm")
		};
	}

	@Test
	public void testCreation() {
		ExportChooser strict = new ExportChooser(true, capabilities);
		assertEquals(capabilities.length, strict.getChoosableFileFilters().length);

		ExportChooser relaxed = new ExportChooser(false, capabilities);
		assertEquals(capabilities.length + 1, relaxed.getChoosableFileFilters().length);
	}

}

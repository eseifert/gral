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
package de.erichseifert.gral.ui;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.io.IOCapabilities;

public class ExportChooserTest {
	private static List<IOCapabilities> capabilities;

	@BeforeClass
	public static void setUpBeforeClass() {
		capabilities = new ArrayList<>(2);
		capabilities.add(new IOCapabilities("Text", "Unformatted text", "text/plain", new String[] {"txt"}));
		capabilities.add(new IOCapabilities("HTML", "HyperText Markup Language", "text/html", new String[] {"html", "htm"}));
	}

	@Test
	public void testCreation() {
		ExportChooser strict = new ExportChooser(true, capabilities);
		assertEquals(capabilities.size(), strict.getChoosableFileFilters().length);

		ExportChooser relaxed = new ExportChooser(false, capabilities);
		assertEquals(capabilities.size() + 1, relaxed.getChoosableFileFilters().length);
	}

}

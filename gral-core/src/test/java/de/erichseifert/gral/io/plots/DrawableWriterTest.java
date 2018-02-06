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
package de.erichseifert.gral.io.plots;

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawableContainer;

public class DrawableWriterTest {
	private static final String[] FORMATS = new String[] {
		"image/bmp",
		"image/gif",
		"image/jpeg",
		"image/png",
		"image/vnd.wap.wbmp",
		"application/pdf",
		"application/postscript",
		"image/svg+xml"
	};

	@Test
	public void testWrite() {
		Drawable d = new DrawableContainer();
		for (String format : FORMATS) {
			ByteArrayOutputStream dest = new ByteArrayOutputStream();
			DrawableWriter writer = DrawableWriterFactory.getInstance().get(format);
			try {
				writer.write(d, dest, 320, 240);
			} catch (IOException e) {
				fail("Error writing Drawable to " + format + "image: " + e.getMessage());
			}
		}
	}

}

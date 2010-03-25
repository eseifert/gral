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

package de.erichseifert.gral.io.plots;

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


import org.junit.Test;

import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.DrawableContainer;
import de.erichseifert.gral.io.plots.DrawableWriter;
import de.erichseifert.gral.io.plots.DrawableWriterFactory;

public class DrawableWriterTest {
	private static final String[] FORMATS = new String[] {
		DrawableWriter.TYPE_BMP,
		DrawableWriter.TYPE_GIF,
		DrawableWriter.TYPE_JPEG,
		DrawableWriter.TYPE_PNG,
		DrawableWriter.TYPE_WBMP,
		DrawableWriter.TYPE_EPS,
		DrawableWriter.TYPE_SVG
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

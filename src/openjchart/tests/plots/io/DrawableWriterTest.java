/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.tests.plots.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import openjchart.Drawable;
import openjchart.DrawableContainer;
import openjchart.plots.io.DrawableWriter;
import openjchart.plots.io.DrawableWriterFactory;

import org.junit.Test;

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
	public void testCreate() {
		for (String format : FORMATS) {
			ByteArrayOutputStream dest = new ByteArrayOutputStream();
			DrawableWriter dw = DrawableWriterFactory.getInstance().getDrawableWriter(dest, format);
			assertEquals(dest, dw.getDestination());
			assertEquals(format, dw.getMimeType());
		}
	}

	@Test
	public void testWrite() {
		Drawable d = new DrawableContainer();
		for (String format : FORMATS) {
			ByteArrayOutputStream dest = new ByteArrayOutputStream();
			DrawableWriter writer = DrawableWriterFactory.getInstance().getDrawableWriter(dest, format);
			try {
				writer.write(d, 320, 240);
			} catch (IOException e) {
				fail("Error writing Drawable to " + format + "image: " + e.getMessage());
			}
		}
	}

}

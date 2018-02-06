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
package de.erichseifert.gral.io.data;

import static org.junit.Assert.assertEquals;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.data.DataTable;

public class ImageWriterTest {
	private static final double DELTA = 1e-15;
	private static DataTable data;

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setUpBeforeClass() {
		data = new DataTable(Double.class, Double.class, Integer.class);
		data.add(255.0,   0.0,   0);
		data.add(  0.0, 255.0,   0);
		data.add(  0.0,   0.0, 255);
		data.add(127.0, 127.0, 127);
	}

	@Test
	public void testWriter() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		DataWriter writer = DataWriterFactory.getInstance().get("image/png");
		writer.write(data, output);

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		BufferedImage image = ImageIO.read(input);
		assertEquals(data.getColumnCount(), image.getWidth());
		assertEquals(data.getRowCount(), image.getHeight());

		byte[] imageData = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		for (int i = 0; i < imageData.length; i++) {
			int col = i % image.getWidth();
			int row = i / image.getWidth();
			double expected = ((Number) data.get(col, row)).doubleValue();
			double value = imageData[i];
			if (value < 0.0) {
				value += 256.0;
			}
			assertEquals(expected, value, DELTA);
		}
	}

}

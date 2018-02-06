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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import javax.imageio.ImageIO;

import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.data.DataSource;

public class ImageReaderTest {
	private static final double DELTA = 1e-15;
	private static InputStream imageData;

	private static int rgb(double value) {
		int v = (int) Math.round(value);
		return (255 << 24) | (v << 16) | (v <<  8) | v;
	}

	@BeforeClass
	public static void setUpBeforeClass() {
		BufferedImage image = new BufferedImage(3, 4, BufferedImage.TYPE_BYTE_GRAY);
		int[] rgbData = {
			rgb(255.0), rgb(  0.0), rgb(  0.0),
			rgb(  0.0), rgb(255.0), rgb(  0.0),
			rgb(  0.0), rgb(  0.0), rgb(255.0),
			rgb(127.0), rgb(127.0), rgb(127.0)
		};
		image.setRGB(0, 0, image.getWidth(), image.getHeight(), rgbData, 0, image.getWidth());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "png", out);
		} catch (IOException e) {
		}
		imageData = new ByteArrayInputStream(out.toByteArray());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testReader() throws IOException, ParseException {
		DataReader reader = DataReaderFactory.getInstance().get("image/png");
		DataSource data = reader.read(imageData);
		assertEquals(3, data.getColumnCount());
		assertEquals(4, data.getRowCount());

		double[] expected = new double[] {
			255.0,   0.0,   0.0,
			  0.0, 255.0,   0.0,
			  0.0,   0.0, 255.0,
			127.0, 127.0, 127.0
		};
		for (int i = 0; i < expected.length; i++) {
			int col = i % 3;
			int row = i / 3;
			double value = ((Number) data.get(col, row)).doubleValue();
			assertEquals(expected[i], value, DELTA);
		}
	}

}

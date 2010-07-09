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

package de.erichseifert.gral.io.data;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.io.IOCapabilities;


/**
 * Class that reads a DataSource from a binary image file.
 */
public class ImageReader extends AbstractDataReader {
	static {
		addCapabilities(new IOCapabilities(
			"BMP",
			"Windows Bitmap",
			"image/bmp",
			"bmp", "dib"
		));

		addCapabilities(new IOCapabilities(
			"GIF",
			"Graphics Interchange Format",
			"image/gif",
			"gif"
		));

		addCapabilities(new IOCapabilities(
			"JPEG/JFIF",
			"JPEG File Interchange Format",
			"image/jpeg",
			"jpg", "jpeg", "jpe", "jif", "jfif", "jfi"
		));

		addCapabilities(new IOCapabilities(
			"PNG",
			"Portable Network Graphics",
			"image/png",
			"png"
		));

		addCapabilities(new IOCapabilities(
			"WBMP",
			"Wireless Application Protocol Bitmap",
			"image/vnd.wap.wbmp",
			"wbmp"
		));
	}

	/**
	 * Creates a new instance with the specified MIME type.
	 * @param mimeType MIME type of the file format to be read.
	 */
	public ImageReader(String mimeType) {
		super(mimeType);
		setDefault("factor", 1.0/255.0);
		setDefault("offset", 0.0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public DataSource read(InputStream input, Class<? extends Number>... types)
			throws IOException, ParseException {
		BufferedImage image = ImageIO.read(input);
		
		int w = image.getWidth();
		int h = image.getHeight();

		Class[] colTypes = new Class[w];
		Arrays.fill(colTypes, Double.class);
		DataTable data = new DataTable(colTypes);

		double factor = this.<Double>getSetting("factor");
		double offset = this.<Double>getSetting("offset");
		
		int[] pixelData = new int[w];
		Double[] rowData = new Double[w];
		for (int y = 0; y < h; y++) {
			image.getRGB(0, y, pixelData.length, 1, pixelData, 0, 0);
			for (int x = 0; x < pixelData.length; x++) {
				//double b = (pixelData[x] >>  0) & 0xFF;
				//double g = (pixelData[x] >>  8) & 0xFF;
				double r = (pixelData[x] >> 16) & 0xFF;
				//double a = (pixelData[x] >> 24) & 0xFF;
				rowData[x] = r * factor + offset;
			}
			data.add(rowData);
		}

		return data;
	}

}

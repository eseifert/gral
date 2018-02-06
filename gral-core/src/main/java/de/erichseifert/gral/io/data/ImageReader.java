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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.io.IOCapabilities;
import de.erichseifert.gral.util.Messages;


/**
 * Class that reads a data source from a binary image file. This class
 * shouldn't be used directly but using the {@link DataReaderFactory}.
 */
public class ImageReader extends AbstractDataReader {
	static {
		addCapabilities(new IOCapabilities(
			"BMP", //$NON-NLS-1$
			Messages.getString("ImageIO.bmpDescription"), //$NON-NLS-1$
			"image/bmp", //$NON-NLS-1$
			new String[] {"bmp", "dib"} //$NON-NLS-1$ //$NON-NLS-2$
		));

		addCapabilities(new IOCapabilities(
			"GIF", //$NON-NLS-1$
			Messages.getString("ImageIO.gifDescription"), //$NON-NLS-1$
			"image/gif", //$NON-NLS-1$
			new String[] {"gif"} //$NON-NLS-1$
		));

		addCapabilities(new IOCapabilities(
			"JPEG/JFIF", //$NON-NLS-1$
			Messages.getString("ImageIO.jpegDescription"), //$NON-NLS-1$
			"image/jpeg", //$NON-NLS-1$
			new String[] {
				"jpg", "jpeg", "jpe", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				"jif", "jfif", "jfi"} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		));

		addCapabilities(new IOCapabilities(
			"PNG", //$NON-NLS-1$
			Messages.getString("ImageIO.pngDescription"), //$NON-NLS-1$
			"image/png", //$NON-NLS-1$
			new String[] {"png"} //$NON-NLS-1$
		));

		addCapabilities(new IOCapabilities(
			"WBMP", //$NON-NLS-1$
			Messages.getString("ImageIO.wbmpDescription"), //$NON-NLS-1$
			"image/vnd.wap.wbmp", //$NON-NLS-1$
			new String[] {"wbmp"} //$NON-NLS-1$
		));
	}

	/**
	 * Creates a new instance with the specified MIME type.
	 * @param mimeType MIME type of the file format to be read.
	 */
	public ImageReader(String mimeType) {
		super(mimeType);
		setDefault("factor", 1.0); //$NON-NLS-1$
		setDefault("offset", 0.0); //$NON-NLS-1$
	}

	/**
	 * Returns a data source that was imported.
	 * @param input Input to be read.
	 * @param types Number types for the columns of the data source.
	 * @return DataSource Imported data.
	 * @throws IOException when the file format is not valid or when
	 *         experiencing an error during file operations.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DataSource read(InputStream input, Class<? extends Comparable<?>>... types)
			throws IOException {
		BufferedImage image = ImageIO.read(input);

		int w = image.getWidth();
		int h = image.getHeight();

		Class[] colTypes = new Class[w];
		Arrays.fill(colTypes, Double.class);
		DataTable data = new DataTable(colTypes);

		double factor = this.<Number>getSetting("factor").doubleValue(); //$NON-NLS-1$
		double offset = this.<Number>getSetting("offset").doubleValue(); //$NON-NLS-1$

		int[] pixelData = new int[w];
		Double[] rowData = new Double[w];
		for (int y = 0; y < h; y++) {
			image.getRGB(0, y, pixelData.length, 1, pixelData, 0, 0);
			for (int x = 0; x < pixelData.length; x++) {
				//double a = (pixelData[x] >> 24) & 0xFF;
				double r = (pixelData[x] >> 16) & 0xFF;
				//double g = (pixelData[x] >>  8) & 0xFF;
				//double b = (pixelData[x] >>  0) & 0xFF;
				rowData[x] = r*factor + offset;
			}
			data.add(rowData);
		}

		return data;
	}

}

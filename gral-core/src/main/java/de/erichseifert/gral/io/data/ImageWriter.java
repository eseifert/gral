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
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.imageio.ImageIO;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.io.IOCapabilities;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.Messages;


/**
 * Class that writes a data source to a binary image file. This class
 * shouldn't be used directly but using the {@link DataWriterFactory}.
 */
public class ImageWriter extends AbstractDataWriter {
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
	public ImageWriter(String mimeType) {
		super(mimeType);
		setDefault("factor", 1.0); //$NON-NLS-1$
		setDefault("offset", 0.0); //$NON-NLS-1$
	}

	/**
	 * Stores the specified data source.
	 * @param data DataSource to be stored.
	 * @param output OutputStream to be written to.
	 * @throws IOException if writing the data failed
	 */
	public void write(DataSource data, OutputStream output) throws IOException {
		int w = data.getColumnCount();
		int h = data.getRowCount();

		double factor = this.<Number>getSetting("factor").doubleValue(); //$NON-NLS-1$
		double offset = this.<Number>getSetting("offset").doubleValue(); //$NON-NLS-1$

		byte[] pixelData = new byte[w*h];
		int pos = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				Comparable<?> cell = data.get(x, y);
				if (!(cell instanceof Number)) {
					continue;
				}
				Number numericCell = (Number) cell;
				double value = numericCell.doubleValue()*factor + offset;
				byte v = (byte) Math.round(MathUtils.limit(value, 0.0, 255.0));
				pixelData[pos++] = v;
			}
		}

        BufferedImage image =
        	new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        image.getRaster().setDataElements(0, 0, w, h, pixelData);

        Iterator<javax.imageio.ImageWriter> writers =
        	ImageIO.getImageWritersByMIMEType(getMimeType());
        try {
        	javax.imageio.ImageWriter writer = writers.next();
        	writer.setOutput(ImageIO.createImageOutputStream(output));
        	writer.write(image);
        } catch (NoSuchElementException e) {
        	throw new IOException(MessageFormat.format(
        			"No writer found for MIME type {0}.", getMimeType())); //$NON-NLS-1$
        }
	}

}

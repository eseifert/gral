/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2010 Erich Seifert <dev[at]richseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.imageio.ImageIO;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.io.IOCapabilities;
import de.erichseifert.gral.util.MathUtils;


/**
 * Class that writes a DataSource to a binary image file.
 */
public class ImageWriter extends AbstractDataWriter {
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
	public ImageWriter(String mimeType) {
		super(mimeType);
		setDefault("factor", 1.0);
		setDefault("offset", 0.0);
	}

	@Override
	public void write(DataSource data, OutputStream output) throws IOException {
		int w = data.getColumnCount();
		int h = data.getRowCount();

		double factor = this.<Number>getSetting("factor").doubleValue();
		double offset = this.<Number>getSetting("offset").doubleValue();

		byte[] pixelData = new byte[w*h];
		int pos = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				double value = data.get(x, y).doubleValue()*factor + offset;
				byte v = (byte) Math.round(MathUtils.limit(value, 0.0, 255.0));
				pixelData[pos++] = v;
			}
		}

        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        image.getRaster().setDataElements(0, 0, w, h, pixelData);

        Iterator<javax.imageio.ImageWriter> writers = ImageIO.getImageWritersByMIMEType(getMimeType());
        try {
        	javax.imageio.ImageWriter writer = writers.next();
        	writer.setOutput(ImageIO.createImageOutputStream(output));
        	writer.write(image);
        } catch (NoSuchElementException e) {
        	throw new IOException("No writer found for MIME type " + getMimeType());
        }
	}

}

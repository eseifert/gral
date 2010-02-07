/* GRAL : a free graphing library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.io.plots;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.io.AbstractWriter;
import de.erichseifert.gral.io.WriterCapabilities;


/**
 * Class that stores <code>Drawable</code> instances as bitmap graphics.
 * Supported formats:
 * <ul>
 *   <li>BMP</li>
 *   <li>GIF</li>
 *   <li>JPEG</li>
 *   <li>PNG</li>
 *   <li>WBMP</li>
 * </ul>
 */
public class BitmapWriter extends AbstractWriter implements DrawableWriter {
	static {
		WriterCapabilities BMP_CAPABILITIES = new WriterCapabilities(
			"BMP",
			"Windows Bitmap",
			TYPE_BMP,
			"bmp", "dib"
		);
		addCapabilities(BMP_CAPABILITIES);

		WriterCapabilities GIF_CAPABILITIES = new WriterCapabilities(
			"GIF",
			"Graphics Interchange Format",
			TYPE_GIF,
			"gif"
		);
		addCapabilities(GIF_CAPABILITIES);

		WriterCapabilities JPG_CAPABILITIES = new WriterCapabilities(
			"JPEG/JFIF",
			"JPEG File Interchange Format",
			TYPE_JPEG,
			"jpg", "jpeg", "jpe", "jif", "jfif", "jfi"
		);
		addCapabilities(JPG_CAPABILITIES);

		WriterCapabilities PNG_CAPABILITIES = new WriterCapabilities(
			"PNG",
			"Portable Network Graphics",
			TYPE_PNG,
			"png"
		);
		addCapabilities(PNG_CAPABILITIES);

		WriterCapabilities WBMP_CAPABILITIES = new WriterCapabilities(
			"WBMP",
			"Wireless Application Protocol Bitmap",
			TYPE_WBMP,
			"wbmp"
		);
		addCapabilities(WBMP_CAPABILITIES);
	}

	private String mimeType;

	/**
	 * Creates a new <code>BitmapWriter</code> object with the specified MIME-Type.
	 * @param mimeType Output MIME-Type.
	 */
	protected BitmapWriter(String mimeType) {
		this.mimeType = mimeType;
		// TODO: Option to set transparency
		// TODO: Possibility to choose a background color
	}

	@Override
	public void write(Drawable d, OutputStream destination, double width, double height) throws IOException {
		write(d, destination, 0.0, 0.0, width, height);
	}

	@Override
	public void write(Drawable d, OutputStream destination, double x, double y, double width, double height) throws IOException {
		int rasterFormat = BufferedImage.TYPE_INT_RGB;
		if (TYPE_PNG.equals(getMimeType())) {
			rasterFormat = BufferedImage.TYPE_INT_ARGB;
		} else if (TYPE_WBMP.equals(getMimeType())) {
			rasterFormat = BufferedImage.TYPE_BYTE_BINARY;
		}
		BufferedImage image = new BufferedImage(
				(int)Math.ceil(width), (int)Math.ceil(height), rasterFormat);
		d.draw((Graphics2D) image.getGraphics());
		Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(getMimeType());
		while (writers.hasNext()) {
			ImageWriter writer = writers.next();
			ImageOutputStream ios = ImageIO.createImageOutputStream(destination);
			writer.setOutput(ios);
			Rectangle2D boundsOld = d.getBounds();
			d.setBounds(x, y, width, height);
			try {
				writer.write(image);
			} finally {
				d.setBounds(boundsOld);
				ios.close();
			}
			return;
		}
	}

	@Override
	public String getMimeType() {
		return this.mimeType;
	}
}

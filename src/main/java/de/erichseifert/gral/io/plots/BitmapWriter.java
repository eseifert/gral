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
import de.erichseifert.gral.io.IOCapabilities;
import de.erichseifert.gral.io.IOCapabilitiesStorage;


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
public class BitmapWriter extends IOCapabilitiesStorage implements DrawableWriter {
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

	private final String mimeType;
	private int rasterFormat;

	/**
	 * Creates a new <code>BitmapWriter</code> object with the specified MIME-Type.
	 * @param mimeType Output MIME-Type.
	 */
	protected BitmapWriter(String mimeType) {
		this.mimeType = mimeType;

		boolean isAlphaSupported = "image/png".equals(mimeType);
		boolean isColorSupported = !"image/vnd.wap.wbmp".equals(mimeType);
		boolean isGrayscaleSupported = !"image/vnd.wap.wbmp".equals(mimeType);

		if (isColorSupported) {
			if (isAlphaSupported) {
				rasterFormat = BufferedImage.TYPE_INT_ARGB;
			} else {
				rasterFormat = BufferedImage.TYPE_INT_RGB;
			}
		} else {
			if (isGrayscaleSupported) {
				rasterFormat = BufferedImage.TYPE_BYTE_GRAY;
			} else {
				rasterFormat = BufferedImage.TYPE_BYTE_BINARY;
			}
		}

		// TODO: Option to set transparency
		// TODO: Possibility to choose a background color
	}

	@Override
	public void write(Drawable d, OutputStream destination, double width, double height) throws IOException {
		write(d, destination, 0.0, 0.0, width, height);
	}

	@Override
	public void write(Drawable d, OutputStream destination, double x, double y, double width, double height) throws IOException {
		BufferedImage image = new BufferedImage(
				(int)Math.ceil(width), (int)Math.ceil(height), rasterFormat);

		d.draw((Graphics2D) image.getGraphics());

		Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(mimeType);
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

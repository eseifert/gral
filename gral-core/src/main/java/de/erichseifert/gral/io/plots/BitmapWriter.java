/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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
import de.erichseifert.gral.DrawingContext;
import de.erichseifert.gral.io.IOCapabilities;
import de.erichseifert.gral.io.IOCapabilitiesStorage;
import de.erichseifert.gral.util.Messages;


/**
 * Class that stores <code>Drawable</code> instances as bitmap graphics.
 * Supported formats:
 * <ul>
 *   <li>Windows Bitmap (BMP)</li>
 *   <li>Graphics Interchange Format (GIF)</li>
 *   <li>JPEG File Interchange Format (JPEG)</li>
 *   <li>Portable Network Graphics (PNG)</li>
 *   <li>Wireless Application Protocol Bitmap (WBMP)</li>
 * </ul>
 * <p>This class shouldn't be used directly but using the
 * {@link DrawableWriterFactory}.</p>
 */
public class BitmapWriter extends IOCapabilitiesStorage
		implements DrawableWriter {
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

	/** Data format as MIME type string. */
	private final String mimeType;
	/** Bitmap raster format. */
	private int rasterFormat;

	/**
	 * Creates a new <code>BitmapWriter</code> object with the specified
	 * MIME-Type.
	 * @param mimeType Output MIME-Type.
	 */
	protected BitmapWriter(String mimeType) {
		this.mimeType = mimeType;

		boolean isAlphaSupported =
			"image/png".equals(mimeType); //$NON-NLS-1$
		boolean isColorSupported =
			!"image/vnd.wap.wbmp".equals(mimeType); //$NON-NLS-1$
		boolean isGrayscaleSupported =
			!"image/vnd.wap.wbmp".equals(mimeType); //$NON-NLS-1$

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

		// TODO Option to set transparency
		// TODO Possibility to choose a background color
	}

	@Override
	public void write(Drawable d, OutputStream destination,
			double width, double height) throws IOException {
		write(d, destination, 0.0, 0.0, width, height);
	}

	@Override
	public void write(Drawable d, OutputStream destination,
			double x, double y, double width, double height)
			throws IOException {
		BufferedImage image = new BufferedImage(
				(int)Math.ceil(width), (int)Math.ceil(height), rasterFormat);

		DrawingContext context =
			new DrawingContext((Graphics2D) image.getGraphics());
		d.draw(context);

		Iterator<ImageWriter> writers =
			ImageIO.getImageWritersByMIMEType(mimeType);
		while (writers.hasNext()) {
			ImageWriter writer = writers.next();
			ImageOutputStream ios =
				ImageIO.createImageOutputStream(destination);
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

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

import java.io.IOException;
import java.io.OutputStream;

import de.erichseifert.gral.Drawable;


/**
 * Interface providing functions for rendering <code>Drawable</code>
 * instances and writing them to an output stream. As an example: a plot
 * can be saved into a bitmap file.
 */
public interface DrawableWriter {
	/** Use the BMP bitmap format for saving. */
	public static final String TYPE_BMP = "image/bmp";
	/** Use the EPS vector format for saving. */
	public static final String TYPE_EPS = "application/postscript";
	/** Use the GIF bitmap format for saving. */
	public static final String TYPE_GIF = "image/gif";
	/** Use the JFIF/JPEG bitmap format for saving. */
	public static final String TYPE_JPEG = "image/jpeg";
	/** Use the PDF vector format for saving. */
	public static final String TYPE_PDF = "application/pdf";
	/** Use the PNG bitmap format for saving. */
	public static final String TYPE_PNG = "image/png";
	/** Use the SVG vector format for saving. */
	public static final String TYPE_SVG = "image/svg+xml";
	/** Use the WBMP bitmap format for saving. */
	public static final String TYPE_WBMP = "image/vnd.wap.wbmp";

	/**
	 * Returns the output format of this writer.
	 * @return String representing the MIME-Type.
	 */
	public String getMimeType();

	/**
	 * Stores the specified <code>Drawable</code> instance.
	 * @param d <code>Drawable</code> to be written.
	 * @param destination Stream to write to
	 * @param width Width of the image.
	 * @param height Height of the image.
	 * @throws IOException if writing to stream fails
	 */
	public void write(Drawable d, OutputStream destination, double width, double height) throws IOException;

	/**
	 * Stores the specified <code>Drawable</code> instance.
	 * @param d <code>Drawable</code> to be written.
	 * @param destination Stream to write to
	 * @param x Horizontal position.
	 * @param y Vertical position.
	 * @param width Width of the image.
	 * @param height Height of the image.
	 * @throws IOException if writing to stream fails
	 */
	public void write(Drawable d, OutputStream destination, double x, double y, double width, double height) throws IOException;
}

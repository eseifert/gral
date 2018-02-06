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
package de.erichseifert.gral.io.plots;

import java.io.IOException;
import java.io.OutputStream;

import de.erichseifert.gral.graphics.Drawable;


/**
 * Interface providing functions for rendering {@code Drawable}
 * instances and writing them to an output stream. As an example: a plot
 * can be saved into a bitmap file.
 * @see DrawableWriterFactory
 */
public interface DrawableWriter {
	/**
	 * Returns the output format of this writer.
	 * @return String representing the MIME-Type.
	 */
	String getMimeType();

	/**
	 * Stores the specified {@code Drawable} instance.
	 * @param d {@code Drawable} to be written.
	 * @param destination Stream to write to
	 * @param width Width of the image.
	 * @param height Height of the image.
	 * @throws IOException if writing to stream fails
	 */
	void write(Drawable d, OutputStream destination,
			   double width, double height) throws IOException;

	/**
	 * Stores the specified {@code Drawable} instance.
	 * @param d {@code Drawable} to be written.
	 * @param destination Stream to write to
	 * @param x Horizontal position.
	 * @param y Vertical position.
	 * @param width Width of the image.
	 * @param height Height of the image.
	 * @throws IOException if writing to stream fails
	 */
	void write(Drawable d, OutputStream destination,
			   double x, double y, double width, double height) throws IOException;
}

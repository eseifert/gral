/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2026 Erich Seifert <dev[at]erichseifert.de>,
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
 * <p>Renders a {@link Drawable} at a given size and writes the result to a
 * stream, for example to save a plot as a PNG or a PDF. Obtain an instance
 * from {@link DrawableWriterFactory} rather than constructing one:</p>
 *
 * <pre>
 * DrawableWriter writer = DrawableWriterFactory.getInstance().get("image/png");
 * try (OutputStream out = new FileOutputStream("plot.png")) {
 *     writer.write(plot, out, 800.0, 600.0);
 * }
 * </pre>
 *
 * <p>The writer sets the bounds of the drawable to the requested size for the
 * duration of the call and restores them afterwards, so a plot that is
 * displayed on screen can be exported at a different size without disturbing
 * the view. No window and no display are needed, which makes this the way to
 * produce plots on a headless machine.</p>
 *
 * @see DrawableWriterFactory
 */
public interface DrawableWriter {
	/**
	 * Returns the output format of this writer.
	 * @return String representing the MIME-Type.
	 */
	String getMimeType();

	/**
	 * Writes the specified drawable at the given size, positioned at the
	 * origin. The stream is not closed by this method.
	 * @param d {@code Drawable} to be written.
	 * @param destination Stream to write to
	 * @param width Width of the image.
	 * @param height Height of the image.
	 * @throws IOException if writing to stream fails
	 */
	void write(Drawable d, OutputStream destination,
			   double width, double height) throws IOException;

	/**
	 * Writes the specified drawable at the given position and size. The output
	 * itself is {@code width} by {@code height}, so a non-zero position shifts
	 * the drawable within it rather than enlarging it. The stream is not closed
	 * by this method.
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

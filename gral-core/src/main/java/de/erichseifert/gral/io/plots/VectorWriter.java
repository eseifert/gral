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

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.graphics.DrawingContext.Quality;
import de.erichseifert.gral.graphics.DrawingContext.Target;
import de.erichseifert.gral.io.IOCapabilities;
import de.erichseifert.gral.io.IOCapabilitiesStorage;
import de.erichseifert.gral.util.Messages;

/**
 * <p>Class that stores {@code Drawable} instances as vector graphics.
 * This implementation requires the <i>VectorGraphics2D</i> library to provide
 * support for the following file formats:</p>
 * <ul>
 *   <li>Encapsulated PostScript (EPS)</li>
 *   <li>Portable Document Format (PDF)</li>
 *   <li>Scalable Vector Graphics (SVG)</li>
 * </ul>
 *
 * <p>If the <i>VectorGraphics2D</i> library isn't available the file formats
 * aren't registered in the plug-in system. This class shouldn't be used directly
 * but using the {@link DrawableWriterFactory}.</p>
 */
public class VectorWriter extends IOCapabilitiesStorage
		implements DrawableWriter {
	/** Mapping of MIME type string to {@code Processor} implementation. */
	private static final Map<String, String> processors;
	/** Java package that contains the VecorGraphics2D package. */
	private static final String VECTORGRAPHICS2D_PACKAGE =
		"de.erichseifert.vectorgraphics2d"; //$NON-NLS-1$

	static {
		processors = new HashMap<>();

		addCapabilities(new IOCapabilities(
			"EPS", //$NON-NLS-1$
			Messages.getString("ImageIO.epsDescription"), //$NON-NLS-1$
			"application/postscript", //$NON-NLS-1$
			new String[] {"eps", "epsf", "epsi"} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		));
		processors.put("application/postscript", "eps"); //$NON-NLS-1$ //$NON-NLS-2$

		addCapabilities(new IOCapabilities(
			"PDF", //$NON-NLS-1$
			Messages.getString("ImageIO.pdfDescription"), //$NON-NLS-1$
			"application/pdf", //$NON-NLS-1$
			new String[] {"pdf"} //$NON-NLS-1$
		));
		processors.put("application/pdf", "pdf"); //$NON-NLS-1$ //$NON-NLS-2$

		addCapabilities(new IOCapabilities(
			"SVG", //$NON-NLS-1$
			Messages.getString("ImageIO.svgDescription"), //$NON-NLS-1$
			"image/svg+xml", //$NON-NLS-1$
			new String[] {"svg", "svgz"} //$NON-NLS-1$ //$NON-NLS-2$
		));
		processors.put("image/svg+xml", "svg"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/** Current data format as MIME type string. */
	private final String mimeType;

	/**
	 * Creates a new {@code VectorWriter} object with the specified
	 * MIME-Type.
	 * @param mimeType Output MIME-Type.
	 */
	@SuppressWarnings("unchecked")
	protected VectorWriter(String mimeType) {
		this.mimeType = mimeType;
		if (!processors.containsKey(mimeType)) {
			throw new IllegalArgumentException(MessageFormat.format(
				"Unsupported file format: {0}", mimeType)); //$NON-NLS-1$
		}
	}

	/**
	 * Stores the specified {@code Drawable} instance.
	 * @param d {@code Drawable} to be written.
	 * @param destination Stream to write to
	 * @param width Width of the image.
	 * @param height Height of the image.
	 * @throws IOException if writing to stream fails
	 */
	public void write(Drawable d, OutputStream destination,
			double width, double height) throws IOException {
		write(d, destination, 0.0, 0.0, width, height);
	}

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
	public void write(Drawable d, OutputStream destination,
			double x, double y, double width, double height)
			throws IOException {
		// Temporary change size of drawable
		Rectangle2D boundsOld = d.getBounds();
		d.setBounds(x, y, width, height);

		try {
			// Create an instance of Graphics2D implementation
			Class<?> vg2dClass = Class.forName(VECTORGRAPHICS2D_PACKAGE +
					".VectorGraphics2D"); //$NON-NLS-1$
			Graphics2D g = (Graphics2D) vg2dClass.newInstance();
			// Paint the Drawable instance
			d.draw(new DrawingContext(g, Quality.QUALITY, Target.VECTOR));
			// Get sequence of commands
			Class<?> commandSequenceClass = Class.forName(VECTORGRAPHICS2D_PACKAGE +
					".intermediate.CommandSequence");  //$NON-NLS-1$
			Object commands = vg2dClass.getMethod("getCommands").invoke(g); //$NON-NLS-1$
			// Define page size
			Class<?> pageSizeClass = Class.forName(VECTORGRAPHICS2D_PACKAGE +
					".util.PageSize"); //$NON-NLS-1$
			Object pageSize = pageSizeClass
					.getConstructor(Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE)
					.newInstance(x, y, width, height);
			// Get the corresponding VectorGraphics2D processor instance
			Class<?> processorsClass = Class.forName(VECTORGRAPHICS2D_PACKAGE +
					".Processors");  //$NON-NLS-1$
			Object processor = processorsClass.getMethod("get", String.class) //$NON-NLS-1$
					.invoke(null, processors.get(mimeType));
			Class<?> processorClass = processor.getClass();
			// Get document from commands with defined page size
			Object document = processorClass
					.getMethod("getDocument", commandSequenceClass, pageSizeClass) //$NON-NLS-1$
					.invoke(processor, commands, pageSize);
			// Write document to destination stream
			Class<?> documentClass = Class.forName(VECTORGRAPHICS2D_PACKAGE +
					".Document"); //$NON-NLS-1$
			documentClass.getMethod("writeTo", OutputStream.class) //$NON-NLS-1$
					.invoke(document, destination);
		} catch (ClassNotFoundException | SecurityException | InvocationTargetException |
				IllegalAccessException | InstantiationException | IllegalArgumentException |
				NoSuchMethodException e) {
			throw new IllegalStateException(e);
		} finally {
			d.setBounds(boundsOld);
		}
	}

	/**
	 * Returns the output format of this writer.
	 * @return String representing the MIME-Type.
	 */
	public String getMimeType() {
		return mimeType;
	}

}

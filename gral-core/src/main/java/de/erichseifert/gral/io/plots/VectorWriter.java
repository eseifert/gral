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

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Arrays;
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
 * <p>Two variants of the library are supported: the original
 * <i>VectorGraphics2D</i> and the version that is maintained as part of
 * <i>Eclipse SWTChart</i>. They provide the same API, but use different Java
 * packages. Whichever is found on the class path will be used.</p>
 *
 * <p>If neither is available, writing fails with an
 * {@code IllegalStateException}. This class shouldn't be used directly but
 * using the {@link DrawableWriterFactory}.</p>
 */
public class VectorWriter extends IOCapabilitiesStorage
		implements DrawableWriter {
	/** Mapping of MIME type string to {@code Processor} implementation. */
	private static final Map<String, String> processors;
	/**
	 * Java packages that may contain a VectorGraphics2D implementation. The
	 * original library has been archived and is continued under the umbrella of
	 * Eclipse SWTChart. The original package is tried first, because that is
	 * the variant GRAL declares as a dependency.
	 */
	private static final String[] VECTORGRAPHICS2D_PACKAGES = {
		"de.erichseifert.vectorgraphics2d", //$NON-NLS-1$
		"org.eclipse.swtchart.vectorgraphics2d", //$NON-NLS-1$
	};

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
			String vg2dPackage = findPackage();
			// Create an instance of Graphics2D implementation
			Class<?> vg2dClass = findClass(vg2dPackage, "VectorGraphics2D"); //$NON-NLS-1$
			Graphics2D g = (Graphics2D) vg2dClass.getDeclaredConstructor().newInstance();
			// Paint the Drawable instance
			d.draw(new DrawingContext(g, Quality.QUALITY, Target.VECTOR));
			// Get sequence of commands
			Class<?> commandSequenceClass =
					findClass(vg2dPackage, "intermediate.CommandSequence"); //$NON-NLS-1$
			Object commands = vg2dClass.getMethod("getCommands").invoke(g); //$NON-NLS-1$
			// Define page size
			Class<?> pageSizeClass = findClass(vg2dPackage, "util.PageSize"); //$NON-NLS-1$
			Object pageSize = pageSizeClass
					.getConstructor(Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE)
					.newInstance(x, y, width, height);
			// Get the corresponding VectorGraphics2D processor instance
			Class<?> processorsClass = findClass(vg2dPackage, "Processors"); //$NON-NLS-1$
			Object processor = processorsClass.getMethod("get", String.class) //$NON-NLS-1$
					.invoke(null, processors.get(mimeType));
			Class<?> processorClass = processor.getClass();
			// Get document from commands with defined page size
			Object document = processorClass
					.getMethod("getDocument", commandSequenceClass, pageSizeClass) //$NON-NLS-1$
					.invoke(processor, commands, pageSize);
			// Write document to destination stream
			Class<?> documentClass = findClass(vg2dPackage, "Document"); //$NON-NLS-1$
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
	 * Returns the package of the VectorGraphics2D variant that is available on
	 * the class path.
	 * @return Name of a Java package.
	 * @throws ClassNotFoundException if no variant could be found.
	 */
	private static String findPackage() throws ClassNotFoundException {
		for (String packageName : VECTORGRAPHICS2D_PACKAGES) {
			try {
				findClass(packageName, "VectorGraphics2D"); //$NON-NLS-1$
				return packageName;
			} catch (ClassNotFoundException e) {
				// Continue with the next variant of the library
			}
		}
		throw new ClassNotFoundException(MessageFormat.format(
			"No VectorGraphics2D implementation found in any of the packages: {0}", //$NON-NLS-1$
			Arrays.toString(VECTORGRAPHICS2D_PACKAGES)));
	}

	/**
	 * Returns a class of the VectorGraphics2D library. The classes that used to
	 * reside in the root package have been moved to a sub-package named
	 * {@code core} in the Eclipse SWTChart variant, so both locations are
	 * tried.
	 * @param packageName Name of the VectorGraphics2D base package.
	 * @param className Name of the class relative to the base package.
	 * @return The class object.
	 * @throws ClassNotFoundException if the class could not be found.
	 */
	private static Class<?> findClass(String packageName, String className)
			throws ClassNotFoundException {
		try {
			return Class.forName(packageName + ".core." + className); //$NON-NLS-1$
		} catch (ClassNotFoundException e) {
			return Class.forName(packageName + '.' + className);
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

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
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.DrawingContext;
import de.erichseifert.gral.DrawingContext.Quality;
import de.erichseifert.gral.DrawingContext.Target;
import de.erichseifert.gral.io.IOCapabilities;
import de.erichseifert.gral.io.IOCapabilitiesStorage;
import de.erichseifert.gral.util.Messages;

/**
 * <p>Class that stores <code>Drawable</code> instances as vector graphics.
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
	/** Mapping of MIME type string to <code>Graphics2D</code>
	implementation. */
	private static final Map<String, Class<?>> graphics;
	/** Java package that contains the VecorGraphics2D package. */
	private static final String VECTORGRAPHICS2D_PACKAGE =
		"de.erichseifert.vectorgraphics2d"; //$NON-NLS-1$

	static {
		graphics = new HashMap<String, Class<?>>();
		Class<?> cls;

		try {
			cls = Class.forName(VECTORGRAPHICS2D_PACKAGE
					+ ".EPSGraphics2D"); //$NON-NLS-1$
			addCapabilities(new IOCapabilities(
				"EPS", //$NON-NLS-1$
				Messages.getString("ImageIO.epsDescription"), //$NON-NLS-1$
				"application/postscript", //$NON-NLS-1$
				new String[] {"eps", "epsf", "epsi"} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			));
			graphics.put("application/postscript", cls); //$NON-NLS-1$
		} catch (ClassNotFoundException e) {
			cls = null;
		}

		try {
			cls = Class.forName(VECTORGRAPHICS2D_PACKAGE
					+ ".PDFGraphics2D"); //$NON-NLS-1$
			addCapabilities(new IOCapabilities(
				"PDF", //$NON-NLS-1$
				Messages.getString("ImageIO.pdfDescription"), //$NON-NLS-1$
				"application/pdf", //$NON-NLS-1$
				new String[] {"pdf"} //$NON-NLS-1$
			));
			graphics.put("application/pdf", cls); //$NON-NLS-1$
		} catch (ClassNotFoundException e) {
			cls = null;
		}

		try {
			cls = Class.forName(VECTORGRAPHICS2D_PACKAGE
					+ ".SVGGraphics2D"); //$NON-NLS-1$
			addCapabilities(new IOCapabilities(
				"SVG", //$NON-NLS-1$
				Messages.getString("ImageIO.svgDescription"), //$NON-NLS-1$
				"image/svg+xml", //$NON-NLS-1$
				new String[] {"svg", "svgz"} //$NON-NLS-1$ //$NON-NLS-2$
			));
			graphics.put("image/svg+xml", cls); //$NON-NLS-1$
		} catch (ClassNotFoundException e) {
			cls = null;
		}
	}

	/** Current data format as MIME type string. */
	private final String mimeType;
	/** Current <code>Graphics2D</code> implementation used for rendering. */
	private final Class<? extends Graphics2D> graphicsClass;

	/**
	 * Creates a new <code>VectorWriter</code> object with the specified
	 * MIME-Type.
	 * @param mimeType Output MIME-Type.
	 */
	protected VectorWriter(String mimeType) {
		this.mimeType = mimeType;
		Class<? extends Graphics2D> gfxCls = null;
		try {
			gfxCls = (Class<? extends Graphics2D>) graphics.get(mimeType);
		} catch (ClassCastException e) {
			gfxCls = null;
		}
		graphicsClass = gfxCls;
		if (graphicsClass == null) {
			throw new IllegalArgumentException(MessageFormat.format(
				"Unsupported file format: {0}", mimeType)); //$NON-NLS-1$
		}
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
		try {
			// Create instance of export class
			Constructor<? extends Graphics2D> constructor =
				graphicsClass.getConstructor(
					double.class, double.class, double.class, double.class);
			Graphics2D g = constructor.newInstance(x, y, width, height);

			// Output data
			Rectangle2D boundsOld = d.getBounds();
			d.setBounds(x, y, width, height);
			DrawingContext context =
				new DrawingContext(g, Quality.QUALITY, Target.VECTOR);
			d.draw(context);
			byte[] data = (byte[]) graphicsClass.getMethod(
				"getBytes").invoke(g); //$NON-NLS-1$
			destination.write(data);
			d.setBounds(boundsOld);
		} catch (SecurityException e) {
			throw new IllegalStateException(e);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException(e);
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException(e);
		} catch (InstantiationException e) {
			throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		} catch (InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public String getMimeType() {
		return mimeType;
	}

}

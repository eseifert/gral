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
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.io.IOCapabilities;
import de.erichseifert.gral.io.IOCapabilitiesStorage;

/**
 * Class that stores <code>Drawable</code> instances as vector graphics.
 * Supported formats:
 * <ul>
 *   <li>EPS</li>
 *   <li>PDF</li>
 *   <li>SVG</li>
 * </ul>
 */
public class VectorWriter extends IOCapabilitiesStorage implements DrawableWriter {
	private static final Map<String, Class<?>> graphics;
	private static final String VECTORGRAPHICS2D_PACKAGE = "de.erichseifert.vectorgraphics2d";

	static {
		graphics = new HashMap<String, Class<?>>();
		Class<?> cls;

		try {
			cls = Class.forName(VECTORGRAPHICS2D_PACKAGE+".EPSGraphics2D");
			addCapabilities(new IOCapabilities(
				"EPS",
				"Encapsulated PostScript",
				"application/postscript",
				"eps", "epsf", "epsi"
			));
			graphics.put("application/postscript", cls);
		} catch (ClassNotFoundException e) {
		}

		try {
			cls = Class.forName(VECTORGRAPHICS2D_PACKAGE+".PDFGraphics2D");
			addCapabilities(new IOCapabilities(
				"PDF",
				"Portable Document Format",
				"application/pdf",
				"pdf"
			));
			graphics.put("application/pdf", cls);
		} catch (ClassNotFoundException e) {
		}

		try {
			cls = Class.forName(VECTORGRAPHICS2D_PACKAGE+".SVGGraphics2D");
			addCapabilities(new IOCapabilities(
				"SVG",
				"Scalable Vector Graphics",
				"image/svg+xml",
				"svg", "svgz"
			));
			graphics.put("image/svg+xml", cls);
		} catch (ClassNotFoundException e) {
		}
	}

	private final String mimeType;
	private final Class<? extends Graphics2D> graphicsClass;

	/**
	 * Creates a new <code>VectorWriter</code> object with the specified MIME-Type.
	 * @param mimeType Output MIME-Type.
	 */
	protected VectorWriter(String mimeType) {
		this.mimeType = mimeType;
		Class<? extends Graphics2D> gfxCls = null;
		try {
			gfxCls = (Class<? extends Graphics2D>) graphics.get(mimeType);
		} catch (ClassCastException e) {
		}
		graphicsClass = gfxCls;
		if (graphicsClass == null) {
			throw new IllegalArgumentException("Unsupported file format: " +mimeType);
		}
	}

	@Override
	public void write(Drawable d, OutputStream destination, double width, double height) throws IOException {
		write(d, destination, 0.0, 0.0, width, height);
	}

	@Override
	public void write(Drawable d, OutputStream destination, double x, double y, double width, double height) throws IOException {
		Graphics2D g2d = null;
		try {
			Constructor<? extends Graphics2D> constructor =
				graphicsClass.getConstructor(double.class, double.class, double.class, double.class);
			g2d = constructor.newInstance(x, y, width, height);
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

		Rectangle2D boundsOld = d.getBounds();
		d.setBounds(x, y, width, height);
		d.draw(g2d);
		destination.write(g2d.toString().getBytes());
		d.setBounds(boundsOld);
	}

	@Override
	public String getMimeType() {
		return mimeType;
	}

}

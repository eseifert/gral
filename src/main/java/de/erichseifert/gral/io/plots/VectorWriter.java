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
import de.erichseifert.vectorgraphics2d.EPSGraphics2D;
import de.erichseifert.vectorgraphics2d.PDFGraphics2D;
import de.erichseifert.vectorgraphics2d.SVGGraphics2D;
import de.erichseifert.vectorgraphics2d.VectorGraphics2D;

/**
 * Class that stores <code>Drawable</code> instances as vector graphics.
 * Supported formats:
 * <ul>
 * <li>EPS</li>
 * <li>SVG</li>
 * </ul>
 */
public class VectorWriter extends IOCapabilitiesStorage implements DrawableWriter {
	private static Map<String, Class<? extends VectorGraphics2D>> graphics;

	static {
		graphics = new HashMap<String, Class<? extends VectorGraphics2D>>();

		addCapabilities(new IOCapabilities(
			"EPS",
			"Encapsulated PostScript",
			"application/postscript",
			"eps", "epsf", "epsi"
		));
		graphics.put("application/postscript", EPSGraphics2D.class);

		addCapabilities(new IOCapabilities(
			"PDF",
			"Portable Document Format",
			"application/pdf",
			"pdf"
		));
		graphics.put("application/pdf", PDFGraphics2D.class);

		addCapabilities(new IOCapabilities(
			"SVG",
			"Scalable Vector Graphics",
			"image/svg+xml",
			"svg", "svgz"
		));
		graphics.put("image/svg+xml", SVGGraphics2D.class);
	}

	private final String mimeType;
	private final Class<? extends VectorGraphics2D> graphicsClass;

	/**
	 * Creates a new <code>VectorWriter</code> object with the specified MIME-Type.
	 * @param mimeType Output MIME-Type.
	 */
	protected VectorWriter(String mimeType) {
		this.mimeType = mimeType;
		graphicsClass = graphics.get(mimeType);
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
		VectorGraphics2D g2d = null;
		try {
			Constructor<? extends VectorGraphics2D> constructor =
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

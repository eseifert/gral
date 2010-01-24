/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.plots.io;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;

import openjchart.Drawable;
import vectorgraphics2d.EPSGraphics2D;
import vectorgraphics2d.SVGGraphics2D;

/**
 * Class that stores <code>Drawable</code> instances as vector graphics.
 * Supported formats:
 * <ul>
 * <li>EPS</li>
 * <li>SVG</li>
 * </ul>
 */
public class VectorWriter extends AbstractDrawableWriter {
	static {
		WriterCapabilities EPS_CAPABILITIES = new WriterCapabilities(
			"EPS",
			"Encapsulated PostScript",
			TYPE_EPS,
			"eps", "epsf", "epsi"
		);
		addCapabilities(EPS_CAPABILITIES);

		WriterCapabilities SVG_CAPABILITIES = new WriterCapabilities(
			"SVG",
			"Scalable Vector Graphics",
			TYPE_SVG,
			"svg", "svgz"
		);
		addCapabilities(SVG_CAPABILITIES);
	}

	/**
	 * Creates a new <code>VectorWriter</code> object with the specified format.
	 * @param format Output format.
	 */
	protected VectorWriter(String format) {
		super(format);
	}

	@Override
	public void write(Drawable d, OutputStream destination, double width, double height) throws IOException {
		write(d, destination, 0.0, 0.0, width, height);
	}

	@Override
	public void write(Drawable d, OutputStream destination, double x, double y, double width,	double height) throws IOException {
		Rectangle2D boundsOld = d.getBounds();
		d.setBounds(x, y, width, height);

		Graphics2D g2d = null;
		if (TYPE_EPS.equals(getMimeType())) {
			g2d = new EPSGraphics2D(x, y, width, height);
		} else if (TYPE_SVG.equals(getMimeType())) {
			g2d = new SVGGraphics2D(x, y, width, height);
		} else {
			throw new IllegalArgumentException("Unsupported format: " +getMimeType());
		}
		d.draw(g2d);
		destination.write(g2d.toString().getBytes());

		d.setBounds(boundsOld);
	}

}

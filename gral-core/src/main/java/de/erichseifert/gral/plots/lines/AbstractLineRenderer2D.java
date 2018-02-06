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
package de.erichseifert.gral.plots.lines;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import de.erichseifert.gral.util.SerializationUtils;


/**
 * <p>Abstract class that renders a line in two-dimensional space.</p>
 * <p>Functionality includes:</p>
 * <ul>
 *   <li>Punching data points out of the line's shape</li>
 *   <li>Administration of settings</li>
 * </ul>
 */
public abstract class AbstractLineRenderer2D implements LineRenderer, Serializable {
	/** Version id for serialization. */
	private static final long serialVersionUID = -4172505541305453796L;

	/** Stroke to draw the line. */
	private transient Stroke stroke;
	/** Gap between points and the line. */
	private double gap;
	/** Decides whether the shape of the gap between points and the line is
	 * rounded. */
	private boolean gapRounded;
	/** Paint to fill the line. */
	private Paint color;

	/**
	 * Initializes a new {@code AbstractLineRenderer2D} instance with
	 * default settings.
	 */
	public AbstractLineRenderer2D() {
		stroke = new BasicStroke(1.5f);
		gap = 0.0;
		gapRounded = false;
		color = Color.BLACK;
	}

	/**
	 * Returns the stroked shape of the specified line.
	 * @param line Shape of the line.
	 * @return Stroked shape.
	 */
	protected Shape stroke(Shape line) {
		if (line == null) {
			return null;
		}
		Stroke stroke = getStroke();
		return stroke.createStrokedShape(line);
	}

	/**
	 * Custom deserialization method.
	 * @param in Input stream.
	 * @throws ClassNotFoundException if a serialized class doesn't exist anymore.
	 * @throws IOException if there is an error while reading data from the
	 *         input stream.
	 */
	private void readObject(ObjectInputStream in)
			throws ClassNotFoundException, IOException {
		// Default deserialization
		in.defaultReadObject();
		// Custom deserialization
		stroke = (Stroke) SerializationUtils.unwrap(
				(Serializable) in.readObject());
	}

	/**
	 * Custom serialization method.
	 * @param out Output stream.
	 * @throws ClassNotFoundException if a serialized class doesn't exist.
	 * @throws IOException if there is an error while writing data to the
	 *         output stream.
	 */
	private void writeObject(ObjectOutputStream out)
			throws ClassNotFoundException, IOException {
		// Default serialization
		out.defaultWriteObject();
		// Custom serialization
		out.writeObject(SerializationUtils.wrap(stroke));
	}

	@Override
	public Stroke getStroke() {
		return stroke;
	}

	@Override
	public void setStroke(Stroke stroke) {
		this.stroke = stroke;
	}

	@Override
	public double getGap() {
		return gap;
	}

	@Override
	public void setGap(double gap) {
		this.gap = gap;
	}

	@Override
	public boolean isGapRounded() {
		return gapRounded;
	}

	@Override
	public void setGapRounded(boolean gapRounded) {
		this.gapRounded = gapRounded;
	}

	@Override
	public Paint getColor() {
		return color;
	}

	@Override
	public void setColor(Paint color) {
		this.color = color;
	}
}

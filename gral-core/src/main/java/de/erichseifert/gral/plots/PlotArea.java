/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael[at]erichseifert.de>
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
package de.erichseifert.gral.plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.SerializationUtils;


/**
 * Abstract class that represents a canvas on which plot data will be drawn.
 * It serves as base for specialized implementations for different plot types.
 * Derived classes have to implement how the actual drawing is done.
 */
public abstract class PlotArea extends AbstractDrawable {
	/** Version id for serialization. */
	private static final long serialVersionUID = 2745982325709470005L;

	/** Paint used for background drawing. */
	private Paint background;
	/** Stroke used to draw the border. */
	// Property will be serialized using a wrapper.
	private transient Stroke borderStroke;
	/** Paint used for border drawing. */
	private Paint borderColor;
	/** Clipping offset in pixels relative to the outline of the plot area. */
	private Insets2D clippingOffset;

	/**
	 * Initializes a new instance with default background color and border.
	 */
	public PlotArea() {
		background = Color.WHITE;
		borderStroke = new BasicStroke(1f);
		borderColor = Color.BLACK;
		clippingOffset = new Insets2D.Double(0.0);
	}

	/**
	 * Draws the background of this Legend with the specified Graphics2D
	 * object.
	 * @param context Environment used for drawing.
	 */
	protected void drawBackground(DrawingContext context) {
		// FIXME duplicate code! See de.erichseifert.gral.Legend
		Paint paint = getBackground();
		if (paint != null) {
			GraphicsUtils.fillPaintedShape(context.getGraphics(),
					getBounds(), paint, null);
		}
	}

	/**
	 * Draws the border of this Legend with the specified Graphics2D
	 * object.
	 * @param context Environment used for drawing.
	 */
	protected void drawBorder(DrawingContext context) {
		// FIXME duplicate code! See de.erichseifert.gral.Legend
		Stroke stroke = getBorderStroke();
		if (stroke != null) {
			Paint borderColor = getBorderColor();
			GraphicsUtils.drawPaintedShape(context.getGraphics(),
					getBounds(), borderColor, null, stroke);
		}
	}

	/**
	 * Draws the data using the specified Graphics2D object.
	 * @param context Environment used for drawing.
	 */
	protected abstract void drawPlot(DrawingContext context);

	/**
	 * Custom deserialization method.
	 * @param in Input stream.
	 * @throws ClassNotFoundException if a serialized class doesn't exist anymore.
	 * @throws IOException if there is an error while reading data from the input stream.
	 */
	private void readObject(ObjectInputStream in)
			throws ClassNotFoundException, IOException {
		in.defaultReadObject();
		borderStroke = (Stroke) SerializationUtils.unwrap((Serializable) in.readObject());
	}

	/**
	 * Custom serialization method.
	 * @param out Output stream.
	 * @throws ClassNotFoundException if a deserialized class does not exist.
	 * @throws IOException if there is an error while writing data to the
	 *         output stream.
	 */
	private void writeObject(ObjectOutputStream out)
			throws ClassNotFoundException, IOException {
		out.defaultWriteObject();
		out.writeObject(SerializationUtils.wrap(borderStroke));
	}

	/**
	 * Returns the paint used to draw the background of the plot area.
	 * @return Paint used for background drawing.
	 */
	public Paint getBackground() {
		return background;
	}

	/**
	 * Sets the paint used to draw the background of the plot area.
	 * @param background Paint used for background drawing.
	 */
	public void setBackground(Paint background) {
		this.background = background;
	}

	/**
	 * Returns the stroke used to draw the border of the plot area.
	 * @return Stroke used for border drawing.
	 */
	public Stroke getBorderStroke() {
		return borderStroke;
	}

	/**
	 * Sets the stroke used to draw the border of the plot area.
	 * @param borderStroke Stroke used for border drawing.
	 */
	public void setBorderStroke(Stroke borderStroke) {
		this.borderStroke = borderStroke;
	}

	/**
	 * Returns the paint used to fill the border of the plot area.
	 * @return Paint used for border drawing.
	 */
	public Paint getBorderColor() {
		return borderColor;
	}

	/**
	 * Sets the paint used to fill the border of the plot area.
	 * @param borderColor Paint used for border drawing.
	 */
	public void setBorderColor(Paint borderColor) {
		this.borderColor = borderColor;
	}

	/**
	 * Returns the clipping offset of the plotted data relative to the plot area.
	 * Positive inset values result in clipping inside the plot area,
	 * negative values result in clipping outside the plot area.
	 * Specifying a {@code null} values will turn off clipping.
	 * @return Clipping offset in pixels relative to the outline of the plot area.
	 */
	public Insets2D getClippingOffset() {
		return clippingOffset;
	}

	/**
	 * Sets the clipping offset of the plotted data relative to the plot area.
	 * Positive inset values result in clipping inside the plot area,
	 * negative values result in clipping outside the plot area.
	 * Specifying a {@code null} values will turn off clipping.
	 * @param clippingOffset Clipping offset in pixels relative to the outline of the plot area.
	 */
	public void setClippingArea(Insets2D clippingOffset) {
		this.clippingOffset = clippingOffset;
	}
}

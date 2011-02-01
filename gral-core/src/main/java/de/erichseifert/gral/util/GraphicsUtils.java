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
package de.erichseifert.gral.util;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * Abstract class that contains utility functions for working with graphics.
 * For example, this includes font handling.
 */
public abstract class GraphicsUtils {
	/** Default font render context. */
	private static final FontRenderContext frc = new FontRenderContext(null, true, true);

	/**
	 * Default constructor that prevents creation of class.
	 */
	protected GraphicsUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the layout for the specified text with the specified font.
	 * @param text Text to be displayed.
	 * @param font Font of the Text.
	 * @return TextLayout.
	 */
	public static TextLayout getLayout(String text, Font font) {
		TextLayout layout = new TextLayout(text, font, frc);
		return layout;
	}

	/**
	 * Fills a Shape with the specified Paint object.
	 * @param graphics Graphics to be painted into.
	 * @param shape Shape to be filled.
	 * @param paint Paint to be used.
	 * @param paintBounds Optional bounds describing the painted area.
	 */
	public static void fillPaintedShape(Graphics2D graphics, Shape shape,
			Paint paint, Rectangle2D paintBounds) {
		if (shape == null) {
			return;
		}
		if (paintBounds == null) {
			paintBounds = shape.getBounds2D();
		}
		AffineTransform txOrig = graphics.getTransform();
		graphics.translate(paintBounds.getX(), paintBounds.getY());
		graphics.scale(paintBounds.getWidth(), paintBounds.getHeight());
		Paint paintOld = null;
		if (paint != null) {
			paintOld = graphics.getPaint();
			graphics.setPaint(paint);
		}
		AffineTransform tx = AffineTransform.getScaleInstance(
				1.0/paintBounds.getWidth(), 1.0/paintBounds.getHeight());
		tx.translate(-paintBounds.getX(), -paintBounds.getY());
		graphics.fill(tx.createTransformedShape(shape));
		if (paintOld != null) {
			graphics.setPaint(paintOld);
		}
		graphics.setTransform(txOrig);
	}

	/**
	 * Draws a filled Shape with the specified Paint object.
	 * @param graphics Graphics to be painted into.
	 * @param shape Shape to be filled.
	 * @param paint Paint to be used.
	 * @param paintBounds Optional bounds describing the painted area.
	 * @param stroke Stroke to be used for outlines.
	 */
	public static void drawPaintedShape(Graphics2D graphics, Shape shape,
			Paint paint, Rectangle2D paintBounds, Stroke stroke) {
		if (shape == null) {
			return;
		}
		if (stroke == null) {
			stroke = graphics.getStroke();
		}
		shape = stroke.createStrokedShape(shape);
		fillPaintedShape(graphics, shape, paint, paintBounds);
	}

}

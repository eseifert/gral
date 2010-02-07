/* GRAL : a free graphing library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
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
	private static final FontRenderContext frc = new FontRenderContext(null, true, true);

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
	 * @param g2d Graphics to be painted into.
	 * @param shape Shape to be filled.
	 * @param paint Paint to be used.
	 * @param paintBounds Optional bounds describing the painted area.
	 */
	public static void fillPaintedShape(Graphics2D g2d, Shape shape, Paint paint, Rectangle2D paintBounds) {
		if (paintBounds == null) {
			paintBounds = shape.getBounds2D();
		}
		AffineTransform txOrig = g2d.getTransform();
		g2d.translate(paintBounds.getX(), paintBounds.getY());
		g2d.scale(paintBounds.getWidth(), paintBounds.getHeight());
		Paint paintOld = null;
		if (paint != null) {
			paintOld = g2d.getPaint();
			g2d.setPaint(paint);
		}
		AffineTransform tx = AffineTransform.getScaleInstance(1.0/paintBounds.getWidth(), 1.0/paintBounds.getHeight());
		tx.translate(-paintBounds.getX(), -paintBounds.getY());
		g2d.fill(tx.createTransformedShape(shape));
		if (paintOld != null) {
			g2d.setPaint(paintOld);
		}
		g2d.setTransform(txOrig);
	}

	/**
	 * Draws a filled Shape with the specified Paint object.
	 * @param g2d Graphics to be painted into.
	 * @param shape Shape to be filled.
	 * @param paint Paint to be used.
	 * @param paintBounds Optional bounds describing the painted area.
	 * @param stroke Stroke to be used for outlines.
	 */
	public static void drawPaintedShape(Graphics2D g2d, Shape shape, Paint paint, Rectangle2D paintBounds, Stroke stroke) {
		if (stroke == null) {
			stroke = g2d.getStroke();
		}
		shape = stroke.createStrokedShape(shape);
		fillPaintedShape(g2d, shape, paint, paintBounds);
	}

}

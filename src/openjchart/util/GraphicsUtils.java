/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
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

package openjchart.util;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;

import javax.swing.ImageIcon;

/**
 * Abstract class that contains utility functions for working with graphics.
 * For example, this includes font handling.
 */
public abstract class GraphicsUtils {
	private static final FontRenderContext frc = new FontRenderContext(null, true, true);

	private GraphicsUtils() {
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
	 * @param g2d Graphics to be painted into.
	 * @param shape Shape to be filled.
	 * @param paint Paint to be used.
	 * @param paintBounds Bounds of the paint.
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
	 * @param paintBounds Bounds of the paint.
	 * @param stroke Stroke to be used for outlines.
	 */
	public static void drawPaintedShape(Graphics2D g2d, Shape shape, Paint paint, Rectangle2D paintBounds, Stroke stroke) {
		if (stroke == null) {
			stroke = g2d.getStroke();
		}
		shape = stroke.createStrokedShape(shape);
		fillPaintedShape(g2d, shape, paint, paintBounds);
	}

	/**
	 * This method returns <code>true</code> if the specified image
	 * has transparent pixels.
	 * Taken from http://www.exampledepot.com/egs/java.awt.image/HasAlpha.html
	 * @param image
	 * @return
	 */
	public static boolean hasAlpha(Image image) {
		// If buffered image, the color model is readily available
		if (image instanceof BufferedImage) {
			BufferedImage bimage = (BufferedImage) image;
			return bimage.getColorModel().hasAlpha();
		}
		// Use a pixel grabber to retrieve the image's color model;
		// grabbing a single pixel is usually sufficient
		PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
		}
		// Get the image's color model
		ColorModel cm = pg.getColorModel();
		return cm.hasAlpha();
	}

	/**
	 * This method returns a buffered image with the contents of an image.
	 * Taken from http://www.exampledepot.com/egs/java.awt.image/Image2Buf.html
	 * @param image
	 * @return
	 */
	public static BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage)image;
		}
		// This code ensures that all the pixels in the image are loaded
		image = new ImageIcon(image).getImage();
		// Determine if the image has transparent pixels; for this method's
		// implementation, see Determining If an Image Has Transparent Pixels
		boolean hasAlpha = hasAlpha(image);
		// Create a buffered image with a format that's compatible with the screen
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			// Determine the type of transparency of the new buffered image
			int transparency = Transparency.OPAQUE;
			if (hasAlpha) {
				transparency = Transparency.BITMASK;
			}
			// Create the buffered image
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(
					image.getWidth(null), image.getHeight(null), transparency);
		} catch (HeadlessException e) {
			// The system does not have a screen
		} if (bimage == null) {
			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;
			if (hasAlpha) {
				type = BufferedImage.TYPE_INT_ARGB;	
			}
			bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		}
		// Copy image to buffered image
		Graphics g = bimage.createGraphics();
		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return bimage;
	}
}

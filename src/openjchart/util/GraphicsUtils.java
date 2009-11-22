package openjchart.util;

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
 * Class that contains utility functions for working with graphics.
 * For example, This includes font handling.
 * @author Erich Seifert
 */
public abstract class GraphicsUtils {
	private static final FontRenderContext frc = new FontRenderContext(null, true, true);

	public static TextLayout getLayout(String text, Font font) {
		TextLayout layout = new TextLayout(text, font, frc);
		return layout;
	}

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

	public static void drawPaintedShape(Graphics2D g2d, Shape shape, Paint paint, Rectangle2D paintBounds, Stroke stroke) {
		if (stroke == null) {
			stroke = g2d.getStroke();
		}
		shape = stroke.createStrokedShape(shape);
		fillPaintedShape(g2d, shape, paint, paintBounds);
	}
}

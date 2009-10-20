package openjchart.charts.axes;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

import openjchart.AbstractDrawable;
import openjchart.Drawable;

/**
 * Renderer used to display a horizontal or vertical axis with linear scale.
 * The component used for displaying is a simple line with ticks, which
 * are accordingly labeled.
 * The size of the ticks, as well as their frequency in which they appear
 * if configurable in the renderer.
 * @author Michael Seifert
 *
 */
public class LinearRenderer2D extends AbstractAxisRenderer2D {

	@Override
	public Drawable getRendererComponent(final Axis axis, final Orientation orientation) {
		final Drawable component = new AbstractDrawable() {
			@Override
			public void draw(Graphics2D g2d) {
				AffineTransform txOld = g2d.getTransform();
				Color colorOld = g2d.getColor();

				double axisMin = axis.getMin().doubleValue();
				double axisMax = axis.getMax().doubleValue();
				double minTick = getMinTick(axisMin);
				double maxTick = getMaxTick(axisMax);

				g2d.setColor(axisColor);
				if (Orientation.HORIZONTAL.equals(orientation)) {
					// Draw baseline
					g2d.draw(new Line2D.Double(0.0, getHeight()/2.0, getWidth(), getHeight()/2.0));

					// Draw ticks
					Line2D tick = new Line2D.Double(0.0, getHeight()/2.0 - tickLength/2.0, 0.0, getHeight()/2.0 + tickLength/2.0);
					for (double i = minTick; i <= maxTick; i += tickSpacing) {
						double translateX = (getWidth()-1)*axis.getPos(i);
						g2d.translate(translateX, 0.0);
						g2d.draw(tick);
						// Draw numbers
						FontMetrics metrics = g2d.getFontMetrics();
						int textHeight = metrics.getHeight();
						String label = String.valueOf(i);
						float stringOffsetX = -metrics.stringWidth(label)/2f;
						float stringOffsetY = (float) (getHeight()/2.0 + tickLength/2.0 + textHeight);
						g2d.drawString(label, stringOffsetX, stringOffsetY);
						g2d.setTransform(txOld);
					}
				}
				else if (Orientation.VERTICAL.equals(orientation)) {
					// Draw baseline
					g2d.draw(new Line2D.Double(getWidth()/2.0, 0.0, getWidth()/2.0, getHeight()));

					// Draw ticks
					Line2D tick = new Line2D.Double(getWidth()/2.0 - tickLength/2.0, 0, getWidth()/2.0 + tickLength/2.0, 0);
					for (double i = minTick; i <= maxTick; i += tickSpacing) {
						double translateY = (getHeight() - 1)*(1.0 - axis.getPos(i));
						g2d.translate(0.0, translateY);
						g2d.draw(tick);
						// Draw numbers
						FontMetrics metrics = g2d.getFontMetrics();
						int textHeight = metrics.getAscent();
						String label = String.valueOf(i);
						float stringOffsetX = (float) (getWidth()/2.0 - tickLength/2.0 - textHeight/4.0 - metrics.stringWidth(label));
						float stringOffsetY = textHeight/4f;
						g2d.drawString(label, stringOffsetX, stringOffsetY);
						g2d.setTransform(txOld);
					}
				}
				g2d.setColor(colorOld);
			}
		};

		return component;
	}
}

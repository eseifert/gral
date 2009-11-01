package openjchart.charts;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

import openjchart.AbstractDrawable;
import openjchart.Drawable;

public class DefaultLabelRenderer implements LabelRenderer {

	public DefaultLabelRenderer() {
	}

	@Override
	public Drawable getLabel(final String label) {
		Drawable d = new AbstractDrawable() {
			@Override
			public void draw(Graphics2D g2d) {
				FontMetrics metrics = g2d.getFontMetrics();
				int titleWidth = metrics.stringWidth(label);
				int titleHeight = metrics.getHeight();
				float x = (float) (getX() + getWidth()/2.0 - titleWidth);
				float y = (float) (getY() + getHeight()/2.0 - titleHeight);

				g2d.drawString(label, x, y);
			}
		};
		return d;
	}
}

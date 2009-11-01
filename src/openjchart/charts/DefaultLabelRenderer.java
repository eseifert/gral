package openjchart.charts;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import openjchart.AbstractDrawable;
import openjchart.Drawable;

public class DefaultLabelRenderer extends AbstractLabelRenderer {

	public DefaultLabelRenderer() {
		setSettingDefault(KEY_ALIGNMENT, 0.5);
		Font fontDefault = new Font("Arial", Font.PLAIN, 12);
		setSettingDefault(KEY_FONT, fontDefault);
	}

	@Override
	public Drawable getLabel(final String label) {
		Drawable d = new AbstractDrawable() {
			@Override
			public void draw(Graphics2D g2d) {
				Font fontOld = g2d.getFont();

				g2d.setFont(DefaultLabelRenderer.this.<Font>getSetting(KEY_FONT));
				FontMetrics metrics = g2d.getFontMetrics();
				int titleWidth = metrics.stringWidth(label);
				float x = (float) (getX() - titleWidth + getWidth()*DefaultLabelRenderer.this.<Double>getSetting(KEY_ALIGNMENT));
				float y = (float) (getY() + getHeight()/2.0);

				g2d.drawString(label, x, y);

				g2d.setFont(fontOld);
			}
		};
		return d;
	}
}

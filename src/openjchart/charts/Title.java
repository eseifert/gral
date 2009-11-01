package openjchart.charts;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

import openjchart.AbstractDrawable;

public class Title extends AbstractDrawable {
	private String title;

	public Title(String title) {
		this.title = title;
	}

	@Override
	public void draw(Graphics2D g2d) {
		FontMetrics metrics = g2d.getFontMetrics();
		int titleWidth = metrics.stringWidth(title);
		int titleHeight = metrics.getHeight();
		float x = (float) (getX() + getWidth()/2.0 - titleWidth);
		float y = (float) (getY() + getHeight()/2.0 - titleHeight);

		g2d.drawString(title, x, y);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}

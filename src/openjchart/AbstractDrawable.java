package openjchart;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import openjchart.util.Dimension2D;

public abstract class AbstractDrawable implements Drawable {
	private Rectangle2D bounds;

	public AbstractDrawable() {
		bounds = new Rectangle2D.Double();
	}

	@Override
	public abstract void draw(Graphics2D graphics);

	@Override
	public Rectangle2D getBounds() {
		Rectangle2D b = new Rectangle2D.Double();
		b.setFrame(bounds);
		return b;
	}

	@Override
	public double getHeight() {
		return bounds.getHeight();
	}

	@Override
	public double getWidth() {
		return bounds.getWidth();
	}

	@Override
	public double getX() {
		return bounds.getX();
	}

	@Override
	public double getY() {
		return bounds.getY();
	}

	@Override
	public void setBounds(Rectangle2D bounds) {
		this.bounds.setFrame(bounds);
	}

	@Override
	public Dimension2D getPreferredSize() {
		return new Dimension2D.Double();
	}
}
